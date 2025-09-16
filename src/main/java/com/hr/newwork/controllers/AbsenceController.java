package com.hr.newwork.controllers;

import com.hr.newwork.data.dto.AbsenceRequestDto;
import com.hr.newwork.services.AbsenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/absences")
@RequiredArgsConstructor
public class AbsenceController {
    private final AbsenceService absenceService;

    @Operation(summary = "Submit absence request", description = "Submit a new absence request.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Absence request submitted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You are not allowed to submit this request."),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<AbsenceRequestDto> submitAbsence(@RequestBody AbsenceRequestDto absenceRequest) {
        return ResponseEntity.ok(absenceService.submitAbsence(absenceRequest));
    }

    @Operation(summary = "List personal absences", description = "List absences for the specified user. Only the user or an admin can access this endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Absence list returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You are not allowed to view these absences."),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<AbsenceRequestDto>> listAbsences(@PathVariable String userId) {
        return ResponseEntity.ok(absenceService.listAbsences(userId));
    }

    @Operation(summary = "Approve absence", description = "Approve an absence request. Manager only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Absence approved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Only a manager or admin can approve."),
            @ApiResponse(responseCode = "404", description = "Absence not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}/approve")
    public ResponseEntity<AbsenceRequestDto> approveAbsence(@PathVariable UUID id) {
        return ResponseEntity.ok(absenceService.approveAbsence(id));
    }

    @Operation(summary = "Reject absence", description = "Reject an absence request. Manager only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Absence rejected"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Only a manager or admin can reject."),
            @ApiResponse(responseCode = "404", description = "Absence not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}/reject")
    public ResponseEntity<AbsenceRequestDto> rejectAbsence(@PathVariable UUID id) {
        return ResponseEntity.ok(absenceService.rejectAbsence(id));
    }

    @Operation(summary = "List absences for manager's reports", description = "List all absences for users managed by the current authenticated manager filtered by status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Absences returned"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Only a manager can view absences for their reports."),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/reports")
    public ResponseEntity<List<AbsenceRequestDto>> listAbsencesForReports(
            @RequestParam(value = "status", required = false, defaultValue = "PENDING") String statusStr) {
        try {
            com.hr.newwork.util.enums.AbsenceStatus status = com.hr.newwork.util.enums.AbsenceStatus.valueOf(statusStr.toUpperCase());
            return ResponseEntity.ok(absenceService.listAbsencesForCurrentManagerByStatus(status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
