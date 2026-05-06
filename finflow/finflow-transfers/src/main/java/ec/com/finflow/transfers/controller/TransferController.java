package ec.com.finflow.transfers.controller;

import ec.com.finflow.transfers.dto.request.InitiateTransferRequest;
import ec.com.finflow.transfers.dto.response.TransferResponse;
import ec.com.finflow.transfers.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para transferencias.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    /**
     * Inicia una nueva transferencia.
     */
    @PostMapping
    public ResponseEntity<TransferResponse> initiateTransfer(
            @Valid @RequestBody InitiateTransferRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @RequestHeader(value = "X-User-ID", required = false) String userId) {

        log.info("[{}] Initiating transfer: {} to {} amount: {}",
                correlationId,
                request.getSourceAccountId(),
                request.getTargetAccountId(),
                request.getAmount());

        if (correlationId != null) {
            request.setCorrelationId(correlationId);
        }
        if (userId != null && request.getUserId() == null) {
            request.setUserId(userId);
        }

        TransferResponse response = transferService.initiateTransfer(request);

        HttpStatus status = response.getStatus().isFinal() &&
                response.getStatus() != ec.com.finflow.transfers.domain.enums.TransferStatus.COMPLETED
                ? HttpStatus.UNPROCESSABLE_ENTITY
                : HttpStatus.CREATED;

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Obtiene una transferencia por ID.
     */
    @GetMapping("/{transferId}")
    public ResponseEntity<TransferResponse> getTransfer(
            @PathVariable Long transferId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.debug("[{}] Getting transfer: {}", correlationId, transferId);
        TransferResponse response = transferService.getTransfer(transferId);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene una transferencia por número de referencia.
     */
    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<TransferResponse> getTransferByReference(
            @PathVariable String referenceNumber,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.debug("[{}] Getting transfer by reference: {}", correlationId, referenceNumber);
        TransferResponse response = transferService.getTransferByReference(referenceNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene historial de transferencias de un usuario.
     */
    @GetMapping("/history")
    public ResponseEntity<Page<TransferResponse>> getTransferHistory(
            @RequestParam String userId,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.debug("[{}] Getting transfer history for user: {}", correlationId, userId);
        Page<TransferResponse> response = transferService.getTransferHistory(userId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene las transferencias del usuario autenticado.
     * Usa el X-User-ID del header (inyectado por el gateway desde el JWT).
     */
    @GetMapping("/my-transfers")
    public ResponseEntity<Page<TransferResponse>> getMyTransfers(
            @RequestHeader(value = "X-User-ID", required = false) String userId,
            @PageableDefault(size = 20, sort = "initiatedAt") Pageable pageable,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Getting transfers for authenticated user: {}", correlationId, userId);

        if (userId == null || userId.isBlank()) {
            log.warn("No user ID provided in request");
            return ResponseEntity.ok(Page.empty());
        }

        Page<TransferResponse> response = transferService.getTransferHistory(userId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene transferencias de una cuenta.
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<Page<TransferResponse>> getAccountTransfers(
            @PathVariable Long accountId,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.debug("[{}] Getting transfers for account: {}", correlationId, accountId);
        Page<TransferResponse> response = transferService.getAccountTransfers(accountId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancela una transferencia pendiente.
     */
    @PostMapping("/{transferId}/cancel")
    public ResponseEntity<TransferResponse> cancelTransfer(
            @PathVariable Long transferId,
            @RequestParam String reason,
            @RequestHeader(value = "X-User-ID") String userId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Cancelling transfer {} by {}: {}",
                correlationId, transferId, userId, reason);
        TransferResponse response = transferService.cancelTransfer(transferId, userId, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * Revierte una transferencia completada.
     */
    @PostMapping("/{transferId}/reverse")
    public ResponseEntity<TransferResponse> reverseTransfer(
            @PathVariable Long transferId,
            @RequestParam String reason,
            @RequestHeader(value = "X-User-ID") String userId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Reversing transfer {} by {}: {}",
                correlationId, transferId, userId, reason);
        TransferResponse response = transferService.reverseTransfer(transferId, userId, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Transfer service is healthy");
    }
}
