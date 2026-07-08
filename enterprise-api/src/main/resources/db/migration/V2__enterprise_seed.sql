-- Phase 4 seed: demo tenant settings and scheduled task

INSERT INTO enterprise.tenant_settings (tenant_id, setting_key, setting_value, updated_by) VALUES
    ('tenant-demo', 'fraud.velocity.threshold', '5000', 'user-admin'),
    ('tenant-demo', 'aml.auto_escalate_score', '80', 'user-admin');

INSERT INTO enterprise.scheduled_tasks (id, tenant_id, task_type, cron_expression, payload, enabled, next_run_at) VALUES
    ('sched-rd-reminder', 'tenant-demo', 'RD_INSTALLMENT_REMINDER', '0 0 9 * * *', '{"partyId":"party-customer-1"}', TRUE, CURRENT_TIMESTAMP);
