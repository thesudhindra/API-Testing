package com.playground.banking.card.repository;

import com.playground.banking.card.entity.CardAuthorizationEntity;
import com.playground.banking.card.entity.CardEntity;
import com.playground.banking.domain.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<CardEntity, String> {

    List<CardEntity> findByTenantIdAndPartyIdAndStatusNot(String tenantId, String partyId, CardStatus status);

    Optional<CardEntity> findByIdAndTenantId(String id, String tenantId);
}
