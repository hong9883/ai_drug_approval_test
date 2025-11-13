package com.drugapproval.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * ChromaDB 벡터 데이터베이스 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChromaService {

    @Qualifier("chromaWebClient")
    private final WebClient chromaWebClient;

    private final ObjectMapper objectMapper;

    @Value("${app.chroma.collection-name}")
    private String collectionName;

    /**
     * 컬렉션 생성
     */
    public void createCollection() {
        try {
            Map<String, Object> request = Map.of(
                "name", collectionName,
                "metadata", Map.of("description", "Drug approval documents")
            );

            chromaWebClient.post()
                .uri("/api/v1/collections")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    log.warn("Collection may already exist: {}", e.getMessage());
                    return Mono.just("");
                })
                .block();

            log.info("Collection '{}' created or already exists", collectionName);
        } catch (Exception e) {
            log.error("Error creating collection", e);
        }
    }

    /**
     * 문서 임베딩 추가
     */
    public String addDocuments(List<String> texts, List<Map<String, Object>> metadatas, List<String> ids) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("documents", texts);
            request.put("metadatas", metadatas);
            request.put("ids", ids);

            String response = chromaWebClient.post()
                .uri("/api/v1/collections/" + collectionName + "/add")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info("Added {} documents to ChromaDB", texts.size());
            return response;
        } catch (Exception e) {
            log.error("Error adding documents to ChromaDB", e);
            throw new RuntimeException("Failed to add documents to ChromaDB", e);
        }
    }

    /**
     * 유사 문서 검색
     */
    public List<SearchResult> query(String queryText, int nResults) {
        try {
            Map<String, Object> request = Map.of(
                "query_texts", List.of(queryText),
                "n_results", nResults
            );

            String response = chromaWebClient.post()
                .uri("/api/v1/collections/" + collectionName + "/query")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return parseSearchResults(response);
        } catch (Exception e) {
            log.error("Error querying ChromaDB", e);
            return Collections.emptyList();
        }
    }

    /**
     * 문서 삭제
     */
    public void deleteDocuments(List<String> ids) {
        try {
            Map<String, Object> request = Map.of("ids", ids);

            chromaWebClient.post()
                .uri("/api/v1/collections/" + collectionName + "/delete")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info("Deleted {} documents from ChromaDB", ids.size());
        } catch (Exception e) {
            log.error("Error deleting documents from ChromaDB", e);
        }
    }

    /**
     * 검색 결과 파싱
     */
    private List<SearchResult> parseSearchResults(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            List<SearchResult> results = new ArrayList<>();

            JsonNode ids = root.path("ids").get(0);
            JsonNode documents = root.path("documents").get(0);
            JsonNode metadatas = root.path("metadatas").get(0);
            JsonNode distances = root.path("distances").get(0);

            for (int i = 0; i < ids.size(); i++) {
                SearchResult result = new SearchResult();
                result.setId(ids.get(i).asText());
                result.setDocument(documents.get(i).asText());
                result.setMetadata(objectMapper.convertValue(metadatas.get(i), Map.class));
                result.setDistance(distances.get(i).asDouble());
                result.setSimilarity(1.0 - result.getDistance());

                results.add(result);
            }

            return results;
        } catch (Exception e) {
            log.error("Error parsing search results", e);
            return Collections.emptyList();
        }
    }

    /**
     * 검색 결과 클래스
     */
    public static class SearchResult {
        private String id;
        private String document;
        private Map<String, Object> metadata;
        private Double distance;
        private Double similarity;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getDocument() { return document; }
        public void setDocument(String document) { this.document = document; }

        public Map<String, Object> metadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

        public Double getDistance() { return distance; }
        public void setDistance(Double distance) { this.distance = distance; }

        public Double getSimilarity() { return similarity; }
        public void setSimilarity(Double similarity) { this.similarity = similarity; }
    }
}
