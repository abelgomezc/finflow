package ec.com.finflow.transfers.domain.enums;

/**
 * Estados de una transferencia.
 * Define las transiciones válidas del estado.
 */
public enum TransferStatus {
    /**
     * Creada, pendiente de validación.
     */
    PENDING,

    /**
     * En proceso de validación anti-fraude.
     */
    VALIDATING,

    /**
     * Validación OK, ejecutando débito/crédito.
     */
    PROCESSING,

    /**
     * Completada exitosamente.
     */
    COMPLETED,

    /**
     * Fallida por validación o error.
     */
    FAILED,

    /**
     * Revertida (compensating transaction ejecutada).
     */
    REVERSED,

    /**
     * Cancelada por el usuario antes de procesar.
     */
    CANCELLED;

    /**
     * Verifica si esta transición de estado es válida.
     */
    public boolean canTransitionTo(TransferStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == VALIDATING || newStatus == FAILED || newStatus == CANCELLED;
            case VALIDATING -> newStatus == PROCESSING || newStatus == FAILED;
            case PROCESSING -> newStatus == COMPLETED || newStatus == FAILED || newStatus == REVERSED;
            case COMPLETED -> newStatus == REVERSED;  // Para reversar una completada
            case FAILED, REVERSED, CANCELLED -> false;  // Estados finales
        };
    }

    /**
     * Verifica si es un estado final.
     */
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == REVERSED || this == CANCELLED;
    }
}
