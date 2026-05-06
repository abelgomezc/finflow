package ec.com.finflow.audit.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Evento genérico recibido de Kafka para auditoría.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditEvent {

    private String eventId;
    private String eventType;
    private OffsetDateTime timestamp;

    // Transfer data
    private UUID transferId;
    private String referenceNumber;
    private UUID sourceAccountId;
    private UUID targetAccountId;
    private BigDecimal amount;
    private String currency;

    // Status
    private String status;
    private String failureReason;
    private String failureMessage;
    private String reversalReason;

    // User data
    private String initiatedBy;
    private String correlationId;
    private String ipAddress;

    // Account data (para eventos de cuentas)
    private UUID accountId;
    private UUID blockId;
    private UUID transactionId;
    private String transactionType;
    private BigDecimal previousBalance;
    private BigDecimal newBalance;

    // Metadata adicional
    private String sourceService;
}
