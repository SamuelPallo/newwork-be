package com.hr.newwork.services;

import com.hr.newwork.data.dto.UserDto;
import com.hr.newwork.data.dto.UserRegistrationDto;
import com.hr.newwork.data.entity.User;
import com.hr.newwork.exceptions.ForbiddenException;
import com.hr.newwork.exceptions.NotFoundException;
import com.hr.newwork.repositories.UserRepository;
import com.hr.newwork.util.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    /**
     * Retrieves a user profile by ID. Sensitive fields are included only for self, manager, or admin.
     * @param id the user ID
     * @return the user profile DTO
     */
    public UserDto getUserProfile(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        if (isCurrentUser(user) || isCurrentUserManagerOf(user) || isCurrentUserAdmin()) {
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
    public UserDto updateUserProfile(UUID id, UserDto updateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        if (!(isCurrentUser(user) || isCurrentUserManagerOf(user) || isCurrentUserAdmin())) {
            throw new ForbiddenException("You do not have permission to update this profile");
        }
        user = UserMapper.fromDto(updateRequest, user);
        userRepository.save(user);
        return UserMapper.toDtoWithSensitive(user);
    }

    /**
     * Lists users, optionally filtered by department and managerId.
     * @param department the department to filter by (optional)
     * @param managerId the manager ID to filter by (optional)
     * @return list of user DTOs
     */
    public List<UserDto> listUsers(String department, UUID managerId) {
        List<User> users;
        if (department != null && managerId != null) {
            users = userRepository.findByDepartmentAndManager_Id(department, managerId);
        } else if (department != null) {
            users = userRepository.findByDepartment(department);
        } else if (managerId != null) {
            users = userRepository.findByManager_Id(managerId);
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
    public UserDto getCurrentUserProfile() {
        User user = getCurrentUser();
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

    // Helper methods
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private boolean isCurrentUser(User user) {
        return getCurrentUser().getId().equals(user.getId());
    }

    private boolean isCurrentUserManagerOf(User user) {
        User current = getCurrentUser();
        return user.getManager() != null && user.getManager().getId().equals(current.getId());
    }

    private boolean isCurrentUserAdmin() {
        return getCurrentUser().getRole() != null && getCurrentUser().getRole().name().equals("ADMIN");
    }
}
