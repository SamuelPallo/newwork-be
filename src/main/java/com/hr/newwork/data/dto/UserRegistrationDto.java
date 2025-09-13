package com.hr.newwork.data.dto;

import com.hr.newwork.util.enums.Role;
import lombok.Data;

@Data
public class UserRegistrationDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
}

