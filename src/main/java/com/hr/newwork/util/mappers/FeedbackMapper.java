package com.hr.newwork.util.mappers;

import com.hr.newwork.data.entity.Feedback;
import com.hr.newwork.data.dto.FeedbackDto;

public class FeedbackMapper {
    private static String buildFullName(String firstName, String lastName) {
        StringBuilder sb = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) sb.append(firstName);
        if (lastName != null && !lastName.isBlank()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(lastName);
        }
        return sb.toString().trim();
    }

    public static FeedbackDto toDto(Feedback feedback) {
        if (feedback == null) return null;
        String authorName = null;
        if (feedback.getAuthor() != null) {
            authorName = buildFullName(feedback.getAuthor().getFirstName(), feedback.getAuthor().getLastName());
        }
        String targetUserName = null;
        if (feedback.getTargetUser() != null) {
            targetUserName = buildFullName(feedback.getTargetUser().getFirstName(), feedback.getTargetUser().getLastName());
        }
        return FeedbackDto.builder()
            .id(feedback.getId() != null ? feedback.getId().toString() : null)
            .authorName(authorName)
            .targetUserName(targetUserName)
            .content(feedback.getContent())
            .polishedContent(feedback.getPolishedContent())
            .createdAt(feedback.getCreatedAt())
            .visibility(feedback.getVisibility() != null ? feedback.getVisibility().name() : null)
            .status(feedback.getStatus() != null ? feedback.getStatus().name() : null)
            .polishError(feedback.getPolishError())
            .build();
    }
}
