-- ============================================================
-- FinFlow Accounts - Schema Inicial
-- V1: Tablas principales para cuentas y clientes
-- ============================================================

-- Extensión para UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- ENUM TYPES
-- ============================================================

CREATE TYPE account_status AS ENUM ('ACTIVE', 'INACTIVE', 'BLOCKED', 'CLOSED');
CREATE TYPE account_type AS ENUM ('CHECKING', 'SAVINGS', 'BUSINESS');
CREATE TYPE transaction_type AS ENUM ('DEBIT', 'CREDIT', 'BLOCK', 'RELEASE', 'REVERSAL');
CREATE TYPE block_status AS ENUM ('ACTIVE', 'EXECUTED', 'RELEASED', 'EXPIRED');

-- ============================================================
-- TABLA: customers
-- Información de los clientes del banco
-- ============================================================

CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,

    -- Identificación
    document_type VARCHAR(20) NOT NULL,  -- DNI, RUC, PASSPORT, etc.
    document_number VARCHAR(50) NOT NULL,

    -- Datos personales
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),

    -- Dirección
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(3) DEFAULT 'ECU',  -- ISO 3166-1 alpha-3
    postal_code VARCHAR(20),

    -- Estado
    is_active BOOLEAN NOT NULL DEFAULT true,

    -- Auditoría
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    -- Constraints
    CONSTRAINT uk_customers_document UNIQUE (document_type, document_number),
    CONSTRAINT uk_customers_email UNIQUE (email)
);

-- Índices para búsquedas frecuentes
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_document ON customers(document_type, document_number);
CREATE INDEX idx_customers_name ON customers(last_name, first_name);

-- ============================================================
-- TABLA: accounts
-- Cuentas bancarias de los clientes
-- ============================================================

CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,

    -- Relación con cliente
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,

    -- Número de cuenta (visible al usuario)
    account_number VARCHAR(20) NOT NULL,

    -- Tipo y estado
    account_type VARCHAR(20) NOT NULL DEFAULT 'CHECKING',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Moneda y saldo
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    balance DECIMAL(18, 2) NOT NULL DEFAULT 0.00,

    -- Límites configurables por cuenta
    daily_transfer_limit DECIMAL(18, 2) NOT NULL DEFAULT 10000.00,
    per_transfer_limit DECIMAL(18, 2) NOT NULL DEFAULT 5000.00,

    -- Auditoría
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    -- Constraints
    CONSTRAINT uk_accounts_number UNIQUE (account_number),
    CONSTRAINT chk_accounts_balance_positive CHECK (balance >= 0),
    CONSTRAINT chk_accounts_limits_positive CHECK (daily_transfer_limit > 0 AND per_transfer_limit > 0)
);

-- Índices
CREATE INDEX idx_accounts_customer ON accounts(customer_id);
CREATE INDEX idx_accounts_number ON accounts(account_number);
CREATE INDEX idx_accounts_status ON accounts(status);

-- ============================================================
-- TABLA: blocked_amounts
-- Montos bloqueados (reservados) para transferencias pendientes
-- ============================================================

CREATE TABLE blocked_amounts (
    id BIGSERIAL PRIMARY KEY,

    -- Cuenta afectada
    account_id BIGINT NOT NULL REFERENCES accounts(id) ON DELETE RESTRICT,

    -- Monto bloqueado
    amount DECIMAL(18, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',

    -- Referencia a la transferencia
    block_reference VARCHAR(100) NOT NULL,  -- transfer_id
    reason VARCHAR(255),

    -- Estado del bloqueo
    status block_status NOT NULL DEFAULT 'ACTIVE',

    -- Expiración automática
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    executed_at TIMESTAMP WITH TIME ZONE,  -- Cuando se convirtió en débito
    released_at TIMESTAMP WITH TIME ZONE,  -- Cuando se liberó sin débito

    -- Auditoría
    created_by VARCHAR(100),

    -- Constraints
    CONSTRAINT chk_blocked_amount_positive CHECK (amount > 0),
    CONSTRAINT uk_blocked_reference UNIQUE (block_reference)
);

-- Índices
CREATE INDEX idx_blocked_account ON blocked_amounts(account_id);
CREATE INDEX idx_blocked_status ON blocked_amounts(status);
CREATE INDEX idx_blocked_expires ON blocked_amounts(expires_at) WHERE status = 'ACTIVE';

-- ============================================================
-- TABLA: balance_history
-- Historial de todos los movimientos de saldo
-- ============================================================

CREATE TABLE balance_history (
    id BIGSERIAL PRIMARY KEY,

    -- Cuenta afectada
    account_id BIGINT NOT NULL REFERENCES accounts(id) ON DELETE RESTRICT,

    -- Tipo de movimiento
    transaction_type VARCHAR(50) ,
    created_by VARCHAR(100),
    reference_id VARCHAR(100),
    operation_type VARCHAR(20),
    -- Montos
    amount DECIMAL(18, 2) NOT NULL,
    balance_before DECIMAL(18, 2) NOT NULL,
    balance_after DECIMAL(18, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',

    -- Referencias
    transfer_id VARCHAR(100),       -- ID de la transferencia (si aplica)
    block_id BIGINT,                 -- ID del bloqueo (si aplica)
    idempotency_key VARCHAR(100),   -- Para evitar duplicados

    -- Descripción
    description VARCHAR(500),

    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Auditoría
    executed_by VARCHAR(100),
    correlation_id VARCHAR(100),

    -- Constraints
    CONSTRAINT chk_history_amount_positive CHECK (amount > 0)
);

-- Índices para consultas frecuentes
CREATE INDEX idx_history_account ON balance_history(account_id);
CREATE INDEX idx_history_created ON balance_history(created_at DESC);
CREATE INDEX idx_history_transfer ON balance_history(transfer_id) WHERE transfer_id IS NOT NULL;
CREATE INDEX idx_history_idempotency ON balance_history(idempotency_key) WHERE idempotency_key IS NOT NULL;
CREATE INDEX idx_history_account_date ON balance_history(account_id, created_at DESC);

-- ============================================================
-- TABLA: idempotency_keys
-- Control de idempotencia para operaciones financieras
-- ============================================================

CREATE TABLE idempotency_keys (
    id BIGSERIAL PRIMARY KEY,

    idempotency_key VARCHAR(100) NOT NULL,
    operation_type VARCHAR(50) NOT NULL,  -- DEBIT, CREDIT, BLOCK, etc.

    -- Resultado de la operación original
    result_transaction_id BIGINT,
    result_success BOOLEAN NOT NULL,
    result_error_code VARCHAR(50),
    result_error_message TEXT,

    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uk_idempotency_key UNIQUE (idempotency_key)
);

-- Índice para limpieza de registros expirados
CREATE INDEX idx_idempotency_expires ON idempotency_keys(expires_at);

-- ============================================================
-- SECUENCIA para números de cuenta
-- ============================================================

CREATE SEQUENCE account_number_seq
    START WITH 1000000001
    INCREMENT BY 1
    NO MAXVALUE
    CACHE 10;

-- ============================================================
-- COMENTARIOS EN TABLAS
-- ============================================================

COMMENT ON TABLE customers IS 'Clientes del banco con información personal y de contacto';
COMMENT ON TABLE accounts IS 'Cuentas bancarias con saldo y configuración de límites';
COMMENT ON TABLE blocked_amounts IS 'Montos bloqueados temporalmente para transferencias en proceso';
COMMENT ON TABLE balance_history IS 'Registro inmutable de todos los movimientos de saldo';
COMMENT ON TABLE idempotency_keys IS 'Control de idempotencia para evitar operaciones duplicadas';
