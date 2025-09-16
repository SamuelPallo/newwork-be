package com.hr.newwork.util.mappers;

import com.hr.newwork.data.dto.AbsenceRequestDto;
import com.hr.newwork.data.entity.AbsenceRequest;
import com.hr.newwork.data.entity.User;
import com.hr.newwork.util.enums.AbsenceStatus;
import com.hr.newwork.util.enums.AbsenceType;

public class AbsenceRequestMapper {
    public static AbsenceRequestDto toDto(AbsenceRequest ar) {
        if (ar == null) return null;
        return AbsenceRequestDto.builder()
            .id(ar.getId())
            .userId(ar.getUser() != null ? ar.getUser().getId() : null)
            .startDate(ar.getStartDate())
            .endDate(ar.getEndDate())
            .type(ar.getType() != null ? ar.getType().name() : null) // AbsenceType as String
            .status(ar.getStatus() != null ? ar.getStatus().name() : null)
            .reason(ar.getReason())
            .createdAt(ar.getCreatedAt())
            .updatedAt(ar.getUpdatedAt())
            .build();
    }

    public static AbsenceRequest toEntity(AbsenceRequestDto dto, User user) {
        if (dto == null) return null;
        AbsenceType typeEnum = null;
        if (dto.getType() != null) {
            try {
                typeEnum = AbsenceType.fromString(dto.getType());
            } catch (IllegalArgumentException e) {
                typeEnum = AbsenceType.PERSONAL; // default or handle as needed
            }
        }
        AbsenceRequest entity = new AbsenceRequest();
        entity.setUser(user);
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setType(typeEnum);
        entity.setStatus(dto.getStatus() != null ? AbsenceStatus.valueOf(dto.getStatus()) : AbsenceStatus.PENDING);
        entity.setReason(dto.getReason());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
}
