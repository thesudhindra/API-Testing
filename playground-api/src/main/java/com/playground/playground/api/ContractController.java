package com.playground.playground.api;

import com.playground.playground.config.OpenApiConfig;
import com.playground.playground.contract.dto.ContractContent;
import com.playground.playground.contract.dto.ContractSummary;
import com.playground.playground.contract.service.ContractRegistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/contracts")
@Tag(name = "Contracts", description = "OpenAPI contract registry")
@SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
public class ContractController {

    private final ContractRegistryService contractRegistryService;

    public ContractController(ContractRegistryService contractRegistryService) {
        this.contractRegistryService = contractRegistryService;
    }

    @GetMapping
    @Operation(summary = "List available contracts")
    public List<ContractSummary> listContracts() {
        return contractRegistryService.listContracts();
    }

    @GetMapping("/{service}/{file}")
    @Operation(summary = "Get contract content")
    public ContractContent getContract(@PathVariable String service, @PathVariable String file) {
        return contractRegistryService.getContract(service, file);
    }
}
