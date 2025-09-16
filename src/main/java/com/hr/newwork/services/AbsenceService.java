package com.hr.newwork.services;

import com.hr.newwork.data.dto.AbsenceRequestDto;
import com.hr.newwork.data.entity.AbsenceRequest;
import com.hr.newwork.data.entity.User;
import com.hr.newwork.exceptions.ForbiddenException;
import com.hr.newwork.exceptions.NotFoundException;
import com.hr.newwork.repositories.AbsenceRequestRepository;
import com.hr.newwork.repositories.UserRepository;
import com.hr.newwork.util.enums.AbsenceStatus;
import com.hr.newwork.util.mappers.AbsenceRequestMapper;
import lombok.RequiredArgsConstructor;
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
    private final com.hr.newwork.util.SecurityUtil securityUtil;

    /**
     * Submits a new absence request for the current user.
     * @param dto the absence request DTO
     * @return the created AbsenceRequestDto
     */
    @Transactional
    public AbsenceRequestDto submitAbsence(AbsenceRequestDto dto) {
        User user = securityUtil.getCurrentUser();
        AbsenceRequest entity = AbsenceRequestMapper.toEntity(dto, user);
        entity.setStatus(AbsenceStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        AbsenceRequest saved = absenceRequestRepository.save(entity);
        return AbsenceRequestMapper.toDto(saved);
    }

    /**
     * Lists absence requests for the current user only (personal absences).
     * @return list of AbsenceRequestDto
     */
    public List<AbsenceRequestDto> listAbsences() {
        User current = securityUtil.getCurrentUser();
        List<AbsenceRequest> absences = absenceRequestRepository.findByUserId(current.getId());
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
        boolean isManager = securityUtil.isCurrentUserManagerOf(ar.getUser());
        boolean isAdmin = securityUtil.isCurrentUserAdmin();
        if (!(isManager || isAdmin)) {
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
        boolean isManager = securityUtil.isCurrentUserManagerOf(ar.getUser());
        boolean isAdmin = securityUtil.isCurrentUserAdmin();
        if (!(isManager || isAdmin)) {
            throw new ForbiddenException("Forbidden");
        }
        ar.setStatus(AbsenceStatus.REJECTED);
        ar.setUpdatedAt(LocalDateTime.now());
        AbsenceRequest saved = absenceRequestRepository.save(ar);
        return AbsenceRequestMapper.toDto(saved);
    }

    /**
     * Lists absence requests for a user by UUID or email.
     * Accepts either a UUID string or an email as input.
     * @param userIdOrEmail the user's UUID or email
     * @return list of AbsenceRequestDto
     */
    public List<AbsenceRequestDto> listAbsences(String userIdOrEmail) {
        User user = null;
        try {
            UUID userId = UUID.fromString(userIdOrEmail);
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found for id: " + userIdOrEmail));
        } catch (IllegalArgumentException e) {
            // Not a UUID, try as email
            user = userRepository.findByEmail(userIdOrEmail)
                    .orElseThrow(() -> new NotFoundException("User not found for email: " + userIdOrEmail));
        }
        List<AbsenceRequest> absences = absenceRequestRepository.findByUserId(user.getId());
        return absences.stream().map(AbsenceRequestMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Lists all absences for users managed by the current authenticated manager filtered by status.
     * Only accessible by managers.
     * @param status the absence status to filter by
     * @return list of AbsenceRequestDto
     */
    public List<AbsenceRequestDto> listAbsencesForCurrentManagerByStatus(AbsenceStatus status) {
        User current = securityUtil.getCurrentUser();
        if (!securityUtil.isCurrentUserManager()) {
            throw new ForbiddenException("Only managers can view absences for their reports.");
        }
        List<AbsenceRequest> absences = absenceRequestRepository.findByUser_Manager_IdAndStatus(current.getId(), status);
        return absences.stream().map(AbsenceRequestMapper::toDto).collect(Collectors.toList());
    }
}
