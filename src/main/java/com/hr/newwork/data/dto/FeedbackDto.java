package com.hr.newwork.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
public class FeedbackDto {
    private UUID id;
    private UUID authorId;
    private UUID targetUserId;
    private String content;
    private String polishedContent;
    private LocalDateTime createdAt;
    private String visibility;
    private String status;
    private String polishError;
    // Getters and setters
}
