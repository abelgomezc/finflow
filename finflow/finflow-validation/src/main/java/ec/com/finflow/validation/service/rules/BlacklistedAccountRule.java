package ec.com.finflow.validation.service.rules;

import ec.com.finflow.validation.dto.request.ValidateTransferRequest;
import ec.com.finflow.validation.dto.response.FraudIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Regla: Rechaza transferencias a cuentas en lista negra.
 * Esta regla siempre debería causar rechazo (score = 100).
 */
@Component
public class BlacklistedAccountRule implements FraudRule {

    @Value("${finflow.validation.score-weights.blacklisted-recipient:100}")
    private int scoreImpact;

    @Override
    public String getCode() {
        return "BLACKLISTED_ACCOUNT";
    }

    @Override
    public String getName() {
        return "Cuenta en lista negra";
    }

    @Override
    public int getOrder() {
        return 1;  // Evaluar primero
    }

    @Override
    public Optional<FraudIndicator> evaluate(ValidateTransferRequest request, FraudRuleContext context) {
        if (context.isTargetIsBlacklisted()) {
            return Optional.of(FraudIndicator.blacklistedAccount(
                    context.getBlacklistReason() != null ?
                            context.getBlacklistReason() : "Account is blacklisted",
                    scoreImpact
            ));
        }
        return Optional.empty();
    }
}
