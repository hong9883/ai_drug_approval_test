package com.drugapproval.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * OpenSearch 검색 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final OpenSearchClient openSearchClient;

    @Value("${app.opensearch.index-name}")
    private String indexName;

    /**
     * 인덱스 생성
     */
    public void createIndex() {
        try {
            ExistsRequest existsRequest = ExistsRequest.of(e -> e.index(indexName));
            boolean exists = openSearchClient.indices().exists(existsRequest).value();

            if (!exists) {
                CreateIndexRequest createIndexRequest = CreateIndexRequest.of(c -> c
                    .index(indexName)
                );

                openSearchClient.indices().create(createIndexRequest);
                log.info("Index '{}' created", indexName);
            } else {
                log.info("Index '{}' already exists", indexName);
            }
        } catch (Exception e) {
            log.error("Error creating index", e);
        }
    }

    /**
     * 문서 인덱싱
     */
    public String indexDocument(String id, Map<String, Object> document) {
        try {
            IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                .index(indexName)
                .id(id)
                .document(document)
            );

            IndexResponse response = openSearchClient.index(request);
            log.info("Document indexed with id: {}", response.id());
            return response.id();
        } catch (Exception e) {
            log.error("Error indexing document", e);
            throw new RuntimeException("Failed to index document", e);
        }
    }

    /**
     * 문서 검색
     */
    public List<Map<String, Object>> search(String queryText, int size) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(indexName)
                .query(q -> q
                    .multiMatch(m -> m
                        .query(queryText)
                        .fields("content", "fileName", "description")
                    )
                )
                .size(size)
            );

            SearchResponse<Map> response = openSearchClient.search(searchRequest, Map.class);

            List<Map<String, Object>> results = new ArrayList<>();
            for (Hit<Map> hit : response.hits().hits()) {
                Map<String, Object> result = new HashMap<>(hit.source());
                result.put("_id", hit.id());
                result.put("_score", hit.score());
                results.add(result);
            }

            return results;
        } catch (Exception e) {
            log.error("Error searching documents", e);
            return Collections.emptyList();
        }
    }

    /**
     * 문서 가져오기
     */
    public Map<String, Object> getDocument(String id) {
        try {
            GetRequest getRequest = GetRequest.of(g -> g
                .index(indexName)
                .id(id)
            );

            GetResponse<Map> response = openSearchClient.get(getRequest, Map.class);
            return response.source();
        } catch (Exception e) {
            log.error("Error getting document", e);
            return null;
        }
    }

    /**
     * 문서 삭제
     */
    public void deleteDocument(String id) {
        try {
            DeleteRequest request = DeleteRequest.of(d -> d
                .index(indexName)
                .id(id)
            );

            openSearchClient.delete(request);
            log.info("Document deleted with id: {}", id);
        } catch (Exception e) {
            log.error("Error deleting document", e);
        }
    }
}
