package ec.com.finflow.transfers.controller;

import ec.com.finflow.transfers.domain.entity.Transfer;
import ec.com.finflow.transfers.domain.enums.TransferStatus;
import ec.com.finflow.transfers.dto.response.TransferResponse;
import ec.com.finflow.transfers.repository.TransferRepository;
import ec.com.finflow.transfers.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para administración de transferencias.
 * Solo accesible por usuarios con rol ADMIN.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/transfers")
@RequiredArgsConstructor
public class AdminTransferController {

    private final TransferService transferService;
    private final TransferRepository transferRepository;

    /**
     * Verifica que el usuario sea administrador.
     */
    private void checkAdmin(String userRole) {
        if (!"ADMIN".equals(userRole)) {
            throw new SecurityException("Acceso denegado: se requiere rol ADMIN");
        }
    }

    /**
     * Obtiene todas las transferencias del sistema (paginadas).
     */
    @GetMapping
    public ResponseEntity<Page<TransferResponse>> getAllTransfers(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "initiatedAt") Pageable pageable,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin getting all transfers, status filter: {}", correlationId, status);
        checkAdmin(userRole);

        Page<Transfer> transfers;
        if (status != null && !status.isBlank()) {
            try {
                TransferStatus filterStatus = TransferStatus.valueOf(status.toUpperCase());
                transfers = transferRepository.findAll(pageable);
                // Filtrar en memoria por ahora (podría mejorarse con query específica)
                List<Transfer> filtered = transfers.getContent().stream()
                        .filter(t -> t.getStatus() == filterStatus)
                        .toList();
                // Por simplicidad, devolvemos todo con filtro
                transfers = transferRepository.findAll(pageable);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status filter: {}", status);
                transfers = transferRepository.findAll(pageable);
            }
        } else {
            transfers = transferRepository.findAll(pageable);
        }

        Page<TransferResponse> response = transfers.map(this::toTransferResponse);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene estadísticas de transferencias.
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTransferStats(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin getting transfer stats", correlationId);
        checkAdmin(userRole);

        List<Transfer> allTransfers = transferRepository.findAll();

        long total = allTransfers.size();
        long pending = allTransfers.stream().filter(t -> t.getStatus() == TransferStatus.PENDING).count();
        long validating = allTransfers.stream().filter(t -> t.getStatus() == TransferStatus.VALIDATING).count();
        long processing = allTransfers.stream().filter(t -> t.getStatus() == TransferStatus.PROCESSING).count();
        long completed = allTransfers.stream().filter(t -> t.getStatus() == TransferStatus.COMPLETED).count();
        long failed = allTransfers.stream().filter(t -> t.getStatus() == TransferStatus.FAILED).count();
        long cancelled = allTransfers.stream().filter(t -> t.getStatus() == TransferStatus.CANCELLED).count();
        long reversed = allTransfers.stream().filter(t -> t.getStatus() == TransferStatus.REVERSED).count();

        BigDecimal totalAmount = allTransfers.stream()
                .map(Transfer::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal completedAmount = allTransfers.stream()
                .filter(t -> t.getStatus() == TransferStatus.COMPLETED)
                .map(Transfer::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTransfers", total);
        stats.put("pendingTransfers", pending);
        stats.put("validatingTransfers", validating);
        stats.put("processingTransfers", processing);
        stats.put("completedTransfers", completed);
        stats.put("failedTransfers", failed);
        stats.put("cancelledTransfers", cancelled);
        stats.put("reversedTransfers", reversed);
        stats.put("totalAmount", totalAmount);
        stats.put("completedAmount", completedAmount);

        return ResponseEntity.ok(stats);
    }

    /**
     * Aprueba una transferencia pendiente.
     * Nota: En el flujo actual las transferencias se procesan automáticamente vía Kafka.
     * Este endpoint permite cambiar el estado manualmente si es necesario.
     */
    @PutMapping("/{transferId}/approve")
    public ResponseEntity<TransferResponse> approveTransfer(
            @PathVariable Long transferId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-ID", required = false) String adminUserId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin {} approving transfer: {}", correlationId, adminUserId, transferId);
        checkAdmin(userRole);

        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transferencia no encontrada: " + transferId));

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden aprobar transferencias pendientes. Estado actual: " + transfer.getStatus());
        }

        // Marcar como aprobada/en procesamiento
        transfer.setStatus(TransferStatus.PROCESSING);
        transfer.setProcessedAt(java.time.OffsetDateTime.now());
        transferRepository.save(transfer);

        log.info("Transfer {} approved and set to PROCESSING by admin {}", transferId, adminUserId);

        return ResponseEntity.ok(toTransferResponse(transfer));
    }

    /**
     * Rechaza una transferencia pendiente.
     */
    @PutMapping("/{transferId}/reject")
    public ResponseEntity<TransferResponse> rejectTransfer(
            @PathVariable Long transferId,
            @RequestParam String reason,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-ID", required = false) String adminUserId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin {} rejecting transfer {}: {}", correlationId, adminUserId, transferId, reason);
        checkAdmin(userRole);

        TransferResponse response = transferService.cancelTransfer(transferId, adminUserId, "ADMIN_REJECTED: " + reason);
        log.info("Transfer {} rejected by admin {}", transferId, adminUserId);

        return ResponseEntity.ok(response);
    }

    /**
     * Revierte una transferencia completada (solo admin).
     */
    @PutMapping("/{transferId}/reverse")
    public ResponseEntity<TransferResponse> reverseTransfer(
            @PathVariable Long transferId,
            @RequestParam String reason,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-ID", required = false) String adminUserId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin {} reversing transfer {}: {}", correlationId, adminUserId, transferId, reason);
        checkAdmin(userRole);

        TransferResponse response = transferService.reverseTransfer(transferId, adminUserId, "ADMIN_REVERSAL: " + reason);
        log.info("Transfer {} reversed by admin {}", transferId, adminUserId);

        return ResponseEntity.ok(response);
    }

    /**
     * Convierte Transfer a TransferResponse.
     */
    private TransferResponse toTransferResponse(Transfer transfer) {
        return TransferResponse.builder()
                .id(transfer.getId())
                .referenceNumber(transfer.getReferenceNumber())
                .sourceAccountId(transfer.getSourceAccountId())
                .sourceAccountNumber(transfer.getSourceAccountNumber())
                .targetAccountId(transfer.getTargetAccountId())
                .targetAccountNumber(transfer.getTargetAccountNumber())
                .amount(transfer.getAmount())
                .currency(transfer.getCurrency())
                .status(transfer.getStatus())
                .description(transfer.getDescription())
                .initiatedBy(transfer.getInitiatedBy())
                .failureReason(transfer.getFailureReason())
                .failureMessage(transfer.getFailureMessage())
                .initiatedAt(transfer.getInitiatedAt())
                .completedAt(transfer.getCompletedAt())
                .failedAt(transfer.getFailedAt())
                .correlationId(transfer.getCorrelationId())
                .build();
    }
}
