package com.drugapproval.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * WebClient 설정 (ChromaDB, Ollama 통신용)
 */
@Configuration
public class WebClientConfig {

    @Value("${app.chroma.url}")
    private String chromaUrl;

    @Value("${app.chroma.auth-token}")
    private String chromaAuthToken;

    @Value("${app.ollama.url}")
    private String ollamaUrl;

    @Bean(name = "chromaWebClient")
    public WebClient chromaWebClient() {
        HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofMinutes(5));

        return WebClient.builder()
            .baseUrl(chromaUrl)
            .defaultHeader("X_CHROMA_TOKEN", chromaAuthToken)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }

    @Bean(name = "ollamaWebClient")
    public WebClient ollamaWebClient() {
        HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofMinutes(10));

        return WebClient.builder()
            .baseUrl(ollamaUrl)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }
}
