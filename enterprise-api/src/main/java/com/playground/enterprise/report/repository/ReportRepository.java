package com.playground.enterprise.report.repository;

import com.playground.enterprise.domain.ReportStatus;
import com.playground.enterprise.report.entity.ReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity, String> {

    Optional<ReportEntity> findByIdAndTenantId(String id, String tenantId);

    Page<ReportEntity> findByTenantId(String tenantId, Pageable pageable);

    Page<ReportEntity> findByTenantIdAndStatus(String tenantId, ReportStatus status, Pageable pageable);
}
