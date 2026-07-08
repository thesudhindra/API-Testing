package com.playground.playground.testdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.common.exception.BadRequestException;
import com.playground.playground.domain.TestDataEntityType;
import com.playground.playground.testdata.dto.TestDataHandleResponse;
import com.playground.playground.testdata.entity.TestDataHandleEntity;
import com.playground.playground.testdata.repository.TestDataHandleRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TestDataService {

    private static final String TENANT_DEMO = "tenant-demo";

    private final TestDataHandleRepository repository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public TestDataService(
            TestDataHandleRepository repository,
            JdbcTemplate jdbcTemplate,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public List<TestDataHandleResponse> generate(String namespace, String profile) {
        return switch (profile) {
            case "retail-customer" -> generateRetailCustomer(namespace);
            case "high-balance" -> generateHighBalance(namespace);
            case "aml-ready" -> generateAmlReady(namespace);
            default -> throw new BadRequestException("Unknown test data profile: " + profile);
        };
    }

    public List<TestDataHandleResponse> listHandles(String namespace) {
        return repository.findByNamespace(namespace).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteNamespace(String namespace) {
        repository.deleteByNamespace(namespace);
    }

    private List<TestDataHandleResponse> generateRetailCustomer(String namespace) {
        List<TestDataHandleResponse> handles = new ArrayList<>();
        handles.add(saveHandle(namespace, TestDataEntityType.PARTY, "party-customer-1",
                Map.of("profile", "retail-customer", "seeded", true)));
        handles.add(saveHandle(namespace, TestDataEntityType.ACCOUNT, "acct-customer-1",
                Map.of("profile", "retail-customer", "currency", "GBP")));
        return handles;
    }

    private List<TestDataHandleResponse> generateHighBalance(String namespace) {
        String partyId = UUID.randomUUID().toString();
        String accountId = UUID.randomUUID().toString();
        String accountNumber = "GB" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        jdbcTemplate.update("""
                INSERT INTO banking.parties (id, tenant_id, party_type, status, first_name, last_name, email, version)
                VALUES (?, ?, 'INDIVIDUAL', 'ACTIVE', 'High', 'Balance', ?, 0)
                """, partyId, TENANT_DEMO, partyId + "@lab.example.com");

        jdbcTemplate.update("""
                INSERT INTO banking.accounts (id, tenant_id, party_id, account_number, product_code, currency,
                    status, available_balance, ledger_balance, version)
                VALUES (?, ?, ?, ?, 'CURRENT', 'GBP', 'ACTIVE', 500000.0000, 500000.0000, 0)
                """, accountId, TENANT_DEMO, partyId, accountNumber);

        List<TestDataHandleResponse> handles = new ArrayList<>();
        handles.add(saveHandle(namespace, TestDataEntityType.PARTY, partyId,
                Map.of("profile", "high-balance")));
        handles.add(saveHandle(namespace, TestDataEntityType.ACCOUNT, accountId,
                Map.of("profile", "high-balance", "balance", 500000)));
        return handles;
    }

    private List<TestDataHandleResponse> generateAmlReady(String namespace) {
        String partyId = UUID.randomUUID().toString();
        String accountId = UUID.randomUUID().toString();
        String accountNumber = "GB" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        jdbcTemplate.update("""
                INSERT INTO banking.parties (id, tenant_id, party_type, status, first_name, last_name, email, version)
                VALUES (?, ?, 'INDIVIDUAL', 'ACTIVE', 'Sanctioned', 'Watchlist', ?, 0)
                """, partyId, TENANT_DEMO, "aml-" + partyId + "@lab.example.com");

        jdbcTemplate.update("""
                INSERT INTO banking.kyc_cases (id, party_id, status, level)
                VALUES (?, ?, 'APPROVED', 'STANDARD')
                """, UUID.randomUUID().toString(), partyId);

        jdbcTemplate.update("""
                INSERT INTO banking.accounts (id, tenant_id, party_id, account_number, product_code, currency,
                    status, available_balance, ledger_balance, version)
                VALUES (?, ?, ?, ?, 'CURRENT', 'GBP', 'ACTIVE', 10000.0000, 10000.0000, 0)
                """, accountId, TENANT_DEMO, partyId, accountNumber);

        List<TestDataHandleResponse> handles = new ArrayList<>();
        handles.add(saveHandle(namespace, TestDataEntityType.PARTY, partyId,
                Map.of("profile", "aml-ready", "amlScreening", "REVIEW")));
        handles.add(saveHandle(namespace, TestDataEntityType.ACCOUNT, accountId,
                Map.of("profile", "aml-ready")));
        return handles;
    }

    private TestDataHandleResponse saveHandle(
            String namespace,
            TestDataEntityType entityType,
            String entityId,
            Map<String, Object> metadata) {
        TestDataHandleEntity entity = new TestDataHandleEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setNamespace(namespace);
        entity.setEntityType(entityType);
        entity.setEntityId(entityId);
        entity.setMetadata(toJson(metadata));
        entity.setCreatedAt(Instant.now());
        return toResponse(repository.save(entity));
    }

    private String toJson(Map<String, Object> metadata) {
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize metadata", ex);
        }
    }

    private TestDataHandleResponse toResponse(TestDataHandleEntity entity) {
        return new TestDataHandleResponse(
                entity.getId(),
                entity.getNamespace(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getMetadata(),
                entity.getCreatedAt()
        );
    }
}
