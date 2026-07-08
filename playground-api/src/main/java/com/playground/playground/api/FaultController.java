package com.playground.playground.api;

import com.playground.playground.config.OpenApiConfig;
import com.playground.playground.fault.dto.FaultRuleRequest;
import com.playground.playground.fault.dto.FaultRuleResponse;
import com.playground.playground.fault.service.FaultInjectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/playground/faults")
@Tag(name = "Faults", description = "Fault injection rules")
@SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
public class FaultController {

    private final FaultInjectionService faultInjectionService;

    public FaultController(FaultInjectionService faultInjectionService) {
        this.faultInjectionService = faultInjectionService;
    }

    @GetMapping
    @Operation(summary = "List fault rules")
    public List<FaultRuleResponse> listFaults(
            @RequestParam(required = false) Boolean enabledOnly) {
        return faultInjectionService.listRules(enabledOnly);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create fault rule")
    public FaultRuleResponse createFault(@Valid @RequestBody FaultRuleRequest request) {
        return faultInjectionService.createRule(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Disable fault rule")
    public void disableFault(@PathVariable String id) {
        faultInjectionService.disableRule(id);
    }
}
