package ec.com.finflow.validation.service.rules;

import ec.com.finflow.validation.dto.request.ValidateTransferRequest;
import ec.com.finflow.validation.dto.response.FraudIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Regla: Detecta montos inusualmente altos para el perfil del usuario.
 */
@Component
public class UnusualAmountRule implements FraudRule {

    @Value("${finflow.validation.amount-anomaly-multiplier:3.0}")
    private double anomalyMultiplier;

    @Value("${finflow.validation.score-weights.unusual-amount:40}")
    private int scoreImpact;

    @Override
    public String getCode() {
        return "UNUSUAL_AMOUNT";
    }

    @Override
    public String getName() {
        return "Monto inusual";
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public Optional<FraudIndicator> evaluate(ValidateTransferRequest request, FraudRuleContext context) {
        // Si no hay perfil o no tiene historial suficiente, no evaluar
        if (context.getUserProfile() == null || !context.getUserProfile().getHasSufficientHistory()) {
            return Optional.empty();
        }

        BigDecimal userAvg = context.getUserProfile().getAvgTransferAmount();
        if (userAvg == null || userAvg.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }

        BigDecimal threshold = userAvg.multiply(BigDecimal.valueOf(anomalyMultiplier));

        if (request.getAmount().compareTo(threshold) > 0) {
            return Optional.of(FraudIndicator.unusualAmount(
                    userAvg.toString(),
                    request.getAmount().toString(),
                    scoreImpact
            ));
        }

        return Optional.empty();
    }
}
