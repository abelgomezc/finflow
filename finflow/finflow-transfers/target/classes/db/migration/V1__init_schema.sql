-- ============================================================
-- FinFlow Transfers - Schema Inicial
-- V1: Tablas para gestión de transferencias
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- ENUM TYPES
-- ============================================================

CREATE TYPE transfer_status AS ENUM (
    'PENDING',      -- Creada, pendiente de validación
    'VALIDATING',   -- En proceso de validación
    'PROCESSING',   -- Validación OK, ejecutando débito/crédito
    'COMPLETED',    -- Completada exitosamente
    'FAILED',       -- Fallida
    'REVERSED',     -- Revertida (compensating transaction)
    'CANCELLED'     -- Cancelada por usuario
);

-- ============================================================
-- TABLA: transfers
-- Registro principal de transferencias
-- ============================================================

CREATE TABLE transfers (
    id BIGSERIAL PRIMARY KEY,

    -- Número de referencia visible
    reference_number VARCHAR(30) NOT NULL UNIQUE,

    -- Cuentas involucradas
    source_account_id BIGINT NOT NULL,
    target_account_id BIGINT NOT NULL,

    -- Monto y moneda
    amount DECIMAL(18, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    source_account_number VARCHAR(30),
    source_balance_after DECIMAL(18, 2),
    target_account_number VARCHAR(30),
    target_balance_after DECIMAL(18, 2),

    -- Estado actual
    status VARCHAR(20),

    -- Descripción/concepto
    description VARCHAR(500),

    -- Usuario que inició
    initiated_by VARCHAR(100) NOT NULL,

    -- IDs de operaciones relacionadas
    validation_id BIGINT,
    block_id BIGINT,
    debit_transaction_id BIGINT,
    credit_transaction_id BIGINT,
    reversal_transaction_id BIGINT,


    -- Información de error (si aplica)
    failure_reason VARCHAR(100),
    failure_message TEXT,

    -- Timestamps
    initiated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    validated_at TIMESTAMP WITH TIME ZONE,
    processed_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    failed_at TIMESTAMP WITH TIME ZONE,
    reversed_at TIMESTAMP WITH TIME ZONE,

    -- Metadata
    correlation_id VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent TEXT,

    -- Auditoría
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT chk_transfer_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_transfer_different_accounts CHECK (source_account_id != target_account_id)
);

-- Índices
CREATE INDEX idx_transfers_reference ON transfers(reference_number);
CREATE INDEX idx_transfers_source ON transfers(source_account_id);
CREATE INDEX idx_transfers_target ON transfers(target_account_id);
CREATE INDEX idx_transfers_status ON transfers(status);
CREATE INDEX idx_transfers_initiated_by ON transfers(initiated_by);
CREATE INDEX idx_transfers_initiated_at ON transfers(initiated_at DESC);
CREATE INDEX idx_transfers_correlation ON transfers(correlation_id) WHERE correlation_id IS NOT NULL;

-- ============================================================
-- TABLA: transfer_events
-- Log de eventos/cambios de estado de una transferencia
-- ============================================================

CREATE TABLE transfer_events (
    id BIGSERIAL PRIMARY KEY,

    transfer_id BIGINT NOT NULL REFERENCES transfers(id) ON DELETE CASCADE,

    -- Estado anterior y nuevo
    previous_status transfer_status,
    new_status transfer_status NOT NULL,

    -- Descripción del evento
    event_type VARCHAR(50) NOT NULL,
    event_description TEXT,

    -- Metadata adicional (JSON)
    event_data TEXT,

    -- Timestamp
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100)
);

CREATE INDEX idx_events_transfer ON transfer_events(transfer_id);
CREATE INDEX idx_events_created ON transfer_events(created_at DESC);

-- ============================================================
-- TABLA: outbox_events
-- Patrón Outbox para garantizar publicación a Kafka
-- ============================================================

