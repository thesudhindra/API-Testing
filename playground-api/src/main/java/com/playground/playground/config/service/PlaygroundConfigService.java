package com.playground.playground.config.service;

import com.playground.common.exception.ResourceNotFoundException;
import com.playground.playground.config.dto.ConfigEntryResponse;
import com.playground.playground.config.entity.PlaygroundConfigEntity;
import com.playground.playground.config.repository.PlaygroundConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class PlaygroundConfigService {

    private final PlaygroundConfigRepository repository;

    public PlaygroundConfigService(PlaygroundConfigRepository repository) {
        this.repository = repository;
    }

    public List<ConfigEntryResponse> listConfig() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(PlaygroundConfigEntity::getConfigKey))
                .map(this::toResponse)
                .toList();
    }

    public ConfigEntryResponse get(String key) {
        return repository.findById(key)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Config key not found: " + key));
    }

    @Transactional
    public ConfigEntryResponse upsert(String key, String value) {
        PlaygroundConfigEntity entity = repository.findById(key).orElseGet(PlaygroundConfigEntity::new);
        entity.setConfigKey(key);
        entity.setConfigValue(value);
        entity.setUpdatedAt(Instant.now());
        return toResponse(repository.save(entity));
    }

    private ConfigEntryResponse toResponse(PlaygroundConfigEntity entity) {
        return new ConfigEntryResponse(entity.getConfigKey(), entity.getConfigValue(), entity.getUpdatedAt());
    }
}
