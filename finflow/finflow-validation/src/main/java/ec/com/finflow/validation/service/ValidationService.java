package ec.com.finflow.validation.service;

import ec.com.finflow.validation.dto.request.ValidateTransferRequest;
import ec.com.finflow.validation.dto.response.ValidationResult;

/**
 * Servicio principal de validación de transferencias.
 */
public interface ValidationService {

    /**
     * Valida una transferencia completa.
     * Incluye: validación de cuentas, saldos, límites y análisis de fraude.
     *
     * @param request Datos de la transferencia a validar
     * @return Resultado de la validación
     */
    ValidationResult validateTransfer(ValidateTransferRequest request);
}
