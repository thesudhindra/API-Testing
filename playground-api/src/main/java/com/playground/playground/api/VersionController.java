package com.playground.playground.api;

import com.playground.common.web.VersionSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Platform", description = "Service version metadata")
public class VersionController {

    private static final String FALLBACK_NAME = "playground-api";

    private final BuildProperties buildProperties;
    private final String applicationVersion;

    public VersionController(
            @Autowired(required = false) BuildProperties buildProperties,
            @Value("${playground.api.version:0.1.0-SNAPSHOT}") String applicationVersion) {
        this.buildProperties = buildProperties;
        this.applicationVersion = applicationVersion;
    }

    @GetMapping("/version")
    @Operation(summary = "Application version")
    public ResponseEntity<Map<String, Object>> version() {
        return ResponseEntity.ok(VersionSupport.buildResponse(buildProperties, FALLBACK_NAME, applicationVersion));
    }
}
