-- ============================================================
-- FinFlow Audit - Schema de Auditoría
-- ============================================================

-- Tabla principal de eventos de auditoría
CREATE TABLE audit_events (
    id BIGSERIAL PRIMARY KEY,

    -- Identificadores
    correlation_id VARCHAR(100),
    event_id VARCHAR(100) NOT NULL,

    -- Tipo de evento
    event_type VARCHAR(50) NOT NULL,
    event_category VARCHAR(50) NOT NULL,

    -- Entidad relacionada
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,

    -- Datos del evento (JSON)
    event_data JSONB NOT NULL,

    -- Metadata
    source_service VARCHAR(50) NOT NULL,
    user_id VARCHAR(100),
    ip_address VARCHAR(45),

    -- Timestamps
    event_timestamp TIMESTAMPTZ NOT NULL,
    received_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Kafka metadata
    kafka_topic VARCHAR(100),
    kafka_partition INTEGER,
    kafka_offset BIGINT,

    -- Índices para búsqueda
    CONSTRAINT uq_audit_event_id UNIQUE (event_id)
);

-- Índices para consultas frecuentes
CREATE INDEX idx_audit_correlation_id ON audit_events(correlation_id);
CREATE INDEX idx_audit_event_type ON audit_events(event_type);
CREATE INDEX idx_audit_entity ON audit_events(entity_type, entity_id);
CREATE INDEX idx_audit_user ON audit_events(user_id);
CREATE INDEX idx_audit_timestamp ON audit_events(event_timestamp DESC);
CREATE INDEX idx_audit_category ON audit_events(event_category);
CREATE INDEX idx_audit_received ON audit_events(received_at DESC);

-- Índice GIN para búsqueda en JSON
CREATE INDEX idx_audit_event_data ON audit_events USING GIN (event_data);

-- Tabla de resumen diario (para reportes)
CREATE TABLE audit_daily_summary (
    id BIGSERIAL PRIMARY KEY,
    summary_date DATE NOT NULL,
    event_category VARCHAR(50) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_count INTEGER NOT NULL DEFAULT 0,
    success_count INTEGER NOT NULL DEFAULT 0,
    failure_count INTEGER NOT NULL DEFAULT 0,
    total_amount NUMERIC(19, 4) DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_daily_summary UNIQUE (summary_date, event_category, event_type)
);

CREATE INDEX idx_summary_date ON audit_daily_summary(summary_date DESC);
CREATE INDEX idx_summary_category ON audit_daily_summary(event_category);

