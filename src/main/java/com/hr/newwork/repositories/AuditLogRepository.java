package com.hr.newwork.repositories;

import com.hr.newwork.data.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

/**
 * Repository interface for accessing and querying audit log entries.
 * Provides methods for paginated and filtered retrieval of audit logs.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    /**
     * Finds audit logs matching the provided filters, with pagination and sorting.
     *
     * @param actorId     Optional filter by actor (user) UUID
     * @param action      Optional filter by action type
     * @param targetTable Optional filter by target table/entity
     * @param from        Optional filter for logs after this timestamp (inclusive)
     * @param to          Optional filter for logs before this timestamp (inclusive)
     * @param pageable    Pagination and sorting information
     * @return Page of audit logs matching the filters
     */
    @Query("SELECT a FROM AuditLog a WHERE (:actorId IS NULL OR a.actor.id = :actorId) " +
           "AND (:action IS NULL OR a.action = :action) " +
           "AND (:targetTable IS NULL OR a.targetTable = :targetTable) " +
           "AND (:from IS NULL OR a.timestamp >= :from) " +
           "AND (:to IS NULL OR a.timestamp <= :to)")
    Page<AuditLog> findFiltered(@Param("actorId") UUID actorId,
                                @Param("action") String action,
                                @Param("targetTable") String targetTable,
                                @Param("from") Instant from,
                                @Param("to") Instant to,
                                Pageable pageable);
}
