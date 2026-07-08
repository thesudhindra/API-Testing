package com.playground.enterprise.resilience.service;

import com.playground.enterprise.domain.CircuitBreakerState;
import com.playground.enterprise.resilience.entity.CircuitBreakerEntity;
import com.playground.enterprise.resilience.repository.CircuitBreakerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerServiceTest {

    @Mock
    private CircuitBreakerRepository repository;

    @InjectMocks
    private CircuitBreakerService circuitBreakerService;

    @Test
    void recordsFailureAndOpensAfterThreshold() {
        CircuitBreakerEntity breaker = new CircuitBreakerEntity();
        breaker.setName("webhook-delivery");
        breaker.setState(CircuitBreakerState.CLOSED);
        breaker.setFailureCount(4);
        breaker.setSuccessCount(0);
        breaker.setUpdatedAt(Instant.now());

        when(repository.findById("webhook-delivery")).thenReturn(Optional.of(breaker));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        circuitBreakerService.recordFailure("webhook-delivery");

        assertThat(breaker.getState()).isEqualTo(CircuitBreakerState.OPEN);
        assertThat(breaker.getFailureCount()).isEqualTo(5);
    }
}
