package ec.com.finflow.validation.service.rules;

import ec.com.finflow.validation.dto.request.ValidateTransferRequest;
import ec.com.finflow.validation.dto.response.FraudIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Regla: Detecta múltiples transferencias en un corto período.
 */
@Component
public class RapidTransfersRule implements FraudRule {

    @Value("${finflow.validation.default-max-transfers-per-hour:10}")
    private int maxTransfersPerHour;

    @Value("${finflow.validation.score-weights.rapid-succession:15}")
    private int scoreImpact;

    @Override
    public String getCode() {
        return "RAPID_TRANSFERS";
    }

    @Override
    public String getName() {
        return "Transferencias rápidas";
    }

    @Override
    public int getOrder() {
        return 40;
    }

    @Override
    public Optional<FraudIndicator> evaluate(ValidateTransferRequest request, FraudRuleContext context) {
        int maxAllowed = context.getMaxTransfersPerHour() > 0 ?
                context.getMaxTransfersPerHour() : maxTransfersPerHour;

        // Si ya está cerca del límite (80%), es sospechoso
        int threshold = (int) (maxAllowed * 0.8);

        if (context.getTransfersLastHour() >= threshold) {
            return Optional.of(FraudIndicator.rapidTransfers(
                    context.getTransfersLastHour(),
                    1,
                    scoreImpact
            ));
        }
        return Optional.empty();
    }
}
