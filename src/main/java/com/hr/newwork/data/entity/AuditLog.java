package com.hr.newwork.data.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "audit_log")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    private String action;
    private String targetTable;
    private UUID targetId;
    private LocalDateTime timestamp;

    @Column(columnDefinition = "jsonb")
    private String details; // Store as JSON string
}

