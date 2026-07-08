package com.playground.playground.reset.service;

import com.playground.playground.domain.ResetScope;
import com.playground.playground.reset.dto.ResetResponse;
import com.playground.playground.reset.entity.ResetAuditEntity;
import com.playground.playground.reset.repository.ResetAuditRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class ResetService {

    private final JdbcTemplate jdbcTemplate;
    private final ResetAuditRepository resetAuditRepository;

    public ResetService(JdbcTemplate jdbcTemplate, ResetAuditRepository resetAuditRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.resetAuditRepository = resetAuditRepository;
    }

    @Transactional
    public ResetResponse reset(ResetScope scope) {
        return switch (scope) {
            case PLAYGROUND -> audit(scope, resetPlayground());
            case BANKING -> audit(scope, resetBanking());
            case ENTERPRISE -> audit(scope, resetEnterprise());
            case ALL -> audit(scope, resetPlayground() + "; " + resetBanking() + "; " + resetEnterprise());
        };
    }

    private String resetPlayground() {
        jdbcTemplate.update("DELETE FROM playground.test_data_handles");
        jdbcTemplate.update("DELETE FROM playground.scenario_runs");
        jdbcTemplate.update("DELETE FROM playground.fault_rules");
        jdbcTemplate.update("DELETE FROM playground.mock_endpoints");
        jdbcTemplate.update("DELETE FROM playground.reset_audit");

        jdbcTemplate.update("""
                INSERT INTO playground.mock_endpoints (id, path, http_method, status_code, response_body, delay_ms) VALUES
                ('mock-aml-clear', '/v1/mocks/aml/screen/clear', 'POST', 200,
                 '{"result":"CLEAR","matchScore":0}', 0)
                """);
        jdbcTemplate.update("""
                INSERT INTO playground.mock_endpoints (id, path, http_method, status_code, response_body, delay_ms) VALUES
                ('mock-aml-review', '/v1/mocks/aml/screen/review', 'POST', 200,
                 '{"result":"REVIEW","matchScore":75}', 200)
                """);
        return "Playground lab tables cleared (config retained)";
    }

    private String resetBanking() {
        jdbcTemplate.update("DELETE FROM banking.statement_lines");
        jdbcTemplate.update("DELETE FROM banking.statements");
        jdbcTemplate.update("DELETE FROM banking.ledger_entries");
        jdbcTemplate.update("DELETE FROM banking.card_authorizations");
        jdbcTemplate.update("DELETE FROM banking.fx_conversions");
        jdbcTemplate.update("DELETE FROM banking.payments");
        jdbcTemplate.update("DELETE FROM banking.transfers");
        jdbcTemplate.update("DELETE FROM banking.financial_transactions");
        jdbcTemplate.update("DELETE FROM banking.fx_quotes");
        jdbcTemplate.update("DELETE FROM banking.audit_events");
        jdbcTemplate.update("DELETE FROM banking.idempotency_keys");

        jdbcTemplate.update("""
                UPDATE banking.accounts SET ledger_balance = 5000.0000, available_balance = 5000.0000, version = 0
                WHERE id = 'acct-customer-1'
                """);
        jdbcTemplate.update("""
                UPDATE banking.accounts SET ledger_balance = 1000.0000, available_balance = 1000.0000, version = 0
                WHERE id = 'acct-customer-2'
                """);
        jdbcTemplate.update("""
                UPDATE banking.accounts SET ledger_balance = 200.0000, available_balance = 200.0000, version = 0
                WHERE id = 'acct-customer-eur'
                """);
        return "Banking transactional data cleared and seed balances restored";
    }

    private String resetEnterprise() {
        jdbcTemplate.update("DELETE FROM enterprise.webhook_deliveries");
        jdbcTemplate.update("DELETE FROM enterprise.background_jobs");
        jdbcTemplate.update("DELETE FROM enterprise.domain_events");
        jdbcTemplate.update("DELETE FROM enterprise.fraud_alerts");
        jdbcTemplate.update("DELETE FROM enterprise.aml_screenings");
        jdbcTemplate.update("DELETE FROM enterprise.loan_repayments");
        jdbcTemplate.update("DELETE FROM enterprise.recurring_deposit_installments");
        jdbcTemplate.update("DELETE FROM enterprise.notifications");
        jdbcTemplate.update("DELETE FROM enterprise.reports");
        jdbcTemplate.update("DELETE FROM enterprise.documents");
        jdbcTemplate.update("DELETE FROM enterprise.audit_events");
        return "Enterprise transactional data cleared";
    }

    private ResetResponse audit(ResetScope scope, String details) {
        ResetAuditEntity entity = new ResetAuditEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setScope(scope);
        entity.setStatus("COMPLETED");
        entity.setDetails(details);
        entity.setCreatedAt(Instant.now());
        ResetAuditEntity saved = resetAuditRepository.save(entity);
        return new ResetResponse(saved.getId(), saved.getScope(), saved.getStatus(), saved.getDetails(), saved.getCreatedAt());
    }
}
