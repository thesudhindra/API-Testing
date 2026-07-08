package com.playground.banking.account.service;

import com.playground.banking.account.dto.CreateAccountRequest;
import com.playground.banking.account.repository.AccountRepository;
import com.playground.banking.customer.service.PartyService;
import com.playground.banking.kyc.service.KycService;
import com.playground.common.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PartyService partyService;

    @Mock
    private KycService kycService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountRequiresApprovedKyc() {
        doNothing().when(partyService).requirePartyInTenant("tenant-demo", "party-1");
        when(kycService.hasApprovedKyc("tenant-demo", "party-1")).thenReturn(false);

        assertThatThrownBy(() -> accountService.createAccount(
                "tenant-demo",
                new CreateAccountRequest("party-1", "GBP", "CURRENT")))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("approved KYC");
    }
}
