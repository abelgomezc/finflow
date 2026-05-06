package ec.com.finflow.notifications.controller;

import ec.com.finflow.notifications.dto.response.NotificationResponse;
import ec.com.finflow.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST para notificaciones.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Obtiene notificaciones de un usuario.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationResponse>> getUserNotifications(
            @PathVariable String userId,
            @PageableDefault(size = 20) Pageable pageable) {

        log.debug("Getting notifications for user: {}", userId);
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtiene una notificación por ID.
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotification(
            @PathVariable UUID notificationId) {

        log.debug("Getting notification: {}", notificationId);
        NotificationResponse notification = notificationService.getNotification(notificationId);
        return ResponseEntity.ok(notification);
    }

    /**
     * Fuerza el reintento de notificaciones fallidas.
     */
    @PostMapping("/retry")
    public ResponseEntity<String> retryFailedNotifications(
            @RequestHeader(value = "X-User-ID") String userId) {

        log.info("Manual retry of failed notifications requested by: {}", userId);
        notificationService.retryFailedNotifications();
        return ResponseEntity.ok("Retry process started");
    }

    /**
     * Health check.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification service is healthy");
    }
}
