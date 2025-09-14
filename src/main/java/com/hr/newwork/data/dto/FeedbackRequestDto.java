package com.hr.newwork.data.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for creating feedback, including the content and whether to polish with AI.
 */
@Getter
@Setter
public class FeedbackRequestDto {
    private String content;
    private boolean polish;
}

