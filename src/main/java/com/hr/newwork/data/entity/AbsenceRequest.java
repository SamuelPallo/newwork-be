package com.hr.newwork.data.entity;

import com.hr.newwork.util.enums.AbsenceStatus;
import com.hr.newwork.util.enums.AbsenceType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@Table(name = "absence_requests")
public class AbsenceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private AbsenceType type;

    @Enumerated(EnumType.STRING)
    private AbsenceStatus status;

    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
