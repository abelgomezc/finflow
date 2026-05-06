package ec.com.finflow.notifications.service;

import ec.com.finflow.notifications.dto.response.NotificationResponse;
import ec.com.finflow.notifications.kafka.TransferNotificationEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio de notificaciones.
 */
public interface NotificationService {

    /**
     * Envía una notificación de transferencia.
     */
    void sendTransferNotification(TransferNotificationEvent event, String eventType,
                                   String topic, int partition, long offset);

    /**
     * Obtiene notificaciones de un usuario.
     */
    Page<NotificationResponse> getUserNotifications(String userId, Pageable pageable);

    /**
     * Obtiene una notificación por ID.
     */
    NotificationResponse getNotification(Long notificationId);

    /**
     * Reintenta enviar notificaciones fallidas.
     */
    void retryFailedNotifications();
}
