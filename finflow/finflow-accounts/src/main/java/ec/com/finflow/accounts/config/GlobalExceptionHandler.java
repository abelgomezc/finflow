package ec.com.finflow.accounts.config;

import ec.com.finflow.accounts.domain.exception.AccountException;
import ec.com.finflow.accounts.domain.exception.BlockException;
import ec.com.finflow.accounts.domain.exception.FinFlowException;
import ec.com.finflow.accounts.domain.exception.TransactionException;
import ec.com.finflow.accounts.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones.
 * Convierte excepciones del dominio a respuestas HTTP apropiadas.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de cuenta (AccountException).
     */
    @ExceptionHandler(AccountException.class)
    public ResponseEntity<ErrorResponse> handleAccountException(
            AccountException ex, HttpServletRequest request) {

        log.warn("Account exception: {} - {}", ex.getErrorCode(), ex.getMessage());

        HttpStatus status = mapErrorCodeToStatus(ex.getErrorCode());

        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .correlationId(ex.getCorrelationId())
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    /**
     * Maneja excepciones de bloqueo (BlockException).
     */
    @ExceptionHandler(BlockException.class)
    public ResponseEntity<ErrorResponse> handleBlockException(
            BlockException ex, HttpServletRequest request) {

        log.warn("Block exception: {} - {}", ex.getErrorCode(), ex.getMessage());

        HttpStatus status = mapErrorCodeToStatus(ex.getErrorCode());

        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .correlationId(ex.getCorrelationId())
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    /**
     * Maneja excepciones de transacción (TransactionException).
     */
    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ErrorResponse> handleTransactionException(
            TransactionException ex, HttpServletRequest request) {

        log.warn("Transaction exception: {} - {}", ex.getErrorCode(), ex.getMessage());

        HttpStatus status = mapErrorCodeToStatus(ex.getErrorCode());

        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .correlationId(ex.getCorrelationId())
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    /**
     * Maneja excepciones base de FinFlow.
     */
    @ExceptionHandler(FinFlowException.class)
    public ResponseEntity<ErrorResponse> handleFinFlowException(
            FinFlowException ex, HttpServletRequest request) {

        log.warn("FinFlow exception: {} - {}", ex.getErrorCode(), ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .correlationId(ex.getCorrelationId())
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja errores de validación de DTOs.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> details = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("Validation failed: {}", details);

        ErrorResponse error = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Request validation failed")
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI())
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja errores de integridad de datos (ej: duplicados).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityException(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        log.error("Data integrity violation: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .code("DATA_INTEGRITY_ERROR")
                .message("Data integrity violation. The operation conflicts with existing data.")
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Maneja errores de stored procedures de PostgreSQL.
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(
            org.springframework.dao.DataAccessException ex, HttpServletRequest request) {

        log.error("Database error: {}", ex.getMessage());

        String message = extractPostgresErrorMessage(ex);
        String code = extractPostgresErrorCode(ex);

        ErrorResponse error = ErrorResponse.builder()
                .code(code)
                .message(message)
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI())
                .build();

        HttpStatus status = mapPostgresCodeToStatus(code);
        return ResponseEntity.status(status).body(error);
    }

    /**
     * Maneja excepciones genéricas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .code("INTERNAL_ERROR")
                .message("An unexpected error occurred")
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ============================================================
    // Métodos auxiliares
    // ============================================================

    private HttpStatus mapErrorCodeToStatus(String errorCode) {
        return switch (errorCode) {
            case "ACCOUNT_NOT_FOUND", "BLOCK_NOT_FOUND", "TRANSACTION_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "INSUFFICIENT_FUNDS", "INSUFFICIENT_AVAILABLE_BALANCE" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "ACCOUNT_BLOCKED", "ACCOUNT_NOT_ACTIVE", "BLOCK_NOT_ACTIVE" -> HttpStatus.FORBIDDEN;
            case "DUPLICATE_BLOCK_REFERENCE", "DUPLICATE_OPERATION" -> HttpStatus.CONFLICT;
            case "TRANSACTION_ALREADY_REVERSED" -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };
    }

    private String extractPostgresErrorMessage(Exception ex) {
        String message = ex.getMessage();
        if (message != null && message.contains("ERROR:")) {
            int start = message.indexOf("ERROR:") + 7;
            int end = message.indexOf("\n", start);
            if (end > start) {
                return message.substring(start, end).trim();
            }
        }
        return "Database operation failed";
    }

    private String extractPostgresErrorCode(Exception ex) {
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("P0002")) return "NOT_FOUND";
            if (message.contains("P0003")) return "ACCOUNT_NOT_ACTIVE";
            if (message.contains("P0004")) return "INSUFFICIENT_FUNDS";
            if (message.contains("P0005")) return "DUPLICATE_BLOCK_REFERENCE";
            if (message.contains("P0006")) return "BLOCK_NOT_ACTIVE";
            if (message.contains("P0007")) return "INVALID_TRANSACTION_TYPE";
            if (message.contains("P0008")) return "TRANSACTION_ALREADY_REVERSED";
            if (message.contains("P0010")) return "NEGATIVE_BALANCE";
        }
        return "DATABASE_ERROR";
    }

    private HttpStatus mapPostgresCodeToStatus(String code) {
        return switch (code) {
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "INSUFFICIENT_FUNDS", "NEGATIVE_BALANCE" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "ACCOUNT_NOT_ACTIVE", "BLOCK_NOT_ACTIVE" -> HttpStatus.FORBIDDEN;
            case "DUPLICATE_BLOCK_REFERENCE", "TRANSACTION_ALREADY_REVERSED" -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
