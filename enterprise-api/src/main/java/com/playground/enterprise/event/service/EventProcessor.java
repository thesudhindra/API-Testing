package com.playground.enterprise.event.service;

import com.playground.enterprise.domain.EventStatus;
import com.playground.enterprise.event.entity.DomainEventEntity;
import com.playground.enterprise.event.repository.DomainEventRepository;
import com.playground.enterprise.webhook.service.WebhookDispatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class EventProcessor {

    private static final Logger log = LoggerFactory.getLogger(EventProcessor.class);

    private final DomainEventRepository domainEventRepository;
    private final WebhookDispatchService webhookDispatchService;

    public EventProcessor(
            DomainEventRepository domainEventRepository,
            WebhookDispatchService webhookDispatchService) {
        this.domainEventRepository = domainEventRepository;
        this.webhookDispatchService = webhookDispatchService;
    }

    @Scheduled(fixedDelayString = "${playground.enterprise.events.poll-interval-ms:2000}")
    @Transactional
    public void pollAndPublish() {
        List<DomainEventEntity> pending = domainEventRepository.findByStatusOrderByCreatedAtAsc(EventStatus.PENDING);
        for (DomainEventEntity event : pending) {
            try {
                event.setStatus(EventStatus.PUBLISHED);
                event.setPublishedAt(Instant.now());
                domainEventRepository.save(event);

                webhookDispatchService.enqueueForEvent(
                        event.getTenantId(),
                        event.getId(),
                        event.getEventType(),
                        event.getPayload());
            } catch (Exception e) {
                log.warn("Failed to publish event {}: {}", event.getId(), e.getMessage());
                event.setStatus(EventStatus.FAILED);
                domainEventRepository.save(event);
            }
        }
    }
}
