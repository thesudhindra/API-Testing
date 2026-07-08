package com.playground.enterprise.event.service;

import com.playground.enterprise.domain.EventStatus;
import com.playground.enterprise.event.dto.EventResponse;
import com.playground.enterprise.event.entity.DomainEventEntity;
import com.playground.enterprise.event.repository.DomainEventRepository;
import com.playground.common.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class EventOutboxService {

    private final DomainEventRepository domainEventRepository;

    public EventOutboxService(DomainEventRepository domainEventRepository) {
        this.domainEventRepository = domainEventRepository;
    }

    @Transactional
    public EventResponse publish(
            String tenantId,
            String aggregateType,
            String aggregateId,
            String eventType,
            String payload) {
        DomainEventEntity event = new DomainEventEntity();
        event.setId(UUID.randomUUID().toString());
        event.setTenantId(tenantId);
        event.setAggregateType(aggregateType);
        event.setAggregateId(aggregateId);
        event.setEventType(eventType);
        event.setPayload(payload);
        event.setStatus(EventStatus.PENDING);
        event.setCreatedAt(Instant.now());

        return toResponse(domainEventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public PageResponse<EventResponse> listEvents(
            String tenantId, EventStatus status, int page, int size) {
        List<DomainEventEntity> tenantEvents = domainEventRepository.findAll().stream()
                .filter(event -> tenantId.equals(event.getTenantId()))
                .filter(event -> status == null || event.getStatus() == status)
                .sorted(Comparator.comparing(DomainEventEntity::getCreatedAt).reversed())
                .toList();

        int fromIndex = Math.min(page * size, tenantEvents.size());
        int toIndex = Math.min(fromIndex + size, tenantEvents.size());
        List<EventResponse> items = tenantEvents.subList(fromIndex, toIndex).stream()
                .map(EventOutboxService::toResponse)
                .toList();

        Page<DomainEventEntity> result = new PageImpl<>(
                tenantEvents.subList(fromIndex, toIndex),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")),
                tenantEvents.size());

        return new PageResponse<>(
                items,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                null);
    }

    static EventResponse toResponse(DomainEventEntity event) {
        return new EventResponse(
                event.getId(),
                event.getTenantId(),
                event.getAggregateType(),
                event.getAggregateId(),
                event.getEventType(),
                event.getPayload(),
                event.getStatus(),
                event.getPublishedAt(),
                event.getCreatedAt());
    }
}
