package ec.com.finflow.validation.domain.enums;

/**
 * Razones de rechazo de una validación de transferencia.
 */
public enum RejectionReason {
    // Errores de cuenta origen
    SOURCE_ACCOUNT_NOT_FOUND("La cuenta origen no existe"),
    SOURCE_ACCOUNT_INACTIVE("La cuenta origen no está activa"),
    SOURCE_ACCOUNT_BLOCKED("La cuenta origen está bloqueada"),

    // Errores de cuenta destino
    TARGET_ACCOUNT_NOT_FOUND("La cuenta destino no existe"),
    TARGET_ACCOUNT_INACTIVE("La cuenta destino no está activa"),
    TARGET_ACCOUNT_BLOCKED("La cuenta destino está bloqueada"),

    // Errores de saldo
    INSUFFICIENT_FUNDS("Saldo insuficiente"),
    INSUFFICIENT_AVAILABLE_BALANCE("Saldo disponible insuficiente"),

    // Límites excedidos
    AMOUNT_EXCEEDS_TRANSFER_LIMIT("El monto excede el límite por transferencia"),
    DAILY_LIMIT_EXCEEDED("Se ha excedido el límite diario"),
    MONTHLY_LIMIT_EXCEEDED("Se ha excedido el límite mensual"),
    TRANSFERS_PER_HOUR_EXCEEDED("Se ha excedido el límite de transferencias por hora"),

    // Fraude
    BLACKLISTED_ACCOUNT("La cuenta destino está en lista negra"),
    SUSPICIOUS_ACTIVITY("Actividad sospechosa detectada"),
    UNUSUAL_AMOUNT("Monto inusualmente alto para este usuario"),
    UNUSUAL_TIME("Transferencia en horario inusual"),
    HIGH_FRAUD_SCORE("Score de fraude elevado"),

    // Otros
    SAME_ACCOUNT_TRANSFER("No se puede transferir a la misma cuenta"),
    INVALID_CURRENCY("Moneda no válida o no coincide"),
    SYSTEM_ERROR("Error del sistema");

    private final String description;

    RejectionReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
