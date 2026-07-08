package com.playground.banking.kyc.service;

import com.playground.banking.customer.service.PartyService;
import com.playground.banking.domain.KycStatus;
import com.playground.banking.kyc.dto.ReviewKycRequest;
import com.playground.banking.kyc.dto.SubmitKycRequest;
import com.playground.banking.kyc.entity.KycCaseEntity;
import com.playground.banking.kyc.repository.KycCaseRepository;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ConflictException;
import com.playground.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KycServiceTest {

    @Mock
    private KycCaseRepository kycCaseRepository;

    @Mock
    private PartyService partyService;

    @InjectMocks
    private KycService kycService;

    @Test
    void submitCreatesOpenCaseWhenNoOpenCaseExists() {
        doNothing().when(partyService).requirePartyInTenant("tenant-demo", "party-1");
        when(kycCaseRepository.findByTenantIdAndPartyId("tenant-demo", "party-1")).thenReturn(List.of());
        when(kycCaseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = kycService.submit("tenant-demo", new SubmitKycRequest("party-1"));

        assertThat(response.status()).isEqualTo(KycStatus.OPEN);
        assertThat(response.level()).isEqualTo("STANDARD");
        verify(kycCaseRepository).save(any(KycCaseEntity.class));
    }

    @Test
    void submitRejectsWhenOpenCaseExists() {
        KycCaseEntity open = new KycCaseEntity();
        open.setStatus(KycStatus.OPEN);
        doNothing().when(partyService).requirePartyInTenant("tenant-demo", "party-1");
        when(kycCaseRepository.findByTenantIdAndPartyId("tenant-demo", "party-1")).thenReturn(List.of(open));

        assertThatThrownBy(() -> kycService.submit("tenant-demo", new SubmitKycRequest("party-1")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void reviewApprovesCase() {
        KycCaseEntity kycCase = new KycCaseEntity();
        kycCase.setId("kyc-1");
        kycCase.setPartyId("party-1");
        kycCase.setStatus(KycStatus.OPEN);
        kycCase.setLevel("STANDARD");
        kycCase.setCreatedAt(Instant.now());
        kycCase.setUpdatedAt(Instant.now());
        when(kycCaseRepository.findByIdAndTenantId("kyc-1", "tenant-demo")).thenReturn(Optional.of(kycCase));
        when(kycCaseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = kycService.review("tenant-demo", "kyc-1", new ReviewKycRequest(KycStatus.APPROVED, "ok"));

        assertThat(response.status()).isEqualTo(KycStatus.APPROVED);
        assertThat(response.decisionReason()).isEqualTo("ok");
    }

    @Test
    void reviewRejectsInvalidStatus() {
        assertThatThrownBy(() -> kycService.review("tenant-demo", "kyc-1", new ReviewKycRequest(KycStatus.OPEN, null)))
                .isInstanceOf(BadRequestException.class);
    }
}
