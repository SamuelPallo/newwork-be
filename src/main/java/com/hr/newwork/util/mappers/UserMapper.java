package com.hr.newwork.util.mappers;

import com.hr.newwork.data.entity.User;
import com.hr.newwork.data.entity.SensitiveData;
import com.hr.newwork.data.dto.UserDto;
import com.hr.newwork.data.dto.UserWithSensitiveDataDto;
import com.hr.newwork.data.dto.SensitiveDataDto;

public class UserMapper {
    public static UserDto toDto(User user) {
        if (user == null) return null;
        return UserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .jobTitle(user.getJobTitle())
            .department(user.getDepartment())
            .managerId(user.getManager() != null ? user.getManager().getId() : null)
            .isActive(user.isActive())
            .hireDate(user.getHireDate())
            .role(user.getRole() != null ? user.getRole().name() : null)
            .build();
    }
    public static UserWithSensitiveDataDto toDtoWithSensitive(User user) {
        if (user == null) return null;
        return UserWithSensitiveDataDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .jobTitle(user.getJobTitle())
            .department(user.getDepartment())
            .managerId(user.getManager() != null ? user.getManager().getId() : null)
            .isActive(user.isActive())
            .hireDate(user.getHireDate())
            .role(user.getRole() != null ? user.getRole().name() : null)
            .sensitiveData(toSensitiveDataDto(user.getSensitiveData()))
            .build();
    }
    public static SensitiveDataDto toSensitiveDataDto(SensitiveData data) {
        if (data == null) return null;
        return SensitiveDataDto.builder()
            .phone(data.getPhone())
            .address(data.getAddress())
            .salary(data.getSalary())
            .build();
    }
}
