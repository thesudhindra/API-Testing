package com.playground.enterprise.api;

import com.playground.enterprise.domain.JobStatus;
import com.playground.enterprise.job.dto.JobResponse;
import com.playground.enterprise.job.dto.SubmitJobRequest;
import com.playground.enterprise.job.service.BackgroundJobService;
import com.playground.common.security.TenantAccess;
import com.playground.common.dto.PageResponse;
import com.playground.common.api.ApiHeaders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/jobs")
@Tag(name = "Jobs", description = "Background job management")
@SecurityRequirement(name = "bearerAuth")
public class JobController {

    private final BackgroundJobService backgroundJobService;

    public JobController(BackgroundJobService backgroundJobService) {
        this.backgroundJobService = backgroundJobService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit background job")
    public JobResponse submitJob(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody SubmitJobRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        String jobId = backgroundJobService.submit(tenantId, request);
        return backgroundJobService.getJob(tenantId, jobId);
    }

    @GetMapping("/{jobId}")
    @Operation(summary = "Get job by id")
    public JobResponse getJob(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String jobId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        return backgroundJobService.getJob(tenantId, jobId);
    }

    @GetMapping
    @Operation(summary = "List jobs")
    public PageResponse<JobResponse> listJobs(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam(required = false) JobStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        return backgroundJobService.listJobs(tenantId, status, page, size);
    }
}
