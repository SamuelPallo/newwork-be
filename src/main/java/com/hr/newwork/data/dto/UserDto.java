package com.hr.newwork.data.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@SuperBuilder
@Getter
@Setter
public class UserDto {
    private String id; // Change UUID to String for id
    private String email;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String department;
    private String managerId;
    private boolean isActive;
    private LocalDate hireDate;
    private String role;
    private String managerName; // Add manager full name
    // Getters and setters
}
