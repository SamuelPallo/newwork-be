package com.hr.newwork.data.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserRegistrationDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Set<String> roles;
    private String jobTitle;
    private String department;
    private LocalDate hireDate;
    private String phone;
    private String address;
    private Double salary;
    private String managerId;
}
