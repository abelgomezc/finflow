package ec.com.finflow.notifications.mapper;

import ec.com.finflow.notifications.domain.entity.Notification;
import ec.com.finflow.notifications.dto.response.NotificationResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-01T11:13:38-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Ubuntu)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationResponse toResponse(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationResponse.NotificationResponseBuilder notificationResponse = NotificationResponse.builder();

        notificationResponse.id( notification.getId() );
        notificationResponse.correlationId( notification.getCorrelationId() );
        notificationResponse.userId( notification.getUserId() );
        notificationResponse.recipientEmail( notification.getRecipientEmail() );
        notificationResponse.notificationType( notification.getNotificationType() );
        notificationResponse.templateCode( notification.getTemplateCode() );
        notificationResponse.subject( notification.getSubject() );
        notificationResponse.eventType( notification.getEventType() );
        notificationResponse.entityType( notification.getEntityType() );
        notificationResponse.entityId( notification.getEntityId() );
        notificationResponse.status( notification.getStatus() );
        notificationResponse.sentAt( notification.getSentAt() );
        notificationResponse.deliveredAt( notification.getDeliveredAt() );
        notificationResponse.errorMessage( notification.getErrorMessage() );
        notificationResponse.retryCount( notification.getRetryCount() );
        notificationResponse.createdAt( notification.getCreatedAt() );

        return notificationResponse.build();
    }
}
