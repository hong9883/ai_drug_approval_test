package com.drugapproval.controller;

import com.drugapproval.dto.QueryRequest;
import com.drugapproval.dto.QueryResponse;
import com.drugapproval.entity.QueryHistory;
import com.drugapproval.service.QueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 질의응답 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/queries")
@RequiredArgsConstructor
public class QueryController {

    private final QueryService queryService;

    /**
     * 질문 처리
     */
    @PostMapping
    public ResponseEntity<QueryResponse> processQuery(@Valid @RequestBody QueryRequest request) {
        log.info("Processing query from user: {}", request.getUserName());
        QueryResponse response = queryService.processQuery(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 질문 이력 조회
     */
    @GetMapping("/history")
    public ResponseEntity<Page<QueryHistory>> getQueryHistory(Pageable pageable) {
        Page<QueryHistory> history = queryService.getQueryHistory(pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * 사용자별 질문 이력 조회
     */
    @GetMapping("/history/user/{userName}")
    public ResponseEntity<Page<QueryHistory>> getUserQueryHistory(
        @PathVariable String userName,
        Pageable pageable
    ) {
        Page<QueryHistory> history = queryService.getUserQueryHistory(userName, pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * 질문 이력 상세 조회
     */
    @GetMapping("/history/{id}")
    public ResponseEntity<QueryResponse> getQueryDetail(@PathVariable Long id) {
        QueryResponse response = queryService.getQueryDetail(id);
        return ResponseEntity.ok(response);
    }
}
