package com.playground.playground.performance.service;

import com.playground.playground.performance.dto.LoadHintsResponse;
import com.playground.playground.performance.dto.PerformanceProfile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PerformanceSupportService {

    private static final List<PerformanceProfile> PROFILES = List.of(
            new PerformanceProfile(
                    "payment-throughput",
                    "/v1/payments",
                    "POST",
                    20,
                    120,
                    "Sustained payment creation with idempotency keys; watch p95 latency and error rate."),
            new PerformanceProfile(
                    "transfer-burst",
                    "/v1/transfers",
                    "POST",
                    50,
                    60,
                    "Burst internal transfers to trigger optimistic locking conflicts."),
            new PerformanceProfile(
                    "account-read",
                    "/v1/accounts/{accountId}",
                    "GET",
                    100,
                    180,
                    "Read-heavy mix on seeded accounts; baseline cache and DB pool sizing."),
            new PerformanceProfile(
                    "health-probe",
                    "/health",
                    "GET",
                    200,
                    30,
                    "Synthetic uptime probe across all services for soak testing.")
    );

    public List<PerformanceProfile> getProfiles() {
        return PROFILES;
    }

    public LoadHintsResponse getLoadHints() {
        Map<String, String> hints = Map.of(
                "rampUp", "Start at 25% target VUs for 30s before full load",
                "thinkTime", "Add 100-500ms between steps for realistic user pacing",
                "idempotency", "Use unique Idempotency-Key per POST unless testing replay",
                "correlation", "Propagate X-Correlation-Id to trace requests across services",
                "abort", "Stop test if error rate exceeds 5% for 30 consecutive seconds"
        );
        return new LoadHintsResponse(PROFILES, hints);
    }
}
