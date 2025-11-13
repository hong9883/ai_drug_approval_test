package com.drugapproval.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 질문 이력 엔티티
 */
@Entity
@Table(name = "query_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromptType promptType;

    @Column(nullable = false)
    private String userName;

    @Column(length = 100)
    private String userDepartment;

    @Column(columnDefinition = "TEXT")
    private String relevantDocuments;

    @Column
    private Integer responseTimeMs;

    public enum PromptType {
        BASIC("기본"),
        STRUCTURED("구조화"),
        SIMPLE("간단"),
        DETAILED("상세"),
        POINT("포인트"),
        FACT_CHECK("사실 확인"),
        STEP_BY_STEP("단계별 사고");

        private final String description;

        PromptType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
