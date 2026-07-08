package com.playground.banking.card.repository;

import com.playground.banking.card.entity.CardAuthorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardAuthorizationRepository extends JpaRepository<CardAuthorizationEntity, String> {
}
