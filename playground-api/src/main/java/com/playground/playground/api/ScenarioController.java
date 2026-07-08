package com.playground.playground.api;

import com.playground.playground.config.OpenApiConfig;
import com.playground.playground.scenario.dto.CompleteScenarioRunRequest;
import com.playground.playground.scenario.dto.ScenarioDefinition;
import com.playground.playground.scenario.dto.ScenarioRunResponse;
import com.playground.playground.scenario.service.ScenarioCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/scenarios")
@Tag(name = "Scenarios", description = "Lab scenario catalog and runs")
@SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
public class ScenarioController {

    private final ScenarioCatalogService scenarioCatalogService;

    public ScenarioController(ScenarioCatalogService scenarioCatalogService) {
        this.scenarioCatalogService = scenarioCatalogService;
    }

    @GetMapping
    @Operation(summary = "List scenarios")
    public List<ScenarioDefinition> listScenarios() {
        return scenarioCatalogService.listScenarios();
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get scenario by slug")
    public ScenarioDefinition getScenario(@PathVariable String slug) {
        return scenarioCatalogService.getScenario(slug);
    }

    @PostMapping("/{slug}/runs")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Start a scenario run")
    public ScenarioRunResponse startRun(@PathVariable String slug) {
        return scenarioCatalogService.startRun(slug);
    }

    @PatchMapping("/runs/{runId}")
    @Operation(summary = "Complete a scenario run")
    public ScenarioRunResponse completeRun(
            @PathVariable String runId,
            @Valid @RequestBody CompleteScenarioRunRequest request) {
        return scenarioCatalogService.completeRun(runId, request.status());
    }
}
