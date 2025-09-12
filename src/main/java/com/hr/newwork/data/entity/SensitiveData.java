package com.hr.newwork.data.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class SensitiveData {
    private String phone;
    private String address;
    private Double salary;
    // Getters and setters
}

