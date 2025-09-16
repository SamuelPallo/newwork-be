package com.hr.newwork.services;

import com.hr.newwork.data.dto.FeedbackDto;
import com.hr.newwork.data.entity.Feedback;
import com.hr.newwork.data.entity.User;
import com.hr.newwork.exceptions.BadRequestException;
import com.hr.newwork.exceptions.ForbiddenException;
import com.hr.newwork.exceptions.NotFoundException;
import com.hr.newwork.repositories.FeedbackRepository;
import com.hr.newwork.repositories.UserRepository;
import com.hr.newwork.services.polish.FeedbackPolisher;
import com.hr.newwork.util.enums.FeedbackPolishStatus;
import com.hr.newwork.util.enums.Visibility;
import com.hr.newwork.util.mappers.FeedbackMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final FeedbackPolisher feedbackPolisher;

    @Transactional
    public FeedbackDto createFeedback(String ignoredUserId, String content, String model) {
        User author = getCurrentUser();
        User targetUser = author.getManager();
        if (targetUser == null) {
            throw new BadRequestException("Current user does not have a manager to send feedback to.");
        }
        Feedback feedback = new Feedback();
        feedback.setAuthor(author);
        feedback.setTargetUser(targetUser);
        feedback.setContent(content);
        feedback.setCreatedAt(LocalDateTime.now());
        feedback.setVisibility(Visibility.PUBLIC); // set as needed
        if (model != null && !model.isBlank()) {
            feedback.setStatus(FeedbackPolishStatus.POLISHING);
            feedback.setPolishedContent(null);
            feedback.setPolishError(null);
            Feedback saved = feedbackRepository.save(feedback);
            polishAsync(saved.getId(), content, model);
            return FeedbackMapper.toDto(saved);
        } else {
            feedback.setStatus(null);
            feedback.setPolishedContent(null);
            feedback.setPolishError(null);
            Feedback saved = feedbackRepository.save(feedback);
            return FeedbackMapper.toDto(saved);
        }
    }

    public List<FeedbackDto> listFeedback(String userIdStr) {
        UUID userId;
        try {
            userId = UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid user ID format");
        }
        User requester = getCurrentUser();
        // Check if requester is a manager (has subordinates)
        List<User> subordinates = userRepository.findByManager_Id(requester.getId());
        boolean isManager = !subordinates.isEmpty();
        List<Feedback> feedbackList;
        if (isManager) {
            // Collect subordinate IDs
            List<UUID> subordinateIds = subordinates.stream().map(User::getId).toList();
            // Feedbacks addressed to subordinates
            List<Feedback> forSubordinates = subordinateIds.isEmpty() ? List.of() : feedbackRepository.findByTargetUserIdIn(subordinateIds);
            // Feedbacks addressed to manager
            List<Feedback> forManager = feedbackRepository.findByTargetUserId(requester.getId());
            // Feedbacks authored by manager
            List<Feedback> authoredByManager = feedbackRepository.findByAuthorId(requester.getId());
            // Merge and deduplicate
            feedbackList = new java.util.ArrayList<>();
            feedbackList.addAll(forSubordinates);
            feedbackList.addAll(forManager);
            feedbackList.addAll(authoredByManager);
            feedbackList = feedbackList.stream().distinct().toList();
        } else {
            // Normal user: only feedbacks they made
            feedbackList = feedbackRepository.findByAuthorId(requester.getId());
        }
        // Filter by visibility (customize as needed)
        return feedbackList.stream()
            .filter(fb -> canViewFeedback(requester, fb))
            .map(FeedbackMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public FeedbackDto editFeedback(String feedbackId, String content, String model) {
        Feedback feedback = feedbackRepository.findById(UUID.fromString(feedbackId))
            .orElseThrow(() -> new NotFoundException("Feedback not found"));
        feedback.setContent(content);
        if (model != null && !model.isBlank()) {
            feedback.setStatus(FeedbackPolishStatus.POLISHING);
            feedback.setPolishedContent(null);
            feedback.setPolishError(null);
            feedbackRepository.save(feedback);
            polishAsync(feedback.getId(), content, model);
        } else {
            feedback.setStatus(null);
            feedback.setPolishedContent(null);
            feedback.setPolishError(null);
            feedbackRepository.save(feedback);
        }
        return FeedbackMapper.toDto(feedback);
    }

    public FeedbackDto getFeedback(String feedbackIdStr) {
        UUID feedbackId;
        try {
            feedbackId = UUID.fromString(feedbackIdStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid feedback ID format");
        }
        Feedback feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new NotFoundException("Feedback not found"));
        User requester = getCurrentUser();
        if (!canViewFeedback(requester, feedback)) {
            throw new ForbiddenException("Not allowed to view this feedback");
        }
        return FeedbackMapper.toDto(feedback);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private boolean canLeaveFeedback(User author, User target) {
        // Allow self, manager, or admin (customize as needed)
        if (author.getId().equals(target.getId())) return true;
        if (target.getManager() != null && target.getManager().getId().equals(author.getId())) return true;
        if (author.getRoles() != null && author.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()))) return true;
        return false;
    }

    private boolean canViewFeedback(User requester, Feedback feedback) {
        // Customize visibility logic as needed
        return true;
    }

    private boolean canEditFeedback(User user, Feedback feedback) {
        // Allow author, manager of target, or admin
        if (feedback.getAuthor().getId().equals(user.getId())) return true;
        if (feedback.getTargetUser().getManager() != null && feedback.getTargetUser().getManager().getId().equals(user.getId())) return true;
        if (user.getRoles() != null && user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()))) return true;
        return false;
    }

    @Async
    public void polishAsync(UUID feedbackId, String content, String model) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElse(null);
        if (feedback == null) return;
        try {
            String polished = feedbackPolisher.polish(content, model);
            feedback.setPolishedContent(polished);
            feedback.setStatus(FeedbackPolishStatus.READY);
            feedback.setPolishError(null);
        } catch (Exception e) {
            feedback.setPolishedContent(null);
            feedback.setStatus(FeedbackPolishStatus.FAILED);
            feedback.setPolishError(e.getMessage());
        }
        feedbackRepository.save(feedback);
    }
}
