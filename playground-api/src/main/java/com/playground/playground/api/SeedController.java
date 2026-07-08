package com.playground.playground.api;

import com.playground.playground.config.OpenApiConfig;
import com.playground.playground.seed.dto.SeedGenerateRequest;
import com.playground.playground.seed.dto.SeedGenerationResponse;
import com.playground.playground.seed.service.SeedDataGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/playground/seed")
@Tag(name = "Seed", description = "Seed data generation")
@SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
public class SeedController {

    private final SeedDataGeneratorService seedDataGeneratorService;

    public SeedController(SeedDataGeneratorService seedDataGeneratorService) {
        this.seedDataGeneratorService = seedDataGeneratorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Generate seed data by profile")
    public SeedGenerationResponse generate(@Valid @RequestBody SeedGenerateRequest request) {
        return seedDataGeneratorService.generate(request.profile());
    }
}
