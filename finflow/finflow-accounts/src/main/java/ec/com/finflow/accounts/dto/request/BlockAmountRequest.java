package ec.com.finflow.accounts.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para bloquear (reservar) un monto en una cuenta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockAmountRequest {

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Size(max = 255, message = "Reason must not exceed 255 characters")
    private String reason;

    @Positive(message = "Expiration minutes must be positive")
    @Builder.Default
    private Integer expirationMinutes = 15;

    @Size(max = 100)
    private String executedBy;

    @Size(max = 100)
    private String correlationId;
}
