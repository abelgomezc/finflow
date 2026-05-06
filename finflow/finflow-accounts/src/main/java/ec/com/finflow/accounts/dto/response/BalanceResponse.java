package ec.com.finflow.accounts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO de respuesta para información de balance.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceResponse {

    private Long accountId;
    private String accountNumber;
    private String currency;
    private BigDecimal currentBalance;
    private BigDecimal availableBalance;
    private BigDecimal blockedAmount;
    private OffsetDateTime asOf;
}
