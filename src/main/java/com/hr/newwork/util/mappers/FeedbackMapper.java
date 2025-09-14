package com.hr.newwork.util.mappers;

import com.hr.newwork.data.entity.Feedback;
import com.hr.newwork.data.dto.FeedbackDto;

public class FeedbackMapper {
    public static FeedbackDto toDto(Feedback feedback) {
        if (feedback == null) return null;
        return FeedbackDto.builder()
            .id(feedback.getId())
            .authorId(feedback.getAuthor() != null ? feedback.getAuthor().getId() : null)
            .targetUserId(feedback.getTargetUser() != null ? feedback.getTargetUser().getId() : null)
            .content(feedback.getContent())
            .polishedContent(feedback.getPolishedContent())
            .createdAt(feedback.getCreatedAt())
            .visibility(feedback.getVisibility() != null ? feedback.getVisibility().name() : null)
            .status(feedback.getStatus() != null ? feedback.getStatus().name() : null)
            .polishError(feedback.getPolishError())
            .build();
    }
}
