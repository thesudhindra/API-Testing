package com.playground.enterprise.job.repository;

import com.playground.enterprise.domain.JobStatus;
import com.playground.enterprise.job.entity.BackgroundJobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BackgroundJobRepository extends JpaRepository<BackgroundJobEntity, String> {

    Optional<BackgroundJobEntity> findByIdAndTenantId(String id, String tenantId);

    List<BackgroundJobEntity> findByStatusIn(Collection<JobStatus> statuses);

    Page<BackgroundJobEntity> findByTenantIdAndStatusIn(
            String tenantId, Collection<JobStatus> statuses, Pageable pageable);
}
