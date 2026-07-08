package com.playground.enterprise.job.service;

import com.playground.enterprise.domain.JobStatus;
import com.playground.enterprise.job.entity.BackgroundJobEntity;
import com.playground.enterprise.job.repository.BackgroundJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class JobProcessor {

    private static final Logger log = LoggerFactory.getLogger(JobProcessor.class);

    private static final String REPORT_GENERATION = "REPORT_GENERATION";
    private static final String AML_BATCH = "AML_BATCH";

    private final BackgroundJobRepository backgroundJobRepository;

    public JobProcessor(BackgroundJobRepository backgroundJobRepository) {
        this.backgroundJobRepository = backgroundJobRepository;
    }

    @Scheduled(fixedDelayString = "${playground.enterprise.jobs.poll-interval-ms:3000}")
    public void processJobs() {
        List<BackgroundJobEntity> jobs = backgroundJobRepository.findByStatusIn(
                List.of(JobStatus.PENDING, JobStatus.RUNNING));
        for (BackgroundJobEntity job : jobs) {
            processJob(job.getId());
        }
    }

    @Transactional
    public void processJob(String jobId) {
        BackgroundJobEntity job = backgroundJobRepository.findById(jobId).orElse(null);
        if (job == null) {
            return;
        }
        if (job.getStatus() != JobStatus.PENDING && job.getStatus() != JobStatus.RUNNING) {
            return;
        }

        try {
            if (job.getStatus() == JobStatus.PENDING) {
                job.setStatus(JobStatus.RUNNING);
                job.setUpdatedAt(Instant.now());
                backgroundJobRepository.save(job);
            }

            switch (job.getJobType()) {
                case REPORT_GENERATION -> processReportGeneration(job);
                case AML_BATCH -> processAmlBatch(job);
                default -> {
                    job.setStatus(JobStatus.FAILED);
                    job.setErrorMessage("Unsupported job type: " + job.getJobType());
                    job.setUpdatedAt(Instant.now());
                    backgroundJobRepository.save(job);
                }
            }
        } catch (OptimisticLockingFailureException e) {
            handleFailure(job, "Optimistic lock conflict");
        } catch (Exception e) {
            handleFailure(job, e.getMessage());
        }
    }

    private void processReportGeneration(BackgroundJobEntity job) {
        int nextProgress = Math.min(job.getProgress() + 34, 100);
        job.setProgress(nextProgress);
        job.setUpdatedAt(Instant.now());

        if (nextProgress >= 100) {
            job.setStatus(JobStatus.COMPLETED);
            job.setResult("{\"result_location\":\"s3://reports/" + job.getTenantId() + "/"
                    + UUID.randomUUID() + ".pdf\"}");
        }

        backgroundJobRepository.save(job);
    }

    private void processAmlBatch(BackgroundJobEntity job) {
        job.setStatus(JobStatus.COMPLETED);
        job.setProgress(100);
        job.setResult("{\"status\":\"batch_completed\"}");
        job.setUpdatedAt(Instant.now());
        backgroundJobRepository.save(job);
    }

    private void handleFailure(BackgroundJobEntity job, String message) {
        log.warn("Job {} failed: {}", job.getId(), message);
        job.setRetryCount(job.getRetryCount() + 1);
        job.setErrorMessage(message);
        job.setUpdatedAt(Instant.now());

        if (job.getRetryCount() >= job.getMaxRetries()) {
            job.setStatus(JobStatus.FAILED);
        } else {
            job.setStatus(JobStatus.PENDING);
        }

        try {
            backgroundJobRepository.save(job);
        } catch (OptimisticLockingFailureException e) {
            log.debug("Optimistic lock conflict while saving failure for job {}", job.getId());
        }
    }
}
