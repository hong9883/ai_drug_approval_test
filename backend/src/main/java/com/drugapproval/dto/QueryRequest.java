package com.drugapproval.dto;

import com.drugapproval.entity.QueryHistory.PromptType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 질문 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {

    @NotBlank(message = "질문은 필수입니다")
    private String question;

    @NotNull(message = "프롬프트 타입은 필수입니다")
    private PromptType promptType;

    @NotBlank(message = "사용자 이름은 필수입니다")
    private String userName;

    private String userDepartment;
}
