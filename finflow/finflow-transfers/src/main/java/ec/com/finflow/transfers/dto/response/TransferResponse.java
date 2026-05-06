package ec.com.finflow.transfers.dto.response;

import ec.com.finflow.transfers.domain.enums.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Response de una transferencia.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponse {

    private Long id;
    private String referenceNumber;
    private Long sourceAccountId;
    private String sourceAccountNumber;
    private Long targetAccountId;
    private String targetAccountNumber;
    private BigDecimal amount;
    private String currency;
    private TransferStatus status;
    private String description;
    private String initiatedBy;

    // Información de error (si aplica)
    private String failureReason;
    private String failureMessage;

    // Saldos después de la transferencia
    private BigDecimal sourceBalanceAfter;
    private BigDecimal targetBalanceAfter;

    // Timestamps
    private OffsetDateTime initiatedAt;
    private OffsetDateTime completedAt;
    private OffsetDateTime failedAt;

    // Metadata
    private String correlationId;
}
