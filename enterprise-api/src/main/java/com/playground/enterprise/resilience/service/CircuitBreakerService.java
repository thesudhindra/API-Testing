package com.playground.enterprise.resilience.service;

import com.playground.enterprise.domain.CircuitBreakerState;
import com.playground.enterprise.resilience.dto.CircuitBreakerResponse;
import com.playground.enterprise.resilience.entity.CircuitBreakerEntity;
import com.playground.enterprise.resilience.repository.CircuitBreakerRepository;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class CircuitBreakerService {

    private static final int FAILURE_THRESHOLD = 5;
    private static final int SUCCESS_THRESHOLD = 2;
    private static final Duration HALF_OPEN_DELAY = Duration.ofSeconds(30);

    private final CircuitBreakerRepository circuitBreakerRepository;

    public CircuitBreakerService(CircuitBreakerRepository circuitBreakerRepository) {
        this.circuitBreakerRepository = circuitBreakerRepository;
    }

    @Transactional
    public CircuitBreakerState getState(String name) {
        CircuitBreakerEntity breaker = requireBreaker(name);
        if (breaker.getState() == CircuitBreakerState.OPEN
                && breaker.getOpenedAt() != null
                && Duration.between(breaker.getOpenedAt(), Instant.now()).compareTo(HALF_OPEN_DELAY) >= 0) {
            breaker.setState(CircuitBreakerState.HALF_OPEN);
            breaker.setSuccessCount(0);
            breaker.setUpdatedAt(Instant.now());
            circuitBreakerRepository.save(breaker);
            return CircuitBreakerState.HALF_OPEN;
        }
        return breaker.getState();
    }

    @Transactional
    public void recordSuccess(String name) {
        CircuitBreakerEntity breaker = requireBreaker(name);
        Instant now = Instant.now();

        if (breaker.getState() == CircuitBreakerState.HALF_OPEN) {
            breaker.setSuccessCount(breaker.getSuccessCount() + 1);
            if (breaker.getSuccessCount() >= SUCCESS_THRESHOLD) {
                breaker.setState(CircuitBreakerState.CLOSED);
                breaker.setFailureCount(0);
                breaker.setSuccessCount(0);
                breaker.setOpenedAt(null);
            }
        } else if (breaker.getState() == CircuitBreakerState.CLOSED) {
            breaker.setFailureCount(0);
            breaker.setSuccessCount(0);
        }

        breaker.setUpdatedAt(now);
        circuitBreakerRepository.save(breaker);
    }

    @Transactional
    public void recordFailure(String name) {
        CircuitBreakerEntity breaker = requireBreaker(name);
        Instant now = Instant.now();

        if (breaker.getState() == CircuitBreakerState.HALF_OPEN) {
            breaker.setState(CircuitBreakerState.OPEN);
            breaker.setOpenedAt(now);
            breaker.setFailureCount(FAILURE_THRESHOLD);
            breaker.setSuccessCount(0);
        } else {
            breaker.setFailureCount(breaker.getFailureCount() + 1);
            if (breaker.getFailureCount() >= FAILURE_THRESHOLD) {
                breaker.setState(CircuitBreakerState.OPEN);
                breaker.setOpenedAt(now);
                breaker.setSuccessCount(0);
            }
        }

        breaker.setUpdatedAt(now);
        circuitBreakerRepository.save(breaker);
    }

    @Transactional(readOnly = true)
    public List<CircuitBreakerResponse> listBreakers() {
        return circuitBreakerRepository.findAll().stream()
                .map(CircuitBreakerService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CircuitBreakerResponse getBreaker(String name) {
        return toResponse(requireBreaker(name));
    }

    private CircuitBreakerEntity requireBreaker(String name) {
        return circuitBreakerRepository.findById(name)
                .orElseThrow(() -> new ResourceNotFoundException("Circuit breaker not found: " + name));
    }

    static CircuitBreakerResponse toResponse(CircuitBreakerEntity breaker) {
        return new CircuitBreakerResponse(
                breaker.getName(),
                breaker.getState(),
                breaker.getFailureCount(),
                breaker.getSuccessCount(),
                breaker.getOpenedAt(),
                breaker.getUpdatedAt());
    }
}
