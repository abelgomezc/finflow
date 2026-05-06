-- Tabla para registrar actividad del usuario
CREATE TABLE IF NOT EXISTS user_activity (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    activity_type VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    entity_type VARCHAR(50),
    entity_id VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para búsqueda eficiente
CREATE INDEX idx_user_activity_user_id ON user_activity(user_id);
CREATE INDEX idx_user_activity_type ON user_activity(activity_type);
CREATE INDEX idx_user_activity_created_at ON user_activity(created_at DESC);

-- Tipos de actividad comunes:
-- LOGIN, LOGOUT, LOGIN_FAILED
-- TRANSFER_INITIATED, TRANSFER_COMPLETED, TRANSFER_FAILED
-- PROFILE_UPDATED, PASSWORD_CHANGED
-- ACCOUNT_VIEWED

-- Insertar actividad de ejemplo para usuarios existentes (login history simulado)
INSERT INTO user_activity (user_id, activity_type, description, ip_address, created_at)
SELECT
    id,
    'LOGIN',
    'Inicio de sesión exitoso',
    '192.168.1.1',
    last_login_at
FROM users
WHERE last_login_at IS NOT NULL;
