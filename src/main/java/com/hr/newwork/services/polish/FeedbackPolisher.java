package com.hr.newwork.services.polish;

/**
 * Strategy interface for polishing feedback content using AI or other means.
 */
public interface FeedbackPolisher {
    /**
     * Polish the given feedback content using the specified model (e.g., using HuggingFace or a mock).
     * @param content the original feedback content
     * @param model the model to use for polishing
     * @return the polished content
     * @throws Exception if polishing fails
     */
    String polish(String content, String model) throws Exception;
}
