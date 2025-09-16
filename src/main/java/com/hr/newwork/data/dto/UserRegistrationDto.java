package com.hr.newwork.data.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserRegistrationDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Set<String> roles;
}
