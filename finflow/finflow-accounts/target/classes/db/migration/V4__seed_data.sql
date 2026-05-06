-- ============================================================
-- FinFlow Accounts - Datos de Prueba
-- V4: Clientes y cuentas de ejemplo para desarrollo
-- ============================================================

-- ============================================================
-- CLIENTES DE PRUEBA (IDs auto-generados: 1, 2, 3, 4)
-- ============================================================

INSERT INTO customers (document_type, document_number, first_name, last_name, email, phone, city, country, is_active)
VALUES
    ('DNI', '1234567890', 'Juan', 'Pérez', 'juan.perez@email.com', '+593999111222', 'Quito', 'ECU', true),
    ('DNI', '0987654321', 'María', 'García', 'maria.garcia@email.com', '+593999333444', 'Guayaquil', 'ECU', true),
    ('RUC', '1791234567001', 'Empresa', 'Demo S.A.', 'contacto@empresademo.com', '+593999555666', 'Cuenca', 'ECU', true),
    ('PASSPORT', 'AB123456', 'Carlos', 'Rodríguez', 'carlos.rodriguez@email.com', '+593999777888', 'Quito', 'ECU', true);

-- ============================================================
-- CUENTAS DE PRUEBA (customer_id referencia a customers.id)
-- ============================================================

-- Cuentas de Juan Pérez (customer_id = 1)
INSERT INTO accounts (customer_id, account_number, account_type, status, currency, balance, daily_transfer_limit, per_transfer_limit)
VALUES
    (1, '1000000001', 'CHECKING', 'ACTIVE', 'USD', 5000.00, 10000.00, 5000.00),
    (1, '1000000002', 'SAVINGS', 'ACTIVE', 'USD', 15000.00, 5000.00, 2000.00);

-- Cuentas de María García (customer_id = 2)
INSERT INTO accounts (customer_id, account_number, account_type, status, currency, balance, daily_transfer_limit, per_transfer_limit)
VALUES
    (2, '1000000003', 'CHECKING', 'ACTIVE', 'USD', 8500.00, 10000.00, 5000.00);

-- Cuenta empresarial (customer_id = 3)
INSERT INTO accounts (customer_id, account_number, account_type, status, currency, balance, daily_transfer_limit, per_transfer_limit)
VALUES
    (3, '1000000004', 'BUSINESS', 'ACTIVE', 'USD', 50000.00, 100000.00, 25000.00);

-- Cuenta de Carlos (customer_id = 4)
INSERT INTO accounts (customer_id, account_number, account_type, status, currency, balance, daily_transfer_limit, per_transfer_limit)
VALUES
    (4, '1000000005', 'CHECKING', 'ACTIVE', 'USD', 3000.00, 10000.00, 5000.00);

-- Cuenta bloqueada de Carlos (customer_id = 4)
INSERT INTO accounts (customer_id, account_number, account_type, status, currency, balance, daily_transfer_limit, per_transfer_limit)
VALUES
    (4, '1000000006', 'CHECKING', 'BLOCKED', 'USD', 1000.00, 10000.00, 5000.00);

-- ============================================================
-- HISTORIAL DE EJEMPLO (account_id referencia a accounts.id)
-- ============================================================

-- Algunos movimientos de ejemplo para Juan (accounts 1 y 2)
INSERT INTO balance_history (account_id, transaction_type, amount, balance_before, balance_after, description, executed_by)
VALUES
    (1, 'CREDIT', 5000.00, 0.00, 5000.00, 'Depósito inicial', 'SYSTEM'),
    (2, 'CREDIT', 15000.00, 0.00, 15000.00, 'Depósito inicial', 'SYSTEM');

-- Movimientos de María (account 3)
INSERT INTO balance_history (account_id, transaction_type, amount, balance_before, balance_after, description, executed_by)
VALUES
    (3, 'CREDIT', 10000.00, 0.00, 10000.00, 'Depósito inicial', 'SYSTEM'),
    (3, 'DEBIT', 1500.00, 10000.00, 8500.00, 'Transferencia a terceros', 'SYSTEM');

-- ============================================================
-- COMENTARIOS
-- ============================================================

COMMENT ON TABLE customers IS 'Datos de prueba: 4 clientes de ejemplo';
COMMENT ON TABLE accounts IS 'Datos de prueba: 6 cuentas con diferentes estados y tipos';
