package com.drugapproval.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 문서 엔티티
 */
@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String mimeType;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer pageCount;

    @Column(length = 500)
    private String chromaCollectionId;

    @Column(length = 500)
    private String openSearchDocumentId;

    @Column(nullable = false)
    private String uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @Column(length = 1000)
    private String errorMessage;

    public enum DocumentStatus {
        UPLOADING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}
