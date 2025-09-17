package com.hr.newwork.controllers;

import com.hr.newwork.data.dto.UserDto;
import com.hr.newwork.data.dto.UserRegistrationDto;
import com.hr.newwork.data.dto.UserWithSensitiveDataDto;
import com.hr.newwork.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get user profile", description = "Returns the user profile. Sensitive fields are included only for self, manager, or admin.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User profile returned"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserWithSensitiveDataDto> getUserProfile(@PathVariable String id) {
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
    @PatchMapping("/{id}")
    public ResponseEntity<UserWithSensitiveDataDto> updateUserProfile(@PathVariable String id, @RequestBody UserWithSensitiveDataDto updateRequest) {
        return ResponseEntity.ok(userService.updateUserProfile(id, updateRequest));
    }

    @Operation(summary = "List users", description = "Lists users. Supports filtering by department, managerId, and role. Coworkers see only non-sensitive fields.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User list returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String managerId,
            @RequestParam(required = false) String managerEmail,
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(userService.listUsers(department, managerId, managerEmail, role));
    }

    @Operation(summary = "Get current user profile", description = "Returns the profile of the currently authenticated user, including sensitive fields.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Current user profile returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me")
    public ResponseEntity<UserWithSensitiveDataDto> getCurrentUserProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @Operation(summary = "Register user", description = "Registers a new user.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "409", description = "Conflict - user already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        UserDto userDto = userService.registerUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @Operation(summary = "Get user profile by email", description = "Returns the user profile by email. Sensitive fields are included only for self, manager, or admin.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User profile returned"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserDto> getUserProfileByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserProfileByEmail(email));
    }

    @Operation(summary = "Delete user", description = "Deletes a user. Admins can delete any user except themselves. Managers can delete users managed by them.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get manager's team", description = "Returns a list of users managed by the given manager (no sensitive data).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Team list returned"),
        @ApiResponse(responseCode = "404", description = "Manager not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/team/{userId}")
    public ResponseEntity<List<UserDto>> getTeam(@PathVariable String userId, @RequestParam String scope) {
        return ResponseEntity.ok(userService.getTeam(userId, scope));
    }
}