-- Tabla de alertas de auditoría
CREATE TABLE audit_alerts (
    id BIGSERIAL PRIMARY KEY,

    -- Referencia al evento
    audit_event_id BIGINT NOT NULL REFERENCES audit_events(id),

    -- Tipo de alerta
    alert_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,

    -- Detalles
    title VARCHAR(200) NOT NULL,
    description TEXT,

    -- Estado
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    acknowledged_by VARCHAR(100),
    acknowledged_at TIMESTAMPTZ,
    resolved_by VARCHAR(100),
    resolved_at TIMESTAMPTZ,
    resolution_notes TEXT,

    -- Timestamps
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_alert_status ON audit_alerts(status);
CREATE INDEX idx_alert_severity ON audit_alerts(severity);
CREATE INDEX idx_alert_type ON audit_alerts(alert_type);
CREATE INDEX idx_alert_created ON audit_alerts(created_at DESC);

-- ============================================================
-- Stored Procedure: Guardar evento de auditoría
-- ============================================================
CREATE OR REPLACE FUNCTION sp_save_audit_event(
    p_correlation_id VARCHAR(100),
    p_event_id VARCHAR(100),
    p_event_type VARCHAR(50),
    p_event_category VARCHAR(50),
    p_entity_type VARCHAR(50),
    p_entity_id BIGINT,
    p_event_data JSONB,
    p_source_service VARCHAR(50),
    p_user_id VARCHAR(100),
    p_ip_address VARCHAR(45),
    p_event_timestamp TIMESTAMPTZ,
    p_kafka_topic VARCHAR(100),
    p_kafka_partition INTEGER,
    p_kafka_offset BIGINT
) RETURNS UUID AS $$
DECLARE
    v_audit_id BIGINT;
BEGIN
    INSERT INTO audit_events (
        correlation_id,
        event_id,
        event_type,
        event_category,
        entity_type,
        entity_id,
        event_data,
        source_service,
        user_id,
        ip_address,
        event_timestamp,
        kafka_topic,
        kafka_partition,
        kafka_offset
    ) VALUES (
        p_correlation_id,
        p_event_id,
        p_event_type,
        p_event_category,
        p_entity_type,
        p_entity_id,
        p_event_data,
        p_source_service,
        p_user_id,
        p_ip_address,
        p_event_timestamp,
        p_kafka_topic,
        p_kafka_partition,
        p_kafka_offset
    )
    ON CONFLICT (event_id) DO NOTHING
    RETURNING id INTO v_audit_id;

    -- Actualizar resumen diario
    INSERT INTO audit_daily_summary (
        summary_date,
        event_category,
        event_type,
        event_count,
        success_count,
        failure_count,
        total_amount
    ) VALUES (
        DATE(p_event_timestamp),
        p_event_category,
        p_event_type,
        1,
        CASE WHEN p_event_type LIKE '%COMPLETED%' OR p_event_type LIKE '%SUCCESS%' THEN 1 ELSE 0 END,
        CASE WHEN p_event_type LIKE '%FAILED%' OR p_event_type LIKE '%ERROR%' THEN 1 ELSE 0 END,
        COALESCE((p_event_data->>'amount')::NUMERIC, 0)
    )
    ON CONFLICT (summary_date, event_category, event_type)
    DO UPDATE SET
        event_count = audit_daily_summary.event_count + 1,
        success_count = audit_daily_summary.success_count +
            CASE WHEN p_event_type LIKE '%COMPLETED%' OR p_event_type LIKE '%SUCCESS%' THEN 1 ELSE 0 END,
        failure_count = audit_daily_summary.failure_count +
            CASE WHEN p_event_type LIKE '%FAILED%' OR p_event_type LIKE '%ERROR%' THEN 1 ELSE 0 END,
        total_amount = audit_daily_summary.total_amount + COALESCE((p_event_data->>'amount')::NUMERIC, 0),
        updated_at = NOW();

    RETURN v_audit_id;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- Stored Procedure: Crear alerta de auditoría
-- ============================================================
CREATE OR REPLACE FUNCTION sp_create_audit_alert(
    p_audit_event_id BIGINT,
    p_alert_type VARCHAR(50),
    p_severity VARCHAR(20),
    p_title VARCHAR(200),
    p_description TEXT
) RETURNS UUID AS $$
DECLARE
    v_alert_id BIGINT;
BEGIN
    INSERT INTO audit_alerts (
        audit_event_id,
        alert_type,
        severity,
        title,
        description
    ) VALUES (
        p_audit_event_id,
        p_alert_type,
        p_severity,
        p_title,
        p_description
    )
    RETURNING id INTO v_alert_id;

    RETURN v_alert_id;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- Vista: Eventos recientes con alertas
-- ============================================================
CREATE OR REPLACE VIEW v_recent_audit_events AS
SELECT
    ae.id,
    ae.correlation_id,
    ae.event_type,
    ae.event_category,
    ae.entity_type,
    ae.entity_id,
    ae.event_data,
    ae.source_service,
    ae.user_id,
    ae.event_timestamp,
    ae.received_at,
    CASE WHEN aa.id IS NOT NULL THEN true ELSE false END as has_alert,
    aa.severity as alert_severity
FROM audit_events ae
LEFT JOIN audit_alerts aa ON ae.id = aa.audit_event_id
ORDER BY ae.event_timestamp DESC;

-- Comentarios
COMMENT ON TABLE audit_events IS 'Almacena todos los eventos de auditoría del sistema FinFlow';
COMMENT ON TABLE audit_daily_summary IS 'Resumen diario de eventos para reportes y dashboards';
COMMENT ON TABLE audit_alerts IS 'Alertas generadas a partir de eventos de auditoría';
