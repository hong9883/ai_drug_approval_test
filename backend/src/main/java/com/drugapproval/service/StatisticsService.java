package com.drugapproval.service;

import com.drugapproval.dto.StatisticsDto;
import com.drugapproval.entity.Document;
import com.drugapproval.entity.QueryHistory;
import com.drugapproval.repository.DocumentRepository;
import com.drugapproval.repository.QueryHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 통계 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final DocumentRepository documentRepository;
    private final QueryHistoryRepository queryHistoryRepository;

    /**
     * 전체 통계 조회
     */
    public StatisticsDto getStatistics() {
        return StatisticsDto.builder()
            .documentStatistics(getDocumentStatistics())
            .queryStatistics(getQueryStatistics())
            .build();
    }

    /**
     * 문서 통계
     */
    public StatisticsDto.DocumentStatistics getDocumentStatistics() {
        long totalDocuments = documentRepository.count();
        long completedDocuments = documentRepository.countByStatus(Document.DocumentStatus.COMPLETED);
        long processingDocuments = documentRepository.countByStatus(Document.DocumentStatus.PROCESSING);
        long failedDocuments = documentRepository.countByStatus(Document.DocumentStatus.FAILED);

        List<Object[]> statusCounts = documentRepository.countByStatusGrouped();
        Map<String, Long> statusCountsMap = statusCounts.stream()
            .collect(Collectors.toMap(
                arr -> arr[0].toString(),
                arr -> ((Number) arr[1]).longValue()
            ));

        List<Document> allDocuments = documentRepository.findAll();
        long totalSize = allDocuments.stream()
            .mapToLong(Document::getFileSize)
            .sum();

        int totalPages = allDocuments.stream()
            .mapToInt(Document::getPageCount)
            .sum();

        return StatisticsDto.DocumentStatistics.builder()
            .totalDocuments(totalDocuments)
            .completedDocuments(completedDocuments)
            .processingDocuments(processingDocuments)
            .failedDocuments(failedDocuments)
            .totalSize(totalSize)
            .totalPages(totalPages)
            .statusCounts(statusCountsMap)
            .build();
    }

    /**
     * 질의 통계
     */
    public StatisticsDto.QueryStatistics getQueryStatistics() {
        long totalQueries = queryHistoryRepository.count();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusWeeks(1);
        LocalDateTime monthStart = now.minusMonths(1);

        long queriesToday = queryHistoryRepository.countSince(todayStart);
        long queriesThisWeek = queryHistoryRepository.countSince(weekStart);
        long queriesThisMonth = queryHistoryRepository.countSince(monthStart);

        // 프롬프트 타입별 개수
        List<Object[]> promptTypeCounts = queryHistoryRepository.countByPromptTypeGrouped();
        Map<QueryHistory.PromptType, Long> promptTypeCountsMap = promptTypeCounts.stream()
            .collect(Collectors.toMap(
                arr -> (QueryHistory.PromptType) arr[0],
                arr -> ((Number) arr[1]).longValue()
            ));

        // 프롬프트 타입별 평균 응답 시간
        Map<QueryHistory.PromptType, Double> averageResponseTimes = new HashMap<>();
        for (QueryHistory.PromptType type : QueryHistory.PromptType.values()) {
            Double avgTime = queryHistoryRepository.averageResponseTimeByPromptType(type);
            if (avgTime != null) {
                averageResponseTimes.put(type, avgTime);
            }
        }

        // 상위 사용자 (간단한 구현)
        List<StatisticsDto.TopUser> topUsers = getTopUsers();

        return StatisticsDto.QueryStatistics.builder()
            .totalQueries(totalQueries)
            .queriesToday(queriesToday)
            .queriesThisWeek(queriesThisWeek)
            .queriesThisMonth(queriesThisMonth)
            .promptTypeCounts(promptTypeCountsMap)
            .averageResponseTimes(averageResponseTimes)
            .topUsers(topUsers)
            .build();
    }

    /**
     * 상위 사용자 목록
     */
    private List<StatisticsDto.TopUser> getTopUsers() {
        List<QueryHistory> allQueries = queryHistoryRepository.findAll();

        Map<String, Long> userCounts = allQueries.stream()
            .collect(Collectors.groupingBy(
                QueryHistory::getUserName,
                Collectors.counting()
            ));

        return userCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .map(entry -> {
                QueryHistory sampleQuery = allQueries.stream()
                    .filter(q -> q.getUserName().equals(entry.getKey()))
                    .findFirst()
                    .orElse(null);

                return StatisticsDto.TopUser.builder()
                    .userName(entry.getKey())
                    .department(sampleQuery != null ? sampleQuery.getUserDepartment() : "")
                    .queryCount(entry.getValue())
                    .build();
            })
            .collect(Collectors.toList());
    }
}
