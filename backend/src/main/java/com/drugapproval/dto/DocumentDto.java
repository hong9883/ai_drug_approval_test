package com.drugapproval.dto;

import com.drugapproval.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 문서 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {

    private Long id;
    private String fileName;
    private String originalFileName;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private String description;
    private Integer pageCount;
    private String uploadedBy;
    private Document.DocumentStatus status;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DocumentDto fromEntity(Document document) {
        return DocumentDto.builder()
            .id(document.getId())
            .fileName(document.getFileName())
            .originalFileName(document.getOriginalFileName())
            .filePath(document.getFilePath())
            .fileSize(document.getFileSize())
            .mimeType(document.getMimeType())
            .description(document.getDescription())
            .pageCount(document.getPageCount())
            .uploadedBy(document.getUploadedBy())
            .status(document.getStatus())
            .errorMessage(document.getErrorMessage())
            .createdAt(document.getCreatedAt())
            .updatedAt(document.getUpdatedAt())
            .build();
    }
}
