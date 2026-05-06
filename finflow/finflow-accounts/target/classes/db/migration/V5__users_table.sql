-- =====================================================
-- V5: Tabla de usuarios para autenticación
-- =====================================================

-- Tabla de usuarios
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(200) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_locked BOOLEAN NOT NULL DEFAULT false,
    failed_attempts INTEGER NOT NULL DEFAULT 0,
    last_login_at TIMESTAMP WITH TIME ZONE,
    last_login_ip VARCHAR(45),
    profile_photo_url VARCHAR(500),
    customer_id BIGINT REFERENCES customers(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Índices
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_customer ON users(customer_id);
CREATE INDEX idx_users_active ON users(is_active, is_locked);

-- Trigger para updated_at (usa la funcion existente de V3)
CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION fn_set_updated_at();

-- Comentarios
COMMENT ON TABLE users IS 'Tabla de usuarios del sistema con autenticación BCrypt';
COMMENT ON COLUMN users.password_hash IS 'Hash BCrypt del password (12 rounds)';
COMMENT ON COLUMN users.failed_attempts IS 'Contador de intentos fallidos de login';
COMMENT ON COLUMN users.is_locked IS 'Cuenta bloqueada después de 5 intentos fallidos';

-- =====================================================
-- Usuarios de prueba con passwords BCrypt
-- Password: "finflow123" para todos
-- BCrypt hash generado con 12 rounds
-- =====================================================

-- Hash BCrypt correcto para "finflow123" generado con 12 rounds
-- customer_id references customers(id) que es BIGINT (ver V4 seed data para IDs)
INSERT INTO users (username, password_hash, email, full_name, role, customer_id) VALUES
    -- admin / finflow123
    ('admin', '$2a$12$uSZvml4h414zYasAVBJ7JecIzJDyMdv6hG56Fr1xY2OnAJUad7vFq',
     'admin@finflow.ec', 'Administrador Sistema', 'ADMIN', NULL),

    -- jperez / finflow123 (Juan Pérez - customer_id = 1)
    ('asgomez', '$2a$12$uSZvml4h414zYasAVBJ7JecIzJDyMdv6hG56Fr1xY2OnAJUad7vFq',
     'asgomez@email.com', 'Abel Gomez', 'USER', 1),

    -- mgarcia / finflow123 (María García - customer_id = 2)
    ('mgarcia', '$2a$12$uSZvml4h414zYasAVBJ7JecIzJDyMdv6hG56Fr1xY2OnAJUad7vFq',
     'maria.garcia@email.com', 'María García', 'USER', 2),

    -- empresa / finflow123 (Empresa Demo - customer_id = 3)
    ('empresa', '$2a$12$uSZvml4h414zYasAVBJ7JecIzJDyMdv6hG56Fr1xY2OnAJUad7vFq',
     'empresa@demo.ec', 'Empresa Demo S.A.', 'BUSINESS', 3),

    -- crodriguez / finflow123 (Carlos Rodríguez - customer_id = 4)
    ('crodriguez', '$2a$12$uSZvml4h414zYasAVBJ7JecIzJDyMdv6hG56Fr1xY2OnAJUad7vFq',
     'carlos.rodriguez@email.com', 'Carlos Rodríguez', 'USER', 4);
