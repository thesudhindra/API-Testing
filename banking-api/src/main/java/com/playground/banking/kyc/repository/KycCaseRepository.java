package com.playground.banking.kyc.repository;

import com.playground.banking.domain.KycStatus;
import com.playground.banking.kyc.entity.KycCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KycCaseRepository extends JpaRepository<KycCaseEntity, String> {

    @Query("""
            SELECT k FROM KycCaseEntity k
            WHERE k.partyId = :partyId
              AND EXISTS (
                  SELECT 1 FROM PartyEntity p
                  WHERE p.id = k.partyId AND p.tenantId = :tenantId)
            """)
    List<KycCaseEntity> findByTenantIdAndPartyId(@Param("tenantId") String tenantId, @Param("partyId") String partyId);

    @Query("""
            SELECT k FROM KycCaseEntity k
            WHERE k.id = :caseId
              AND EXISTS (
                  SELECT 1 FROM PartyEntity p
                  WHERE p.id = k.partyId AND p.tenantId = :tenantId)
            """)
    Optional<KycCaseEntity> findByIdAndTenantId(@Param("caseId") String caseId, @Param("tenantId") String tenantId);

    Optional<KycCaseEntity> findFirstByPartyIdAndStatusOrderByCreatedAtDesc(String partyId, KycStatus status);
}
