package com.hr.newwork.services;

import com.hr.newwork.data.dto.UserDto;
import com.hr.newwork.data.dto.UserRegistrationDto;
import com.hr.newwork.data.dto.UserWithSensitiveDataDto;
import com.hr.newwork.data.entity.User;
import com.hr.newwork.exceptions.BadRequestException;
import com.hr.newwork.exceptions.ForbiddenException;
import com.hr.newwork.exceptions.NotFoundException;
import com.hr.newwork.repositories.UserRepository;
import com.hr.newwork.util.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for user-related operations: registration, profile management, and listing.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.hr.newwork.util.SecurityUtil securityUtil;

    /**
     * Retrieves a user profile by ID. Sensitive fields are included only for self, manager, or admin.
     * @param id the user ID
     * @return the user profile DTO
     */
    public UserDto getUserProfile(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        if (securityUtil.isCurrentUser(user) || securityUtil.isCurrentUserManagerOf(user) || securityUtil.isCurrentUserAdmin()) {
            return UserMapper.toDtoWithSensitive(user);
        }
        return UserMapper.toDto(user);
    }

    /**
     * Updates a user profile. Allowed for self, manager, or admin.
     * @param id the user ID
     * @param updateRequest the update request DTO
     * @return the updated user profile DTO
     */
    @Transactional
    public UserWithSensitiveDataDto updateUserProfile(UUID id, UserWithSensitiveDataDto updateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        if (!(securityUtil.isCurrentUser(user) || securityUtil.isCurrentUserManagerOf(user) || securityUtil.isCurrentUserAdmin())) {
            throw new ForbiddenException("You do not have permission to update this profile");
        }
        user = UserMapper.fromDto(updateRequest, user);
        userRepository.save(user);
        return UserMapper.toDtoWithSensitive(user);
    }

    /**
     * Lists users, optionally filtered by department, managerId, or managerEmail.
     * @param department the department to filter by (optional)
     * @param managerId the manager ID to filter by (optional, as String)
     * @param managerEmail the manager email to filter by (optional, as String)
     * @return list of user DTOs
     */
    public List<UserDto> listUsers(String department, String managerId, String managerEmail) {
        List<User> users;
        UUID managerUuid = null;
        if (managerId != null && !managerId.isBlank()) {
            try {
                managerUuid = UUID.fromString(managerId);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid managerId format: must be a UUID");
            }
        }
        if (managerUuid != null) {
            if (department != null) {
                users = userRepository.findByDepartmentAndManager_Id(department, managerUuid);
            } else {
                users = userRepository.findByManager_Id(managerUuid);
            }
        } else if (managerEmail != null && !managerEmail.isBlank()) {
            User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new NotFoundException("Manager not found for email: " + managerEmail));
            if (department != null) {
                users = userRepository.findByDepartmentAndManager_Id(department, manager.getId());
            } else {
                users = userRepository.findByManager_Id(manager.getId());
            }
        } else if (department != null) {
            users = userRepository.findByDepartment(department);
        } else {
            users = userRepository.findAll();
        }
        return users.stream()
            .map(UserMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves the profile of the currently authenticated user, including sensitive fields.
     * @return the current user profile DTO
     */
    public UserWithSensitiveDataDto getCurrentUserProfile() {
        User user = securityUtil.getCurrentUser();
        return UserMapper.toDtoWithSensitive(user);
    }

    /**
     * Registers a new user. Checks for unique email, hashes password, assigns role.
     * @param registrationDto the registration request DTO
     * @return the created user DTO
     */
    @Transactional
    public UserDto registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new ForbiddenException("Email already in use");
        }
        User user = UserMapper.fromRegistrationDto(registrationDto);
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        userRepository.save(user);
        return UserMapper.toDto(user);
    }

    /**
     * Retrieves a user profile by email. Sensitive fields are included only for self, manager, or admin.
     * @param email the user email
     * @return the user profile DTO
     */
    public UserDto getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        if (securityUtil.isCurrentUser(user) || securityUtil.isCurrentUserManagerOf(user) || securityUtil.isCurrentUserAdmin()) {
            return UserMapper.toDtoWithSensitive(user);
        }
        return UserMapper.toDto(user);
    }

    /**
     * Deletes a user. Admins can delete any user except themselves. Managers can delete users managed by them.
     * No user can delete themselves.
     * Accepts a String id, parses to UUID, and handles errors.
     * @param id the user ID to delete (as String)
     */
    @Transactional
    public void deleteUser(String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Invalid user id format");
        }
        User targetUser = userRepository.findById(uuid)
            .orElseThrow(() -> new NotFoundException("User not found"));
        if (securityUtil.isCurrentUser(targetUser)) {
            throw new ForbiddenException("No user can delete themselves.");
        }
        if (securityUtil.isCurrentUserManagerOf(targetUser)) {
            userRepository.deleteById(uuid);
            return;
        }
        if (securityUtil.isCurrentUserAdmin()) {
            userRepository.deleteById(uuid);
            return;
        }
        throw new ForbiddenException("You do not have permission to delete this user.");
    }
}
