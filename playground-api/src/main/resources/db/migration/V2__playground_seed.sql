INSERT INTO playground.playground_config (config_key, config_value) VALUES
    ('default_tenant', 'tenant-demo'),
    ('reset_enabled', 'true'),
    ('fault_injection_enabled', 'true'),
    ('max_test_data_handles', '1000');

INSERT INTO playground.mock_endpoints (id, path, http_method, status_code, response_body, delay_ms) VALUES
    ('mock-aml-clear', '/v1/mocks/aml/screen/clear', 'POST', 200,
     '{"result":"CLEAR","matchScore":0}', 0),
    ('mock-aml-review', '/v1/mocks/aml/screen/review', 'POST', 200,
     '{"result":"REVIEW","matchScore":75}', 200);
