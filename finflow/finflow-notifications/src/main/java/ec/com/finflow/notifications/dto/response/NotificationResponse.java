package ec.com.finflow.notifications.dto.response;

import ec.com.finflow.notifications.domain.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de respuesta para notificaciones.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private String correlationId;
    private String userId;
    private String recipientEmail;
    private String notificationType;
    private String templateCode;
    private String subject;
    private String eventType;
    private String entityType;
    private UUID entityId;
    private NotificationStatus status;
    private OffsetDateTime sentAt;
    private OffsetDateTime deliveredAt;
    private String errorMessage;
    private Integer retryCount;
    private OffsetDateTime createdAt;
}
