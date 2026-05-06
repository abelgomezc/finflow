package ec.com.finflow.accounts.domain.enums;

/**
 * Tipos de transacción en el historial de balance.
 */
public enum TransactionType {
    /**
     * Débito: salida de fondos de la cuenta.
     */
    DEBIT,

    /**
     * Crédito: entrada de fondos a la cuenta.
     */
    CREDIT,

    /**
     * Bloqueo: reserva de fondos para una transferencia pendiente.
     */
    BLOCK,

    /**
     * Liberación: libera fondos bloqueados sin ejecutar débito.
     */
    RELEASE,

    /**
     * Reversión: anula un débito previo (compensating transaction).
     */
    REVERSAL
}
