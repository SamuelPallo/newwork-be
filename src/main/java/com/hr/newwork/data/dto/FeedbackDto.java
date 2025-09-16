package com.hr.newwork.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class FeedbackDto {
    private String id;
    private String authorName;
    private String targetUserName;
    private String content;
    private String polishedContent;
    private LocalDateTime createdAt;
    private String visibility;
    private String status;
    private String polishError;
    // Getters and setters
}
