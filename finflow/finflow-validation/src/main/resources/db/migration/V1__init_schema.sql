-- ============================================================
-- FinFlow Validation - Schema Inicial
-- V1: Tablas para validación y anti-fraude
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- TABLA: fraud_rules
-- Reglas configurables de anti-fraude
-- ============================================================

CREATE TABLE fraud_rules (
    id BIGSERIAL PRIMARY KEY,

    rule_code VARCHAR(50) NOT NULL UNIQUE,
    rule_name VARCHAR(100) NOT NULL,
    description TEXT,

    -- Valores de configuración
    max_amount_per_transfer DECIMAL(18, 2),
    max_daily_amount DECIMAL(18, 2),
    max_monthly_amount DECIMAL(18, 2),
    max_transfers_per_hour INT,
    max_transfers_per_day INT,

    -- Score que agrega esta regla si se viola
    fraud_score_impact INT NOT NULL DEFAULT 0,

    -- Estado
    is_active BOOLEAN NOT NULL DEFAULT true,

    -- Auditoría
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Insertar reglas por defecto
INSERT INTO fraud_rules (rule_code, rule_name, description, max_amount_per_transfer, fraud_score_impact)
VALUES
    ('MAX_TRANSFER', 'Límite por transferencia', 'Monto máximo permitido por transferencia individual', 5000.00, 0),
    ('DAILY_LIMIT', 'Límite diario', 'Monto máximo acumulado por día', 10000.00, 0),
    ('HOURLY_FREQUENCY', 'Frecuencia por hora', 'Máximo de transferencias por hora', NULL, 15);

UPDATE fraud_rules SET max_transfers_per_hour = 10 WHERE rule_code = 'HOURLY_FREQUENCY';

-- ============================================================
-- TABLA: blacklisted_accounts
-- Cuentas en lista negra
-- ============================================================

CREATE TABLE blacklisted_accounts (
    id BIGSERIAL PRIMARY KEY,

    account_id BIGINT NOT NULL,
    account_number VARCHAR(20),

    reason VARCHAR(500) NOT NULL,
    severity VARCHAR(20) NOT NULL DEFAULT 'HIGH',  -- LOW, MEDIUM, HIGH, CRITICAL

    -- Quién reportó
    reported_by VARCHAR(100) NOT NULL,
    report_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Bloqueo temporal o permanente
    expires_at TIMESTAMP WITH TIME ZONE,  -- NULL = permanente
    is_active BOOLEAN NOT NULL DEFAULT true,

    -- Evidencia adicional (JSON)
    evidence_data TEXT,

    -- Auditoría
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_blacklist_account UNIQUE (account_id)
);

CREATE INDEX idx_blacklist_active ON blacklisted_accounts(account_id) WHERE is_active = true;

-- ============================================================
-- TABLA: user_transfer_profile
-- Perfil de transferencias del usuario para detección de anomalías
-- ============================================================

CREATE TABLE user_transfer_profile (
    id BIGSERIAL PRIMARY KEY,

    user_id VARCHAR(100) NOT NULL UNIQUE,

    -- Estadísticas de montos
    avg_transfer_amount DECIMAL(18, 2) DEFAULT 0,
    max_transfer_amount DECIMAL(18, 2) DEFAULT 0,
    min_transfer_amount DECIMAL(18, 2) DEFAULT 0,
    total_transfer_count INT DEFAULT 0,

    -- Estadísticas de los últimos 30 días
    transfers_last_30_days INT DEFAULT 0,
    amount_last_30_days DECIMAL(18, 2) DEFAULT 0,

    -- Patrones de tiempo (horas frecuentes como array JSON)
    usual_hours TEXT,  -- ej: "[9, 10, 11, 14, 15, 16]"
    usual_days TEXT,   -- ej: "[\"MON\", \"TUE\", \"WED\", \"THU\", \"FRI\"]"

    -- Cuentas destino frecuentes (JSON array de account_ids)
    frequent_recipients TEXT,

    -- Indicadores de riesgo
    failed_transfers_last_30_days INT DEFAULT 0,
    fraud_alerts_count INT DEFAULT 0,
    last_fraud_alert TIMESTAMP WITH TIME ZONE,

    -- Indica si tiene suficiente historial (>= 10 transferencias)
    has_sufficient_history BOOLEAN DEFAULT false,

    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_profile_user ON user_transfer_profile(user_id);

-- ============================================================
-- TABLA: validation_logs
-- Log de cada validación ejecutada
-- ============================================================

CREATE TABLE validation_logs (
    id BIGSERIAL PRIMARY KEY,

    -- Identificadores
    transfer_id VARCHAR(100) NOT NULL,
    correlation_id VARCHAR(100),

    -- Cuentas involucradas
    source_account_id BIGINT NOT NULL,
    target_account_id BIGINT NOT NULL,
    user_id VARCHAR(100) NOT NULL,

    -- Monto
    amount DECIMAL(18, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',

    -- Resultado
    approved BOOLEAN NOT NULL,
    rejection_reason VARCHAR(100),
    rejection_message TEXT,
    fraud_score INT NOT NULL DEFAULT 0,

    -- Indicadores detectados (JSON)
    fraud_indicators TEXT,

    -- Tiempos de procesamiento
    validation_time_ms BIGINT,
    account_check_time_ms BIGINT,
    fraud_check_time_ms BIGINT,

    -- Contexto
    ip_address VARCHAR(45),
    user_agent TEXT,
    device_fingerprint VARCHAR(100),

    -- Timestamp
    validated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_validation_transfer ON validation_logs(transfer_id);
CREATE INDEX idx_validation_user ON validation_logs(user_id);
CREATE INDEX idx_validation_date ON validation_logs(validated_at DESC);
CREATE INDEX idx_validation_rejected ON validation_logs(approved) WHERE approved = false;

-- ============================================================
-- TABLA: rule_evaluations
-- Detalle de cada regla evaluada en una validación
-- ============================================================

CREATE TABLE rule_evaluations (
    id BIGSERIAL PRIMARY KEY,

    validation_id BIGINT NOT NULL REFERENCES validation_logs(id) ON DELETE CASCADE,

    rule_code VARCHAR(50) NOT NULL,
    rule_name VARCHAR(100) NOT NULL,
    passed BOOLEAN NOT NULL,
    failure_reason TEXT,
    score_impact INT DEFAULT 0,
    evaluation_time_ms BIGINT,

    -- Datos de contexto de la evaluación (JSON)
    evaluation_data TEXT
);

CREATE INDEX idx_rule_eval_validation ON rule_evaluations(validation_id);

-- ============================================================
-- TABLA: transfer_velocity
-- Tracking de velocidad de transferencias (para límites de frecuencia)
-- ============================================================

CREATE TABLE transfer_velocity (
    id BIGSERIAL PRIMARY KEY,

    account_id BIGINT NOT NULL,
    user_id VARCHAR(100) NOT NULL,

    transfer_id VARCHAR(100) NOT NULL,
    amount DECIMAL(18, 2) NOT NULL,
    target_account_id BIGINT NOT NULL,

    -- Timestamp con precisión para cálculos de velocidad
    executed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_velocity_account ON transfer_velocity(account_id, executed_at DESC);
CREATE INDEX idx_velocity_user ON transfer_velocity(user_id, executed_at DESC);

-- Limpiar registros de más de 24 horas (ejecutar periódicamente)
-- DELETE FROM transfer_velocity WHERE executed_at < NOW() - INTERVAL '24 hours';

-- ============================================================
-- FUNCIONES AUXILIARES
-- ============================================================

-- Cuenta transferencias de una cuenta en las últimas N horas
CREATE OR REPLACE FUNCTION fn_count_transfers_in_period(
    p_account_id BIGINT,
    p_hours INT
)
RETURNS INT AS $$
DECLARE
    v_count INT;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM transfer_velocity
    WHERE account_id = p_account_id
      AND executed_at >= (CURRENT_TIMESTAMP - (p_hours || ' hours')::INTERVAL);

    RETURN v_count;
END;
$$ LANGUAGE plpgsql;

-- Suma de montos transferidos en un día
CREATE OR REPLACE FUNCTION fn_daily_amount(
    p_account_id BIGINT,
    p_date DATE DEFAULT CURRENT_DATE
)
RETURNS DECIMAL(18, 2) AS $$
DECLARE
    v_total DECIMAL(18, 2);
BEGIN
    SELECT COALESCE(SUM(amount), 0)
    INTO v_total
    FROM transfer_velocity
    WHERE account_id = p_account_id
      AND DATE(executed_at) = p_date;

    RETURN v_total;
END;
$$ LANGUAGE plpgsql;

-- Verifica si una cuenta está en lista negra
CREATE OR REPLACE FUNCTION fn_is_blacklisted(p_account_id BIGINT)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1
        FROM blacklisted_accounts
        WHERE account_id = p_account_id
          AND is_active = true
          AND (expires_at IS NULL OR expires_at > CURRENT_TIMESTAMP)
    );
END;
$$ LANGUAGE plpgsql;
