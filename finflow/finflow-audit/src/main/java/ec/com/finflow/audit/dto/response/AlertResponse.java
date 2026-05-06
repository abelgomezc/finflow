package ec.com.finflow.audit.dto.response;

import ec.com.finflow.audit.domain.enums.AlertSeverity;
import ec.com.finflow.audit.domain.enums.AlertStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * DTO de respuesta para alertas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponse {

    private Long id;
    private Long auditEventId;
    private String alertType;
    private AlertSeverity severity;
    private String title;
    private String description;
    private AlertStatus status;
    private String acknowledgedBy;
    private OffsetDateTime acknowledgedAt;
    private String resolvedBy;
    private OffsetDateTime resolvedAt;
    private String resolutionNotes;
    private OffsetDateTime createdAt;
}
