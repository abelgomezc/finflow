package ec.com.finflow.accounts.domain.enums;

/**
 * Estados de un monto bloqueado.
 */
public enum BlockStatus {
    /**
     * Bloqueo activo, fondos reservados.
     */
    ACTIVE,

    /**
     * Bloqueo ejecutado: se convirtió en débito real.
     */
    EXECUTED,

    /**
     * Bloqueo liberado: se canceló sin ejecutar débito.
     */
    RELEASED,

    /**
     * Bloqueo expirado: superó el tiempo límite.
     */
    EXPIRED
}
