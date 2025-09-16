package com.hr.newwork.util.mappers;

import com.hr.newwork.data.entity.User;
import com.hr.newwork.data.entity.SensitiveData;
import com.hr.newwork.data.dto.UserDto;
import com.hr.newwork.data.dto.UserWithSensitiveDataDto;
import com.hr.newwork.data.dto.SensitiveDataDto;
import com.hr.newwork.util.enums.Role;

import java.util.Set;
import java.util.stream.Collectors;

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
        Set<String> roles = user.getRoles() != null ? user.getRoles().stream().map(Role::name).collect(Collectors.toSet()) : null;
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
            .roles(roles)
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
        Set<String> roles = user.getRoles() != null ? user.getRoles().stream().map(Role::name).collect(Collectors.toSet()) : null;
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
            .roles(roles)
            .managerName(managerName)
            .sensitiveData(user.getSensitiveData() != null ? UserMapper.toSensitiveDataDto(user.getSensitiveData()) : null)
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
        if (dto.getRoles() != null) {
            Set<Role> roles = dto.getRoles().stream()
                .map(roleStr -> {
                    try {
                        return Role.valueOf(roleStr);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(r -> r != null)
                .collect(Collectors.toSet());
            user.setRoles(roles);
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
        Set<Role> roles = new java.util.HashSet<>();
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            roles.addAll(dto.getRoles());
        } else {
            roles.add(Role.EMPLOYEE);
        }
        user.setRoles(roles);
        user.setActive(true);
        return user;
    }

    /**
     * Updates an existing User entity from a UserWithSensitiveDataDto (for profile updates with sensitive data).
     * Does not update email or managerId for safety.
     * @param dto the UserWithSensitiveDataDto
     * @param user the existing User entity
     * @return the updated User entity
     */
    public static User fromDto(UserWithSensitiveDataDto dto, User user) {
        if (dto == null || user == null) return user;
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setJobTitle(dto.getJobTitle());
        user.setDepartment(dto.getDepartment());
        user.setActive(dto.isActive());
        user.setHireDate(dto.getHireDate());
        if (dto.getRoles() != null) {
            Set<Role> roles = dto.getRoles().stream()
                .map(roleStr -> {
                    try {
                        return Role.valueOf(roleStr);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(r -> r != null)
                .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        // managerId and email are not updated here for safety
        // Update sensitive data if present
        if (dto.getSensitiveData() != null) {
            if (user.getSensitiveData() == null) {
                user.setSensitiveData(new SensitiveData());
            }
            SensitiveDataDto sdd = dto.getSensitiveData();
            SensitiveData sd = user.getSensitiveData();
            sd.setPhone(sdd.getPhone());
            sd.setAddress(sdd.getAddress());
            sd.setSalary(sdd.getSalary());
        }
        return user;
    }
}
