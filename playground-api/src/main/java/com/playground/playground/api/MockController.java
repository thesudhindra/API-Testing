package com.playground.playground.api;

import com.playground.common.exception.ResourceNotFoundException;
import com.playground.playground.config.OpenApiConfig;
import com.playground.playground.mock.dto.MockEndpointRequest;
import com.playground.playground.mock.dto.MockEndpointResponse;
import com.playground.playground.mock.dto.MockResponse;
import com.playground.playground.mock.service.MockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Mocks", description = "Mock endpoint management and public mock server")
public class MockController {

    private final MockService mockService;

    public MockController(MockService mockService) {
        this.mockService = mockService;
    }

    @GetMapping("/v1/playground/mocks")
    @Operation(summary = "List mock endpoints")
    @SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
    public List<MockEndpointResponse> listMocks() {
        return mockService.listEndpoints();
    }

    @PostMapping("/v1/playground/mocks")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create mock endpoint")
    @SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
    public MockEndpointResponse createMock(@Valid @RequestBody MockEndpointRequest request) {
        return mockService.createEndpoint(request);
    }

    @RequestMapping(path = "/v1/mocks/**")
    @Operation(summary = "Serve mock response (public)")
    public ResponseEntity<String> handleMockRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        MockResponse response;
        try {
            response = mockService.handleRequest(request.getMethod(), path);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"mock not found\"}");
        }
        return ResponseEntity.status(response.statusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.body());
    }
}
