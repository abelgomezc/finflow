-- ============================================================
-- FinFlow Validation - Datos de Prueba
-- V2: Perfiles de usuario y cuentas en lista negra de ejemplo
-- ============================================================

-- ============================================================
-- PERFILES DE USUARIO
-- ============================================================

-- Usuario con historial establecido (Juan Pérez)
INSERT INTO user_transfer_profile (
    user_id, avg_transfer_amount, max_transfer_amount, min_transfer_amount,
    total_transfer_count, transfers_last_30_days, amount_last_30_days,
    usual_hours, usual_days, frequent_recipients, has_sufficient_history
)
VALUES (
    'user-juan-perez',
    500.00, 2000.00, 50.00,
    45, 12, 6000.00,
    '[9, 10, 11, 14, 15, 16, 17]',
    '["MON", "TUE", "WED", "THU", "FRI"]',
    '["33333333-3333-3333-3333-333333333333", "44444444-4444-4444-4444-444444444444"]',
    true
);

-- Usuario con poco historial (Carlos)
INSERT INTO user_transfer_profile (
    user_id, avg_transfer_amount, max_transfer_amount, min_transfer_amount,
    total_transfer_count, transfers_last_30_days, has_sufficient_history
)
VALUES (
    'user-carlos-rodriguez',
    300.00, 500.00, 100.00,
    3, 3,
    false
);

-- Usuario empresarial con altos volúmenes
INSERT INTO user_transfer_profile (
    user_id, avg_transfer_amount, max_transfer_amount, min_transfer_amount,
    total_transfer_count, transfers_last_30_days, amount_last_30_days,
    usual_hours, usual_days, has_sufficient_history
)
VALUES (
    'user-empresa-demo',
    5000.00, 25000.00, 500.00,
    150, 35, 175000.00,
    '[8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18]',
    '["MON", "TUE", "WED", "THU", "FRI"]',
    true
);

-- ============================================================
-- CUENTAS EN LISTA NEGRA (para pruebas)
-- ============================================================

-- Cuenta de ejemplo bloqueada por fraude
INSERT INTO blacklisted_accounts (
    account_id, account_number, reason, severity, reported_by
)
VALUES (
    '1',
    '1000000001',
    'Cuenta identificada en esquema de fraude. Múltiples reportes de phishing.',
    'CRITICAL',
    'FRAUD_TEAM'
);

-- Cuenta bloqueada temporalmente
INSERT INTO blacklisted_accounts (
    account_id, account_number, reason, severity, reported_by, expires_at
)
VALUES (
    '2',
    '1000000002',
    'Actividad sospechosa detectada. Bloqueo temporal para investigación.',
    'MEDIUM',
    'SYSTEM',
    CURRENT_TIMESTAMP + INTERVAL '7 days'
);

-- ============================================================
-- REGLAS DE FRAUDE ADICIONALES
-- ============================================================

INSERT INTO fraud_rules (rule_code, rule_name, description, fraud_score_impact, is_active)
VALUES
    ('UNUSUAL_AMOUNT', 'Monto inusual', 'Monto significativamente mayor al promedio del usuario', 40, true),
    ('UNUSUAL_HOUR', 'Hora inusual', 'Transferencia en horario nocturno (02:00-05:00)', 20, true),
    ('NEW_RECIPIENT', 'Destinatario nuevo', 'Primera transferencia a esta cuenta destino', 10, true),
    ('RAPID_TRANSFERS', 'Transferencias rápidas', 'Múltiples transferencias en corto período', 15, true),
    ('BLACKLISTED', 'Cuenta en lista negra', 'Cuenta destino está en lista negra', 100, true);
