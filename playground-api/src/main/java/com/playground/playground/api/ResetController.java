package com.playground.playground.api;

import com.playground.playground.config.OpenApiConfig;
import com.playground.playground.reset.dto.ResetRequest;
import com.playground.playground.reset.dto.ResetResponse;
import com.playground.playground.reset.service.ResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/playground/reset")
@Tag(name = "Reset", description = "Lab environment reset")
@SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
public class ResetController {

    private final ResetService resetService;

    public ResetController(ResetService resetService) {
        this.resetService = resetService;
    }

    @PostMapping
    @Operation(summary = "Reset lab data by scope")
    public ResetResponse reset(@Valid @RequestBody ResetRequest request) {
        return resetService.reset(request.scope());
    }
}
