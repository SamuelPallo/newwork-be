package com.hr.newwork.config.security;

import com.hr.newwork.data.entity.User;
import com.hr.newwork.repositories.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret:defaultSecretKey1234567890}")
    private String jwtSecret;

    @Value("${jwt.expiration:3600000}") // 1 hour default
    private long jwtExpirationMs;

    private Key key;

    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }

    public String generateToken(Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        // Use explicit algorithm for signing (HS256 as example, change as needed for your key type)
        return Jwts.builder().subject(authentication.getName())
                .claim("roles", authorities)
                .claim("userId", getUserIdFromAuthentication(authentication))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        Object rolesClaim = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles");
        if (rolesClaim == null) return Collections.emptyList();
        if (rolesClaim instanceof String rolesString) {
            if (rolesString.isBlank()) return Collections.emptyList();
            return Arrays.asList(rolesString.split(","));
        } else if (rolesClaim instanceof Collection<?> rolesCollection) {
            return rolesCollection.stream().map(Object::toString).toList();
        } else {
            return Collections.emptyList();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String getUserIdFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            // Try to get userId from UserDetails if available
            if (userDetails instanceof User userEntity) {
                return userEntity.getId() != null ? userEntity.getId().toString() : null;
            }
            // If not, try to get from username (email) via repository
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email).orElse(null);
            return user != null && user.getId() != null ? user.getId().toString() : null;
        }
        // Fallback: try to get from name
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null && user.getId() != null ? user.getId().toString() : null;
    }
}
