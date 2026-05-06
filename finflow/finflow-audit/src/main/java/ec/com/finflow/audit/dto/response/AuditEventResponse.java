package ec.com.finflow.audit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * DTO de respuesta para eventos de auditoría.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEventResponse {

    private Long id;
    private String correlationId;
    private String eventId;
    private String eventType;
    private String eventCategory;
    private String entityType;
    private String entityId;
    private Map<String, Object> eventData;
    private String sourceService;
    private String userId;
    private OffsetDateTime eventTimestamp;
    private OffsetDateTime receivedAt;
}
