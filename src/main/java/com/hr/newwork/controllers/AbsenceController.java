package com.hr.newwork.controllers;

import com.hr.newwork.data.dto.AbsenceRequestDto;
import com.hr.newwork.services.AbsenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "List absences", description = "List absences for the current user or manager's reports.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Absence list returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You are not allowed to view these absences."),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<AbsenceRequestDto>> listAbsences(@RequestParam(required = false) UUID managerId) {
        return ResponseEntity.ok(absenceService.listAbsences(managerId));
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
}
