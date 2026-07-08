package com.playground.playground.fault.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.common.exception.ResourceNotFoundException;
import com.playground.playground.config.PlaygroundProperties;
import com.playground.playground.domain.FaultTargetService;
import com.playground.playground.fault.dto.FaultRuleRequest;
import com.playground.playground.fault.dto.FaultRuleResponse;
import com.playground.playground.fault.entity.FaultRuleEntity;
import com.playground.playground.fault.repository.FaultRuleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FaultInjectionService {

    private final FaultRuleRepository repository;
    private final PlaygroundProperties properties;
    private final ObjectMapper objectMapper;

    public FaultInjectionService(
            FaultRuleRepository repository,
            PlaygroundProperties properties,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public FaultRuleResponse createRule(FaultRuleRequest request) {
        FaultRuleEntity entity = new FaultRuleEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setTargetService(request.targetService());
        entity.setPathPattern(request.pathPattern());
        entity.setFaultType(request.faultType());
        entity.setConfigJson(toJson(request.config()));
        entity.setEnabled(true);
        entity.setExpiresAt(Instant.now().plusSeconds(properties.getFaultTtlMinutes() * 60L));
        entity.setCreatedAt(Instant.now());
        return toResponse(repository.save(entity));
    }

    public List<FaultRuleResponse> listRules(Boolean enabledOnly) {
        List<FaultRuleEntity> rules = Boolean.TRUE.equals(enabledOnly)
                ? repository.findByEnabledTrue()
                : repository.findAll();
        return rules.stream()
                .sorted(Comparator.comparing(FaultRuleEntity::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void disableRule(String id) {
        FaultRuleEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fault rule not found: " + id));
        entity.setEnabled(false);
        repository.save(entity);
    }

    public List<FaultRuleResponse> getActiveRulesForService(FaultTargetService service) {
        return repository.findByTargetServiceAndEnabledTrue(service).stream()
                .map(this::toResponse)
                .toList();
    }

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void cleanupExpiredRules() {
        List<FaultRuleEntity> expired = repository.findByEnabledTrueAndExpiresAtBefore(Instant.now());
        for (FaultRuleEntity rule : expired) {
            rule.setEnabled(false);
        }
        repository.saveAll(expired);
    }

    private String toJson(Map<String, Object> config) {
        try {
            return objectMapper.writeValueAsString(config != null ? config : Map.of());
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Invalid fault config JSON", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fromJson(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException ex) {
            return Map.of();
        }
    }

    private FaultRuleResponse toResponse(FaultRuleEntity entity) {
        return new FaultRuleResponse(
                entity.getId(),
                entity.getTargetService(),
                entity.getPathPattern(),
                entity.getFaultType(),
                fromJson(entity.getConfigJson()),
                entity.isEnabled(),
                entity.getExpiresAt(),
                entity.getCreatedAt()
        );
    }
}
