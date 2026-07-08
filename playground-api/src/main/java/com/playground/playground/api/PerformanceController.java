package com.playground.playground.api;

import com.playground.playground.config.OpenApiConfig;
import com.playground.playground.performance.dto.PerformanceProfile;
import com.playground.playground.performance.service.PerformanceSupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/playground/performance")
@Tag(name = "Performance", description = "Load testing profiles")
@SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
public class PerformanceController {

    private final PerformanceSupportService performanceSupportService;

    public PerformanceController(PerformanceSupportService performanceSupportService) {
        this.performanceSupportService = performanceSupportService;
    }

    @GetMapping("/profiles")
    @Operation(summary = "List performance test profiles")
    public List<PerformanceProfile> getProfiles() {
        return performanceSupportService.getProfiles();
    }
}
