package com.hr.newwork.controllers;

import com.hr.newwork.data.dto.UserDto;
import com.hr.newwork.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Get user profile", description = "Returns the user profile. Sensitive fields are included only for self, manager, or admin.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User profile returned"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @Operation(summary = "Update user profile", description = "Updates the user profile. Allowed for self, manager, or admin.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User profile updated"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUserProfile(@PathVariable UUID id, @RequestBody UserDto updateRequest) {
        return ResponseEntity.ok(userService.updateUserProfile(id, updateRequest));
    }

    @Operation(summary = "List users", description = "Lists users. Supports filtering by department and managerId. Coworkers see only non-sensitive fields.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User list returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers(@RequestParam(required = false) String department, @RequestParam(required = false) UUID managerId) {
        return ResponseEntity.ok(userService.listUsers(department, managerId));
    }

    @Operation(summary = "Get current user profile", description = "Returns the profile of the currently authenticated user, including sensitive fields.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Current user profile returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUserProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }
}
