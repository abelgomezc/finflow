package ec.com.finflow.accounts.domain.exception;

import lombok.Getter;

import java.util.UUID;

/**
 * Excepciones relacionadas con transacciones financieras.
 */
@Getter
public class TransactionException extends FinFlowException {

    private final UUID transactionId;

    public TransactionException(String message, UUID transactionId) {
        super(message, "TRANSACTION_ERROR");
        this.transactionId = transactionId;
    }

    public TransactionException(String message, String errorCode, UUID transactionId) {
        super(message, errorCode);
        this.transactionId = transactionId;
    }

    /**
     * Transacción no encontrada.
     */
    public static TransactionException notFound(UUID transactionId) {
        return new TransactionException(
                "Transaction not found: " + transactionId,
                "TRANSACTION_NOT_FOUND",
                transactionId
        );
    }

    /**
     * Transacción ya revertida.
     */
    public static TransactionException alreadyReversed(UUID transactionId) {
        return new TransactionException(
                "Transaction already reversed: " + transactionId,
                "TRANSACTION_ALREADY_REVERSED",
                transactionId
        );
    }

    /**
     * Tipo de transacción inválido para la operación.
     */
    public static TransactionException invalidType(UUID transactionId, String expectedType, String actualType) {
        return new TransactionException(
                String.format("Invalid transaction type. Expected: %s, Actual: %s", expectedType, actualType),
                "INVALID_TRANSACTION_TYPE",
                transactionId
        );
    }

    /**
     * Operación duplicada (idempotencia).
     */
    public static TransactionException duplicate(String idempotencyKey, UUID originalTransactionId) {
        return new TransactionException(
                "Duplicate operation detected. Original transaction: " + originalTransactionId,
                "DUPLICATE_OPERATION",
                originalTransactionId
        );
    }
}
