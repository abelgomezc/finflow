package ec.com.finflow.accounts.controller;

import ec.com.finflow.accounts.dto.request.*;
import ec.com.finflow.accounts.dto.response.*;
import ec.com.finflow.accounts.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones de cuentas.
 * Expone endpoints para gestión de cuentas bancarias.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // ============================================================
    // Endpoints CRUD
    // ============================================================

    /**
     * Crea una nueva cuenta bancaria.
     */
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Creating account for customer: {}", correlationId, request.getCustomerId());
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene una cuenta por ID.
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable Long accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.debug("[{}] Getting account: {}", correlationId, accountId);
        AccountResponse response = accountService.getAccount(accountId);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene una cuenta por número.
     */
    @GetMapping("/by-number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @PathVariable String accountNumber,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.debug("[{}] Getting account by number: {}", correlationId, accountNumber);
        AccountResponse response = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todas las cuentas de un cliente.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomer(
            @PathVariable Long customerId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.debug("[{}] Getting accounts for customer: {}", correlationId, customerId);
        List<AccountResponse> response = accountService.getAccountsByCustomer(customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene las cuentas del usuario autenticado.
     * Usa el X-User-ID del header (inyectado por el gateway desde el JWT).
     */
    @GetMapping("/my-accounts")
    public ResponseEntity<List<AccountResponse>> getMyAccounts(
            @RequestHeader(value = "X-User-ID", required = false) String userId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Getting accounts for authenticated user: {}", correlationId, userId);

        if (userId == null || userId.isBlank()) {
            log.warn("No user ID provided in request");
            return ResponseEntity.ok(List.of());
        }

        List<AccountResponse> response = accountService.getAccountsByUserId(Long.parseLong(userId));
        return ResponseEntity.ok(response);
    }

    /**
     * Valida si una cuenta existe y está activa.
     */
    @GetMapping("/{accountId}/validate")
    public ResponseEntity<Boolean> validateAccount(
            @PathVariable Long accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.debug("[{}] Validating account: {}", correlationId, accountId);
        boolean isValid = accountService.validateAccount(accountId);
        return ResponseEntity.ok(isValid);
    }

    // ============================================================
    // Endpoints de Balance
    // ============================================================

    /**
     * Obtiene el balance de una cuenta.
     */
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BalanceResponse> getBalance(
            @PathVariable Long accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.debug("[{}] Getting balance for account: {}", correlationId, accountId);
        BalanceResponse response = accountService.getBalance(accountId);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // Endpoints de Bloqueo
    // ============================================================

    /**
     * Bloquea un monto en una cuenta.
     */
    @PostMapping("/blocks")
    public ResponseEntity<BlockResponse> blockAmount(
            @Valid @RequestBody BlockAmountRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Blocking {} for account: {}", correlationId, request.getAmount(), request.getAccountId());

        if (correlationId != null) {
            request.setCorrelationId(correlationId);
        }

        BlockResponse response = accountService.blockAmount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Libera un bloqueo.
     */
    @DeleteMapping("/blocks/{blockId}")
    public ResponseEntity<BlockResponse> releaseBlock(
            @PathVariable Long blockId,
            @RequestParam(required = false) String reason,
            @RequestHeader(value = "X-User-ID", required = false) String userId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Releasing block: {}", correlationId, blockId);
        BlockResponse response = accountService.releaseBlock(blockId, reason, userId);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // Endpoints de Transacciones
    // ============================================================

    /**
     * Ejecuta un débito usando un bloqueo previo.
     */
    @PostMapping("/blocks/{blockId}/execute")
    public ResponseEntity<TransactionResponse> executeBlockedDebit(
            @PathVariable Long blockId,
            @RequestParam String transferId,
            @RequestParam(required = false) String description,
            @RequestHeader(value = "X-User-ID", required = false) String userId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Executing blocked debit: {} for transfer: {}", correlationId, blockId, transferId);
        TransactionResponse response = accountService.executeBlockedDebit(
                blockId, transferId, description, userId, correlationId);
        return ResponseEntity.ok(response);
    }

    /**
     * Ejecuta un débito directo.
     */
    @PostMapping("/debit")
    public ResponseEntity<TransactionResponse> executeDebit(
            @Valid @RequestBody ExecuteDebitRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Executing debit for account: {} amount: {}",
                correlationId, request.getAccountId(), request.getAmount());

        if (correlationId != null) {
            request.setCorrelationId(correlationId);
        }

        TransactionResponse response = accountService.executeDebit(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Ejecuta un crédito.
     */
    @PostMapping("/credit")
    public ResponseEntity<TransactionResponse> executeCredit(
            @Valid @RequestBody ExecuteCreditRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Executing credit for account: {} amount: {}",
                correlationId, request.getAccountId(), request.getAmount());

        if (correlationId != null) {
            request.setCorrelationId(correlationId);
        }

        TransactionResponse response = accountService.executeCredit(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Revierte un débito previo.
     */
    @PostMapping("/reverse/{transactionId}")
    public ResponseEntity<TransactionResponse> reverseDebit(
            @PathVariable Long transactionId,
            @RequestParam String transferId,
            @RequestParam String reason,
            @RequestHeader(value = "X-User-ID", required = false) String userId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Reversing transaction: {} for transfer: {}", correlationId, transactionId, transferId);
        TransactionResponse response = accountService.reverseDebit(
                transactionId, transferId, reason, userId, correlationId);
        return ResponseEntity.ok(response);
    }
}
