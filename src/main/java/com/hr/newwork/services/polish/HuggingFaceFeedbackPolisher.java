package com.hr.newwork.services.polish;

import com.hr.newwork.services.RestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Real implementation of FeedbackPolisher using Hugging Face Inference API.
 */
@Component
@Primary
public class HuggingFaceFeedbackPolisher implements FeedbackPolisher {
    @Value("${huggingface.api.key:}")
    private String apiKey;

    @Value("${huggingface.model:google/flan-t5-base}")
    private String model;

    private final RestClientService restClientService;

    @Autowired
    public HuggingFaceFeedbackPolisher(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    @Override
    public String polish(String content, String model) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Hugging Face API key is not configured");
        }
        String endpoint = "https://api-inference.huggingface.co/models/" + (model != null && !model.isBlank() ? model : this.model);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        String payload = "{\"inputs\": " + toJsonString(content) + "}";
        try {
            ResponseEntity<String> response = restClientService.post(
                endpoint,
                payload,
                Map.of(
                    HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                    HttpHeaders.AUTHORIZATION, "Bearer " + apiKey
                ),
                null,
                String.class
            );
            return extractPolishedText(response.getBody());
        } catch (RestClientException e) {
            throw new RuntimeException("Hugging Face API call failed: " + e.getMessage(), e);
        }
    }

    private String toJsonString(String s) {
        return "\"" + s.replace("\"", "\\\"") + "\"";
    }

    private String extractPolishedText(String response) {
        // Simple extraction for text-generation models
        // Response: [{"generated_text": "..."}]
        if (response == null) return null;
        int idx = response.indexOf("generated_text");
        if (idx == -1) return response;
        int start = response.indexOf(":", idx) + 2;
        int end = response.indexOf("\"", start);
        return response.substring(start, end);
    }
}
