package com.hr.newwork.repositories;

import com.hr.newwork.data.entity.RefreshToken;
import com.hr.newwork.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing RefreshToken entities.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    /**
     * Finds a refresh token by its token string.
     * @param token the refresh token string
     * @return an Optional containing the RefreshToken if found, or empty if not
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Deletes all refresh tokens for a user.
     * @param user the user
     */
    void deleteAllByUser(User user);
}

