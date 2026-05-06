package ec.com.finflow.validation.controller;

import ec.com.finflow.validation.dto.request.ValidateTransferRequest;
import ec.com.finflow.validation.dto.response.ValidationResult;
import ec.com.finflow.validation.service.ValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para validaciones.
 * Principalmente usado para testing; en producción se usa gRPC.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/validation")
@RequiredArgsConstructor
public class ValidationController {

    private final ValidationService validationService;

    /**
     * Valida una transferencia.
     */
    @PostMapping("/transfer")
    public ResponseEntity<ValidationResult> validateTransfer(
            @Valid @RequestBody ValidateTransferRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Validating transfer: {} amount: {}",
                correlationId, request.getTransferId(), request.getAmount());

        if (correlationId != null) {
            request.setCorrelationId(correlationId);
        }

        ValidationResult result = validationService.validateTransfer(request);

        if (result.isApproved()) {
            return ResponseEntity.ok(result);
        } else {
            // 422 Unprocessable Entity para rechazos de negocio
            return ResponseEntity.unprocessableEntity().body(result);
        }
    }

    /**
     * Health check del servicio.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Validation service is healthy");
    }
}
