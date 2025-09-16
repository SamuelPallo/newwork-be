package com.hr.newwork.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveDataDto {
    private String phone;
    private String address;
    private Double salary;
    // Getters and setters
}
