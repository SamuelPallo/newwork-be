package com.hr.newwork.services.polish;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of FeedbackPolisher for local/testing use.
 * Simulates AI polishing by appending a marker to the content.
 */
@Component
@ConditionalOnProperty(prefix = "huggingface.api", name = "key", havingValue = "", matchIfMissing = true)
public class MockFeedbackPolisher implements FeedbackPolisher {
    @Override
    public String polish(String content, String model) {
        // Simulate a HuggingFace call by appending a marker and the model name
        return content + " [Polished by AI, model: " + model + "]";
    }
}
