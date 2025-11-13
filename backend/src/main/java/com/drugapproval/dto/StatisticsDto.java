package com.drugapproval.dto;

import com.drugapproval.entity.QueryHistory.PromptType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 통계 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {

    private DocumentStatistics documentStatistics;
    private QueryStatistics queryStatistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentStatistics {
        private long totalDocuments;
        private long completedDocuments;
        private long processingDocuments;
        private long failedDocuments;
        private long totalSize;
        private int totalPages;
        private Map<String, Long> statusCounts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueryStatistics {
        private long totalQueries;
        private long queriesToday;
        private long queriesThisWeek;
        private long queriesThisMonth;
        private Map<PromptType, Long> promptTypeCounts;
        private Map<PromptType, Double> averageResponseTimes;
        private List<TopUser> topUsers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopUser {
        private String userName;
        private String department;
        private long queryCount;
    }
}
