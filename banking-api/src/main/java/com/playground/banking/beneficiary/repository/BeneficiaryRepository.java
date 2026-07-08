package com.playground.banking.beneficiary.repository;

import com.playground.banking.beneficiary.entity.BeneficiaryEntity;
import com.playground.banking.domain.BeneficiaryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository extends JpaRepository<BeneficiaryEntity, String> {

    List<BeneficiaryEntity> findByTenantIdAndPartyIdAndStatusNot(
            String tenantId, String partyId, BeneficiaryStatus status);

    Optional<BeneficiaryEntity> findByIdAndTenantId(String id, String tenantId);
}
