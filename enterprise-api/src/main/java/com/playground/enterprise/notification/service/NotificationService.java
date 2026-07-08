package com.playground.enterprise.notification.service;

import com.playground.enterprise.audit.service.EnterpriseAuditService;
import com.playground.enterprise.domain.NotificationStatus;
import com.playground.enterprise.event.service.EventOutboxService;
import com.playground.enterprise.notification.dto.CreateNotificationRequest;
import com.playground.enterprise.notification.dto.NotificationResponse;
import com.playground.enterprise.notification.entity.NotificationEntity;
import com.playground.enterprise.notification.repository.NotificationRepository;
import com.playground.common.dto.PageResponse;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EnterpriseAuditService auditService;
    private final EventOutboxService eventOutboxService;

    public NotificationService(
            NotificationRepository notificationRepository,
            EnterpriseAuditService auditService,
            EventOutboxService eventOutboxService) {
        this.notificationRepository = notificationRepository;
        this.auditService = auditService;
        this.eventOutboxService = eventOutboxService;
    }

    @Transactional
    public NotificationResponse create(String tenantId, String actorId, CreateNotificationRequest request) {
        Instant now = Instant.now();
        String notificationId = UUID.randomUUID().toString();

        NotificationEntity notification = new NotificationEntity();
        notification.setId(notificationId);
        notification.setTenantId(tenantId);
        notification.setPartyId(request.partyId());
        notification.setChannel(request.channel());
        notification.setSubject(request.subject());
        notification.setBody(request.body());
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setCreatedAt(now);
        notificationRepository.save(notification);

        auditService.record(tenantId, "NOTIFICATION", notificationId, "CREATED", actorId,
                "partyId=" + request.partyId() + ",channel=" + request.channel());
        eventOutboxService.publish(tenantId, "NOTIFICATION", notificationId, "NOTIFICATION_CREATED",
                "partyId=" + request.partyId());

        return toResponse(notification);
    }

    @Transactional(readOnly = true)
    public NotificationResponse get(String tenantId, String notificationId) {
        NotificationEntity notification = notificationRepository.findByIdAndTenantId(notificationId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        return toResponse(notification);
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> listForParty(String tenantId, String partyId, int page, int size) {
        Page<NotificationEntity> result = notificationRepository.findByTenantIdAndPartyId(
                tenantId, partyId, PageRequest.of(page, size));
        return new PageResponse<>(
                result.getContent().stream().map(NotificationService::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), null);
    }

    @Transactional
    public NotificationResponse markRead(String tenantId, String actorId, String notificationId) {
        NotificationEntity notification = notificationRepository.findByIdAndTenantId(notificationId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (notification.getStatus() == NotificationStatus.UNREAD) {
            Instant now = Instant.now();
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(now);
            notificationRepository.save(notification);

            auditService.record(tenantId, "NOTIFICATION", notificationId, "READ", actorId, null);
        }

        return toResponse(notification);
    }

    static NotificationResponse toResponse(NotificationEntity notification) {
        return new NotificationResponse(
                notification.getId(), notification.getPartyId(), notification.getChannel(),
                notification.getSubject(), notification.getBody(), notification.getStatus(),
                notification.getReadAt(), notification.getCreatedAt());
    }
}
