package ec.com.finflow.accounts.grpc;

import ec.com.finflow.accounts.dto.request.BlockAmountRequest;
import ec.com.finflow.accounts.dto.request.ExecuteCreditRequest;
import ec.com.finflow.accounts.dto.request.ExecuteDebitRequest;
import ec.com.finflow.accounts.dto.response.AccountResponse;
import ec.com.finflow.accounts.dto.response.BalanceResponse;
import ec.com.finflow.accounts.dto.response.BlockResponse;
import ec.com.finflow.accounts.dto.response.TransactionResponse;
import ec.com.finflow.accounts.service.AccountService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;

/**
 * Servicio gRPC para operaciones de cuentas.
 * Expone las operaciones de AccountService via gRPC para comunicación
 * con otros microservicios (finflow-transfers, finflow-validation).
 *
 * NOTA: Este es un placeholder. La implementación real requiere que
 * los archivos .proto sean compilados primero con protoc/maven.
 * Una vez compilados, esta clase extendería AccountServiceGrpc.AccountServiceImplBase
 */
@Slf4j
// @GrpcService  // Deshabilitado temporalmente - requiere compilar proto files primero
@RequiredArgsConstructor
public class AccountGrpcService {

    private final AccountService accountService;

    /**
     * Obtiene información de una cuenta.
     * gRPC: GetAccount
     */
    public AccountResponse getAccount(Long accountId) {
        log.debug("gRPC: GetAccount {}", accountId);
        return accountService.getAccount(accountId);
    }

    /**
     * Obtiene el balance de una cuenta.
     * gRPC: GetBalance
     */
    public BalanceResponse getBalance(Long accountId) {
        log.debug("gRPC: GetBalance {}", accountId);
        return accountService.getBalance(accountId);
    }

    /**
     * Valida si una cuenta existe y está activa.
     * gRPC: ValidateAccount
     */
    public boolean validateAccount(Long accountId) {
        log.debug("gRPC: ValidateAccount {}", accountId);
        return accountService.validateAccount(accountId);
    }

    /**
     * Bloquea un monto en una cuenta.
     * gRPC: BlockAmount
     */
    public BlockResponse blockAmount(Long accountId, BigDecimal amount,
                                     String reason, int expirationMinutes,
                                     String executedBy) {
        log.debug("gRPC: BlockAmount {} {}", accountId, amount);

        BlockAmountRequest request = BlockAmountRequest.builder()
                .accountId(accountId)
                .amount(amount)
                .reason(reason)
                .expirationMinutes(expirationMinutes)
                .executedBy(executedBy)
                .build();

        return accountService.blockAmount(request);
    }

    /**
     * Libera un bloqueo.
     * gRPC: ReleaseBlock
     */
    public BlockResponse releaseBlock(Long blockId, String reason, String executedBy) {
        log.debug("gRPC: ReleaseBlock {}", blockId);
        return accountService.releaseBlock(blockId, reason, executedBy);
    }

    /**
     * Ejecuta un débito usando un bloqueo previo.
     * gRPC: ExecuteBlockedDebit
     */
    public TransactionResponse executeBlockedDebit(Long blockId, String transferId,
                                                   String description, String executedBy,
                                                   String correlationId) {
        log.debug("gRPC: ExecuteBlockedDebit {} {}", blockId, transferId);
        return accountService.executeBlockedDebit(
                blockId, transferId, description, executedBy, correlationId);
    }

    /**
     * Ejecuta un débito directo.
     * gRPC: ExecuteDebit
     */
    public TransactionResponse executeDebit(Long accountId, BigDecimal amount,
                                            String transferId, String description,
                                            String idempotencyKey, String executedBy,
                                            String correlationId) {
        log.debug("gRPC: ExecuteDebit {} {} {}", accountId, amount, transferId);

        ExecuteDebitRequest request = ExecuteDebitRequest.builder()
                .accountId(accountId)
                .amount(amount)
                .transferId(transferId)
                .description(description)
                .idempotencyKey(idempotencyKey)
                .executedBy(executedBy)
                .correlationId(correlationId)
                .build();

        return accountService.executeDebit(request);
    }

    /**
     * Ejecuta un crédito.
     * gRPC: ExecuteCredit
     */
    public TransactionResponse executeCredit(Long accountId, BigDecimal amount,
                                             String transferId, String description,
                                             String idempotencyKey, String executedBy,
                                             String correlationId) {
        log.debug("gRPC: ExecuteCredit {} {} {}", accountId, amount, transferId);

        ExecuteCreditRequest request = ExecuteCreditRequest.builder()
                .accountId(accountId)
                .amount(amount)
                .transferId(transferId)
                .description(description)
                .idempotencyKey(idempotencyKey)
                .executedBy(executedBy)
                .correlationId(correlationId)
                .build();

        return accountService.executeCredit(request);
    }

    /**
     * Revierte un débito.
     * gRPC: ReverseDebit
     */
    public TransactionResponse reverseDebit(Long originalTransactionId, String transferId,
                                            String reason, String executedBy, String correlationId) {
        log.debug("gRPC: ReverseDebit {}", originalTransactionId);
        return accountService.reverseDebit(
                originalTransactionId, transferId, reason, executedBy, correlationId);
    }

    /**
     * Obtiene el total transferido en el día.
     * gRPC: GetDailyTransferTotal
     */
    public BigDecimal getDailyTransferTotal(Long accountId) {
        log.debug("gRPC: GetDailyTransferTotal {}", accountId);
        return accountService.getDailyTransferTotal(accountId);
    }

    /**
     * Cuenta transferencias recientes.
     * gRPC: CountRecentTransfers
     */
    public int countRecentTransfers(Long accountId, int hours) {
        log.debug("gRPC: CountRecentTransfers {} hours={}", accountId, hours);
        return accountService.countRecentTransfers(accountId, hours);
    }
}
