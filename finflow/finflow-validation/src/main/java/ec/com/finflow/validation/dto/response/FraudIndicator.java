package ec.com.finflow.validation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Indicador de fraude detectado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudIndicator {

    private String code;
    private String description;
    private int scoreImpact;
    private String severity;  // LOW, MEDIUM, HIGH, CRITICAL

    @Builder.Default
    private Map<String, String> details = new HashMap<>();

    /**
     * Crea indicador de monto inusual.
     */
    public static FraudIndicator unusualAmount(String userAvg, String transferAmount, int score) {
        return FraudIndicator.builder()
                .code("UNUSUAL_AMOUNT")
                .description("Monto significativamente mayor al promedio del usuario")
                .scoreImpact(score)
                .severity("MEDIUM")
                .details(Map.of(
                        "userAverage", userAvg,
                        "transferAmount", transferAmount
                ))
                .build();
    }

    /**
     * Crea indicador de hora inusual.
     */
    public static FraudIndicator unusualHour(int hour, int score) {
        return FraudIndicator.builder()
                .code("UNUSUAL_HOUR")
                .description("Transferencia en horario inusual")
                .scoreImpact(score)
                .severity("LOW")
                .details(Map.of("hour", String.valueOf(hour)))
                .build();
    }

    /**
     * Crea indicador de primer destinatario.
     */
    public static FraudIndicator firstTimeRecipient(String targetAccountId, int score) {
        return FraudIndicator.builder()
                .code("FIRST_TIME_RECIPIENT")
                .description("Primera transferencia a esta cuenta destino")
                .scoreImpact(score)
                .severity("LOW")
                .details(Map.of("targetAccountId", targetAccountId))
                .build();
    }

    /**
     * Crea indicador de transferencias rápidas.
     */
    public static FraudIndicator rapidTransfers(int count, int hours, int score) {
        return FraudIndicator.builder()
                .code("RAPID_TRANSFERS")
                .description("Múltiples transferencias en corto período")
                .scoreImpact(score)
                .severity("MEDIUM")
                .details(Map.of(
                        "transferCount", String.valueOf(count),
                        "periodHours", String.valueOf(hours)
                ))
                .build();
    }

    /**
     * Crea indicador de cuenta en lista negra.
     */
    public static FraudIndicator blacklistedAccount(String reason, int score) {
        return FraudIndicator.builder()
                .code("BLACKLISTED_ACCOUNT")
                .description("La cuenta destino está en lista negra")
                .scoreImpact(score)
                .severity("CRITICAL")
                .details(Map.of("blacklistReason", reason))
                .build();
    }
}
