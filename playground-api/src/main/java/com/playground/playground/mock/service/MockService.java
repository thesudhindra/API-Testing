package com.playground.playground.mock.service;

import com.playground.common.exception.ConflictException;
import com.playground.common.exception.ResourceNotFoundException;
import com.playground.playground.mock.dto.MockEndpointRequest;
import com.playground.playground.mock.dto.MockEndpointResponse;
import com.playground.playground.mock.dto.MockResponse;
import com.playground.playground.mock.entity.MockEndpointEntity;
import com.playground.playground.mock.repository.MockEndpointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class MockService {

    private final MockEndpointRepository repository;

    public MockService(MockEndpointRepository repository) {
        this.repository = repository;
    }

    public List<MockEndpointResponse> listEndpoints() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(MockEndpointEntity::getPath))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MockEndpointResponse createEndpoint(MockEndpointRequest request) {
        String method = request.httpMethod().toUpperCase();
        repository.findByPathAndHttpMethod(request.path(), method).ifPresent(existing -> {
            throw new ConflictException("Mock endpoint already exists for " + method + " " + request.path());
        });
        MockEndpointEntity entity = new MockEndpointEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setPath(request.path());
        entity.setHttpMethod(method);
        entity.setStatusCode(request.statusCode());
        entity.setResponseBody(request.responseBody());
        entity.setDelayMs(request.delayMs() != null ? request.delayMs() : 0);
        entity.setEnabled(request.enabled() == null || request.enabled());
        entity.setCreatedAt(Instant.now());
        return toResponse(repository.save(entity));
    }

    public MockResponse handleRequest(String method, String path) {
        String normalizedMethod = method.toUpperCase();
        MockEndpointEntity entity = repository.findByPathAndHttpMethod(path, normalizedMethod)
                .filter(MockEndpointEntity::isEnabled)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No mock configured for " + normalizedMethod + " " + path));
        if (entity.getDelayMs() > 0) {
            try {
                Thread.sleep(entity.getDelayMs());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        return new MockResponse(entity.getStatusCode(), entity.getResponseBody(), entity.getDelayMs());
    }

    private MockEndpointResponse toResponse(MockEndpointEntity entity) {
        return new MockEndpointResponse(
                entity.getId(),
                entity.getPath(),
                entity.getHttpMethod(),
                entity.getStatusCode(),
                entity.getResponseBody(),
                entity.getDelayMs(),
                entity.isEnabled(),
                entity.getCreatedAt()
        );
    }
}
