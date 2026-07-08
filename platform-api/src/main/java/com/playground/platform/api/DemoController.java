package com.playground.platform.api;

import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.PlaygroundException;
import com.playground.common.exception.ResourceNotFoundException;
import com.playground.common.api.ProblemTypes;
import com.playground.platform.api.dto.DemoValidationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/demo")
@Tag(name = "Demo", description = "Teaching endpoints for RFC 7807 and validation (Phase 1)")
@SecurityRequirement(name = "basicAuth")
public class DemoController {

    @GetMapping("/errors/bad-request")
    @Operation(
            summary = "Trigger 400 Bad Request",
            extensions = @Extension(name = "x-learning-objective", properties = @ExtensionProperty(name = "id", value = "LO-REST-20")))
    public void badRequest() {
        throw new BadRequestException("Deliberate bad request for API testing practice");
    }

    @GetMapping("/errors/not-found")
    @Operation(
            summary = "Trigger 404 Not Found",
            extensions = @Extension(name = "x-learning-objective", properties = @ExtensionProperty(name = "id", value = "LO-REST-20")))
    public void notFound() {
        throw new ResourceNotFoundException("Deliberate not-found for API testing practice");
    }

    @GetMapping("/errors/internal")
    @Operation(
            summary = "Trigger 500 Internal Server Error",
            extensions = @Extension(name = "x-learning-objective", properties = @ExtensionProperty(name = "id", value = "LO-REST-20")))
    public void internalError() {
        throw new PlaygroundException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProblemTypes.INTERNAL,
                "Internal Server Error",
                "Deliberate internal error for API testing practice");
    }

    @PostMapping("/validate")
    @Operation(
            summary = "Validation teaching endpoint",
            description = "Returns 422 with field errors when validation fails.",
            extensions = @Extension(name = "x-learning-objective", properties = @ExtensionProperty(name = "id", value = "LO-REST-20")))
    public Map<String, String> validate(@Valid @RequestBody DemoValidationRequest request) {
        return Map.of("message", "Validation passed", "name", request.name());
    }
}
