package com.hr.newwork.services;

import com.hr.newwork.data.dto.FeedbackDto;
import com.hr.newwork.data.dto.FeedbackRequestDto;
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
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public FeedbackDto createFeedback(String ignoredUserId, FeedbackRequestDto feedbackRequest) {
        // Get the current authenticated user as the author
        User author = getCurrentUser();
        // Parse the target user UUID from the request
        UUID targetUserUuid = UUID.fromString(feedbackRequest.getTargetUserId());
        // Fetch the target user using findById (returns Optional)
        User targetUser = userRepository.findById(targetUserUuid)
            .orElseThrow(() -> new NotFoundException("Target user not found"));

        // Create and populate the Feedback entity
        Feedback feedback = new Feedback();
        feedback.setAuthor(author);
        feedback.setTargetUser(targetUser);
        feedback.setContent(feedbackRequest.getContent());
        feedback.setCreatedAt(LocalDateTime.now());
        feedback.setVisibility(Visibility.PUBLIC); // set as needed

        // If a model is provided, set status to POLISHING and trigger polish
        if (StringUtils.hasText(feedbackRequest.getModel())) {
            String model = feedbackRequest.getModel();
            Feedback saved = feedbackRepository.save(feedback);
            polishAsync(saved.getId(), feedbackRequest.getContent(), model);
            return FeedbackMapper.toDto(saved);
        } else {
            // If no model, just save the feedback with no polish
            feedback.setPolishedContent(null);
            feedback.setStatus(null);
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
        // Fetch feedbacks where user is author
        List<Feedback> asAuthor = feedbackRepository.findByAuthorId(userId);
        // Fetch feedbacks where user is target
        List<Feedback> asTarget = feedbackRepository.findByTargetUserId(userId);
        // Merge and deduplicate
        List<Feedback> allFeedbacks = new ArrayList<>();
        allFeedbacks.addAll(asAuthor);
        allFeedbacks.addAll(asTarget);
        allFeedbacks = allFeedbacks.stream().distinct().toList();
        return allFeedbacks.stream()
            .filter(fb -> canViewFeedback(getCurrentUser(), fb))
            .map(FeedbackMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public FeedbackDto editFeedback(String feedbackId, FeedbackRequestDto editRequest) {
        Feedback feedback = feedbackRepository.findById(UUID.fromString(feedbackId))
            .orElseThrow(() -> new NotFoundException("Feedback not found"));
        feedback.setContent(editRequest.getContent());
        String model = editRequest.getModel();
        if (model != null && !model.isBlank()) {
            feedback.setStatus(FeedbackPolishStatus.POLISHING);
            feedback.setPolishedContent(null);
            feedback.setPolishError(null);
            feedbackRepository.save(feedback);
            polishAsync(feedback.getId(), editRequest.getContent(), model);
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
