-- ============================================================
-- FinFlow Accounts - Stored Procedures
-- V2: Procedimientos para operaciones financieras atómicas
-- ============================================================

-- ============================================================
-- FUNCIÓN: fn_generate_account_number
-- Genera un número de cuenta único con formato específico
-- ============================================================

CREATE OR REPLACE FUNCTION fn_generate_account_number()
RETURNS VARCHAR(20) AS $$
DECLARE
    v_seq BIGINT;
    v_number VARCHAR(20);
BEGIN
    -- Obtener siguiente valor de secuencia
    SELECT nextval('account_number_seq') INTO v_seq;

    -- Formato: prefijo + secuencia (10 dígitos)
    v_number := LPAD(v_seq::TEXT, 10, '0');

    RETURN v_number;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- FUNCIÓN: fn_available_balance
-- Calcula el saldo disponible (balance - montos bloqueados)
-- ============================================================

CREATE OR REPLACE FUNCTION fn_available_balance(p_account_id UUID)
RETURNS DECIMAL(18, 2) AS $$
DECLARE
    v_balance DECIMAL(18, 2);
    v_blocked DECIMAL(18, 2);
BEGIN
    -- Obtener saldo actual
    SELECT balance INTO v_balance
    FROM accounts
    WHERE id = p_account_id;

    IF v_balance IS NULL THEN
        RAISE EXCEPTION 'Account not found: %', p_account_id
            USING ERRCODE = 'P0002';  -- no_data_found
    END IF;

    -- Sumar montos bloqueados activos
    SELECT COALESCE(SUM(amount), 0) INTO v_blocked
    FROM blocked_amounts
    WHERE account_id = p_account_id
      AND status = 'ACTIVE'
      AND expires_at > CURRENT_TIMESTAMP;

    RETURN v_balance - v_blocked;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- FUNCIÓN: fn_daily_transfer_total
-- Retorna el total transferido (débitos) en una fecha específica
-- ============================================================

CREATE OR REPLACE FUNCTION fn_daily_transfer_total(
    p_account_id UUID,
    p_date DATE DEFAULT CURRENT_DATE
)
RETURNS DECIMAL(18, 2) AS $$
DECLARE
    v_total DECIMAL(18, 2);
BEGIN
    SELECT COALESCE(SUM(amount), 0) INTO v_total
    FROM balance_history
    WHERE account_id = p_account_id
      AND transaction_type = 'DEBIT'
      AND DATE(created_at AT TIME ZONE 'UTC') = p_date;

    RETURN v_total;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- FUNCIÓN: fn_count_recent_transfers
-- Cuenta transferencias en las últimas N horas
-- ============================================================

