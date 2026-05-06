package ec.com.finflow.accounts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO de respuesta para operaciones de bloqueo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockResponse {

    private Long blockId;
    private Long accountId;
    private BigDecimal blockedAmount;
    private BigDecimal newAvailableBalance;
    private String status;
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;
}
