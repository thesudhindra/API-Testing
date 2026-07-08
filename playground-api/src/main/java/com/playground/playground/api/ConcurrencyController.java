package com.playground.playground.api;

import com.playground.playground.concurrency.dto.ConcurrencyScenario;
import com.playground.playground.concurrency.dto.RaceProfile;
import com.playground.playground.concurrency.service.ConcurrencyScenarioService;
import com.playground.playground.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/playground/concurrency")
@Tag(name = "Concurrency", description = "Concurrency lab scenarios")
@SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
public class ConcurrencyController {

    private final ConcurrencyScenarioService concurrencyScenarioService;

    public ConcurrencyController(ConcurrencyScenarioService concurrencyScenarioService) {
        this.concurrencyScenarioService = concurrencyScenarioService;
    }

    @GetMapping("/scenarios")
    @Operation(summary = "List concurrency scenarios")
    public List<ConcurrencyScenario> getScenarios() {
        return concurrencyScenarioService.getScenarios();
    }

    @GetMapping("/scenarios/{slug}/race-profile")
    @Operation(summary = "Get race profile for concurrency scenario")
    public RaceProfile getRaceProfile(@PathVariable String slug) {
        return concurrencyScenarioService.getRaceProfile(slug);
    }
}
