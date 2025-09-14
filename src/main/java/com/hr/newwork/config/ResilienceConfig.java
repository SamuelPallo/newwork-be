package com.hr.newwork.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import java.time.Duration;

@Configuration
@EnableRetry
public class ResilienceConfig {
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // % of failures to open circuit
                .waitDurationInOpenState(Duration.ofSeconds(30)) // cooldown
                .slidingWindowSize(10) // number of calls to consider
                .minimumNumberOfCalls(5)
                .permittedNumberOfCallsInHalfOpenState(3)
                .recordException(e -> true) // record all exceptions
                .build();
        return CircuitBreakerRegistry.of(config);
    }

    @Bean
    public CircuitBreaker restClientCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("restClientCircuitBreaker");
    }
}

