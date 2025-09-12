package com.hr.newwork.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
public class AuditLogDto {
    private UUID id;
    private UUID actorId;
    private String action;
    private String targetTable;
    private UUID targetId;
    private LocalDateTime timestamp;
    private String details; // JSON string
    // Getters and setters
}

