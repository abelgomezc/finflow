package ec.com.finflow.notifications.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Evento de transferencia recibido de Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferNotificationEvent {

    private String eventId;
    private String eventType;
    private OffsetDateTime timestamp;

    private UUID transferId;
    private String referenceNumber;
    private UUID sourceAccountId;
    private UUID targetAccountId;
    private BigDecimal amount;
    private String currency;

    private String status;
    private String failureReason;
    private String failureMessage;
    private String reversalReason;

    private String initiatedBy;
    private String correlationId;
}
