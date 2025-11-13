package com.drugapproval.repository;

import com.drugapproval.entity.QueryHistory;
import com.drugapproval.entity.QueryHistory.PromptType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 질문 이력 리포지토리
 */
@Repository
public interface QueryHistoryRepository extends JpaRepository<QueryHistory, Long> {

    Page<QueryHistory> findByUserName(String userName, Pageable pageable);

    Page<QueryHistory> findByPromptType(PromptType promptType, Pageable pageable);

    List<QueryHistory> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT q.promptType, COUNT(q) FROM QueryHistory q GROUP BY q.promptType")
    List<Object[]> countByPromptTypeGrouped();

    @Query("SELECT AVG(q.responseTimeMs) FROM QueryHistory q WHERE q.promptType = :promptType")
    Double averageResponseTimeByPromptType(PromptType promptType);

    @Query("SELECT COUNT(q) FROM QueryHistory q WHERE q.createdAt >= :startDate")
    long countSince(LocalDateTime startDate);
}
