package com.playground.enterprise.resilience.service;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RetryExecutorTest {

    private final RetryExecutor retryExecutor = new RetryExecutor();

    @Test
    void succeedsOnSecondAttempt() {
        AtomicInteger attempts = new AtomicInteger();

        retryExecutor.executeWithRetry(() -> {
            if (attempts.incrementAndGet() < 2) {
                throw new IllegalStateException("transient");
            }
        }, 3, 1);

        assertThat(attempts.get()).isEqualTo(2);
    }

    @Test
    void exhaustsRetries() {
        assertThatThrownBy(() -> retryExecutor.executeWithRetry(() -> {
            throw new IllegalStateException("persistent");
        }, 2, 1))
                .isInstanceOf(IllegalStateException.class);
    }
}
