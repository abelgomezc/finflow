package ec.com.finflow.notifications.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.com.finflow.notifications.domain.entity.Notification;
import ec.com.finflow.notifications.domain.entity.NotificationTemplate;
import ec.com.finflow.notifications.domain.entity.UserNotificationPreferences;
import ec.com.finflow.notifications.domain.enums.NotificationStatus;
import ec.com.finflow.notifications.dto.response.NotificationResponse;
import ec.com.finflow.notifications.kafka.TransferNotificationEvent;
import ec.com.finflow.notifications.mapper.NotificationMapper;
import ec.com.finflow.notifications.repository.NotificationRepository;
import ec.com.finflow.notifications.repository.NotificationTemplateRepository;
import ec.com.finflow.notifications.repository.UserNotificationPreferencesRepository;
import ec.com.finflow.notifications.service.EmailService;
import ec.com.finflow.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio de notificaciones.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final UserNotificationPreferencesRepository preferencesRepository;
    private final EmailService emailService;
    private final NotificationMapper notificationMapper;
    private final ObjectMapper objectMapper;

    @Value("${finflow.notifications.email.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Override
    @Transactional
    public void sendTransferNotification(TransferNotificationEvent event, String eventType,
                                          String topic, int partition, long offset) {
        log.info("[{}] Processing transfer notification: {} for user {}",
                event.getCorrelationId(), eventType, event.getInitiatedBy());

        // Obtener preferencias del usuario
        UserNotificationPreferences preferences = preferencesRepository
                .findByUserId(event.getInitiatedBy())
                .orElse(createDefaultPreferences(event.getInitiatedBy()));

        // Verificar si el usuario tiene habilitadas las notificaciones para este evento
        if (!preferences.isNotificationEnabled(eventType)) {
            log.debug("[{}] User {} has disabled {} notifications",
                    event.getCorrelationId(), event.getInitiatedBy(), eventType);
            return;
        }

        // Obtener template
        NotificationTemplate template = templateRepository
                .findByTemplateCodeAndIsActiveTrue(eventType)
                .orElseThrow(() -> new RuntimeException("Template not found: " + eventType));

        // Preparar variables para el template
        Map<String, Object> variables = prepareTemplateVariables(event);

        // Generar subject
        String subject = processSubjectTemplate(template.getSubjectTemplate(), event);

        // Generar body (para registro)
        String body = generateNotificationBody(event, eventType);

        // Convertir evento a Map para almacenar como JSON
        Map<String, Object> eventData = objectMapper.convertValue(
                event, new TypeReference<>() {});

        // Crear registro de notificación
        Notification notification = Notification.builder()
                .correlationId(event.getCorrelationId())
                .userId(event.getInitiatedBy())
                .recipientEmail(preferences.getEmail())
                .notificationType("EMAIL")
                .templateCode(eventType)
                .subject(subject)
                .body(body)
                .eventType(eventType)
                .entityType("TRANSFER")
                .entityId(event.getTransferId())
                .eventData(eventData)
                .kafkaTopic(topic)
                .kafkaPartition(partition)
                .kafkaOffset(offset)
                .build();

        notification = notificationRepository.save(notification);

        try {
            // Enviar email
            emailService.sendTemplateEmail(
                    preferences.getEmail(),
                    subject,
                    template.getBodyTemplateName(),
                    variables
            );

            notification.markAsSent();
            notificationRepository.save(notification);
            log.info("[{}] Notification sent successfully: {} to {}",
                    event.getCorrelationId(), eventType, preferences.getEmail());

        } catch (Exception e) {
            log.error("[{}] Failed to send notification: {}",
                    event.getCorrelationId(), e.getMessage());

            if (notification.getRetryCount() < maxRetryAttempts) {
                notification.scheduleRetry();
            } else {
                notification.markAsFailed(e.getMessage());
            }
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(String userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));
        return notificationMapper.toResponse(notification);
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${finflow.notifications.email.retry.delay-ms:60000}")
    public void retryFailedNotifications() {
        List<Notification> toRetry = notificationRepository
                .findNotificationsToRetry(OffsetDateTime.now());

        if (toRetry.isEmpty()) {
            return;
        }

        log.info("Retrying {} failed notifications", toRetry.size());

        for (Notification notification : toRetry) {
            try {
                // Obtener template
                NotificationTemplate template = templateRepository
                        .findByTemplateCodeAndIsActiveTrue(notification.getTemplateCode())
                        .orElse(null);

                if (template == null) {
                    notification.markAsFailed("Template not found: " + notification.getTemplateCode());
                    notificationRepository.save(notification);
                    continue;
                }

                // Reconstruir variables del template
                Map<String, Object> variables = new HashMap<>(notification.getEventData());
                variables.put("referenceNumber", notification.getEventData().get("referenceNumber"));

                // Intentar reenviar
                emailService.sendTemplateEmail(
                        notification.getRecipientEmail(),
                        notification.getSubject(),
                        template.getBodyTemplateName(),
                        variables
                );

                notification.markAsSent();
                notificationRepository.save(notification);
                log.info("Retry successful for notification: {}", notification.getId());

            } catch (Exception e) {
                log.error("Retry failed for notification {}: {}",
                        notification.getId(), e.getMessage());

                if (notification.getRetryCount() >= maxRetryAttempts) {
                    notification.markAsFailed("Max retry attempts exceeded: " + e.getMessage());
                } else {
                    notification.scheduleRetry();
                }
                notificationRepository.save(notification);
            }
        }
    }

    private UserNotificationPreferences createDefaultPreferences(String userId) {
        // En producción, esto debería obtener el email del servicio de usuarios
        return UserNotificationPreferences.builder()
                .userId(userId)
                .email(userId + "@example.com") // Placeholder
                .emailEnabled(true)
                .transferInitiatedEnabled(true)
                .transferCompletedEnabled(true)
                .transferFailedEnabled(true)
                .transferReversedEnabled(true)
                .build();
    }

    private Map<String, Object> prepareTemplateVariables(TransferNotificationEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("referenceNumber", event.getReferenceNumber());
        variables.put("amount", event.getAmount());
        variables.put("currency", event.getCurrency());
        variables.put("sourceAccountId", event.getSourceAccountId());
        variables.put("targetAccountId", event.getTargetAccountId());
        variables.put("timestamp", event.getTimestamp() != null ?
                event.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) :
                "N/A");
        variables.put("failureReason", event.getFailureReason());
        variables.put("failureMessage", event.getFailureMessage());
        variables.put("reversalReason", event.getReversalReason());
        return variables;
    }

    private String processSubjectTemplate(String template, TransferNotificationEvent event) {
        return template.replace("#{referenceNumber}", event.getReferenceNumber());
    }

    private String generateNotificationBody(TransferNotificationEvent event, String eventType) {
        return String.format("Transfer %s - Reference: %s, Amount: %s %s",
                eventType, event.getReferenceNumber(), event.getAmount(), event.getCurrency());
    }
}
