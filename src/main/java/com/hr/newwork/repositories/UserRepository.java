package com.hr.newwork.repositories;

import com.hr.newwork.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity.
 * Provides CRUD operations and custom queries for users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Finds a user by their email address.
     * @param email the user's email
     * @return an Optional containing the User if found, or empty if not
     */
    Optional<User> findByEmail(String email);
    // Add custom queries as needed
}
