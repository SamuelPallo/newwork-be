package com.hr.newwork.services;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class FeedbackService {
    // Feedback flows
    public void createFeedback(UUID userId, String content, boolean polish) {
        // Implement feedback creation logic
    }
    public void listFeedback(UUID userId) {
        // Implement feedback listing logic
    }
    public void editFeedback(UUID feedbackId) {
        // Implement feedback editing logic
    }
}

