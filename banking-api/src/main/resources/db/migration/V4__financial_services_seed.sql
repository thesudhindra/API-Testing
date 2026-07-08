-- Phase 3 seed: demo account, beneficiary, card with balance

INSERT INTO banking.accounts (id, tenant_id, party_id, account_number, product_code, currency, status, available_balance, ledger_balance, version) VALUES
    ('acct-customer-1', 'tenant-demo', 'party-customer-1', 'GB1234567890', 'CURRENT', 'GBP', 'ACTIVE', 5000.0000, 5000.0000, 0);

INSERT INTO banking.beneficiaries (id, tenant_id, party_id, nickname, status, sort_code, account_number, verified_at) VALUES
    ('ben-1', 'tenant-demo', 'party-customer-1', 'Savings Pot', 'ACTIVE', '12-34-56', '87654321', CURRENT_TIMESTAMP);

INSERT INTO banking.cards (id, tenant_id, party_id, account_id, pan_last4, product_code, status, version) VALUES
    ('card-1', 'tenant-demo', 'party-customer-1', 'acct-customer-1', '4242', 'DEBIT', 'ACTIVE', 0);

-- GBP savings account for internal transfers
INSERT INTO banking.accounts (id, tenant_id, party_id, account_number, product_code, currency, status, available_balance, ledger_balance, version) VALUES
    ('acct-customer-2', 'tenant-demo', 'party-customer-1', 'GB9876543210', 'SAVINGS', 'GBP', 'ACTIVE', 1000.0000, 1000.0000, 0);

-- EUR account for FX demos
INSERT INTO banking.accounts (id, tenant_id, party_id, account_number, product_code, currency, status, available_balance, ledger_balance, version) VALUES
    ('acct-customer-eur', 'tenant-demo', 'party-customer-1', 'GB1111222233', 'CURRENT', 'EUR', 'ACTIVE', 200.0000, 200.0000, 0);
