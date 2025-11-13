package com.drugapproval.config;

import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenSearch 클라이언트 설정
 */
@Configuration
public class OpenSearchConfig {

    @Value("${app.opensearch.url}")
    private String opensearchUrl;

    @Bean
    public OpenSearchClient openSearchClient() {
        // Parse URL
        String host = opensearchUrl.replace("http://", "").replace("https://", "").split(":")[0];
        int port = 9200;

        if (opensearchUrl.contains(":")) {
            String[] parts = opensearchUrl.replace("http://", "").replace("https://", "").split(":");
            if (parts.length > 1) {
                port = Integer.parseInt(parts[1].replace("/", ""));
            }
        }

        RestClient restClient = RestClient.builder(new HttpHost(host, port, "http")).build();
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        return new OpenSearchClient(transport);
    }
}
