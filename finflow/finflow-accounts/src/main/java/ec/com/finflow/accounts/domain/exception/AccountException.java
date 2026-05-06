package ec.com.finflow.accounts.domain.exception;

import lombok.Getter;

/**
 * Excepciones relacionadas con operaciones de cuentas.
 */
@Getter
public class AccountException extends FinFlowException {

    private final Long accountId;

    public AccountException(String message, Long accountId) {
        super(message, "ACCOUNT_ERROR");
        this.accountId = accountId;
    }

    public AccountException(String message, String errorCode, Long accountId) {
        super(message, errorCode);
        this.accountId = accountId;
    }

    /**
     * Cuenta no encontrada.
     */
    public static AccountException notFound(Long accountId) {
        return new AccountException(
                "Account not found: " + accountId,
                "ACCOUNT_NOT_FOUND",
                accountId
        );
    }

    /**
     * Cuenta no encontrada por número.
     */
    public static AccountException notFoundByNumber(String accountNumber) {
        return new AccountException(
                "Account not found with number: " + accountNumber,
                "ACCOUNT_NOT_FOUND",
                null
        );
    }

    /**
     * Cuenta no está activa.
     */
    public static AccountException notActive(Long accountId, String currentStatus) {
        return new AccountException(
                "Account is not active. Current status: " + currentStatus,
                "ACCOUNT_NOT_ACTIVE",
                accountId
        );
    }

    /**
     * Cuenta bloqueada.
     */
    public static AccountException blocked(Long accountId) {
        return new AccountException(
                "Account is blocked",
                "ACCOUNT_BLOCKED",
                accountId
        );
    }

    /**
     * Saldo insuficiente.
     */
    public static AccountException insufficientFunds(Long accountId, java.math.BigDecimal required, java.math.BigDecimal available) {
        return new AccountException(
                String.format("Insufficient funds. Available: %s, Required: %s", available, required),
                "INSUFFICIENT_FUNDS",
                accountId
        );
    }

    /**
     * Saldo disponible insuficiente (considerando bloqueos).
     */
    public static AccountException insufficientAvailableBalance(Long accountId, java.math.BigDecimal required, java.math.BigDecimal available) {
        return new AccountException(
                String.format("Insufficient available balance. Available: %s, Required: %s", available, required),
                "INSUFFICIENT_AVAILABLE_BALANCE",
                accountId
        );
    }
}
