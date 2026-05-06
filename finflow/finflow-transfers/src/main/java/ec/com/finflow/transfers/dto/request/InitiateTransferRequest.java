package ec.com.finflow.transfers.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request para iniciar una transferencia.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiateTransferRequest {

    @NotNull(message = "Source account ID is required")
    private Long sourceAccountId;

    @NotNull(message = "Target account ID is required")
    private Long targetAccountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Size(max = 3, message = "Currency must be 3 characters")
    @Builder.Default
    private String currency = "USD";

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    // userId viene del header X-User-ID inyectado por el Gateway
    private String userId;

    // Metadata adicional
    private String ipAddress;
    private String userAgent;
    private String correlationId;
}
