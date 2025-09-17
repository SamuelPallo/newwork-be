package com.hr.newwork.util.mappers;

import com.hr.newwork.data.dto.SensitiveDataDto;
import com.hr.newwork.data.dto.UserDto;
import com.hr.newwork.data.dto.UserRegistrationDto;
import com.hr.newwork.data.dto.UserWithSensitiveDataDto;
import com.hr.newwork.data.entity.Role;
import com.hr.newwork.data.entity.SensitiveData;
import com.hr.newwork.data.entity.User;
import com.hr.newwork.exceptions.BadRequestException;
import com.hr.newwork.repositories.RoleRepository;
import com.hr.newwork.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
        Set<String> roles = user.getRoles() != null ? user.getRoles().stream().map(com.hr.newwork.data.entity.Role::getName).collect(Collectors.toSet()) : null;
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
        Set<String> roles = user.getRoles() != null ? user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()) : null;
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
    public static User fromDto(UserDto dto, User user, RoleRepository roleRepository) {
        if (dto == null || user == null) return user;
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setJobTitle(dto.getJobTitle());
        user.setDepartment(dto.getDepartment());
        user.setActive(dto.isActive());
        user.setHireDate(dto.getHireDate());
        if (dto.getRoles() != null) {
            Set<com.hr.newwork.data.entity.Role> roles = dto.getRoles().stream()
                .map(roleRepository::findByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        // managerId and email are not updated here for safety
        return user;
    }

    /**
     * Updates an existing User entity from a UserWithSensitiveDataDto (for profile updates with sensitive data).
     * Does not update email or managerId for safety.
     * @param dto the UserWithSensitiveDataDto
     * @param user the existing User entity
     * @return the updated User entity
     */
    public static User fromDto(UserWithSensitiveDataDto dto, User user, RoleRepository roleRepository, UserRepository userRepository, java.util.function.Supplier<Boolean> isManagerOrAdminSupplier) {
        if (dto == null || user == null) return user;
        Optional.ofNullable(dto.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(dto.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(dto.getJobTitle()).ifPresent(user::setJobTitle);
        Optional.ofNullable(dto.getDepartment()).ifPresent(user::setDepartment);
        Optional.ofNullable(dto.getEmail()).ifPresent(user::setEmail);
        Optional.of(dto.isActive()).ifPresent(user::setActive);
        Optional.ofNullable(dto.getHireDate()).ifPresent(user::setHireDate);
        if (dto.getRoles() != null) {
            Set<Role> roles = dto.getRoles().stream()
                .map(roleRepository::findByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        // Use the new manager update logic
        handleManagerUpdate(dto, user, userRepository, isManagerOrAdminSupplier);
        // Update sensitive data if present
        if (dto.getSensitiveData() != null) {
            if (user.getSensitiveData() == null) {
                user.setSensitiveData(new SensitiveData());
            }
            SensitiveDataDto sdd = dto.getSensitiveData();
            SensitiveData sd = user.getSensitiveData();
            Optional.ofNullable(sdd.getPhone()).ifPresent(sd::setPhone);
            Optional.ofNullable(sdd.getAddress()).ifPresent(sd::setAddress);
            Optional.ofNullable(sdd.getSalary()).ifPresent(sd::setSalary);
        }
        return user;
    }

    /**
     * Handles manager assignment logic for update and registration flows.
     * Compares the managerId from the DTO with the current user's manager, and if different,
     * checks if the current user is a manager or admin, and if so, updates the manager.
     * Throws BadRequestException for invalid managerId or role.
     */
    public static void handleManagerUpdate(UserWithSensitiveDataDto dto, User user, UserRepository userRepository, java.util.function.Supplier<Boolean> isManagerOrAdminSupplier) {
        if (dto.getManagerId() == null) return;
        String newManagerId = dto.getManagerId();
        String currentManagerId = user.getManager() != null ? user.getManager().getId().toString() : null;
        if (newManagerId.isBlank() && currentManagerId == null || newManagerId.equals(currentManagerId)) {
            return; // No change
        }
        if (!isManagerOrAdminSupplier.get()) {
            throw new BadRequestException("Only a manager or admin can change the manager assignment");
        }
        if (newManagerId.isBlank()) {
            user.setManager(null);
            return;
        }
        try {
            UUID managerUuid = UUID.fromString(newManagerId);
            userRepository.findById(managerUuid).ifPresentOrElse(managerUser -> {
                boolean isManager = managerUser.getRoles() != null && managerUser.getRoles().stream().anyMatch(r -> "MANAGER".equalsIgnoreCase(r.getName()));
                if (isManager) {
                    user.setManager(managerUser);
                } else {
                    throw new BadRequestException("The specified manager does not have MANAGER role");
                }
            }, () -> {
                throw new BadRequestException("The specified manager does not exist");
            });
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid managerId format: must be a UUID");
        }
    }

    /**
     * Maps a UserRegistrationDto to a new User entity, including sensitive data, password encoding, and manager assignment.
     * @param dto the registration DTO
     * @param passwordEncoder the password encoder to hash the password
     * @param roleRepository the role repository to resolve roles
     * @param userRepository the user repository to resolve manager
     * @return a new User entity
     */
    public static User fromRegistrationDto(UserRegistrationDto dto, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository) {
        if (dto == null) return null;
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setJobTitle(dto.getJobTitle());
        user.setDepartment(dto.getDepartment());
        user.setHireDate(dto.getHireDate());
        user.setActive(true);
        // Encode and set password
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        // Set roles
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            user.setRoles(dto.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet()));
        } else {
            // Default to EMPLOYEE role if none provided
            roleRepository.findByName("EMPLOYEE").ifPresent(role -> user.setRoles(java.util.Set.of(role)));
        }
        // Set sensitive data
        SensitiveData sensitiveData = new SensitiveData();
        sensitiveData.setPhone(dto.getPhone());
        sensitiveData.setAddress(dto.getAddress());
        if (dto.getSalary() != null) sensitiveData.setSalary(dto.getSalary());
        user.setSensitiveData(sensitiveData);
        // Set manager if managerId is provided
        if (dto.getManagerId() != null && !dto.getManagerId().isBlank()) {
            try {
                UUID managerUuid = UUID.fromString(dto.getManagerId());
                userRepository.findById(managerUuid).ifPresentOrElse(managerUser -> {
                    boolean isManager = managerUser.getRoles() != null && managerUser.getRoles().stream().anyMatch(r -> "MANAGER".equalsIgnoreCase(r.getName()));
                    if (isManager) {
                        user.setManager(managerUser);
                    } else {
                        throw new BadRequestException("The specified manager does not have MANAGER role");
                    }
                }, () -> {
                    throw new BadRequestException("The specified manager does not exist");
                });
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid managerId format: must be a UUID");
            }
        }
        return user;
    }
}
