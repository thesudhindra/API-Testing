package com.playground.banking.idempotency.repository;

import com.playground.banking.idempotency.entity.IdempotencyKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, IdempotencyKeyEntity.Key> {
}
