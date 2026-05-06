-- ============================================================
-- FinFlow - Script de Configuración de Bases de Datos
-- Ejecutar como usuario postgres: sudo -u postgres psql -f setup-databases.sql
-- ============================================================

-- Eliminar bases de datos si existen (para reinstalación limpia)
-- CUIDADO: Descomentar solo si quieres borrar datos existentes
-- DROP DATABASE IF EXISTS finflow_accounts;
-- DROP DATABASE IF EXISTS finflow_transfers;
-- DROP DATABASE IF EXISTS finflow_validation;
-- DROP DATABASE IF EXISTS finflow_audit;
-- DROP DATABASE IF EXISTS finflow_notifications;
-- DROP USER IF EXISTS finflow;

-- Crear usuario de la aplicación
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'finflow') THEN
        CREATE USER finflow WITH PASSWORD 'finflow123' CREATEDB;
        RAISE NOTICE 'Usuario finflow creado exitosamente';
    ELSE
        RAISE NOTICE 'Usuario finflow ya existe';
    END IF;
END
$$;

-- Crear base de datos finflow_accounts
SELECT 'Creando finflow_accounts...' AS status;
CREATE DATABASE finflow_accounts OWNER finflow;
GRANT ALL PRIVILEGES ON DATABASE finflow_accounts TO finflow;

-- Crear base de datos finflow_transfers
SELECT 'Creando finflow_transfers...' AS status;
CREATE DATABASE finflow_transfers OWNER finflow;
GRANT ALL PRIVILEGES ON DATABASE finflow_transfers TO finflow;

-- Crear base de datos finflow_validation
SELECT 'Creando finflow_validation...' AS status;
CREATE DATABASE finflow_validation OWNER finflow;
GRANT ALL PRIVILEGES ON DATABASE finflow_validation TO finflow;

-- Crear base de datos finflow_audit
SELECT 'Creando finflow_audit...' AS status;
CREATE DATABASE finflow_audit OWNER finflow;
GRANT ALL PRIVILEGES ON DATABASE finflow_audit TO finflow;

-- Crear base de datos finflow_notifications
SELECT 'Creando finflow_notifications...' AS status;
CREATE DATABASE finflow_notifications OWNER finflow;
GRANT ALL PRIVILEGES ON DATABASE finflow_notifications TO finflow;

-- Mensaje final
SELECT 'Bases de datos creadas exitosamente' AS resultado;

-- ============================================================
-- NOTA: Ejecutar los siguientes comandos manualmente después
-- para habilitar la extensión uuid-ossp en cada base de datos:
-- ============================================================
-- sudo -u postgres psql -d finflow_accounts -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"
-- sudo -u postgres psql -d finflow_transfers -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"
-- sudo -u postgres psql -d finflow_validation -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"
-- sudo -u postgres psql -d finflow_audit -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"
-- sudo -u postgres psql -d finflow_notifications -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"
