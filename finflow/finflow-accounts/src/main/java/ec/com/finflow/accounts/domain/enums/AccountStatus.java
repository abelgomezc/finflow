package ec.com.finflow.accounts.domain.enums;

/**
 * Estados posibles de una cuenta bancaria.
 */
public enum AccountStatus {
    /**
     * Cuenta activa y operativa. Puede enviar y recibir transferencias.
     */
    ACTIVE,

    /**
     * Cuenta inactiva. No puede iniciar operaciones pero puede recibir fondos.
     */
    INACTIVE,

    /**
     * Cuenta bloqueada por seguridad o detección de fraude.
     * No puede realizar ni recibir operaciones.
     */
    BLOCKED,

    /**
     * Cuenta cerrada permanentemente.
     */
    CLOSED
}
