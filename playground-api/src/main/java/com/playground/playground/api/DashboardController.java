package com.playground.playground.api;

import com.playground.playground.config.OpenApiConfig;
import com.playground.playground.dashboard.dto.DashboardResponse;
import com.playground.playground.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/playground/dashboard")
@Tag(name = "Dashboard", description = "Learner dashboard")
@SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(summary = "Get lab dashboard")
    public DashboardResponse getDashboard() {
        return dashboardService.getDashboard();
    }
}
