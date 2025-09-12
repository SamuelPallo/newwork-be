package com.hr.newwork.util.mappers;

import com.hr.newwork.data.entity.AuditLog;
import com.hr.newwork.data.dto.AuditLogDto;

public class AuditLogMapper {
    public static AuditLogDto toDto(AuditLog log) {
        if (log == null) return null;
        return AuditLogDto.builder()
            .id(log.getId())
            .actorId(log.getActor() != null ? log.getActor().getId() : null)
            .action(log.getAction())
            .targetTable(log.getTargetTable())
            .targetId(log.getTargetId())
            .timestamp(log.getTimestamp())
            .details(log.getDetails())
            .build();
    }
}
