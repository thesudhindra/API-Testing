package com.playground.playground.api;

import com.playground.playground.config.OpenApiConfig;
import com.playground.playground.testdata.dto.TestDataGenerateRequest;
import com.playground.playground.testdata.dto.TestDataHandleResponse;
import com.playground.playground.testdata.service.TestDataService;
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
@RequestMapping("/v1/playground/test-data")
@Tag(name = "Test Data", description = "Lab test data generation")
@SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
public class TestDataController {

    private final TestDataService testDataService;

    public TestDataController(TestDataService testDataService) {
        this.testDataService = testDataService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Generate test data for a namespace")
    public List<TestDataHandleResponse> generate(@Valid @RequestBody TestDataGenerateRequest request) {
        return testDataService.generate(request.namespace(), request.profile());
    }

    @GetMapping
    @Operation(summary = "List test data handles")
    public List<TestDataHandleResponse> listHandles(@RequestParam String namespace) {
        return testDataService.listHandles(namespace);
    }

    @DeleteMapping("/{namespace}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete test data handles for namespace")
    public void deleteNamespace(@PathVariable String namespace) {
        testDataService.deleteNamespace(namespace);
    }
}
