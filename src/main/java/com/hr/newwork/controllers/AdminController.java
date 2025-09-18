package com.hr.newwork.controllers;

import com.hr.newwork.data.dto.AuditLogDto;
import com.hr.newwork.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for administrative audit log operations.
 * Provides endpoints for listing and exporting audit logs with filtering, pagination, and sorting.
 * All endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/api/v1/admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    @Autowired
    private AdminService adminService;

    /**
     * Lists audit logs with optional filtering, pagination, and sorting.
     *
     * @param actorId     Optional filter by actor (user) UUID
     * @param action      Optional filter by action type
     * @param targetTable Optional filter by target table/entity
     * @param from        Optional filter for logs after this timestamp (inclusive, ISO 8601)
     * @param to          Optional filter for logs before this timestamp (inclusive, ISO 8601)
     * @param pageable    Pagination and sorting information (page, size, sort)
     * @return Paginated list of audit logs as DTOs
     */
    @Operation(
        summary = "List audit logs",
        description = "List audit logs with optional filtering by actor, action, target table, and date range. Supports pagination and sorting. Requires ADMIN role.",
        parameters = {
            @Parameter(name = "actorId", description = "Filter by actor (user) UUID", required = false),
            @Parameter(name = "action", description = "Filter by action type", required = false),
            @Parameter(name = "targetTable", description = "Filter by target table/entity", required = false),
            @Parameter(name = "from", description = "Filter for logs after this timestamp (inclusive, ISO 8601)", required = false),
            @Parameter(name = "to", description = "Filter for logs before this timestamp (inclusive, ISO 8601)", required = false),
            @Parameter(name = "page", description = "Page number (0-based)", required = false),
            @Parameter(name = "size", description = "Page size", required = false),
            @Parameter(name = "sort", description = "Sort order, e.g. 'timestamp,desc'", required = false)
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated audit logs returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden (not admin)"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    // GET /admin/audit
    @GetMapping("/audit")
    public Page<AuditLogDto> findAuditLogs(
            @RequestParam(required = false) UUID actorId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String targetTable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @PageableDefault(size = 20, sort = "timestamp,desc") Pageable pageable
    ) {
        return adminService.findAuditLogs(actorId, action, targetTable, from, to, pageable);
    }

    /**
     * Exports filtered audit logs as a CSV file.
     *
     * @param actorId     Optional filter by actor (user) UUID
     * @param action      Optional filter by action type
     * @param targetTable Optional filter by target table/entity
     * @param from        Optional filter for logs after this timestamp (inclusive, ISO 8601)
     * @param to          Optional filter for logs before this timestamp (inclusive, ISO 8601)
     * @return CSV file containing filtered audit logs
     */
    @Operation(
        summary = "Export audit logs as CSV",
        description = "Export filtered audit logs as a CSV file. Requires ADMIN role.",
        parameters = {
            @Parameter(name = "actorId", description = "Filter by actor (user) UUID", required = false),
            @Parameter(name = "action", description = "Filter by action type", required = false),
            @Parameter(name = "targetTable", description = "Filter by target table/entity", required = false),
            @Parameter(name = "from", description = "Filter for logs after this timestamp (inclusive, ISO 8601)", required = false),
            @Parameter(name = "to", description = "Filter for logs before this timestamp (inclusive, ISO 8601)", required = false)
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CSV file returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden (not admin)"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    // GET /admin/audit/export
    @GetMapping("/audit/export")
    public ResponseEntity<byte[]> exportAuditLogsCsv(
            @RequestParam(required = false) UUID actorId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String targetTable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        var logs = adminService.findAuditLogs(actorId, action, targetTable, from, to, Pageable.unpaged()).getContent();
        String csv = "id,actorId,action,targetTable,targetId,timestamp,details\n" +
                logs.stream().map(l -> String.format("%s,%s,%s,%s,%s,%s,%s",
                        l.getId(),
                        l.getActorId(),
                        l.getAction(),
                        l.getTargetTable(),
                        l.getTargetId(),
                        l.getTimestamp(),
                        l.getDetails() != null ? l.getDetails().replaceAll("[\r\n]+", " ") : ""
                )).collect(Collectors.joining("\n"));
        byte[] csvBytes = csv.getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audits.csv");
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }
}
