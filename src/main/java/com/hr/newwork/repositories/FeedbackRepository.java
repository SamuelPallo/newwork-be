package com.hr.newwork.repositories;

import com.hr.newwork.data.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    List<Feedback> findByTargetUserId(UUID userId);
    List<Feedback> findByTargetUserIdIn(List<UUID> userIds);
    List<Feedback> findByAuthorId(UUID authorId);
    // Add custom queries as needed
}