CREATE TABLE outbox_events (
    id BIGSERIAL PRIMARY KEY,

    -- Información del evento
    aggregate_type VARCHAR(50) NOT NULL,  -- 'Transfer'
    aggregate_id BIGINT NOT NULL,            -- transfer_id
    event_type VARCHAR(100) NOT NULL,      -- 'TransferCompleted', etc.

    -- Payload del evento (JSON)
    payload TEXT NOT NULL,

    -- Headers para Kafka
    correlation_id VARCHAR(100),

    -- Estado de publicación
    published BOOLEAN NOT NULL DEFAULT false,
    published_at TIMESTAMP WITH TIME ZONE,
    publish_attempts INT DEFAULT 0,
    last_error TEXT,

    -- Timestamp
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_outbox_unpublished ON outbox_events(published, created_at)
    WHERE published = false;

-- ============================================================
-- SECUENCIA para números de referencia
-- ============================================================

CREATE SEQUENCE transfer_reference_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    CACHE 10;

-- ============================================================
-- FUNCIÓN: fn_generate_reference_number
-- Genera un número de referencia único
-- Formato: TRF-YYYYMMDD-NNNNNN
-- ============================================================

CREATE OR REPLACE FUNCTION fn_generate_reference_number()
RETURNS VARCHAR(30) AS $$
DECLARE
    v_seq BIGINT;
    v_date VARCHAR(8);
    v_number VARCHAR(30);
BEGIN
    SELECT nextval('transfer_reference_seq') INTO v_seq;
    v_date := TO_CHAR(CURRENT_DATE, 'YYYYMMDD');
    v_number := 'TRF-' || v_date || '-' || LPAD(v_seq::TEXT, 6, '0');
    RETURN v_number;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- PROCEDIMIENTO: sp_create_transfer
-- Crea una nueva transferencia y su primer evento
-- ============================================================

CREATE OR REPLACE PROCEDURE sp_create_transfer(
    IN p_source_account_id BIGINT,
    IN p_target_account_id BIGINT,
    IN p_amount DECIMAL(18, 2),
    IN p_currency VARCHAR(3),
    IN p_description VARCHAR(500),
    IN p_initiated_by VARCHAR(100),
    IN p_correlation_id VARCHAR(100),
    IN p_ip_address VARCHAR(45),
    OUT p_transfer_id BIGINT,
    OUT p_reference_number VARCHAR(30)
)
LANGUAGE plpgsql AS $$
BEGIN
    -- Generar referencia
    p_reference_number := fn_generate_reference_number();

    -- Crear la transferencia
    INSERT INTO transfers (
        reference_number, source_account_id, target_account_id,
        amount, currency, description, initiated_by,
        correlation_id, ip_address, status
    ) VALUES (
        p_reference_number, p_source_account_id, p_target_account_id,
        p_amount, p_currency, p_description, p_initiated_by,
        p_correlation_id, p_ip_address, 'PENDING'
    )
    RETURNING id INTO p_transfer_id;

    -- Registrar evento inicial
    INSERT INTO transfer_events (
        transfer_id, new_status, event_type, event_description, created_by
    ) VALUES (
        p_transfer_id, 'PENDING', 'TRANSFER_CREATED',
        'Transfer initiated', p_initiated_by
    );
END;
$$;

-- ============================================================
-- PROCEDIMIENTO: sp_update_transfer_status
-- Actualiza el estado de una transferencia y registra evento
-- ============================================================

CREATE OR REPLACE PROCEDURE sp_update_transfer_status(
    IN p_transfer_id BIGINT,
    IN p_new_status transfer_status,
    IN p_event_type VARCHAR(50),
    IN p_description TEXT,
    IN p_failure_reason VARCHAR(100) DEFAULT NULL,
    IN p_failure_message TEXT DEFAULT NULL,
    IN p_updated_by VARCHAR(100) DEFAULT 'SYSTEM'
)
LANGUAGE plpgsql AS $$
DECLARE
    v_old_status transfer_status;
    v_now TIMESTAMP WITH TIME ZONE := CURRENT_TIMESTAMP;
BEGIN
    -- Obtener estado actual
    SELECT status INTO v_old_status
    FROM transfers
    WHERE id = p_transfer_id
    FOR UPDATE;

    -- Actualizar transferencia
    UPDATE transfers
    SET status = p_new_status,
        failure_reason = COALESCE(p_failure_reason, failure_reason),
        failure_message = COALESCE(p_failure_message, failure_message),
        validated_at = CASE WHEN p_new_status = 'VALIDATING' THEN v_now ELSE validated_at END,
        processed_at = CASE WHEN p_new_status = 'PROCESSING' THEN v_now ELSE processed_at END,
        completed_at = CASE WHEN p_new_status = 'COMPLETED' THEN v_now ELSE completed_at END,
        failed_at = CASE WHEN p_new_status = 'FAILED' THEN v_now ELSE failed_at END,
        reversed_at = CASE WHEN p_new_status = 'REVERSED' THEN v_now ELSE reversed_at END,
        updated_at = v_now
    WHERE id = p_transfer_id;

    -- Registrar evento
    INSERT INTO transfer_events (
        transfer_id, previous_status, new_status, event_type, event_description, created_by
    ) VALUES (
        p_transfer_id, v_old_status, p_new_status, p_event_type, p_description, p_updated_by
    );
END;
$$;

-- ============================================================
-- TRIGGER: auto_updated_at
-- ============================================================

CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_transfers_updated_at
    BEFORE UPDATE ON transfers
    FOR EACH ROW
    EXECUTE FUNCTION fn_set_updated_at();
