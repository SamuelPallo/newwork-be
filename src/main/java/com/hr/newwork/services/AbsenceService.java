package com.hr.newwork.services;

import com.hr.newwork.data.dto.AbsenceRequestDto;
import com.hr.newwork.data.entity.AbsenceRequest;
import com.hr.newwork.data.entity.User;
import com.hr.newwork.repositories.AbsenceRequestRepository;
import com.hr.newwork.repositories.UserRepository;
import com.hr.newwork.util.enums.AbsenceStatus;
import com.hr.newwork.util.mappers.AbsenceRequestMapper;
import com.hr.newwork.exceptions.NotFoundException;
import com.hr.newwork.exceptions.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for handling absence request flows.
 */
@Service
@RequiredArgsConstructor
public class AbsenceService {
    private final AbsenceRequestRepository absenceRequestRepository;
    private final UserRepository userRepository;

    /**
     * Submits a new absence request for the current user.
     * @param dto the absence request DTO
     * @return the created AbsenceRequestDto
     */
    @Transactional
    public AbsenceRequestDto submitAbsence(AbsenceRequestDto dto) {
        User user = getCurrentUser();
        AbsenceRequest entity = AbsenceRequestMapper.toEntity(dto, user);
        entity.setStatus(AbsenceStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        AbsenceRequest saved = absenceRequestRepository.save(entity);
        return AbsenceRequestMapper.toDto(saved);
    }

    /**
     * Lists absence requests for the current user, or for manager's reports if managerId is provided and user is a manager.
     * @param managerId the manager's user ID (optional)
     * @return list of AbsenceRequestDto
     */
    public List<AbsenceRequestDto> listAbsences(UUID managerId) {
        User current = getCurrentUser();
        List<AbsenceRequest> absences;
        if (managerId != null && isCurrentUserManager() && current.getId().equals(managerId)) {
            absences = absenceRequestRepository.findByUser_Manager_Id(managerId);
        } else {
            absences = absenceRequestRepository.findByUserId(current.getId());
        }
        return absences.stream().map(AbsenceRequestMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Approves an absence request. Only allowed for manager or admin.
     * @param id the absence request ID
     * @return the updated AbsenceRequestDto
     * @throws NotFoundException if the absence request is not found
     * @throws ForbiddenException if the user is not allowed to approve
     */
    @Transactional
    public AbsenceRequestDto approveAbsence(UUID id) {
        AbsenceRequest ar = absenceRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("Absence not found"));
        if (!isCurrentUserManagerOf(ar.getUser()) && !isCurrentUserAdmin()) {
            throw new ForbiddenException("Forbidden");
        }
        ar.setStatus(AbsenceStatus.APPROVED);
        ar.setUpdatedAt(LocalDateTime.now());
        AbsenceRequest saved = absenceRequestRepository.save(ar);
        return AbsenceRequestMapper.toDto(saved);
    }

    /**
     * Rejects an absence request. Only allowed for manager or admin.
     * @param id the absence request ID
     * @return the updated AbsenceRequestDto
     * @throws NotFoundException if the absence request is not found
     * @throws ForbiddenException if the user is not allowed to reject
     */
    @Transactional
    public AbsenceRequestDto rejectAbsence(UUID id) {
        AbsenceRequest ar = absenceRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("Absence not found"));
        if (!isCurrentUserManagerOf(ar.getUser()) && !isCurrentUserAdmin()) {
            throw new ForbiddenException("Forbidden");
        }
        ar.setStatus(AbsenceStatus.REJECTED);
        ar.setUpdatedAt(LocalDateTime.now());
        AbsenceRequest saved = absenceRequestRepository.save(ar);
        return AbsenceRequestMapper.toDto(saved);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private boolean isCurrentUserManager() {
        return getCurrentUser().getRole() != null && getCurrentUser().getRole().name().equals("MANAGER");
    }

    private boolean isCurrentUserAdmin() {
        return getCurrentUser().getRole() != null && getCurrentUser().getRole().name().equals("ADMIN");
    }

    private boolean isCurrentUserManagerOf(User user) {
        User current = getCurrentUser();
        return user.getManager() != null && user.getManager().getId().equals(current.getId());
    }
}
