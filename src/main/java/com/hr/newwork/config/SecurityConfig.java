package com.hr.newwork.config;

import com.hr.newwork.config.security.CustomAuthenticationProvider;
import com.hr.newwork.config.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, CustomAuthenticationProvider customAuthenticationProvider) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(customAuthenticationProvider);
        return builder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(
                    "/auth/login",
                    "/auth/refresh",
                    "/swagger-ui/**",
                    "/api-docs",
                    "/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // Admin endpoints
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // User registration: MANAGER, ADMIN only
                .requestMatchers("/users/register").hasAnyRole("MANAGER", "ADMIN")
                // Absence approval/reject: MANAGER, ADMIN only
                .requestMatchers("/absences/*/approve", "/absences/*/reject").hasAnyRole("MANAGER", "ADMIN")
                // All other user, absence, feedback, and logout endpoints: EMPLOYEE, MANAGER, ADMIN
                .requestMatchers(
                    "/users/**",
                    "/absences/**",
                    "/feedback/**"
                ).hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                // Any other request
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
