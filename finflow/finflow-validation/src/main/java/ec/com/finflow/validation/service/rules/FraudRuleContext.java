package ec.com.finflow.validation.service.rules;

import ec.com.finflow.validation.domain.entity.UserTransferProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Contexto para evaluación de reglas de fraude.
 * Contiene información adicional necesaria para las reglas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudRuleContext {

    private UserTransferProfile userProfile;

    // Datos de la cuenta origen
    private BigDecimal availableBalance;
    private BigDecimal dailyTransferTotal;
    private int transfersLastHour;

    // Límites configurados
    private BigDecimal perTransferLimit;
    private BigDecimal dailyLimit;
    private int maxTransfersPerHour;

    // Estado de cuenta destino
    private boolean targetIsBlacklisted;
    private String blacklistReason;

    // Indica si es primera vez que transfiere a este destino
    private boolean isFirstTimeRecipient;
}
