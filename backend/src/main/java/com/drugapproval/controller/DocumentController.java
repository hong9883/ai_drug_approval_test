package com.drugapproval.controller;

import com.drugapproval.dto.DocumentDto;
import com.drugapproval.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 문서 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /**
     * 문서 업로드
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDto> uploadDocument(
        @RequestParam("file") MultipartFile file,
        @RequestParam("uploadedBy") String uploadedBy,
        @RequestParam(value = "description", required = false) String description
    ) {
        log.info("Uploading document: {}", file.getOriginalFilename());
        DocumentDto document = documentService.uploadDocument(file, uploadedBy, description);
        return ResponseEntity.ok(document);
    }

    /**
     * 문서 목록 조회
     */
    @GetMapping
    public ResponseEntity<Page<DocumentDto>> getDocuments(Pageable pageable) {
        Page<DocumentDto> documents = documentService.getDocuments(pageable);
        return ResponseEntity.ok(documents);
    }

    /**
     * 문서 검색
     */
    @GetMapping("/search")
    public ResponseEntity<Page<DocumentDto>> searchDocuments(
        @RequestParam("keyword") String keyword,
        Pageable pageable
    ) {
        Page<DocumentDto> documents = documentService.searchDocuments(keyword, pageable);
        return ResponseEntity.ok(documents);
    }

    /**
     * 문서 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocument(@PathVariable Long id) {
        DocumentDto document = documentService.getDocument(id);
        return ResponseEntity.ok(document);
    }

    /**
     * 문서 다운로드
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        File file = documentService.getDocumentFile(id);
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(resource);
    }

    /**
     * 문서 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
