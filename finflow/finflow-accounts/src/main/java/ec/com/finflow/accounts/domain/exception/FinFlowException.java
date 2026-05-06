package ec.com.finflow.accounts.domain.exception;

import lombok.Getter;

/**
 * Excepción base para todas las excepciones del dominio FinFlow.
 * Proporciona estructura común para manejo de errores.
 */
@Getter
public class FinFlowException extends RuntimeException {

    private final String errorCode;
    private final String correlationId;

    public FinFlowException(String message) {
        super(message);
        this.errorCode = "FINFLOW_ERROR";
        this.correlationId = null;
    }

    public FinFlowException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.correlationId = null;
    }

    public FinFlowException(String message, String errorCode, String correlationId) {
        super(message);
        this.errorCode = errorCode;
        this.correlationId = correlationId;
    }

    public FinFlowException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FINFLOW_ERROR";
        this.correlationId = null;
    }

    public FinFlowException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.correlationId = null;
    }
}
