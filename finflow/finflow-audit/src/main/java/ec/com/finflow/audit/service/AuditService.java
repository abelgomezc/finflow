package ec.com.finflow.audit.service;

import ec.com.finflow.audit.domain.entity.AuditAlert;
import ec.com.finflow.audit.domain.entity.AuditDailySummary;
import ec.com.finflow.audit.dto.response.AuditEventResponse;
import ec.com.finflow.audit.dto.response.AuditStatsResponse;
import ec.com.finflow.audit.dto.response.AlertResponse;
import ec.com.finflow.audit.kafka.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Servicio de auditoría.
 */
public interface AuditService {

    /**
     * Guarda un evento de transferencia.
     */
    void saveTransferEvent(AuditEvent event, String category, String eventType,
                           String topic, int partition, long offset);

    /**
     * Busca eventos por correlationId.
     */
    Page<AuditEventResponse> findByCorrelationId(String correlationId, Pageable pageable);

    /**
     * Busca eventos por entidad.
     */
    Page<AuditEventResponse> findByEntity(String entityType, String entityId, Pageable pageable);

    /**
     * Busca eventos por usuario.
     */
    Page<AuditEventResponse> findByUser(String userId, Pageable pageable);

    /**
     * Busca eventos por rango de fechas.
     */
    Page<AuditEventResponse> findByDateRange(OffsetDateTime start, OffsetDateTime end, Pageable pageable);

    /**
     * Busca eventos por categoría.
     */
    Page<AuditEventResponse> findByCategory(String category, Pageable pageable);

    /**
     * Obtiene estadísticas de auditoría.
     */
    AuditStatsResponse getStats(LocalDate startDate, LocalDate endDate);

    /**
     * Obtiene resumen diario.
     */
    List<AuditDailySummary> getDailySummary(LocalDate date);

    /**
     * Obtiene alertas abiertas.
     */
    Page<AlertResponse> getOpenAlerts(Pageable pageable);

    /**
     * Reconoce una alerta.
     */
    AlertResponse acknowledgeAlert(Long alertId, String userId);

    /**
     * Resuelve una alerta.
     */
    AlertResponse resolveAlert(Long alertId, String userId, String notes);
}
