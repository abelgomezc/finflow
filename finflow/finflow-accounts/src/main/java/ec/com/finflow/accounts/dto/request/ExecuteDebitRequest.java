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
 * DTO para ejecutar un débito en una cuenta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecuteDebitRequest {

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Size(max = 100, message = "Transfer ID must not exceed 100 characters")
    private String transferId;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 100, message = "Idempotency key must not exceed 100 characters")
    private String idempotencyKey;

    @Size(max = 100)
    private String executedBy;

    @Size(max = 100)
    private String correlationId;
}
