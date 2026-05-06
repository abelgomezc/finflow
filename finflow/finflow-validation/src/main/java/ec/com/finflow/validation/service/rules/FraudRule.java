package ec.com.finflow.validation.service.rules;

import ec.com.finflow.validation.dto.request.ValidateTransferRequest;
import ec.com.finflow.validation.dto.response.FraudIndicator;

import java.util.Optional;

/**
 * Interfaz para reglas de detección de fraude.
 * Siguiendo el principio Open/Closed: nuevas reglas se agregan
 * implementando esta interfaz, sin modificar el FraudDetectionService.
 */
public interface FraudRule {

    /**
     * Código único de la regla.
     */
    String getCode();

    /**
     * Nombre descriptivo de la regla.
     */
    String getName();

    /**
     * Evalúa la regla y retorna un indicador si detecta fraude.
     *
     * @param request   La solicitud de transferencia a evaluar
     * @param context   Contexto adicional (perfil de usuario, etc.)
     * @return Optional con el indicador si se detecta fraude, empty si todo OK
     */
    Optional<FraudIndicator> evaluate(ValidateTransferRequest request, FraudRuleContext context);

    /**
     * Indica si esta regla está habilitada.
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * Orden de ejecución (menor = antes).
     */
    default int getOrder() {
        return 100;
    }
}
