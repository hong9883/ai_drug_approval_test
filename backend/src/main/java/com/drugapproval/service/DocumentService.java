package com.drugapproval.service;

import com.drugapproval.dto.DocumentDto;
import com.drugapproval.entity.Document;
import com.drugapproval.entity.Document.DocumentStatus;
import com.drugapproval.repository.DocumentRepository;
import com.drugapproval.util.PdfProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 문서 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PdfProcessor pdfProcessor;
    private final ChromaService chromaService;
    private final OpenSearchService openSearchService;

    @Value("${app.upload.directory}")
    private String uploadDirectory;

    /**
     * 문서 업로드 및 처리
     */
    @Transactional
    public DocumentDto uploadDocument(MultipartFile file, String uploadedBy, String description) {
        try {
            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get(uploadDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            // 문서 엔티티 생성
            Document document = Document.builder()
                .fileName(fileName)
                .originalFileName(file.getOriginalFilename())
                .filePath(filePath.toString())
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .description(description)
                .pageCount(0)
                .uploadedBy(uploadedBy)
                .status(DocumentStatus.UPLOADING)
                .build();

            document = documentRepository.save(document);

            // 비동기로 PDF 처리 (실제로는 별도 스레드나 큐 사용 권장)
            processDocument(document.getId());

            return DocumentDto.fromEntity(document);
        } catch (Exception e) {
            log.error("Error uploading document", e);
            throw new RuntimeException("Failed to upload document", e);
        }
    }

    /**
     * 문서 처리 (PDF 추출, 벡터DB 저장, 검색 인덱싱)
     */
    @Transactional
    public void processDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found"));

        try {
            document.setStatus(DocumentStatus.PROCESSING);
            documentRepository.save(document);

            File pdfFile = new File(document.getFilePath());

            // PDF 페이지 수 추출
            int pageCount = pdfProcessor.getPageCount(pdfFile);
            document.setPageCount(pageCount);

            // PDF 텍스트 추출 (청크 단위)
            List<PdfProcessor.TextChunk> chunks = pdfProcessor.extractChunks(pdfFile, 1000, 200);

            // ChromaDB에 저장
            List<String> texts = chunks.stream()
                .map(PdfProcessor.TextChunk::text)
                .collect(Collectors.toList());

            List<Map<String, Object>> metadatas = chunks.stream()
                .map(chunk -> {
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("documentId", document.getId());
                    metadata.put("fileName", document.getOriginalFileName());
                    metadata.put("pageNumber", chunk.pageNumber());
                    metadata.put("chunkIndex", chunk.chunkIndex());
                    return metadata;
                })
                .collect(Collectors.toList());

            List<String> ids = chunks.stream()
                .map(chunk -> document.getId() + "_" + chunk.pageNumber() + "_" + chunk.chunkIndex())
                .collect(Collectors.toList());

            chromaService.addDocuments(texts, metadatas, ids);
            document.setChromaCollectionId(ids.get(0));

            // OpenSearch에 인덱싱
            String fullText = pdfProcessor.extractAllText(pdfFile);
            Map<String, Object> searchDocument = new HashMap<>();
            searchDocument.put("documentId", document.getId());
            searchDocument.put("fileName", document.getOriginalFileName());
            searchDocument.put("description", document.getDescription());
            searchDocument.put("content", fullText);
            searchDocument.put("pageCount", pageCount);
            searchDocument.put("uploadedBy", document.getUploadedBy());
            searchDocument.put("createdAt", document.getCreatedAt());

            String searchDocId = openSearchService.indexDocument(document.getId().toString(), searchDocument);
            document.setOpenSearchDocumentId(searchDocId);

            // 완료 상태로 변경
            document.setStatus(DocumentStatus.COMPLETED);
            documentRepository.save(document);

            log.info("Document {} processed successfully", document.getId());
        } catch (Exception e) {
            log.error("Error processing document {}", documentId, e);
            document.setStatus(DocumentStatus.FAILED);
            document.setErrorMessage(e.getMessage());
            documentRepository.save(document);
        }
    }

    /**
     * 문서 목록 조회
     */
    public Page<DocumentDto> getDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable)
            .map(DocumentDto::fromEntity);
    }

    /**
     * 문서 검색
     */
    public Page<DocumentDto> searchDocuments(String keyword, Pageable pageable) {
        return documentRepository.findByFileNameContaining(keyword, pageable)
            .map(DocumentDto::fromEntity);
    }

    /**
     * 문서 상세 조회
     */
    public DocumentDto getDocument(Long id) {
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found"));
        return DocumentDto.fromEntity(document);
    }

    /**
     * 문서 삭제
     */
    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found"));

        try {
            // 파일 시스템에서 삭제
            Path filePath = Paths.get(document.getFilePath());
            Files.deleteIfExists(filePath);

            // ChromaDB에서 삭제
            if (document.getChromaCollectionId() != null) {
                chromaService.deleteDocuments(List.of(document.getChromaCollectionId()));
            }

            // OpenSearch에서 삭제
            if (document.getOpenSearchDocumentId() != null) {
                openSearchService.deleteDocument(document.getOpenSearchDocumentId());
            }

            // DB에서 삭제
            documentRepository.delete(document);

            log.info("Document {} deleted successfully", id);
        } catch (Exception e) {
            log.error("Error deleting document {}", id, e);
            throw new RuntimeException("Failed to delete document", e);
        }
    }

    /**
     * 문서 파일 다운로드
     */
    public File getDocumentFile(Long id) {
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found"));
        return new File(document.getFilePath());
    }
}
