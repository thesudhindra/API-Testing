package com.playground.banking.idempotency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.banking.idempotency.entity.IdempotencyKeyEntity;
import com.playground.banking.idempotency.repository.IdempotencyKeyRepository;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ConflictException;
import com.playground.common.util.DigestSupport;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class IdempotencyService {

    private static final int TTL_HOURS = 24;

    private final IdempotencyKeyRepository repository;
    private final ObjectMapper objectMapper;

    public IdempotencyService(IdempotencyKeyRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public <T> IdempotencyOutcome<T> execute(
            String tenantId,
            String idempotencyKey,
            String operation,
            Object requestBody,
            Supplier<T> action,
            Class<T> responseType,
            HttpStatus successStatus) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new BadRequestException("Idempotency-Key header is required");
        }

        String requestHash = hashJson(requestBody);
        IdempotencyKeyEntity.Key key = new IdempotencyKeyEntity.Key(tenantId, idempotencyKey.trim());
        Optional<IdempotencyKeyEntity> existing = repository.findById(key);

        if (existing.isPresent()) {
            IdempotencyKeyEntity record = existing.get();
            if (!record.getRequestHash().equals(requestHash)) {
                throw new ConflictException("Idempotency-Key was already used with a different request body");
            }
            T body = deserialize(record.getResponseBody(), responseType);
            return new IdempotencyOutcome<>(record.getResponseStatus(), body, true);
        }

        T result = action.get();
        int status = successStatus.value();
        String responseJson = serialize(result);

        IdempotencyKeyEntity record = new IdempotencyKeyEntity();
        record.setTenantId(tenantId);
        record.setIdempotencyKey(idempotencyKey.trim());
        record.setOperation(operation);
        record.setRequestHash(requestHash);
        record.setResponseStatus(status);
        record.setResponseBody(responseJson);
        record.setCreatedAt(Instant.now());
        record.setExpiresAt(Instant.now().plusSeconds(TTL_HOURS * 3600L));
        repository.save(record);

        return new IdempotencyOutcome<>(status, result, false);
    }

    private String hashJson(Object requestBody) {
        try {
            return hash(objectMapper.writeValueAsString(requestBody));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to hash request body", ex);
        }
    }

    public static String hash(String payload) {
        return DigestSupport.sha256(payload);
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize idempotent response", ex);
        }
    }

    private <T> T deserialize(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to deserialize idempotent response", ex);
        }
    }

    public record IdempotencyOutcome<T>(int statusCode, T body, boolean replayed) {
    }
}
