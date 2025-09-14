package com.hr.newwork.services;

import com.hr.newwork.data.dto.AuditLogDto;
import com.hr.newwork.repositories.AuditLogRepository;
import com.hr.newwork.util.mappers.AuditLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for handling administrative audit log operations.
 * Provides methods to retrieve and filter audit logs for admin users.
 */
@Service
@RequiredArgsConstructor
public class AdminService {
    private final AuditLogRepository auditLogRepository;

    /**
     * Retrieves all audit logs in the system, mapped to DTOs.
     * Intended for internal or legacy use; prefer paginated/filterable method for production.
     *
     * @return List of all audit logs as DTOs
     */
    public List<AuditLogDto> listAuditLogs() {
        return auditLogRepository.findAll().stream()
            .map(AuditLogMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves a paginated and filtered list of audit logs.
     *
     * @param actorId      Optional filter by actor (user) UUID
     * @param action       Optional filter by action type
     * @param targetTable  Optional filter by target table/entity
     * @param from         Optional filter for logs after this timestamp (inclusive)
     * @param to           Optional filter for logs before this timestamp (inclusive)
     * @param pageable     Pagination and sorting information
     * @return Page of audit logs as DTOs matching the filters
     */
    public Page<AuditLogDto> findAuditLogs(UUID actorId, String action, String targetTable, Instant from, Instant to, Pageable pageable) {
        return auditLogRepository.findFiltered(actorId, action, targetTable, from, to, pageable)
            .map(AuditLogMapper::toDto);
    }
}
