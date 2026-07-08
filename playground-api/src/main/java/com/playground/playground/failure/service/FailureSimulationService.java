package com.playground.playground.failure.service;

import com.playground.common.exception.ResourceNotFoundException;
import com.playground.playground.domain.FaultTargetService;
import com.playground.playground.domain.FaultType;
import com.playground.playground.failure.dto.FailureSimulation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FailureSimulationService {

    private static final List<FailureSimulation> SIMULATIONS = List.of(
            new FailureSimulation(
                    "banking-latency-spike",
                    "Banking API Latency Spike",
                    "Inject 2s latency on payment endpoints to observe timeout handling.",
                    FaultTargetService.BANKING,
                    FaultType.LATENCY,
                    "/v1/payments/**",
                    Map.of("delayMs", 2000),
                    List.of()),
            new FailureSimulation(
                    "enterprise-error-burst",
                    "Enterprise Error Burst",
                    "Return 503 on AML screening for 30% of requests.",
                    FaultTargetService.ENTERPRISE,
                    FaultType.ERROR_RATE,
                    "/v1/aml/screen",
                    Map.of("errorRate", 0.3, "statusCode", 503),
                    List.of()),
            new FailureSimulation(
                    "mock-aml-review",
                    "AML Mock Review Response",
                    "Route AML checks to mock endpoint returning REVIEW result.",
                    FaultTargetService.MOCK,
                    FaultType.RESET,
                    "/v1/mocks/aml/screen/review",
                    Map.of(),
                    List.of("mock-aml-review")),
            new FailureSimulation(
                    "platform-timeout",
                    "Platform Gateway Timeout",
                    "Simulate upstream timeout on platform health aggregation.",
                    FaultTargetService.PLATFORM,
                    FaultType.TIMEOUT,
                    "/health",
                    Map.of("timeoutMs", 5000),
                    List.of())
    );

    public List<FailureSimulation> listSimulations() {
        return SIMULATIONS;
    }

    public FailureSimulation getSimulation(String slug) {
        return SIMULATIONS.stream()
                .filter(s -> s.slug().equals(slug))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Failure simulation not found: " + slug));
    }
}
