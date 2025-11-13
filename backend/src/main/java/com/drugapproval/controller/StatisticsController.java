package com.drugapproval.controller;

import com.drugapproval.dto.StatisticsDto;
import com.drugapproval.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 통계 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 전체 통계 조회
     */
    @GetMapping
    public ResponseEntity<StatisticsDto> getStatistics() {
        StatisticsDto statistics = statisticsService.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * 문서 통계 조회
     */
    @GetMapping("/documents")
    public ResponseEntity<StatisticsDto.DocumentStatistics> getDocumentStatistics() {
        StatisticsDto.DocumentStatistics statistics = statisticsService.getDocumentStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * 질의 통계 조회
     */
    @GetMapping("/queries")
    public ResponseEntity<StatisticsDto.QueryStatistics> getQueryStatistics() {
        StatisticsDto.QueryStatistics statistics = statisticsService.getQueryStatistics();
        return ResponseEntity.ok(statistics);
    }
}
