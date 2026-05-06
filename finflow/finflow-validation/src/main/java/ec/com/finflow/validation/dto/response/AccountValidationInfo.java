package ec.com.finflow.validation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Información de validación de una cuenta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountValidationInfo {

    private Long accountId;
    private boolean exists;
    private boolean isActive;
    private String status;
    private boolean isBlacklisted;
    private String blacklistReason;
    private BigDecimal availableBalance;  // Solo para cuenta origen
}
