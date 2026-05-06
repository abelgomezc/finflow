package ec.com.finflow.validation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Request para validar una transferencia.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateTransferRequest {

    @NotBlank(message = "Transfer ID is required")
    private String transferId;

    @NotNull(message = "Source account ID is required")
    private Long sourceAccountId;

    @NotNull(message = "Target account ID is required")
    private Long targetAccountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Builder.Default
    private String currency = "USD";

    @NotBlank(message = "User ID is required")
    private String userId;

    private String ipAddress;
    private String userAgent;
    private String deviceFingerprint;

    @Builder.Default
    private OffsetDateTime initiatedAt = OffsetDateTime.now();

    private String description;
    private String correlationId;

    // Opciones de validación
    @Builder.Default
    private boolean skipFraudCheck = false;

    private Integer maxFraudScoreAllowed;  // Override del umbral (default: 70)
}
