package ec.com.finflow.validation.service.rules;

import ec.com.finflow.validation.dto.request.ValidateTransferRequest;
import ec.com.finflow.validation.dto.response.FraudIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Regla: Detecta primera transferencia a un nuevo destinatario.
 */
@Component
public class FirstTimeRecipientRule implements FraudRule {

    @Value("${finflow.validation.score-weights.first-time-recipient:10}")
    private int scoreImpact;

    @Override
    public String getCode() {
        return "FIRST_TIME_RECIPIENT";
    }

    @Override
    public String getName() {
        return "Destinatario nuevo";
    }

    @Override
    public int getOrder() {
        return 30;
    }

    @Override
    public Optional<FraudIndicator> evaluate(ValidateTransferRequest request, FraudRuleContext context) {
        if (context.isFirstTimeRecipient()) {
            return Optional.of(FraudIndicator.firstTimeRecipient(
                    request.getTargetAccountId().toString(),
                    scoreImpact
            ));
        }
        return Optional.empty();
    }
}
