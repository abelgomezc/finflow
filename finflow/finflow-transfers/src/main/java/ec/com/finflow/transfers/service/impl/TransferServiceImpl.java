package ec.com.finflow.transfers.service.impl;

import ec.com.finflow.transfers.client.AccountsClient;
import ec.com.finflow.transfers.domain.entity.Transfer;
import ec.com.finflow.transfers.domain.enums.TransferStatus;
import ec.com.finflow.transfers.dto.request.InitiateTransferRequest;
import ec.com.finflow.transfers.dto.response.TransferResponse;
import ec.com.finflow.transfers.kafka.TransferEvent;
import ec.com.finflow.transfers.kafka.TransferKafkaProducer;
import ec.com.finflow.transfers.mapper.TransferMapper;
import ec.com.finflow.transfers.repository.TransferRepository;
import ec.com.finflow.transfers.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Implementación del servicio de transferencias.
 * Implementa el patrón Saga para garantizar consistencia.
 *
 * <p>Flujo del Saga:
 * <ol>
 *   <li>Crear transferencia (PENDING)</li>
 *   <li>Validar transferencia via gRPC (VALIDATING)</li>
 *   <li>Bloquear monto en cuenta origen (PROCESSING)</li>
 *   <li>Ejecutar débito</li>
 *   <li>Ejecutar crédito</li>
 *   <li>Marcar como COMPLETED</li>
 * </ol>
 *
 * <p>Si algún paso falla, se ejecuta la compensación:
 * <ul>
 *   <li>Si falla el crédito → reversar el débito</li>
 *   <li>Si falla el débito → liberar el bloqueo</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;
    private final TransferKafkaProducer kafkaProducer;
    private final AccountsClient accountsClient;

    @Override
    @Transactional
    public TransferResponse initiateTransfer(InitiateTransferRequest request) {
        log.info("Initiating transfer from {} to {} amount: {} by user: {}",
                request.getSourceAccountId(),
                request.getTargetAccountId(),
                request.getAmount(),
                request.getUserId());

        // 1. Generar número de referencia
        String referenceNumber = generateReferenceNumber();

        // 2. Obtener números de cuenta para guardarlos con la transferencia
        String sourceAccountNumber = accountsClient.getAccountNumber(request.getSourceAccountId());
        String targetAccountNumber = accountsClient.getAccountNumber(request.getTargetAccountId());

        // 3. Crear la transferencia directamente con JPA
        Transfer transfer = Transfer.builder()
                .referenceNumber(referenceNumber)
                .sourceAccountId(request.getSourceAccountId())
                .sourceAccountNumber(sourceAccountNumber)
                .targetAccountId(request.getTargetAccountId())
                .targetAccountNumber(targetAccountNumber)
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .description(request.getDescription())
                .initiatedBy(request.getUserId() != null ? request.getUserId() : "SYSTEM")
                .correlationId(request.getCorrelationId())
                .ipAddress(request.getIpAddress())
                .status(TransferStatus.PENDING)
                .initiatedAt(OffsetDateTime.now())
                .build();

        transfer = transferRepository.save(transfer);

        log.info("Transfer created: {} reference: {}", transfer.getId(), referenceNumber);

        try {
            // 2. Publicar evento de inicio
            kafkaProducer.publishInitiated(TransferEvent.initiated(
                    transfer.getId(),
                    transfer.getReferenceNumber(),
                    transfer.getSourceAccountId(),
                    transfer.getTargetAccountId(),
                    transfer.getAmount(),
                    transfer.getCurrency(),
                    transfer.getInitiatedBy(),
                    transfer.getCorrelationId()
            ));

            // 3. Ejecutar validación (simulado - en producción via gRPC)
            updateTransferStatus(transfer, TransferStatus.VALIDATING,
                    "VALIDATION_STARTED", "Starting validation");

            boolean validationPassed = simulateValidation(transfer);
            if (!validationPassed) {
                return failTransfer(transfer, "VALIDATION_FAILED",
                        "Transfer validation failed");
            }

            // 4. Ejecutar débito directo (sin bloqueo previo para simplicidad)
            updateTransferStatus(transfer, TransferStatus.PROCESSING,
                    "PROCESSING_STARTED", "Validation passed, processing transfer");

            Long debitTxId = simulateDebit(transfer);
            if (debitTxId == null) {
                return failTransfer(transfer, "DEBIT_FAILED",
                        "Failed to debit source account");
            }
            transfer.setDebitTransactionId(debitTxId);

            // 6. Ejecutar crédito (simulado)
            Long creditTxId = simulateCredit(transfer);
            if (creditTxId == null) {
                // Compensación: reversar débito
                simulateReverseDebit(debitTxId, transfer);
                return failTransfer(transfer, "CREDIT_FAILED",
                        "Failed to credit target account. Debit reversed.");
            }
            transfer.setCreditTransactionId(creditTxId);

            // 7. Obtener saldos actuales después de la transferencia
            BigDecimal sourceBalanceAfter = accountsClient.getAccountBalance(transfer.getSourceAccountId());
            BigDecimal targetBalanceAfter = accountsClient.getAccountBalance(transfer.getTargetAccountId());
            transfer.setSourceBalanceAfter(sourceBalanceAfter);
            transfer.setTargetBalanceAfter(targetBalanceAfter);
            log.debug("Balances after transfer - source: {}, target: {}",
                    sourceBalanceAfter, targetBalanceAfter);

            // 8. Completar transferencia
            transfer.setStatus(TransferStatus.COMPLETED);
            transfer.setCompletedAt(OffsetDateTime.now());
            transfer = transferRepository.save(transfer);

            updateTransferStatus(transfer, TransferStatus.COMPLETED,
                    "TRANSFER_COMPLETED", "Transfer completed successfully");

            // 9. Publicar evento de completado
            kafkaProducer.publishCompleted(TransferEvent.completed(
                    transfer.getId(),
                    transfer.getReferenceNumber(),
                    transfer.getSourceAccountId(),
                    transfer.getTargetAccountId(),
                    transfer.getAmount(),
                    transfer.getCurrency(),
                    transfer.getInitiatedBy(),
                    transfer.getCorrelationId()
            ));

            log.info("Transfer completed successfully: {}", transfer.getId());
            return transferMapper.toResponse(transfer);

        } catch (Exception e) {
            log.error("Error processing transfer {}: {}", transfer.getId(), e.getMessage(), e);
            return failTransfer(transfer, "SYSTEM_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public TransferResponse getTransfer(Long transferId) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found: " + transferId));
        enrichAccountNumbers(transfer);
        return transferMapper.toResponse(transfer);
    }

    @Override
    @Transactional
    public TransferResponse getTransferByReference(String referenceNumber) {
        Transfer transfer = transferRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new RuntimeException("Transfer not found: " + referenceNumber));
        enrichAccountNumbers(transfer);
        return transferMapper.toResponse(transfer);
    }

    @Override
    @Transactional
    public Page<TransferResponse> getTransferHistory(String userId, Pageable pageable) {
        return transferRepository.findByInitiatedByOrderByInitiatedAtDesc(userId, pageable)
                .map(this::enrichAndMap);
    }

    @Override
    @Transactional
    public Page<TransferResponse> getAccountTransfers(Long accountId, Pageable pageable) {
        return transferRepository.findByAccountId(accountId, pageable)
                .map(this::enrichAndMap);
    }

    /**
     * Enriquece una transferencia con números de cuenta si faltan y la convierte a response.
     */
    private TransferResponse enrichAndMap(Transfer transfer) {
        enrichAccountNumbers(transfer);
        return transferMapper.toResponse(transfer);
    }

    /**
     * Si los números de cuenta están vacíos, los busca del servicio de cuentas y actualiza la transferencia.
     */
    private void enrichAccountNumbers(Transfer transfer) {
        boolean needsUpdate = false;

        if (transfer.getSourceAccountNumber() == null || transfer.getSourceAccountNumber().isBlank()) {
            String accountNumber = accountsClient.getAccountNumber(transfer.getSourceAccountId());
            if (accountNumber != null) {
                transfer.setSourceAccountNumber(accountNumber);
                needsUpdate = true;
                log.debug("Enriched source account number for transfer {}: {}", transfer.getId(), accountNumber);
            }
        }

        if (transfer.getTargetAccountNumber() == null || transfer.getTargetAccountNumber().isBlank()) {
            String accountNumber = accountsClient.getAccountNumber(transfer.getTargetAccountId());
            if (accountNumber != null) {
                transfer.setTargetAccountNumber(accountNumber);
                needsUpdate = true;
                log.debug("Enriched target account number for transfer {}: {}", transfer.getId(), accountNumber);
            }
        }

        // Guardar la actualización para que no tenga que buscarse de nuevo
        if (needsUpdate) {
            transferRepository.save(transfer);
            log.info("Updated transfer {} with account numbers", transfer.getId());
        }
    }

    @Override
    @Transactional
    public TransferResponse cancelTransfer(Long transferId, String userId, String reason) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found: " + transferId));

        if (!transfer.canCancel()) {
            throw new RuntimeException("Cannot cancel transfer in status: " + transfer.getStatus());
        }

        transfer.setStatus(TransferStatus.CANCELLED);
        transfer.setFailureReason("CANCELLED");
        transfer.setFailureMessage("Cancelled by user: " + reason);
        transfer = transferRepository.save(transfer);

        updateTransferStatus(transfer, TransferStatus.CANCELLED,
                "TRANSFER_CANCELLED", "Cancelled by user: " + reason);

        log.info("Transfer {} cancelled by {}: {}", transferId, userId, reason);
        return transferMapper.toResponse(transfer);
    }

    @Override
    @Transactional
    public TransferResponse reverseTransfer(Long transferId, String userId, String reason) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found: " + transferId));

        if (!transfer.canReverse()) {
            throw new RuntimeException("Cannot reverse transfer in status: " + transfer.getStatus());
        }

        // Ejecutar reversión (simulado - en producción via gRPC)
        Long reversalTxId = simulateReverseDebit(transfer.getDebitTransactionId(), transfer);
        if (reversalTxId == null) {
            throw new RuntimeException("Failed to reverse transfer");
        }

        transfer.setReversalTransactionId(reversalTxId);
        transfer.setStatus(TransferStatus.REVERSED);
        transfer.setReversedAt(OffsetDateTime.now());
        transfer.setFailureReason("REVERSED");
        transfer.setFailureMessage("Reversed by user: " + reason);
        transfer = transferRepository.save(transfer);

        updateTransferStatus(transfer, TransferStatus.REVERSED,
                "TRANSFER_REVERSED", "Reversed by user: " + reason);

        // Publicar evento
        kafkaProducer.publishReversed(TransferEvent.reversed(
                transfer.getId(),
                transfer.getReferenceNumber(),
                transfer.getSourceAccountId(),
                transfer.getTargetAccountId(),
                transfer.getAmount(),
                transfer.getCurrency(),
                reason,
                transfer.getInitiatedBy(),
                transfer.getCorrelationId()
        ));

        log.info("Transfer {} reversed by {}: {}", transferId, userId, reason);
        return transferMapper.toResponse(transfer);
    }

    // ============================================================
    // Métodos auxiliares
    // ============================================================

    private TransferResponse failTransfer(Transfer transfer, String reason, String message) {
        transfer.setStatus(TransferStatus.FAILED);
        transfer.setFailedAt(OffsetDateTime.now());
        transfer.setFailureReason(reason);
        transfer.setFailureMessage(message);
        transfer = transferRepository.save(transfer);

        updateTransferStatus(transfer, TransferStatus.FAILED, reason, message);

        kafkaProducer.publishFailed(TransferEvent.failed(
                transfer.getId(),
                transfer.getReferenceNumber(),
                transfer.getSourceAccountId(),
                transfer.getTargetAccountId(),
                transfer.getAmount(),
                transfer.getCurrency(),
                reason,
                message,
                transfer.getInitiatedBy(),
                transfer.getCorrelationId()
        ));

        log.warn("Transfer {} failed: {} - {}", transfer.getId(), reason, message);
        return transferMapper.toResponse(transfer);
    }

    private void updateTransferStatus(Transfer transfer, TransferStatus newStatus,
                                      String eventType, String description) {
        // Actualizar directamente con JPA en lugar de stored procedure
        transfer.setStatus(newStatus);
        transferRepository.save(transfer);
        log.debug("Transfer {} status updated to {}: {}", transfer.getId(), newStatus, description);
    }

    /**
     * Genera un número de referencia único para la transferencia.
     * Formato: TRF-YYYYMMDD-XXXXXXXX
     */
    private String generateReferenceNumber() {
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        String random = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TRF-" + date + "-" + random;
    }

    // ============================================================
    // Account Service Operations (via HTTP client)
    // ============================================================

    private boolean simulateValidation(Transfer transfer) {
        log.debug("Validating accounts for transfer: {}", transfer.getId());
        // Validar que ambas cuentas existen y están activas
        boolean sourceValid = accountsClient.validateAccount(transfer.getSourceAccountId());
        boolean targetValid = accountsClient.validateAccount(transfer.getTargetAccountId());

        if (!sourceValid) {
            log.warn("Source account {} is invalid", transfer.getSourceAccountId());
            return false;
        }
        if (!targetValid) {
            log.warn("Target account {} is invalid", transfer.getTargetAccountId());
            return false;
        }

        return true;
    }

    private Long simulateBlockAmount(Transfer transfer) {
        log.debug("Blocking amount for transfer: {}", transfer.getId());
        return accountsClient.blockAmount(
                transfer.getSourceAccountId(),
                transfer.getAmount(),
                "Transfer " + transfer.getReferenceNumber(),
                transfer.getInitiatedBy(),
                transfer.getCorrelationId()
        );
    }

    private void simulateReleaseBlock(Long blockId) {
        log.debug("Releasing block: {}", blockId);
        accountsClient.releaseBlock(blockId, "Transfer cancelled", "SYSTEM");
    }

    private Long simulateDebit(Transfer transfer) {
        log.debug("Executing debit for transfer: {}", transfer.getId());
        return accountsClient.executeDebit(
                transfer.getSourceAccountId(),
                transfer.getAmount(),
                transfer.getId().toString(),
                transfer.getDescription(),
                transfer.getInitiatedBy(),
                transfer.getCorrelationId()
        );
    }

    private Long simulateCredit(Transfer transfer) {
        log.debug("Executing credit for transfer: {}", transfer.getId());
        return accountsClient.executeCredit(
                transfer.getTargetAccountId(),
                transfer.getAmount(),
                transfer.getId().toString(),
                transfer.getDescription(),
                transfer.getInitiatedBy(),
                transfer.getCorrelationId()
        );
    }

    private Long simulateReverseDebit(Long debitTxId, Transfer transfer) {
        log.debug("Reversing debit: {} for transfer: {}", debitTxId, transfer.getId());
        return accountsClient.reverseDebit(
                debitTxId,
                transfer.getId().toString(),
                "Transfer failed - compensating",
                transfer.getInitiatedBy(),
                transfer.getCorrelationId()
        );
    }
}
