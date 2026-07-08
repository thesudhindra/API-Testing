package com.playground.playground.api;

import com.playground.playground.config.OpenApiConfig;
import com.playground.playground.security.dto.SecurityTestCase;
import com.playground.playground.security.service.SecurityTestSupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/playground/security")
@Tag(name = "Security Lab", description = "Security testing cases")
@SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
public class SecurityLabController {

    private final SecurityTestSupportService securityTestSupportService;

    public SecurityLabController(SecurityTestSupportService securityTestSupportService) {
        this.securityTestSupportService = securityTestSupportService;
    }

    @GetMapping("/test-cases")
    @Operation(summary = "List security test cases")
    public List<SecurityTestCase> getTestCases() {
        return securityTestSupportService.getTestCases();
    }
}
