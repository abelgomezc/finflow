package ec.com.finflow.validation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Resultado de verificación de límites.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LimitCheckResult {

    // Límite por transferencia
    private BigDecimal perTransferLimit;
    private boolean perTransferOk;

    // Límite diario
    private BigDecimal dailyLimit;
    private BigDecimal dailyUsed;
    private BigDecimal dailyRemaining;
    private boolean dailyLimitOk;

    // Límite de frecuencia
    private int maxTransfersPerHour;
    private int transfersLastHour;
    private boolean frequencyOk;

    /**
     * Verifica si todos los límites están OK.
     */
    public boolean allLimitsOk() {
        return perTransferOk && dailyLimitOk && frequencyOk;
    }
}
