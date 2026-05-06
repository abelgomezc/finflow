package ec.com.finflow.transfers.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Evento de transferencia para Kafka.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferEvent {

    private String eventType;  // INITIATED, COMPLETED, FAILED, REVERSED
    private Long transferId;
    private String referenceNumber;
    private Long sourceAccountId;
    private Long targetAccountId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String initiatedBy;
    private String failureReason;
    private String failureMessage;
    private OffsetDateTime eventTimestamp;
    private String correlationId;

    /**
     * Crea evento de transferencia iniciada.
     */
    public static TransferEvent initiated(Long transferId, String referenceNumber,
                                          Long sourceAccountId, Long targetAccountId,
                                          BigDecimal amount, String currency,
                                          String initiatedBy, String correlationId) {
        return TransferEvent.builder()
                .eventType("TRANSFER_INITIATED")
                .transferId(transferId)
                .referenceNumber(referenceNumber)
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(amount)
                .currency(currency)
                .status("PENDING")
                .initiatedBy(initiatedBy)
                .eventTimestamp(OffsetDateTime.now())
                .correlationId(correlationId)
                .build();
    }

    /**
     * Crea evento de transferencia completada.
     */
    public static TransferEvent completed(Long transferId, String referenceNumber,
                                          Long sourceAccountId, Long targetAccountId,
                                          BigDecimal amount, String currency,
                                          String initiatedBy, String correlationId) {
        return TransferEvent.builder()
                .eventType("TRANSFER_COMPLETED")
                .transferId(transferId)
                .referenceNumber(referenceNumber)
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(amount)
                .currency(currency)
                .status("COMPLETED")
                .initiatedBy(initiatedBy)
                .eventTimestamp(OffsetDateTime.now())
                .correlationId(correlationId)
                .build();
    }

    /**
     * Crea evento de transferencia fallida.
     */
    public static TransferEvent failed(Long transferId, String referenceNumber,
                                       Long sourceAccountId, Long targetAccountId,
                                       BigDecimal amount, String currency,
                                       String failureReason, String failureMessage,
                                       String initiatedBy, String correlationId) {
        return TransferEvent.builder()
                .eventType("TRANSFER_FAILED")
                .transferId(transferId)
                .referenceNumber(referenceNumber)
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(amount)
                .currency(currency)
                .status("FAILED")
                .failureReason(failureReason)
                .failureMessage(failureMessage)
                .initiatedBy(initiatedBy)
                .eventTimestamp(OffsetDateTime.now())
                .correlationId(correlationId)
                .build();
    }

    /**
     * Crea evento de transferencia revertida.
     */
    public static TransferEvent reversed(Long transferId, String referenceNumber,
                                         Long sourceAccountId, Long targetAccountId,
                                         BigDecimal amount, String currency,
                                         String reason, String initiatedBy,
                                         String correlationId) {
        return TransferEvent.builder()
                .eventType("TRANSFER_REVERSED")
                .transferId(transferId)
                .referenceNumber(referenceNumber)
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(amount)
                .currency(currency)
                .status("REVERSED")
                .failureReason(reason)
                .initiatedBy(initiatedBy)
                .eventTimestamp(OffsetDateTime.now())
                .correlationId(correlationId)
                .build();
    }
}
