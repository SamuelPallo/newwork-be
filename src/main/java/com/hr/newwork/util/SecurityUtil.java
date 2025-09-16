package com.hr.newwork.util;

import com.hr.newwork.data.entity.User;
import com.hr.newwork.exceptions.NotFoundException;
import com.hr.newwork.repositories.UserRepository;
import com.hr.newwork.util.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public boolean isCurrentUserManager() {
        User user = getCurrentUser();
        return user.getRoles() != null && user.getRoles().stream().anyMatch(role -> "MANAGER".equals(role.getName()));
    }

    public boolean isCurrentUserAdmin() {
        User user = getCurrentUser();
        return user.getRoles() != null && user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));
    }

    public boolean isCurrentUserManagerOf(User user) {
        User current = getCurrentUser();
        return user.getManager() != null && user.getManager().getId().equals(current.getId());
    }

    public boolean isCurrentUser(User user) {
        return getCurrentUser().getId().equals(user.getId());
    }

    public Role getHighestRole() {
        User user = getCurrentUser();
        if (user.getRoles() != null) {
            if (user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()))) {
                return Role.ADMIN;
            }
            if (user.getRoles().stream().anyMatch(role -> "MANAGER".equals(role.getName()))) {
                return Role.MANAGER;
            }
        }
        return Role.EMPLOYEE;
    }
}
