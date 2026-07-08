package com.playground.playground.api;

import com.playground.playground.config.OpenApiConfig;
import com.playground.playground.failure.dto.FailureSimulation;
import com.playground.playground.failure.service.FailureSimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/playground/failures")
@Tag(name = "Failures", description = "Failure simulation catalog")
@SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
public class FailureController {

    private final FailureSimulationService failureSimulationService;

    public FailureController(FailureSimulationService failureSimulationService) {
        this.failureSimulationService = failureSimulationService;
    }

    @GetMapping
    @Operation(summary = "List failure simulations")
    public List<FailureSimulation> listSimulations() {
        return failureSimulationService.listSimulations();
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get failure simulation by slug")
    public FailureSimulation getSimulation(@PathVariable String slug) {
        return failureSimulationService.getSimulation(slug);
    }
}
