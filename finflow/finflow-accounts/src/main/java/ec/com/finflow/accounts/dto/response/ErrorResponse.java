package ec.com.finflow.accounts.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * DTO de respuesta para errores.
 * Record inmutable para respuestas de error estandarizadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String code;
    private String message;
    private String correlationId;
    private OffsetDateTime timestamp;
    private Map<String, String> details;
    private String path;

    /**
     * Crea una respuesta de error simple.
     */
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    /**
     * Crea una respuesta de error con correlation ID.
     */
    public static ErrorResponse of(String code, String message, String correlationId) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .correlationId(correlationId)
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
