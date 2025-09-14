package com.hr.newwork.services;

import com.hr.newwork.exceptions.RestClientServiceException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.function.Supplier;

/**
 * General-purpose REST client service for HTTP requests.
 * Supports GET, POST, PUT, PATCH, DELETE with flexible headers, params, and response types.
 */
@Service
public class RestClientService {
    private static final Logger logger = LoggerFactory.getLogger(RestClientService.class);
    private final RestTemplate restTemplate;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public RestClientService(RestTemplate restTemplate, CircuitBreaker restClientCircuitBreaker) {
        this.restTemplate = restTemplate;
        this.circuitBreaker = restClientCircuitBreaker;
    }

    /**
     * Perform a GET request.
     */
    public <T> ResponseEntity<T> get(String url, Map<String, String> headers, Map<String, ?> params, Class<T> responseType) {
        return exchange(url, HttpMethod.GET, null, headers, params, responseType, null);
    }
    public <T> ResponseEntity<T> get(String url, Map<String, String> headers, Map<String, ?> params, ParameterizedTypeReference<T> responseType) {
        return exchange(url, HttpMethod.GET, null, headers, params, null, responseType);
    }

    /**
     * Perform a POST request.
     */
    public <T, R> ResponseEntity<T> post(String url, R body, Map<String, String> headers, Map<String, ?> params, Class<T> responseType) {
        return exchange(url, HttpMethod.POST, body, headers, params, responseType, null);
    }
    public <T, R> ResponseEntity<T> post(String url, R body, Map<String, String> headers, Map<String, ?> params, ParameterizedTypeReference<T> responseType) {
        return exchange(url, HttpMethod.POST, body, headers, params, null, responseType);
    }

    /**
     * Perform a PUT request.
     */
    public <T, R> ResponseEntity<T> put(String url, R body, Map<String, String> headers, Map<String, ?> params, Class<T> responseType) {
        return exchange(url, HttpMethod.PUT, body, headers, params, responseType, null);
    }
    public <T, R> ResponseEntity<T> put(String url, R body, Map<String, String> headers, Map<String, ?> params, ParameterizedTypeReference<T> responseType) {
        return exchange(url, HttpMethod.PUT, body, headers, params, null, responseType);
    }

    /**
     * Perform a PATCH request.
     */
    public <T, R> ResponseEntity<T> patch(String url, R body, Map<String, String> headers, Map<String, ?> params, Class<T> responseType) {
        return exchange(url, HttpMethod.PATCH, body, headers, params, responseType, null);
    }
    public <T, R> ResponseEntity<T> patch(String url, R body, Map<String, String> headers, Map<String, ?> params, ParameterizedTypeReference<T> responseType) {
        return exchange(url, HttpMethod.PATCH, body, headers, params, null, responseType);
    }

    /**
     * Perform a DELETE request.
     */
    public <T> ResponseEntity<T> delete(String url, Map<String, String> headers, Map<String, ?> params, Class<T> responseType) {
        return exchange(url, HttpMethod.DELETE, null, headers, params, responseType, null);
    }
    public <T> ResponseEntity<T> delete(String url, Map<String, String> headers, Map<String, ?> params, ParameterizedTypeReference<T> responseType) {
        return exchange(url, HttpMethod.DELETE, null, headers, params, null, responseType);
    }

    // Unified internal method for all HTTP verbs
    @Retryable(
        value = { RestClientServiceException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 500, maxDelay = 2000, multiplier = 2)
    )
    private <T, R> ResponseEntity<T> exchange(
            String url,
            HttpMethod method,
            R body,
            Map<String, String> headers,
            Map<String, ?> params,
            Class<T> responseType,
            ParameterizedTypeReference<T> ptrType
    ) {
        HttpHeaders httpHeaders = buildHeaders(headers);
        HttpEntity<R> entity = new HttpEntity<>(body, httpHeaders);
        URI uri = buildUri(url, params);
        try {
            logger.debug("{} {} | Headers: {} | Params: {} | Body: {}", method, uri, httpHeaders, params, body);
            Supplier<ResponseEntity<T>> requestSupplier = () -> {
                try {
                    ResponseEntity<T> response;
                    if (responseType != null) {
                        response = restTemplate.exchange(uri, method, entity, responseType);
                    } else if (ptrType != null) {
                        response = restTemplate.exchange(uri, method, entity, ptrType);
                    } else {
                        throw new IllegalArgumentException("Either responseType or ptrType must be provided");
                    }
                    if (response.getStatusCode().is5xxServerError()) {
                        throw new RestClientServiceException("Server error: " + response.getStatusCode(), null);
                    }
                    if (!response.getStatusCode().is2xxSuccessful() && !response.getStatusCode().is3xxRedirection()) {
                        throw new RestClientServiceException("Non-success status: " + response.getStatusCode(), null);
                    }
                    logger.debug("Response: {} {}", response.getStatusCode(), response.getBody());
                    return response;
                } catch (Exception ex) {
                    throw new RestClientServiceException("REST call failed", ex);
                }
            };
            return circuitBreaker.executeSupplier(requestSupplier);
        } catch (CallNotPermittedException ex) {
            logger.error("Circuit breaker is OPEN for {} {}", method, uri);
            throw new RestClientServiceException("Circuit breaker is OPEN", ex);
        } catch (RestClientServiceException ex) {
            logger.error("REST call failed: {} {} | {}", method, uri, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error: {} {} | {}", method, uri, ex.getMessage());
            throw new RestClientServiceException("Unexpected error", ex);
        }
    }

    private HttpHeaders buildHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }
        return httpHeaders;
    }

    private URI buildUri(String url, Map<String, ?> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        if (params != null) {
            params.forEach(builder::queryParam);
        }
        return builder.build().toUri();
    }
}
