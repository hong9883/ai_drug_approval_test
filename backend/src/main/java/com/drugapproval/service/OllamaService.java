package com.drugapproval.service;

import com.drugapproval.entity.QueryHistory.PromptType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Ollama LLM 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaService {

    @Qualifier("ollamaWebClient")
    private final WebClient ollamaWebClient;

    private final ObjectMapper objectMapper;

    @Value("${app.ollama.model}")
    private String model;

    /**
     * LLM에 질문하고 응답 받기
     */
    public String generate(String prompt, PromptType promptType, List<String> contextDocuments) {
        try {
            String enhancedPrompt = buildPrompt(prompt, promptType, contextDocuments);

            Map<String, Object> request = Map.of(
                "model", model,
                "prompt", enhancedPrompt,
                "stream", false
            );

            String response = ollamaWebClient.post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return extractResponse(response);
        } catch (Exception e) {
            log.error("Error generating response from Ollama", e);
            throw new RuntimeException("Failed to generate response", e);
        }
    }

    /**
     * 프롬프트 타입에 따라 프롬프트 구성
     */
    private String buildPrompt(String question, PromptType promptType, List<String> contextDocuments) {
        StringBuilder prompt = new StringBuilder();

        // 컨텍스트 문서 추가
        if (contextDocuments != null && !contextDocuments.isEmpty()) {
            prompt.append("다음은 참고할 문서 내용입니다:\n\n");
            for (int i = 0; i < contextDocuments.size(); i++) {
                prompt.append("문서 ").append(i + 1).append(":\n");
                prompt.append(contextDocuments.get(i)).append("\n\n");
            }
            prompt.append("---\n\n");
        }

        // 프롬프트 타입별 지시사항
        switch (promptType) {
            case BASIC:
                prompt.append("질문: ").append(question).append("\n\n");
                prompt.append("위 문서를 참고하여 질문에 답변해주세요.");
                break;

            case STRUCTURED:
                prompt.append("다음 형식으로 구조화된 답변을 제공해주세요:\n");
                prompt.append("1. 요약\n");
                prompt.append("2. 상세 설명\n");
                prompt.append("3. 결론\n\n");
                prompt.append("질문: ").append(question);
                break;

            case SIMPLE:
                prompt.append("간단하고 명확하게 답변해주세요.\n\n");
                prompt.append("질문: ").append(question);
                break;

            case DETAILED:
                prompt.append("가능한 한 상세하고 포괄적으로 답변해주세요. ");
                prompt.append("관련된 모든 측면을 고려하여 설명해주세요.\n\n");
                prompt.append("질문: ").append(question);
                break;

            case POINT:
                prompt.append("핵심 포인트를 bullet point 형식으로 정리해주세요.\n\n");
                prompt.append("질문: ").append(question);
                break;

            case FACT_CHECK:
                prompt.append("사실 확인 모드로 답변해주세요:\n");
                prompt.append("- 확인된 사실\n");
                prompt.append("- 불확실한 정보\n");
                prompt.append("- 추가 검증이 필요한 사항\n\n");
                prompt.append("질문: ").append(question);
                break;

            case STEP_BY_STEP:
                prompt.append("단계별로 사고 과정을 보여주며 답변해주세요:\n");
                prompt.append("1. 문제 분석\n");
                prompt.append("2. 관련 정보 파악\n");
                prompt.append("3. 논리적 추론\n");
                prompt.append("4. 결론\n\n");
                prompt.append("질문: ").append(question);
                break;
        }

        return prompt.toString();
    }

    /**
     * Ollama 응답에서 텍스트 추출
     */
    private String extractResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("response").asText();
        } catch (Exception e) {
            log.error("Error parsing Ollama response", e);
            return "응답 처리 중 오류가 발생했습니다.";
        }
    }

    /**
     * 모델이 사용 가능한지 확인
     */
    public boolean isModelAvailable() {
        try {
            String response = ollamaWebClient.get()
                .uri("/api/tags")
                .retrieve()
                .bodyToMono(String.class)
                .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode models = root.path("models");

            for (JsonNode modelNode : models) {
                if (modelNode.path("name").asText().contains(model.split(":")[0])) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Error checking model availability", e);
            return false;
        }
    }
}
