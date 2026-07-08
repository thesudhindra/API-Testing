package com.playground.banking.account.repository;

import com.playground.banking.account.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    Page<AccountEntity> findByTenantIdAndPartyId(String tenantId, String partyId, Pageable pageable);

    Optional<AccountEntity> findByIdAndTenantId(String id, String tenantId);
}
