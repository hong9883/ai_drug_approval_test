package com.drugapproval.service;

import com.drugapproval.dto.QueryRequest;
import com.drugapproval.dto.QueryResponse;
import com.drugapproval.entity.QueryHistory;
import com.drugapproval.repository.QueryHistoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 질의응답 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryService {

    private final QueryHistoryRepository queryHistoryRepository;
    private final ChromaService chromaService;
    private final OllamaService ollamaService;
    private final ObjectMapper objectMapper;

    /**
     * 질문 처리 및 응답 생성
     */
    @Transactional
    public QueryResponse processQuery(QueryRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            // 1. ChromaDB에서 관련 문서 검색
            List<ChromaService.SearchResult> searchResults = chromaService.query(request.getQuestion(), 5);

            // 2. 검색된 문서 컨텍스트 구성
            List<String> contextDocuments = searchResults.stream()
                .map(ChromaService.SearchResult::getDocument)
                .collect(Collectors.toList());

            // 3. LLM을 통해 답변 생성
            String answer = ollamaService.generate(
                request.getQuestion(),
                request.getPromptType(),
                contextDocuments
            );

            // 4. 관련 문서 정보 구성
            List<QueryResponse.RelevantDocument> relevantDocuments = searchResults.stream()
                .map(result -> {
                    Map<String, Object> metadata = result.metadata();
                    return QueryResponse.RelevantDocument.builder()
                        .documentId(getLong(metadata.get("documentId")))
                        .fileName((String) metadata.get("fileName"))
                        .pageNumber(getInteger(metadata.get("pageNumber")))
                        .excerpt(truncate(result.getDocument(), 200))
                        .similarity(result.getSimilarity())
                        .build();
                })
                .collect(Collectors.toList());

            int responseTime = (int) (System.currentTimeMillis() - startTime);

            // 5. 질문 이력 저장
            QueryHistory history = QueryHistory.builder()
                .question(request.getQuestion())
                .answer(answer)
                .promptType(request.getPromptType())
                .userName(request.getUserName())
                .userDepartment(request.getUserDepartment())
                .relevantDocuments(serializeRelevantDocuments(relevantDocuments))
                .responseTimeMs(responseTime)
                .build();

            history = queryHistoryRepository.save(history);

            // 6. 응답 생성
            return QueryResponse.builder()
                .id(history.getId())
                .question(request.getQuestion())
                .answer(answer)
                .promptType(request.getPromptType())
                .relevantDocuments(relevantDocuments)
                .responseTimeMs(responseTime)
                .createdAt(history.getCreatedAt())
                .build();

        } catch (Exception e) {
            log.error("Error processing query", e);
            throw new RuntimeException("Failed to process query", e);
        }
    }

    /**
     * 질문 이력 조회 (페이징)
     */
    public Page<QueryHistory> getQueryHistory(Pageable pageable) {
        return queryHistoryRepository.findAll(pageable);
    }

    /**
     * 사용자별 질문 이력 조회
     */
    public Page<QueryHistory> getUserQueryHistory(String userName, Pageable pageable) {
        return queryHistoryRepository.findByUserName(userName, pageable);
    }

    /**
     * 질문 이력 상세 조회
     */
    public QueryResponse getQueryDetail(Long id) {
        QueryHistory history = queryHistoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Query history not found"));

        List<QueryResponse.RelevantDocument> relevantDocuments =
            deserializeRelevantDocuments(history.getRelevantDocuments());

        return QueryResponse.builder()
            .id(history.getId())
            .question(history.getQuestion())
            .answer(history.getAnswer())
            .promptType(history.getPromptType())
            .relevantDocuments(relevantDocuments)
            .responseTimeMs(history.getResponseTimeMs())
            .createdAt(history.getCreatedAt())
            .build();
    }

    /**
     * 관련 문서 직렬화
     */
    private String serializeRelevantDocuments(List<QueryResponse.RelevantDocument> documents) {
        try {
            return objectMapper.writeValueAsString(documents);
        } catch (Exception e) {
            log.error("Error serializing relevant documents", e);
            return "[]";
        }
    }

    /**
     * 관련 문서 역직렬화
     */
    private List<QueryResponse.RelevantDocument> deserializeRelevantDocuments(String json) {
        try {
            return objectMapper.readValue(json,
                new TypeReference<List<QueryResponse.RelevantDocument>>() {});
        } catch (Exception e) {
            log.error("Error deserializing relevant documents", e);
            return new ArrayList<>();
        }
    }

    /**
     * 텍스트 자르기
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    /**
     * Object to Long 변환
     */
    private Long getLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Object to Integer 변환
     */
    private Integer getInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
