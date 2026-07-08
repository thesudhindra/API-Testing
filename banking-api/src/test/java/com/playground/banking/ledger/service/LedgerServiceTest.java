package com.playground.banking.ledger.service;

import com.playground.banking.account.entity.AccountEntity;
import com.playground.banking.account.repository.AccountRepository;
import com.playground.banking.domain.AccountStatus;
import com.playground.banking.domain.LedgerEntryType;
import com.playground.banking.ledger.entity.LedgerEntryEntity;
import com.playground.banking.ledger.repository.LedgerEntryRepository;
import com.playground.common.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LedgerServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @InjectMocks
    private LedgerService ledgerService;

    @Test
    void debitRejectsInsufficientFunds() {
        AccountEntity account = activeAccount("500.0000");
        when(accountRepository.findByIdAndTenantId("acct-1", "tenant-demo")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> ledgerService.debit(
                "tenant-demo", "acct-1", new BigDecimal("600.00"), "GBP", "txn-1"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Insufficient funds");
    }

    @Test
    void creditUpdatesBalanceAndCreatesEntry() {
        AccountEntity account = activeAccount("500.0000");
        when(accountRepository.findByIdAndTenantId("acct-1", "tenant-demo")).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LedgerService.LedgerPostResult result = ledgerService.credit(
                "tenant-demo", "acct-1", new BigDecimal("100.00"), "GBP", "txn-1");

        assertThat(result.balanceAfter()).isEqualByComparingTo("600.0000");

        ArgumentCaptor<LedgerEntryEntity> entryCaptor = ArgumentCaptor.forClass(LedgerEntryEntity.class);
        verify(ledgerEntryRepository).save(entryCaptor.capture());
        assertThat(entryCaptor.getValue().getEntryType()).isEqualTo(LedgerEntryType.CREDIT);
    }

    private static AccountEntity activeAccount(String balance) {
        AccountEntity account = new AccountEntity();
        account.setId("acct-1");
        account.setTenantId("tenant-demo");
        account.setCurrency("GBP");
        account.setStatus(AccountStatus.ACTIVE);
        account.setLedgerBalance(new BigDecimal(balance));
        account.setAvailableBalance(new BigDecimal(balance));
        return account;
    }
}
