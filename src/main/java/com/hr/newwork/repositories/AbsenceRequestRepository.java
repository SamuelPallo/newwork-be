package com.hr.newwork.repositories;

import com.hr.newwork.data.entity.AbsenceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for AbsenceRequest entity.
 * Provides CRUD operations and custom queries for absence requests.
 */
@Repository
public interface AbsenceRequestRepository extends JpaRepository<AbsenceRequest, UUID> {
    /**
     * Finds all absence requests for a given user.
     * @param userId the user's ID
     * @return list of absence requests
     */
    List<AbsenceRequest> findByUserId(UUID userId);

    /**
     * Finds all absence requests for users managed by a given manager.
     * @param managerId the manager's user ID
     * @return list of absence requests
     */
    List<AbsenceRequest> findByUser_Manager_Id(UUID managerId);
}
