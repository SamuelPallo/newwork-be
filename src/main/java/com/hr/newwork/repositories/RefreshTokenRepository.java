package com.hr.newwork.repositories;

import com.hr.newwork.data.entity.RefreshToken;
import com.hr.newwork.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    /**
     * Finds all valid and unexpired refresh tokens for a user.
     * @param userId the user's ID
     * @param now the current time
     * @return list of valid and unexpired refresh tokens
     */
    List<RefreshToken> findAllByUserIdAndValidTrueAndExpiryDateAfter(UUID userId, Date now);
}
