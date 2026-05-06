package ec.com.finflow.validation.service.rules;

import ec.com.finflow.validation.dto.request.ValidateTransferRequest;
import ec.com.finflow.validation.dto.response.FraudIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Regla: Detecta transferencias en horarios inusuales (nocturnos).
 */
@Component
public class UnusualHourRule implements FraudRule {

    @Value("${finflow.validation.suspicious-hours-start:2}")
    private int suspiciousHourStart;

    @Value("${finflow.validation.suspicious-hours-end:5}")
    private int suspiciousHourEnd;

    @Value("${finflow.validation.score-weights.unusual-hour:20}")
    private int scoreImpact;

    @Override
    public String getCode() {
        return "UNUSUAL_HOUR";
    }

    @Override
    public String getName() {
        return "Hora inusual";
    }

    @Override
    public int getOrder() {
        return 20;
    }

    @Override
    public Optional<FraudIndicator> evaluate(ValidateTransferRequest request, FraudRuleContext context) {
        OffsetDateTime initiatedAt = request.getInitiatedAt();
        if (initiatedAt == null) {
            initiatedAt = OffsetDateTime.now();
        }

        int hour = initiatedAt.getHour();

        // Verificar si está en el rango de horas sospechosas
        if (hour >= suspiciousHourStart && hour <= suspiciousHourEnd) {
            return Optional.of(FraudIndicator.unusualHour(hour, scoreImpact));
        }

        return Optional.empty();
    }
}
