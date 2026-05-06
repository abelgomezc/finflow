-- ============================================================
-- FinFlow Accounts - Triggers
-- V3: Triggers para auditoría y validaciones automáticas
-- ============================================================

-- ============================================================
-- TRIGGER: trg_set_timestamps
-- Actualiza automáticamente updated_at en cualquier UPDATE
-- ============================================================

CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplicar a customers
CREATE TRIGGER trg_customers_updated_at
    BEFORE UPDATE ON customers
    FOR EACH ROW
    EXECUTE FUNCTION fn_set_updated_at();

-- Aplicar a accounts
CREATE TRIGGER trg_accounts_updated_at
    BEFORE UPDATE ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION fn_set_updated_at();

-- ============================================================
-- TRIGGER: trg_prevent_negative_balance
-- Impide que el balance de una cuenta quede negativo
-- ============================================================

CREATE OR REPLACE FUNCTION fn_prevent_negative_balance()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.balance < 0 THEN
        RAISE EXCEPTION 'Cannot set negative balance. Account: %, Attempted balance: %',
            NEW.id, NEW.balance
            USING ERRCODE = 'P0010';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_accounts_prevent_negative
    BEFORE UPDATE ON accounts
    FOR EACH ROW
    WHEN (NEW.balance IS DISTINCT FROM OLD.balance)
    EXECUTE FUNCTION fn_prevent_negative_balance();

-- ============================================================
-- TRIGGER: trg_accounts_status_change_audit
-- Registra cambios de estado de cuenta para auditoría
-- ============================================================

CREATE OR REPLACE FUNCTION fn_audit_account_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status IS DISTINCT FROM OLD.status THEN
        -- Insertar registro de auditoría en balance_history
        -- (usando el campo description para el evento)
        INSERT INTO balance_history (
            account_id, transaction_type, amount, balance_before, balance_after,
            description, executed_by
        ) VALUES (
            NEW.id,
            'DEBIT',  -- Usamos DEBIT como placeholder, amount = 0
            0,
            OLD.balance,
            NEW.balance,
            'Account status changed from ' || OLD.status || ' to ' || NEW.status,
            NEW.updated_by
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Comentado por ahora para no generar registros de 0 en el historial
-- CREATE TRIGGER trg_accounts_status_audit
--     AFTER UPDATE ON accounts
--     FOR EACH ROW
--     WHEN (NEW.status IS DISTINCT FROM OLD.status)
--     EXECUTE FUNCTION fn_audit_account_status_change();

-- ============================================================
-- TRIGGER: trg_validate_account_limits
-- Valida que los límites de cuenta sean coherentes
-- ============================================================

CREATE OR REPLACE FUNCTION fn_validate_account_limits()
RETURNS TRIGGER AS $$
BEGIN
    -- El límite por transferencia no puede ser mayor que el diario
    IF NEW.per_transfer_limit > NEW.daily_transfer_limit THEN
        RAISE EXCEPTION 'Per-transfer limit (%) cannot exceed daily limit (%)',
            NEW.per_transfer_limit, NEW.daily_transfer_limit
            USING ERRCODE = 'P0011';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_accounts_validate_limits
    BEFORE INSERT OR UPDATE ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION fn_validate_account_limits();

-- ============================================================
-- TRIGGER: trg_generate_account_number
-- Genera automáticamente el número de cuenta si no se proporciona
-- ============================================================

CREATE OR REPLACE FUNCTION fn_auto_generate_account_number()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.account_number IS NULL OR NEW.account_number = '' THEN
        NEW.account_number := fn_generate_account_number();
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_accounts_auto_number
    BEFORE INSERT ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION fn_auto_generate_account_number();

-- ============================================================
-- TRIGGER: trg_validate_blocked_amount
-- Valida que el monto a bloquear no exceda el saldo disponible
-- ============================================================

CREATE OR REPLACE FUNCTION fn_validate_block_amount()
RETURNS TRIGGER AS $$
DECLARE
    v_available DECIMAL(18, 2);
BEGIN
    -- Calcular saldo disponible actual
    v_available := fn_available_balance(NEW.account_id);

    -- Validar que hay saldo suficiente para el nuevo bloqueo
    IF v_available < NEW.amount THEN
        RAISE EXCEPTION 'Insufficient available balance for block. Available: %, Requested: %',
            v_available, NEW.amount
            USING ERRCODE = 'P0012';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- NOTA: Este trigger puede causar problemas de rendimiento si hay muchos
-- bloqueos simultáneos. Los stored procedures ya validan esto, así que
-- lo dejamos comentado como validación adicional opcional.

-- CREATE TRIGGER trg_blocked_amounts_validate
--     BEFORE INSERT ON blocked_amounts
--     FOR EACH ROW
--     EXECUTE FUNCTION fn_validate_block_amount();

-- ============================================================
-- TRIGGER: trg_customer_email_lowercase
-- Normaliza el email a minúsculas
-- ============================================================

CREATE OR REPLACE FUNCTION fn_normalize_customer_email()
RETURNS TRIGGER AS $$
BEGIN
    NEW.email := LOWER(TRIM(NEW.email));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_customers_email_normalize
    BEFORE INSERT OR UPDATE ON customers
    FOR EACH ROW
    EXECUTE FUNCTION fn_normalize_customer_email();

-- ============================================================
-- COMENTARIOS
-- ============================================================

COMMENT ON FUNCTION fn_set_updated_at() IS 'Actualiza automáticamente el campo updated_at';
COMMENT ON FUNCTION fn_prevent_negative_balance() IS 'Impide balances negativos en cuentas';
COMMENT ON FUNCTION fn_validate_account_limits() IS 'Valida coherencia de límites de cuenta';
COMMENT ON FUNCTION fn_auto_generate_account_number() IS 'Genera número de cuenta automático';
COMMENT ON FUNCTION fn_normalize_customer_email() IS 'Normaliza emails a minúsculas';
