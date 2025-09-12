package com.hr.newwork.util.mappers;

import com.hr.newwork.data.entity.AbsenceRequest;
import com.hr.newwork.data.dto.AbsenceRequestDto;
import com.hr.newwork.data.entity.User;

public class AbsenceRequestMapper {
    public static AbsenceRequestDto toDto(AbsenceRequest ar) {
        if (ar == null) return null;
        return AbsenceRequestDto.builder()
            .id(ar.getId())
            .userId(ar.getUser() != null ? ar.getUser().getId() : null)
            .startDate(ar.getStartDate())
            .endDate(ar.getEndDate())
            .type(ar.getType() != null ? ar.getType().name() : null)
            .status(ar.getStatus() != null ? ar.getStatus().name() : null)
            .reason(ar.getReason())
            .createdAt(ar.getCreatedAt())
            .updatedAt(ar.getUpdatedAt())
            .build();
    }

    public static AbsenceRequest toEntity(AbsenceRequestDto dto, User user) {
        if (dto == null) return null;
        return AbsenceRequest.builder()
            .user(user)
            .startDate(dto.getStartDate())
            .endDate(dto.getEndDate())
            .type(dto.getType() != null ? com.hr.newwork.util.enums.AbsenceType.valueOf(dto.getType()) : null)
            .status(dto.getStatus() != null ? com.hr.newwork.util.enums.AbsenceStatus.valueOf(dto.getStatus()) : com.hr.newwork.util.enums.AbsenceStatus.PENDING)
            .reason(dto.getReason())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .build();
    }
}
