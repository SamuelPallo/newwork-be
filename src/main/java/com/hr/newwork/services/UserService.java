package com.hr.newwork.services;

import com.hr.newwork.data.dto.UserDto;
import com.hr.newwork.data.entity.User;
import com.hr.newwork.repositories.UserRepository;
import com.hr.newwork.util.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    // User/profile flows
    public UserDto getUserProfile(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (isCurrentUser(user) || isCurrentUserManagerOf(user) || isCurrentUserAdmin()) {
            return userMapper.toDtoWithSensitive(user);
        }
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUserProfile(UUID id, UserDto updateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (!(isCurrentUser(user) || isCurrentUserManagerOf(user) || isCurrentUserAdmin())) {
            throw new RuntimeException("Forbidden");
        }
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setJobTitle(updateRequest.getJobTitle());
        user.setDepartment(updateRequest.getDepartment());
        user.setActive(updateRequest.isActive());
        user.setHireDate(updateRequest.getHireDate());
        userRepository.save(user);
        return userMapper.toDtoWithSensitive(user);
    }

    public List<UserDto> listUsers(String department, UUID managerId) {
        List<User> users = userRepository.findAll();
        return users.stream()
            .filter(u -> department == null || department.equals(u.getDepartment()))
            .filter(u -> managerId == null || (u.getManager() != null && managerId.equals(u.getManager().getId())))
            .map(userMapper::toDto)
            .collect(Collectors.toList());
    }

    public UserDto getCurrentUserProfile() {
        User user = getCurrentUser();
        return userMapper.toDtoWithSensitive(user);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
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