CREATE OR REPLACE FUNCTION fn_count_recent_transfers(
    p_account_id UUID,
    p_hours INT DEFAULT 1
)
RETURNS TABLE (
    transfer_count INT,
    total_amount DECIMAL(18, 2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        COUNT(*)::INT AS transfer_count,
        COALESCE(SUM(amount), 0)::DECIMAL(18, 2) AS total_amount
    FROM balance_history
    WHERE account_id = p_account_id
      AND transaction_type = 'DEBIT'
      AND created_at >= (CURRENT_TIMESTAMP - (p_hours || ' hours')::INTERVAL);
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- PROCEDIMIENTO: sp_block_amount
-- Bloquea (reserva) un monto en una cuenta
-- ============================================================

CREATE OR REPLACE PROCEDURE sp_block_amount(
    IN p_account_id UUID,
    IN p_amount DECIMAL(18, 2),
    IN p_block_reference VARCHAR(100),
    IN p_reason VARCHAR(255),
    IN p_expiration_minutes INT,
    IN p_executed_by VARCHAR(100),
    OUT p_block_id UUID,
    OUT p_new_available_balance DECIMAL(18, 2)
)
LANGUAGE plpgsql AS $$
DECLARE
    v_current_balance DECIMAL(18, 2);
    v_available_balance DECIMAL(18, 2);
    v_account_status account_status;
    v_expires_at TIMESTAMP WITH TIME ZONE;
BEGIN
    -- Bloquear la fila de la cuenta para evitar condiciones de carrera
    SELECT balance, status INTO v_current_balance, v_account_status
    FROM accounts
    WHERE id = p_account_id
    FOR UPDATE;

    -- Validar que la cuenta existe
    IF v_current_balance IS NULL THEN
        RAISE EXCEPTION 'Account not found: %', p_account_id
            USING ERRCODE = 'P0002';
    END IF;

    -- Validar que la cuenta está activa
    IF v_account_status != 'ACTIVE' THEN
        RAISE EXCEPTION 'Account is not active. Status: %', v_account_status
            USING ERRCODE = 'P0003';
    END IF;

    -- Calcular saldo disponible
    v_available_balance := fn_available_balance(p_account_id);

    -- Validar saldo suficiente
    IF v_available_balance < p_amount THEN
        RAISE EXCEPTION 'Insufficient available balance. Available: %, Required: %',
            v_available_balance, p_amount
            USING ERRCODE = 'P0004';
    END IF;

    -- Verificar que no existe un bloqueo con la misma referencia
    IF EXISTS (SELECT 1 FROM blocked_amounts WHERE block_reference = p_block_reference) THEN
        RAISE EXCEPTION 'Block reference already exists: %', p_block_reference
            USING ERRCODE = 'P0005';
    END IF;

    -- Calcular expiración
    v_expires_at := CURRENT_TIMESTAMP + (p_expiration_minutes || ' minutes')::INTERVAL;

    -- Crear el bloqueo
    INSERT INTO blocked_amounts (
        account_id, amount, block_reference, reason, status,
        expires_at, created_by
    ) VALUES (
        p_account_id, p_amount, p_block_reference, p_reason, 'ACTIVE',
        v_expires_at, p_executed_by
    )
    RETURNING id INTO p_block_id;

    -- Calcular nuevo saldo disponible
    p_new_available_balance := v_available_balance - p_amount;

    -- Registrar en historial
    INSERT INTO balance_history (
        account_id, transaction_type, amount, balance_before, balance_after,
        block_id, description, executed_by
    ) VALUES (
        p_account_id, 'BLOCK', p_amount, v_available_balance, p_new_available_balance,
        p_block_id, p_reason, p_executed_by
    );
END;
$$;

-- ============================================================
-- PROCEDIMIENTO: sp_release_block
-- Libera un bloqueo sin ejecutar el débito
-- ============================================================

CREATE OR REPLACE PROCEDURE sp_release_block(
    IN p_block_id UUID,
    IN p_reason VARCHAR(255),
    IN p_executed_by VARCHAR(100),
    OUT p_account_id UUID,
    OUT p_released_amount DECIMAL(18, 2),
    OUT p_new_available_balance DECIMAL(18, 2)
)
LANGUAGE plpgsql AS $$
DECLARE
    v_block_status block_status;
    v_available_before DECIMAL(18, 2);
BEGIN
    -- Obtener y bloquear el registro de bloqueo
    SELECT account_id, amount, status
    INTO p_account_id, p_released_amount, v_block_status
    FROM blocked_amounts
    WHERE id = p_block_id
    FOR UPDATE;

    -- Validar que existe
    IF p_account_id IS NULL THEN
        RAISE EXCEPTION 'Block not found: %', p_block_id
            USING ERRCODE = 'P0002';
    END IF;

    -- Validar que está activo
    IF v_block_status != 'ACTIVE' THEN
        RAISE EXCEPTION 'Block is not active. Status: %', v_block_status
            USING ERRCODE = 'P0006';
    END IF;

    -- Calcular saldo disponible antes de liberar
    v_available_before := fn_available_balance(p_account_id);

    -- Actualizar el bloqueo
    UPDATE blocked_amounts
    SET status = 'RELEASED',
        released_at = CURRENT_TIMESTAMP,
        reason = COALESCE(p_reason, reason)
    WHERE id = p_block_id;

    -- Calcular nuevo saldo disponible
    p_new_available_balance := v_available_before + p_released_amount;

    -- Registrar en historial
    INSERT INTO balance_history (
        account_id, transaction_type, amount, balance_before, balance_after,
        block_id, description, executed_by
    ) VALUES (
        p_account_id, 'RELEASE', p_released_amount, v_available_before, p_new_available_balance,
        p_block_id, p_reason, p_executed_by
    );
END;
$$;

-- ============================================================
-- PROCEDIMIENTO: sp_execute_blocked_debit
-- Convierte un bloqueo activo en un débito real
-- ============================================================

CREATE OR REPLACE PROCEDURE sp_execute_blocked_debit(
    IN p_block_id UUID,
    IN p_transfer_id VARCHAR(100),
    IN p_description VARCHAR(500),
    IN p_executed_by VARCHAR(100),
    IN p_correlation_id VARCHAR(100),
    OUT p_transaction_id UUID,
    OUT p_account_id UUID,
    OUT p_amount DECIMAL(18, 2),
    OUT p_balance_before DECIMAL(18, 2),
    OUT p_balance_after DECIMAL(18, 2)
)
LANGUAGE plpgsql AS $$
DECLARE
    v_block_status block_status;
BEGIN
    -- Obtener y bloquear el registro de bloqueo
    SELECT ba.account_id, ba.amount, ba.status
    INTO p_account_id, p_amount, v_block_status
    FROM blocked_amounts ba
    WHERE ba.id = p_block_id
    FOR UPDATE;

    -- Validar que existe
    IF p_account_id IS NULL THEN
        RAISE EXCEPTION 'Block not found: %', p_block_id
            USING ERRCODE = 'P0002';
    END IF;

    -- Validar que está activo
    IF v_block_status != 'ACTIVE' THEN
        RAISE EXCEPTION 'Block is not active. Status: %', v_block_status
            USING ERRCODE = 'P0006';
    END IF;

    -- Bloquear la cuenta
    SELECT balance INTO p_balance_before
    FROM accounts
    WHERE id = p_account_id
    FOR UPDATE;

    -- Ejecutar el débito
    UPDATE accounts
    SET balance = balance - p_amount,
        updated_at = CURRENT_TIMESTAMP,
        updated_by = p_executed_by
    WHERE id = p_account_id;

    -- Obtener nuevo balance
    SELECT balance INTO p_balance_after
    FROM accounts
    WHERE id = p_account_id;

    -- Marcar el bloqueo como ejecutado
    UPDATE blocked_amounts
    SET status = 'EXECUTED',
        executed_at = CURRENT_TIMESTAMP
    WHERE id = p_block_id;

    -- Registrar en historial
    INSERT INTO balance_history (
        account_id, transaction_type, amount, balance_before, balance_after,
        transfer_id, block_id, description, executed_by, correlation_id
    ) VALUES (
        p_account_id, 'DEBIT', p_amount, p_balance_before, p_balance_after,
        p_transfer_id, p_block_id, p_description, p_executed_by, p_correlation_id
    )
    RETURNING id INTO p_transaction_id;
END;
$$;

-- ============================================================
-- PROCEDIMIENTO: sp_debit_account
-- Ejecuta un débito directo (sin bloqueo previo)
-- ============================================================

CREATE OR REPLACE PROCEDURE sp_debit_account(
    IN p_account_id UUID,
    IN p_amount DECIMAL(18, 2),
    IN p_transfer_id VARCHAR(100),
    IN p_description VARCHAR(500),
    IN p_idempotency_key VARCHAR(100),
    IN p_executed_by VARCHAR(100),
    IN p_correlation_id VARCHAR(100),
    OUT p_transaction_id UUID,
    OUT p_balance_before DECIMAL(18, 2),
    OUT p_balance_after DECIMAL(18, 2),
    OUT p_was_duplicate BOOLEAN
)
LANGUAGE plpgsql AS $$
DECLARE
    v_account_status account_status;
    v_existing_tx_id UUID;
BEGIN
    p_was_duplicate := FALSE;

    -- Verificar idempotencia
    IF p_idempotency_key IS NOT NULL THEN
        SELECT result_transaction_id INTO v_existing_tx_id
        FROM idempotency_keys
        WHERE idempotency_key = p_idempotency_key
          AND operation_type = 'DEBIT'
          AND result_success = TRUE;

        IF v_existing_tx_id IS NOT NULL THEN
            -- Operación ya ejecutada, retornar resultado anterior
            SELECT balance_before, balance_after
            INTO p_balance_before, p_balance_after
            FROM balance_history
            WHERE id = v_existing_tx_id;

            p_transaction_id := v_existing_tx_id;
            p_was_duplicate := TRUE;
            RETURN;
        END IF;
    END IF;

    -- Bloquear la cuenta
    SELECT balance, status INTO p_balance_before, v_account_status
    FROM accounts
    WHERE id = p_account_id
    FOR UPDATE;

    -- Validar que la cuenta existe
    IF p_balance_before IS NULL THEN
        RAISE EXCEPTION 'Account not found: %', p_account_id
            USING ERRCODE = 'P0002';
    END IF;

    -- Validar que la cuenta está activa
    IF v_account_status != 'ACTIVE' THEN
        RAISE EXCEPTION 'Account is not active. Status: %', v_account_status
            USING ERRCODE = 'P0003';
    END IF;

    -- Validar saldo suficiente
    IF p_balance_before < p_amount THEN
        RAISE EXCEPTION 'Insufficient balance. Available: %, Required: %',
            p_balance_before, p_amount
            USING ERRCODE = 'P0004';
    END IF;

    -- Ejecutar el débito
    UPDATE accounts
    SET balance = balance - p_amount,
        updated_at = CURRENT_TIMESTAMP,
        updated_by = p_executed_by
    WHERE id = p_account_id;

    -- Calcular nuevo balance
    p_balance_after := p_balance_before - p_amount;

    -- Registrar en historial
    INSERT INTO balance_history (
        account_id, transaction_type, amount, balance_before, balance_after,
        transfer_id, idempotency_key, description, executed_by, correlation_id
    ) VALUES (
        p_account_id, 'DEBIT', p_amount, p_balance_before, p_balance_after,
        p_transfer_id, p_idempotency_key, p_description, p_executed_by, p_correlation_id
    )
    RETURNING id INTO p_transaction_id;

    -- Registrar idempotencia
    IF p_idempotency_key IS NOT NULL THEN
        INSERT INTO idempotency_keys (
            idempotency_key, operation_type, result_transaction_id, result_success, expires_at
        ) VALUES (
            p_idempotency_key, 'DEBIT', p_transaction_id, TRUE,
            CURRENT_TIMESTAMP + INTERVAL '1 hour'
        );
    END IF;
END;
$$;

-- ============================================================
-- PROCEDIMIENTO: sp_credit_account
-- Acredita fondos a una cuenta
-- ============================================================

CREATE OR REPLACE PROCEDURE sp_credit_account(
    IN p_account_id UUID,
    IN p_amount DECIMAL(18, 2),
    IN p_transfer_id VARCHAR(100),
    IN p_description VARCHAR(500),
    IN p_idempotency_key VARCHAR(100),
    IN p_executed_by VARCHAR(100),
    IN p_correlation_id VARCHAR(100),
    OUT p_transaction_id UUID,
    OUT p_balance_before DECIMAL(18, 2),
    OUT p_balance_after DECIMAL(18, 2),
    OUT p_was_duplicate BOOLEAN
)
LANGUAGE plpgsql AS $$
DECLARE
    v_account_status account_status;
    v_existing_tx_id UUID;
BEGIN
    p_was_duplicate := FALSE;

    -- Verificar idempotencia
    IF p_idempotency_key IS NOT NULL THEN
        SELECT result_transaction_id INTO v_existing_tx_id
        FROM idempotency_keys
        WHERE idempotency_key = p_idempotency_key
          AND operation_type = 'CREDIT'
          AND result_success = TRUE;

        IF v_existing_tx_id IS NOT NULL THEN
            SELECT balance_before, balance_after
            INTO p_balance_before, p_balance_after
            FROM balance_history
            WHERE id = v_existing_tx_id;

            p_transaction_id := v_existing_tx_id;
            p_was_duplicate := TRUE;
            RETURN;
        END IF;
    END IF;

    -- Bloquear la cuenta
    SELECT balance, status INTO p_balance_before, v_account_status
    FROM accounts
    WHERE id = p_account_id
    FOR UPDATE;

    -- Validar que la cuenta existe
    IF p_balance_before IS NULL THEN
        RAISE EXCEPTION 'Account not found: %', p_account_id
            USING ERRCODE = 'P0002';
    END IF;

    -- Validar que la cuenta está activa (para créditos también validamos)
    IF v_account_status NOT IN ('ACTIVE', 'INACTIVE') THEN
        RAISE EXCEPTION 'Account cannot receive credits. Status: %', v_account_status
            USING ERRCODE = 'P0003';
    END IF;

    -- Ejecutar el crédito
    UPDATE accounts
    SET balance = balance + p_amount,
        updated_at = CURRENT_TIMESTAMP,
        updated_by = p_executed_by
    WHERE id = p_account_id;

    -- Calcular nuevo balance
    p_balance_after := p_balance_before + p_amount;

    -- Registrar en historial
    INSERT INTO balance_history (
        account_id, transaction_type, amount, balance_before, balance_after,
        transfer_id, idempotency_key, description, executed_by, correlation_id
    ) VALUES (
        p_account_id, 'CREDIT', p_amount, p_balance_before, p_balance_after,
        p_transfer_id, p_idempotency_key, p_description, p_executed_by, p_correlation_id
    )
    RETURNING id INTO p_transaction_id;

    -- Registrar idempotencia
    IF p_idempotency_key IS NOT NULL THEN
        INSERT INTO idempotency_keys (
            idempotency_key, operation_type, result_transaction_id, result_success, expires_at
        ) VALUES (
            p_idempotency_key, 'CREDIT', p_transaction_id, TRUE,
            CURRENT_TIMESTAMP + INTERVAL '1 hour'
        );
    END IF;
END;
$$;

-- ============================================================
-- PROCEDIMIENTO: sp_reverse_debit
-- Revierte un débito previo (compensating transaction)
-- ============================================================

CREATE OR REPLACE PROCEDURE sp_reverse_debit(
    IN p_original_transaction_id UUID,
    IN p_transfer_id VARCHAR(100),
    IN p_reason VARCHAR(500),
    IN p_executed_by VARCHAR(100),
    IN p_correlation_id VARCHAR(100),
    OUT p_reversal_id UUID,
    OUT p_account_id UUID,
    OUT p_amount DECIMAL(18, 2),
    OUT p_balance_before DECIMAL(18, 2),
    OUT p_balance_after DECIMAL(18, 2)
)
LANGUAGE plpgsql AS $$
DECLARE
    v_original_type transaction_type;
    v_already_reversed BOOLEAN;
BEGIN
    -- Obtener la transacción original
    SELECT account_id, amount, transaction_type
    INTO p_account_id, p_amount, v_original_type
    FROM balance_history
    WHERE id = p_original_transaction_id;

    -- Validar que existe
    IF p_account_id IS NULL THEN
        RAISE EXCEPTION 'Original transaction not found: %', p_original_transaction_id
            USING ERRCODE = 'P0002';
    END IF;

    -- Validar que era un débito
    IF v_original_type != 'DEBIT' THEN
        RAISE EXCEPTION 'Can only reverse DEBIT transactions. Original type: %', v_original_type
            USING ERRCODE = 'P0007';
    END IF;

    -- Verificar que no ha sido ya revertida
    SELECT EXISTS(
        SELECT 1 FROM balance_history
        WHERE transaction_type = 'REVERSAL'
          AND description LIKE '%' || p_original_transaction_id::TEXT || '%'
    ) INTO v_already_reversed;

    IF v_already_reversed THEN
        RAISE EXCEPTION 'Transaction already reversed: %', p_original_transaction_id
            USING ERRCODE = 'P0008';
    END IF;

    -- Bloquear la cuenta
    SELECT balance INTO p_balance_before
    FROM accounts
    WHERE id = p_account_id
    FOR UPDATE;

    -- Ejecutar la reversión (crédito)
    UPDATE accounts
    SET balance = balance + p_amount,
        updated_at = CURRENT_TIMESTAMP,
        updated_by = p_executed_by
    WHERE id = p_account_id;

    -- Calcular nuevo balance
    p_balance_after := p_balance_before + p_amount;

    -- Registrar en historial
    INSERT INTO balance_history (
        account_id, transaction_type, amount, balance_before, balance_after,
        transfer_id, description, executed_by, correlation_id
    ) VALUES (
        p_account_id, 'REVERSAL', p_amount, p_balance_before, p_balance_after,
        p_transfer_id,
        'Reversal of transaction ' || p_original_transaction_id::TEXT || '. Reason: ' || p_reason,
        p_executed_by, p_correlation_id
    )
    RETURNING id INTO p_reversal_id;
END;
$$;

-- ============================================================
-- PROCEDIMIENTO: sp_expire_blocks
-- Expira bloqueos que han superado su tiempo límite
-- (Ejecutar periódicamente con un scheduler)
-- ============================================================

CREATE OR REPLACE PROCEDURE sp_expire_blocks()
LANGUAGE plpgsql AS $$
DECLARE
    v_expired_count INT;
BEGIN
    UPDATE blocked_amounts
    SET status = 'EXPIRED'
    WHERE status = 'ACTIVE'
      AND expires_at < CURRENT_TIMESTAMP;

    GET DIAGNOSTICS v_expired_count = ROW_COUNT;

    IF v_expired_count > 0 THEN
        RAISE NOTICE 'Expired % blocked amounts', v_expired_count;
    END IF;
END;
$$;

-- ============================================================
-- PROCEDIMIENTO: sp_cleanup_idempotency_keys
-- Limpia registros de idempotencia expirados
-- (Ejecutar periódicamente)
-- ============================================================

CREATE OR REPLACE PROCEDURE sp_cleanup_idempotency_keys()
LANGUAGE plpgsql AS $$
DECLARE
    v_deleted_count INT;
BEGIN
    DELETE FROM idempotency_keys
    WHERE expires_at < CURRENT_TIMESTAMP;

    GET DIAGNOSTICS v_deleted_count = ROW_COUNT;

    IF v_deleted_count > 0 THEN
        RAISE NOTICE 'Deleted % expired idempotency keys', v_deleted_count;
    END IF;
END;
$$;
