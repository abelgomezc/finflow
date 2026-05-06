package ec.com.finflow.accounts.dto.request;

import ec.com.finflow.accounts.domain.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para crear una nueva cuenta bancaria.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Account type is required")
    @Builder.Default
    private AccountType accountType = AccountType.CHECKING;

    @Size(max = 3, message = "Currency code must be 3 characters")
    @Builder.Default
    private String currency = "USD";

    @Positive(message = "Initial balance must be positive")
    @Builder.Default
    private BigDecimal initialBalance = BigDecimal.ZERO;

    @Positive(message = "Daily transfer limit must be positive")
    private BigDecimal dailyTransferLimit;

    @Positive(message = "Per transfer limit must be positive")
    private BigDecimal perTransferLimit;
}
