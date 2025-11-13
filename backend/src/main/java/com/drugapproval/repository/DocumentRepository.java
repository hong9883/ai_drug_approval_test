package com.drugapproval.repository;

import com.drugapproval.entity.Document;
import com.drugapproval.entity.Document.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 문서 리포지토리
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);

    Page<Document> findByFileNameContaining(String fileName, Pageable pageable);

    List<Document> findByUploadedBy(String uploadedBy);

    List<Document> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.status = :status")
    long countByStatus(DocumentStatus status);

    @Query("SELECT d.status, COUNT(d) FROM Document d GROUP BY d.status")
    List<Object[]> countByStatusGrouped();
}
