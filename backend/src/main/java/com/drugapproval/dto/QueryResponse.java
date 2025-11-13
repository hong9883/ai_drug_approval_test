package com.drugapproval.dto;

import com.drugapproval.entity.QueryHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 질문 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {

    private Long id;
    private String question;
    private String answer;
    private QueryHistory.PromptType promptType;
    private List<RelevantDocument> relevantDocuments;
    private Integer responseTimeMs;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelevantDocument {
        private Long documentId;
        private String fileName;
        private Integer pageNumber;
        private String excerpt;
        private Double similarity;
    }
}
