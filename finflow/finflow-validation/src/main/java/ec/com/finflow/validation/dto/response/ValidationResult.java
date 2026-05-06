package ec.com.finflow.validation.dto.response;

import ec.com.finflow.validation.domain.enums.RejectionReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de una validación de transferencia.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationResult {

    private Long validationId;
    private String transferId;

    // Resultado principal
    private boolean approved;
    private RejectionReason rejectionReason;
    private String rejectionMessage;

    // Análisis de fraude
    @Builder.Default
    private int fraudScore = 0;

    @Builder.Default
    private List<FraudIndicator> fraudIndicators = new ArrayList<>();

    // Info de cuentas validadas
    private AccountValidationInfo sourceAccount;
    private AccountValidationInfo targetAccount;

    // Info de límites
    private LimitCheckResult limitCheck;

    // Metadata
    private long validationTimeMs;
    private OffsetDateTime validatedAt;

    /**
     * Crea un resultado de aprobación.
     */
    public static ValidationResult approved(String transferId, int fraudScore,
                                            List<FraudIndicator> indicators) {
        return ValidationResult.builder()
                .transferId(transferId)
                .approved(true)
                .fraudScore(fraudScore)
                .fraudIndicators(indicators != null ? indicators : new ArrayList<>())
                .validatedAt(OffsetDateTime.now())
                .build();
    }

    /**
     * Crea un resultado de rechazo.
     */
    public static ValidationResult rejected(String transferId, RejectionReason reason,
                                            String message, int fraudScore) {
        return ValidationResult.builder()
                .transferId(transferId)
                .approved(false)
                .rejectionReason(reason)
                .rejectionMessage(message)
                .fraudScore(fraudScore)
                .validatedAt(OffsetDateTime.now())
                .build();
    }
}
