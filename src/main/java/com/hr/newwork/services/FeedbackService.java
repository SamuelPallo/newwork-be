package com.hr.newwork.services;

import com.hr.newwork.data.dto.FeedbackDto;
import com.hr.newwork.data.entity.Feedback;
import com.hr.newwork.data.entity.User;
import com.hr.newwork.exceptions.ForbiddenException;
import com.hr.newwork.exceptions.NotFoundException;
import com.hr.newwork.repositories.FeedbackRepository;
import com.hr.newwork.repositories.UserRepository;
import com.hr.newwork.services.polish.FeedbackPolisher;
import com.hr.newwork.util.enums.FeedbackPolishStatus;
import com.hr.newwork.util.enums.Role;
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
    public FeedbackDto createFeedback(UUID userId, String content, boolean polish) {
        User author = getCurrentUser();
        User targetUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        // Permission check: allow self, manager, or admin to leave feedback (customize as needed)
        if (!canLeaveFeedback(author, targetUser)) {
            throw new ForbiddenException("Not allowed to leave feedback for this user");
        }
        Feedback feedback = new Feedback();
        feedback.setAuthor(author);
        feedback.setTargetUser(targetUser);
        feedback.setContent(content);
        feedback.setCreatedAt(LocalDateTime.now());
        feedback.setVisibility(Visibility.PUBLIC); // set as needed
        if (polish) {
            feedback.setStatus(FeedbackPolishStatus.POLISHING);
            feedback.setPolishedContent(null);
            feedback.setPolishError(null);
            Feedback saved = feedbackRepository.save(feedback);
            polishAsync(saved.getId(), content);
            return FeedbackMapper.toDto(saved);
        } else {
            feedback.setStatus(null);
            feedback.setPolishedContent(null);
            feedback.setPolishError(null);
            Feedback saved = feedbackRepository.save(feedback);
            return FeedbackMapper.toDto(saved);
        }
    }

    public List<FeedbackDto> listFeedback(UUID userId) {
        User requester = getCurrentUser();
        List<Feedback> feedbackList = feedbackRepository.findByTargetUserId(userId);
        // Filter by visibility (customize as needed)
        return feedbackList.stream()
            .filter(fb -> canViewFeedback(requester, fb))
            .map(FeedbackMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public FeedbackDto editFeedback(UUID feedbackId, String content, boolean polish) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElseThrow(() -> new NotFoundException("Feedback not found"));
        User current = getCurrentUser();
        if (!canEditFeedback(current, feedback)) {
            throw new ForbiddenException("Not allowed to edit this feedback");
        }
        feedback.setContent(content);
        if (polish) {
            feedback.setStatus(FeedbackPolishStatus.POLISHING);
            feedback.setPolishedContent(null);
            feedback.setPolishError(null);
            Feedback saved = feedbackRepository.save(feedback);
            polishAsync(saved.getId(), content);
            return FeedbackMapper.toDto(saved);
        } else {
            feedback.setStatus(null);
            feedback.setPolishedContent(null);
            feedback.setPolishError(null);
            Feedback saved = feedbackRepository.save(feedback);
            return FeedbackMapper.toDto(saved);
        }
    }

    public FeedbackDto getFeedback(UUID feedbackId) {
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
        if (author.getRoles() != null && author.getRoles().contains(Role.ADMIN)) return true;
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
        if (user.getRoles() != null && user.getRoles().contains(com.hr.newwork.util.enums.Role.ADMIN)) return true;
        return false;
    }

    @Async
    public void polishAsync(UUID feedbackId, String content) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElse(null);
        if (feedback == null) return;
        try {
            String polished = feedbackPolisher.polish(content);
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
