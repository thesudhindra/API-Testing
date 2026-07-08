package com.playground.enterprise.job.service;

import com.playground.enterprise.domain.JobStatus;
import com.playground.enterprise.job.dto.JobResponse;
import com.playground.enterprise.job.dto.SubmitJobRequest;
import com.playground.enterprise.job.entity.BackgroundJobEntity;
import com.playground.enterprise.job.repository.BackgroundJobRepository;
import com.playground.common.dto.PageResponse;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class BackgroundJobService {

    private final BackgroundJobRepository backgroundJobRepository;

    public BackgroundJobService(BackgroundJobRepository backgroundJobRepository) {
        this.backgroundJobRepository = backgroundJobRepository;
    }

    @Transactional
    public String submit(String tenantId, String jobType, String payload) {
        BackgroundJobEntity job = new BackgroundJobEntity();
        job.setId(UUID.randomUUID().toString());
        job.setTenantId(tenantId);
        job.setJobType(jobType);
        job.setPayload(payload);
        job.setStatus(JobStatus.PENDING);
        job.setProgress(0);
        job.setRetryCount(0);
        job.setMaxRetries(3);
        Instant now = Instant.now();
        job.setCreatedAt(now);
        job.setUpdatedAt(now);

        backgroundJobRepository.save(job);
        return job.getId();
    }

    @Transactional
    public String submit(String tenantId, SubmitJobRequest request) {
        return submit(tenantId, request.jobType(), request.payload());
    }

    @Transactional(readOnly = true)
    public JobResponse getJob(String tenantId, String jobId) {
        BackgroundJobEntity job = backgroundJobRepository.findByIdAndTenantId(jobId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return toResponse(job);
    }

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> listJobs(String tenantId, JobStatus status, int page, int size) {
        List<JobStatus> statuses = status != null
                ? List.of(status)
                : Arrays.asList(JobStatus.values());

        Page<BackgroundJobEntity> result = backgroundJobRepository.findByTenantIdAndStatusIn(
                tenantId, statuses, PageRequest.of(page, size));

        return new PageResponse<>(
                result.getContent().stream().map(BackgroundJobService::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                null);
    }

    static JobResponse toResponse(BackgroundJobEntity job) {
        return new JobResponse(
                job.getId(),
                job.getTenantId(),
                job.getJobType(),
                job.getPayload(),
                job.getStatus(),
                job.getProgress(),
                job.getResult(),
                job.getErrorMessage(),
                job.getRetryCount(),
                job.getMaxRetries(),
                job.getCreatedAt(),
                job.getUpdatedAt());
    }
}
