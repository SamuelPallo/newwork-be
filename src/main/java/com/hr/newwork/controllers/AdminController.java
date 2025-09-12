package com.hr.newwork.controllers;

import com.hr.newwork.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Operation(summary = "List audit logs", description = "List all audit logs. Admin only.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Audit logs returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    // GET /admin/audit
    @GetMapping("/audit")
    public void listAuditLogs() {
        adminService.listAuditLogs(); // Replace with actual call
    }
}
