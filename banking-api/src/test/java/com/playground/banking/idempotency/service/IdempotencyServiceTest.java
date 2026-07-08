package com.playground.banking.idempotency.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.banking.idempotency.entity.IdempotencyKeyEntity;
import com.playground.banking.idempotency.repository.IdempotencyKeyRepository;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceTest {

    @Mock
    private IdempotencyKeyRepository repository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private IdempotencyService idempotencyService;

    @Test
    void executeRequiresIdempotencyKey() {
        assertThatThrownBy(() -> idempotencyService.execute(
                "tenant-demo", null, "OP", new Object(), () -> "x", String.class, HttpStatus.CREATED))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void executeReplaysStoredResponse() {
        IdempotencyKeyEntity.Key key = new IdempotencyKeyEntity.Key("tenant-demo", "key-1");
        IdempotencyKeyEntity existing = new IdempotencyKeyEntity();
        existing.setTenantId("tenant-demo");
        existing.setIdempotencyKey("key-1");
        existing.setRequestHash(IdempotencyService.hash("{\"amount\":10}"));
        existing.setResponseStatus(201);
        existing.setResponseBody("\"pay-1\"");

        when(repository.findById(key)).thenReturn(Optional.of(existing));

        IdempotencyService.IdempotencyOutcome<String> outcome = idempotencyService.execute(
                "tenant-demo", "key-1", "CREATE_PAYMENT", java.util.Map.of("amount", 10),
                () -> { throw new IllegalStateException("should not run"); },
                String.class, HttpStatus.CREATED);

        assertThat(outcome.replayed()).isTrue();
        assertThat(outcome.body()).isEqualTo("pay-1");
        verify(repository, never()).save(any());
    }

    @Test
    void executeRejectsConflictingRequestBody() {
        IdempotencyKeyEntity.Key key = new IdempotencyKeyEntity.Key("tenant-demo", "key-1");
        IdempotencyKeyEntity existing = new IdempotencyKeyEntity();
        existing.setRequestHash(IdempotencyService.hash("{\"amount\":10}"));

        when(repository.findById(key)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> idempotencyService.execute(
                "tenant-demo", "key-1", "CREATE_PAYMENT", java.util.Map.of("amount", 99),
                () -> "x", String.class, HttpStatus.CREATED))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void executePersistsNewResponse() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        IdempotencyService.IdempotencyOutcome<String> outcome = idempotencyService.execute(
                "tenant-demo", "key-new", "CREATE_PAYMENT", java.util.Map.of("amount", 10),
                () -> "pay-99", String.class, HttpStatus.CREATED);

        assertThat(outcome.replayed()).isFalse();
        assertThat(outcome.body()).isEqualTo("pay-99");

        ArgumentCaptor<IdempotencyKeyEntity> captor = ArgumentCaptor.forClass(IdempotencyKeyEntity.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getOperation()).isEqualTo("CREATE_PAYMENT");
        assertThat(captor.getValue().getResponseStatus()).isEqualTo(201);
    }
}
