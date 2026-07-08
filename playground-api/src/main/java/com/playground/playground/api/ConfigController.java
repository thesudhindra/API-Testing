package com.playground.playground.api;

import com.playground.playground.config.OpenApiConfig;
import com.playground.playground.config.dto.ConfigEntryResponse;
import com.playground.playground.config.dto.UpsertConfigRequest;
import com.playground.playground.config.service.PlaygroundConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/playground/config")
@Tag(name = "Config", description = "Playground lab configuration")
@SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
public class ConfigController {

    private final PlaygroundConfigService playgroundConfigService;

    public ConfigController(PlaygroundConfigService playgroundConfigService) {
        this.playgroundConfigService = playgroundConfigService;
    }

    @GetMapping
    @Operation(summary = "List all config entries")
    public List<ConfigEntryResponse> listConfig() {
        return playgroundConfigService.listConfig();
    }

    @GetMapping("/{key}")
    @Operation(summary = "Get config entry by key")
    public ConfigEntryResponse getConfig(@PathVariable String key) {
        return playgroundConfigService.get(key);
    }

    @PutMapping("/{key}")
    @Operation(summary = "Create or update config entry")
    public ConfigEntryResponse upsertConfig(
            @PathVariable String key,
            @Valid @RequestBody UpsertConfigRequest request) {
        return playgroundConfigService.upsert(key, request.value());
    }
}
