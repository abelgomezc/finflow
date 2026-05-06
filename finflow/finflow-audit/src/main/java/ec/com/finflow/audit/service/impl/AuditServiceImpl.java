package ec.com.finflow.audit.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.com.finflow.audit.domain.entity.AuditAlert;
import ec.com.finflow.audit.domain.entity.AuditDailySummary;
import ec.com.finflow.audit.domain.entity.AuditEvent;
import ec.com.finflow.audit.domain.enums.AlertSeverity;
import ec.com.finflow.audit.domain.enums.AlertStatus;
import ec.com.finflow.audit.dto.response.AlertResponse;
import ec.com.finflow.audit.dto.response.AuditEventResponse;
import ec.com.finflow.audit.dto.response.AuditStatsResponse;
import ec.com.finflow.audit.mapper.AuditMapper;
import ec.com.finflow.audit.repository.AuditAlertRepository;
import ec.com.finflow.audit.repository.AuditDailySummaryRepository;
import ec.com.finflow.audit.repository.AuditEventRepository;
import ec.com.finflow.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio de auditoría.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditEventRepository eventRepository;
    private final AuditAlertRepository alertRepository;
    private final AuditDailySummaryRepository summaryRepository;
    private final AuditMapper auditMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void saveTransferEvent(ec.com.finflow.audit.kafka.AuditEvent kafkaEvent,
                                   String category,
                                   String eventType,
                                   String topic,
                                   int partition,
                                   long offset) {

        // Verificar si ya existe (idempotencia)
        if (eventRepository.existsByEventId(kafkaEvent.getEventId())) {
            log.debug("Event already exists: {}", kafkaEvent.getEventId());
            return;
        }

        // Convertir evento a Map para almacenar como JSON
        Map<String, Object> eventData = objectMapper.convertValue(
                kafkaEvent, new TypeReference<>() {});

        AuditEvent auditEvent = AuditEvent.builder()
                .correlationId(kafkaEvent.getCorrelationId())
                .eventId(kafkaEvent.getEventId())
                .eventType(eventType)
                .eventCategory(category)
                .entityType("TRANSFER")
                .entityId(kafkaEvent.getTransferId())
                .eventData(eventData)
                .sourceService(kafkaEvent.getSourceService() != null ?
                        kafkaEvent.getSourceService() : "finflow-transfers")
                .userId(kafkaEvent.getInitiatedBy())
                .ipAddress(kafkaEvent.getIpAddress())
                .eventTimestamp(kafkaEvent.getTimestamp() != null ?
                        kafkaEvent.getTimestamp() : OffsetDateTime.now())
                .kafkaTopic(topic)
                .kafkaPartition(partition)
                .kafkaOffset(offset)
                .build();

        auditEvent = eventRepository.save(auditEvent);
        log.info("Audit event saved: {} - {}", auditEvent.getId(), eventType);

        // Crear alertas si es necesario
        createAlertsIfNeeded(auditEvent, kafkaEvent);
    }

    private void createAlertsIfNeeded(AuditEvent auditEvent,
                                       ec.com.finflow.audit.kafka.AuditEvent kafkaEvent) {
        // Alerta para transferencias fallidas
        if ("TRANSFER_FAILED".equals(auditEvent.getEventType())) {
            AuditAlert alert = AuditAlert.builder()
                    .auditEvent(auditEvent)
                    .alertType("TRANSFER_FAILURE")
                    .severity(AlertSeverity.MEDIUM)
                    .title("Transfer Failed: " + kafkaEvent.getReferenceNumber())
                    .description("Transfer failed with reason: " + kafkaEvent.getFailureReason() +
                            ". Message: " + kafkaEvent.getFailureMessage())
                    .build();
            alertRepository.save(alert);
            log.info("Alert created for failed transfer: {}", kafkaEvent.getTransferId());
        }

        // Alerta para transferencias de alto valor
        if (kafkaEvent.getAmount() != null &&
                kafkaEvent.getAmount().compareTo(new BigDecimal("10000")) > 0) {
            AuditAlert alert = AuditAlert.builder()
                    .auditEvent(auditEvent)
                    .alertType("HIGH_VALUE_TRANSFER")
                    .severity(AlertSeverity.HIGH)
                    .title("High Value Transfer: " + kafkaEvent.getAmount())
                    .description("Transfer of " + kafkaEvent.getAmount() + " " +
                            kafkaEvent.getCurrency() + " detected")
                    .build();
            alertRepository.save(alert);
            log.info("Alert created for high value transfer: {}", kafkaEvent.getTransferId());
        }

        // Alerta para reversiones
        if ("TRANSFER_REVERSED".equals(auditEvent.getEventType())) {
            AuditAlert alert = AuditAlert.builder()
                    .auditEvent(auditEvent)
                    .alertType("TRANSFER_REVERSAL")
                    .severity(AlertSeverity.HIGH)
                    .title("Transfer Reversed: " + kafkaEvent.getReferenceNumber())
                    .description("Transfer reversed. Reason: " + kafkaEvent.getReversalReason())
                    .build();
            alertRepository.save(alert);
            log.info("Alert created for reversed transfer: {}", kafkaEvent.getTransferId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditEventResponse> findByCorrelationId(String correlationId, Pageable pageable) {
        return eventRepository.findByCorrelationIdOrderByEventTimestampDesc(correlationId, pageable)
                .map(auditMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditEventResponse> findByEntity(String entityType, String entityId, Pageable pageable) {
        return eventRepository.findByEntityTypeAndEntityIdOrderByEventTimestampDesc(
                entityType, entityId, pageable)
                .map(auditMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditEventResponse> findByUser(String userId, Pageable pageable) {
        return eventRepository.findByUserIdOrderByEventTimestampDesc(userId, pageable)
                .map(auditMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditEventResponse> findByDateRange(OffsetDateTime start, OffsetDateTime end,
                                                      Pageable pageable) {
        return eventRepository.findByDateRange(start, end, pageable)
                .map(auditMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditEventResponse> findByCategory(String category, Pageable pageable) {
        return eventRepository.findByEventCategoryOrderByEventTimestampDesc(category, pageable)
                .map(auditMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditStatsResponse getStats(LocalDate startDate, LocalDate endDate) {
        List<AuditDailySummary> summaries = summaryRepository.findByDateRange(startDate, endDate);

        long totalEvents = summaries.stream()
                .mapToLong(AuditDailySummary::getEventCount)
                .sum();

        long successCount = summaries.stream()
                .mapToLong(AuditDailySummary::getSuccessCount)
                .sum();

        long failureCount = summaries.stream()
                .mapToLong(AuditDailySummary::getFailureCount)
                .sum();

        BigDecimal totalAmount = summaries.stream()
                .map(AuditDailySummary::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long openAlerts = alertRepository.countByStatus(AlertStatus.OPEN);

        return AuditStatsResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalEvents(totalEvents)
                .successCount(successCount)
                .failureCount(failureCount)
                .totalTransferAmount(totalAmount)
                .openAlerts(openAlerts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditDailySummary> getDailySummary(LocalDate date) {
        return summaryRepository.findBySummaryDateOrderByEventCategoryAscEventTypeAsc(date);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlertResponse> getOpenAlerts(Pageable pageable) {
        return alertRepository.findByStatusOrderByCreatedAtDesc(AlertStatus.OPEN, pageable)
                .map(auditMapper::toAlertResponse);
    }

    @Override
    @Transactional
    public AlertResponse acknowledgeAlert(Long alertId, String userId) {
        AuditAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));

        alert.acknowledge(userId);
        alert = alertRepository.save(alert);

        log.info("Alert {} acknowledged by {}", alertId, userId);
        return auditMapper.toAlertResponse(alert);
    }

    @Override
    @Transactional
    public AlertResponse resolveAlert(Long alertId, String userId, String notes) {
        AuditAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));

        alert.resolve(userId, notes);
        alert = alertRepository.save(alert);

        log.info("Alert {} resolved by {}", alertId, userId);
        return auditMapper.toAlertResponse(alert);
    }
}
