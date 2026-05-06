package ec.com.finflow.accounts.dto.response;

import ec.com.finflow.accounts.domain.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO de respuesta para transacciones (débito, crédito, reversión).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long transactionId;
    private Long accountId;
    private String transferId;
    private TransactionType type;
    private BigDecimal amount;
    private String currency;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private OffsetDateTime executedAt;
    private boolean wasDuplicate;
    private Long originalTransactionId;
}
