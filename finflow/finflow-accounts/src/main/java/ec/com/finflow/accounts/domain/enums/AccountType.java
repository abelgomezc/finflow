package ec.com.finflow.accounts.domain.enums;

/**
 * Tipos de cuenta bancaria disponibles.
 */
public enum AccountType {
    /**
     * Cuenta corriente para transacciones diarias.
     */
    CHECKING,

    /**
     * Cuenta de ahorros con posibles beneficios de interés.
     */
    SAVINGS,

    /**
     * Cuenta empresarial con límites más altos.
     */
    BUSINESS
}
