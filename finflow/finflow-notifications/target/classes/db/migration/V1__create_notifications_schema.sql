-- ============================================================
-- FinFlow Notifications - Schema de Notificaciones
-- ============================================================

-- Tabla de templates de notificación
CREATE TABLE notification_templates (
    id BIGSERIAL PRIMARY KEY,
    template_code VARCHAR(50) NOT NULL UNIQUE,
    template_name VARCHAR(100) NOT NULL,
    subject_template VARCHAR(200) NOT NULL,
    body_template_name VARCHAR(100) NOT NULL,
    notification_type VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Tabla de preferencias de usuario
CREATE TABLE user_notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    email_enabled BOOLEAN NOT NULL DEFAULT true,
    transfer_initiated_enabled BOOLEAN NOT NULL DEFAULT true,
    transfer_completed_enabled BOOLEAN NOT NULL DEFAULT true,
    transfer_failed_enabled BOOLEAN NOT NULL DEFAULT true,
    transfer_reversed_enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_user_preferences UNIQUE (user_id)
);

CREATE INDEX idx_user_preferences_email ON user_notification_preferences(email);

-- Tabla principal de notificaciones enviadas
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,

    -- Identificadores
    correlation_id VARCHAR(100),

    -- Destinatario
    user_id VARCHAR(100) NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,

    -- Tipo y contenido
    notification_type VARCHAR(50) NOT NULL,
    template_code VARCHAR(50) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    body TEXT NOT NULL,

    -- Datos del evento
    event_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    event_data JSONB NOT NULL,

    -- Estado
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    sent_at TIMESTAMPTZ,
    delivered_at TIMESTAMPTZ,
    error_message TEXT,
    retry_count INTEGER NOT NULL DEFAULT 0,
    next_retry_at TIMESTAMPTZ,

    -- Timestamps
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Kafka metadata
    kafka_topic VARCHAR(100),
    kafka_partition INTEGER,
    kafka_offset BIGINT
);

-- Índices
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_created ON notifications(created_at DESC);
CREATE INDEX idx_notifications_entity ON notifications(entity_type, entity_id);
CREATE INDEX idx_notifications_correlation ON notifications(correlation_id);
CREATE INDEX idx_notifications_retry ON notifications(status, next_retry_at) WHERE status = 'RETRY';

-- ============================================================
-- Datos iniciales: Templates de notificación
-- ============================================================
INSERT INTO notification_templates (template_code, template_name, subject_template, body_template_name, notification_type) VALUES
('TRANSFER_INITIATED', 'Transferencia Iniciada', 'FinFlow: Transferencia #{referenceNumber} iniciada', 'transfer-initiated', 'EMAIL'),
('TRANSFER_COMPLETED', 'Transferencia Completada', 'FinFlow: Transferencia #{referenceNumber} completada exitosamente', 'transfer-completed', 'EMAIL'),
('TRANSFER_FAILED', 'Transferencia Fallida', 'FinFlow: Transferencia #{referenceNumber} no pudo completarse', 'transfer-failed', 'EMAIL'),
('TRANSFER_REVERSED', 'Transferencia Reversada', 'FinFlow: Transferencia #{referenceNumber} ha sido reversada', 'transfer-reversed', 'EMAIL');

-- ============================================================
-- Stored Procedure: Guardar notificación
-- ============================================================
CREATE OR REPLACE FUNCTION sp_save_notification(
    p_correlation_id VARCHAR(100),
    p_user_id VARCHAR(100),
    p_recipient_email VARCHAR(255),
    p_notification_type VARCHAR(50),
    p_template_code VARCHAR(50),
    p_subject VARCHAR(200),
    p_body TEXT,
    p_event_type VARCHAR(50),
    p_entity_type VARCHAR(50),
    p_entity_id BIGINT,
    p_event_data JSONB,
    p_kafka_topic VARCHAR(100),
    p_kafka_partition INTEGER,
    p_kafka_offset BIGINT
) RETURNS UUID AS $$
DECLARE
    v_notification_id BIGINT;
BEGIN
    INSERT INTO notifications (
        correlation_id,
        user_id,
        recipient_email,
        notification_type,
        template_code,
        subject,
        body,
        event_type,
        entity_type,
        entity_id,
        event_data,
        kafka_topic,
        kafka_partition,
        kafka_offset,
        status
    ) VALUES (
        p_correlation_id,
        p_user_id,
        p_recipient_email,
        p_notification_type,
        p_template_code,
        p_subject,
        p_body,
        p_event_type,
        p_entity_type,
        p_entity_id,
        p_event_data,
        p_kafka_topic,
        p_kafka_partition,
        p_kafka_offset,
        'PENDING'
    )
    RETURNING id INTO v_notification_id;

    RETURN v_notification_id;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- Stored Procedure: Actualizar estado de notificación
-- ============================================================
CREATE OR REPLACE FUNCTION sp_update_notification_status(
    p_notification_id BIGINT,
    p_status VARCHAR(20),
    p_error_message TEXT DEFAULT NULL
) RETURNS VOID AS $$
BEGIN
    UPDATE notifications
    SET
        status = p_status,
        error_message = p_error_message,
        sent_at = CASE WHEN p_status = 'SENT' THEN NOW() ELSE sent_at END,
        delivered_at = CASE WHEN p_status = 'DELIVERED' THEN NOW() ELSE delivered_at END,
        retry_count = CASE WHEN p_status = 'RETRY' THEN retry_count + 1 ELSE retry_count END,
        next_retry_at = CASE
            WHEN p_status = 'RETRY' THEN NOW() + (POWER(2, retry_count) * INTERVAL '1 minute')
            ELSE NULL
        END,
        updated_at = NOW()
    WHERE id = p_notification_id;
END;
$$ LANGUAGE plpgsql;

-- Comentarios
COMMENT ON TABLE notifications IS 'Almacena todas las notificaciones enviadas o pendientes';
COMMENT ON TABLE notification_templates IS 'Templates de notificación configurables';
COMMENT ON TABLE user_notification_preferences IS 'Preferencias de notificación por usuario';
