package ec.com.finflow.accounts.service;

import ec.com.finflow.accounts.dto.request.*;
import ec.com.finflow.accounts.dto.response.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz del servicio de cuentas.
 * Define las operaciones de negocio para gestión de cuentas.
 */
public interface AccountService {

    // ============================================================
    // Operaciones CRUD
    // ============================================================

    /**
     * Crea una nueva cuenta bancaria.
     */
    AccountResponse createAccount(CreateAccountRequest request);

    /**
     * Obtiene una cuenta por ID.
     */
    AccountResponse getAccount(Long accountId);

    /**
     * Obtiene una cuenta por número.
     */
    AccountResponse getAccountByNumber(String accountNumber);

    /**
     * Obtiene todas las cuentas de un cliente.
     */
    List<AccountResponse> getAccountsByCustomer(Long customerId);

    /**
     * Obtiene todas las cuentas de un usuario (por user ID).
     * Busca el customer_id asociado al usuario y devuelve sus cuentas.
     */
    List<AccountResponse> getAccountsByUserId(Long userId);

    /**
     * Valida si una cuenta existe y está activa.
     */
    boolean validateAccount(Long accountId);

    /**
     * Valida cuenta por número.
     */
    boolean validateAccountByNumber(String accountNumber);

    // ============================================================
    // Operaciones de Balance
    // ============================================================

    /**
     * Obtiene el balance de una cuenta (total y disponible).
     */
    BalanceResponse getBalance(Long accountId);

    /**
     * Obtiene el saldo disponible (balance - bloqueados).
     */
    BigDecimal getAvailableBalance(Long accountId);

    /**
     * Obtiene el total transferido en el día.
     */
    BigDecimal getDailyTransferTotal(Long accountId);

    /**
     * Cuenta transferencias en las últimas N horas.
     */
    int countRecentTransfers(Long accountId, int hours);

    // ============================================================
    // Operaciones de Bloqueo (Saga Pattern)
    // ============================================================

    /**
     * Bloquea (reserva) un monto en una cuenta.
     * Usado antes de iniciar una transferencia.
     */
    BlockResponse blockAmount(BlockAmountRequest request);

    /**
     * Libera un bloqueo sin ejecutar débito.
     * Usado cuando se cancela una transferencia.
     */
    BlockResponse releaseBlock(Long blockId, String reason, String executedBy);

    /**
     * Libera un bloqueo por referencia.
     */
    BlockResponse releaseBlockByReference(String blockReference, String reason, String executedBy);

    // ============================================================
    // Operaciones Financieras
    // ============================================================

    /**
     * Ejecuta un débito usando un bloqueo previo.
     * Convierte el monto bloqueado en débito real.
     */
    TransactionResponse executeBlockedDebit(Long blockId, String transferId,
                                            String description, String executedBy,
                                            String correlationId);

    /**
     * Ejecuta un débito directo (sin bloqueo previo).
     */
    TransactionResponse executeDebit(ExecuteDebitRequest request);

    /**
     * Ejecuta un crédito en una cuenta.
     */
    TransactionResponse executeCredit(ExecuteCreditRequest request);

    /**
     * Revierte un débito previo (compensating transaction).
     */
    TransactionResponse reverseDebit(Long originalTransactionId, String transferId,
                                     String reason, String executedBy, String correlationId);
}
