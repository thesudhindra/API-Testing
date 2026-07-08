package com.playground.enterprise.report.service;

import com.playground.enterprise.audit.service.EnterpriseAuditService;
import com.playground.enterprise.domain.ReportStatus;
import com.playground.enterprise.event.service.EventOutboxService;
import com.playground.enterprise.job.service.BackgroundJobService;
import com.playground.enterprise.report.dto.GenerateReportRequest;
import com.playground.enterprise.report.dto.ReportResponse;
import com.playground.enterprise.report.entity.ReportEntity;
import com.playground.enterprise.report.repository.ReportRepository;
import com.playground.common.dto.PageResponse;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final BackgroundJobService backgroundJobService;
    private final EnterpriseAuditService auditService;
    private final EventOutboxService eventOutboxService;

    public ReportService(
            ReportRepository reportRepository,
            BackgroundJobService backgroundJobService,
            EnterpriseAuditService auditService,
            EventOutboxService eventOutboxService) {
        this.reportRepository = reportRepository;
        this.backgroundJobService = backgroundJobService;
        this.auditService = auditService;
        this.eventOutboxService = eventOutboxService;
    }

    @Transactional
    public ReportResponse submitReport(String tenantId, String actorId, GenerateReportRequest request) {
        Instant now = Instant.now();
        String reportId = UUID.randomUUID().toString();

        ReportEntity report = new ReportEntity();
        report.setId(reportId);
        report.setTenantId(tenantId);
        report.setReportType(request.reportType());
        report.setParameters(request.parameters());
        report.setStatus(ReportStatus.PENDING);
        report.setRequestedBy(actorId);
        report.setCreatedAt(now);
        reportRepository.save(report);

        backgroundJobService.submit(tenantId, "GENERATE_REPORT", "reportId=" + reportId);

        auditService.record(tenantId, "REPORT", reportId, "SUBMITTED", actorId,
                "reportType=" + request.reportType());
        eventOutboxService.publish(tenantId, "REPORT", reportId, "REPORT_SUBMITTED",
                "reportType=" + request.reportType());

        return toResponse(report);
    }

    @Transactional(readOnly = true)
    public ReportResponse getReport(String tenantId, String reportId) {
        ReportEntity report = reportRepository.findByIdAndTenantId(reportId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        return toResponse(report);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> listReports(
            String tenantId, ReportStatus status, int page, int size) {
        Page<ReportEntity> result = status == null
                ? reportRepository.findByTenantId(tenantId, PageRequest.of(page, size))
                : reportRepository.findByTenantIdAndStatus(tenantId, status, PageRequest.of(page, size));
        return new PageResponse<>(
                result.getContent().stream().map(ReportService::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), null);
    }

    static ReportResponse toResponse(ReportEntity report) {
        return new ReportResponse(
                report.getId(), report.getReportType(), report.getParameters(),
                report.getStatus(), report.getResultLocation(), report.getRequestedBy(),
                report.getCreatedAt(), report.getCompletedAt());
    }
}
