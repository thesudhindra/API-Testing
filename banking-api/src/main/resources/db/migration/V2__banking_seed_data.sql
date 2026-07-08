-- Demo tenant, roles, users (password: password for all)
INSERT INTO banking.tenants (id, name) VALUES ('tenant-demo', 'Demo Bank');

INSERT INTO banking.roles (id, tenant_id, name) VALUES
    ('role-customer', 'tenant-demo', 'RETAIL_CUSTOMER'),
    ('role-ops', 'tenant-demo', 'OPS_AGENT'),
    ('role-admin', 'tenant-demo', 'ADMIN');

-- BCrypt hash for "password" ($2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi)
INSERT INTO banking.users (id, tenant_id, username, password_hash, party_id, enabled) VALUES
    ('user-customer', 'tenant-demo', 'customer', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'party-customer-1', TRUE),
    ('user-ops', 'tenant-demo', 'ops', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, TRUE),
    ('user-admin', 'tenant-demo', 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NULL, TRUE);

INSERT INTO banking.user_roles (user_id, role_id) VALUES
    ('user-customer', 'role-customer'),
    ('user-ops', 'role-ops'),
    ('user-admin', 'role-admin');

INSERT INTO banking.parties (id, tenant_id, party_type, status, first_name, last_name, email, version) VALUES
    ('party-customer-1', 'tenant-demo', 'INDIVIDUAL', 'ACTIVE', 'Jane', 'Doe', 'jane.doe@example.com', 0);

INSERT INTO banking.kyc_cases (id, party_id, status, level) VALUES
    ('kyc-1', 'party-customer-1', 'APPROVED', 'STANDARD');
