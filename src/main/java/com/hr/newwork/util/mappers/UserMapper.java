package com.hr.newwork.util.mappers;

import com.hr.newwork.data.entity.User;
import com.hr.newwork.data.entity.SensitiveData;
import com.hr.newwork.data.dto.UserDto;
import com.hr.newwork.data.dto.UserWithSensitiveDataDto;
import com.hr.newwork.data.dto.SensitiveDataDto;

/**
 * Mapper utility for converting between User entity and DTOs.
 */
public class UserMapper {
    /**
     * Maps a User entity to a UserDto (without sensitive data).
     * @param user the User entity
     * @return the UserDto
     */
    public static UserDto toDto(User user) {
        if (user == null) return null;
        String managerName = null;
        if (user.getManager() != null) {
            managerName = user.getManager().getFirstName() + " " + user.getManager().getLastName();
        }
        return UserDto.builder()
            .id(user.getId() != null ? user.getId().toString() : null)
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .jobTitle(user.getJobTitle())
            .department(user.getDepartment())
            .managerId(user.getManager() != null ? user.getManager().getId().toString() : null)
            .isActive(user.isActive())
            .hireDate(user.getHireDate())
            .role(user.getRole() != null ? user.getRole().name() : null)
            .managerName(managerName)
            .build();
    }

    /**
     * Maps a User entity to a UserWithSensitiveDataDto (includes sensitive data).
     * @param user the User entity
     * @return the UserWithSensitiveDataDto
     */
    public static UserWithSensitiveDataDto toDtoWithSensitive(User user) {
        if (user == null) return null;
        String managerName = null;
        if (user.getManager() != null) {
            managerName = user.getManager().getFirstName() + " " + user.getManager().getLastName();
        }
        return UserWithSensitiveDataDto.builder()
            .id(user.getId() != null ? user.getId().toString() : null)
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .jobTitle(user.getJobTitle())
            .department(user.getDepartment())
            .managerId(user.getManager() != null ? user.getManager().getId().toString() : null)
            .isActive(user.isActive())
            .hireDate(user.getHireDate())
            .role(user.getRole() != null ? user.getRole().name() : null)
            .managerName(managerName)
            .sensitiveData(toSensitiveDataDto(user.getSensitiveData()))
            .build();
    }

    /**
     * Maps a SensitiveData entity to a SensitiveDataDto.
     * @param data the SensitiveData entity
     * @return the SensitiveDataDto
     */
    public static SensitiveDataDto toSensitiveDataDto(SensitiveData data) {
        if (data == null) return null;
        return SensitiveDataDto.builder()
            .phone(data.getPhone())
            .address(data.getAddress())
            .salary(data.getSalary())
            .build();
    }

    /**
     * Updates an existing User entity from a UserDto (for profile updates).
     * Does not update email or managerId for safety.
     * @param dto the UserDto
     * @param user the existing User entity
     * @return the updated User entity
     */
    public static User fromDto(UserDto dto, User user) {
        if (dto == null || user == null) return user;
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setJobTitle(dto.getJobTitle());
        user.setDepartment(dto.getDepartment());
        user.setActive(dto.isActive());
        user.setHireDate(dto.getHireDate());
        if (dto.getRole() != null) {
            try {
                user.setRole(com.hr.newwork.util.enums.Role.valueOf(dto.getRole()));
            } catch (Exception ignored) {}
        }
        // managerId and email are not updated here for safety
        return user;
    }

    /**
     * Creates a new User entity from a UserRegistrationDto (for registration).
     * @param dto the UserRegistrationDto
     * @return the new User entity
     */
    public static User fromRegistrationDto(com.hr.newwork.data.dto.UserRegistrationDto dto) {
        if (dto == null) return null;
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setRole(dto.getRole() != null ? dto.getRole() : com.hr.newwork.util.enums.Role.USER);
        user.setActive(true);
        return user;
    }
}
