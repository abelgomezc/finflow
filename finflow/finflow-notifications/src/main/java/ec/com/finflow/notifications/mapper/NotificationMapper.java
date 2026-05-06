package ec.com.finflow.notifications.mapper;

import ec.com.finflow.notifications.domain.entity.Notification;
import ec.com.finflow.notifications.dto.response.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper para notificaciones.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);
}
