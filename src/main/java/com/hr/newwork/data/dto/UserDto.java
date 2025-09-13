package com.hr.newwork.data.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@SuperBuilder
@Getter
@Setter
public class UserDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String department;
    private UUID managerId;
    private boolean isActive;
    private LocalDate hireDate;
    private String role;
    // Getters and setters
}
