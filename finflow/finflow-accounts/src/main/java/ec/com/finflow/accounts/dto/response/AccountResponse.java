package ec.com.finflow.accounts.dto.response;

import ec.com.finflow.accounts.domain.enums.AccountStatus;
import ec.com.finflow.accounts.domain.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO de respuesta para información de cuenta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private Long customerId;
    private String customerName;
    private String customerDocumentType;
    private String customerDocumentNumber;
    private AccountType accountType;
    private AccountStatus status;
    private String currency;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private BigDecimal blockedAmount;
    private BigDecimal dailyTransferLimit;
    private BigDecimal perTransferLimit;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
