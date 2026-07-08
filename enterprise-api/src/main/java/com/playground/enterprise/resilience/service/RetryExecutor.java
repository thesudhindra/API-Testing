package com.playground.enterprise.resilience.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RetryExecutor {

    private static final Logger log = LoggerFactory.getLogger(RetryExecutor.class);

    public void executeWithRetry(Runnable action, int maxRetries, long backoffMs) {
        int attempt = 0;
        while (true) {
            try {
                action.run();
                return;
            } catch (RuntimeException e) {
                if (attempt >= maxRetries) {
                    throw e;
                }
                attempt++;
                log.debug("Retry attempt {} of {} after failure: {}", attempt, maxRetries, e.getMessage());
                sleep(backoffMs);
            }
        }
    }

    private static void sleep(long backoffMs) {
        try {
            Thread.sleep(backoffMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry interrupted", e);
        }
    }
}
