package com.hr.newwork.data.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for creating feedback, including the content and model for feedback polishing.
 */
@Getter
@Setter
public class FeedbackRequestDto {
    private String content;
    private String model; // e.g. "hf-model"
}
