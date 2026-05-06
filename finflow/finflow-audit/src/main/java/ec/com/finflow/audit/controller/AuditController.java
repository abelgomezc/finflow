package ec.com.finflow.audit.controller;

import ec.com.finflow.audit.domain.entity.AuditDailySummary;
import ec.com.finflow.audit.dto.response.AlertResponse;
import ec.com.finflow.audit.dto.response.AuditEventResponse;
import ec.com.finflow.audit.dto.response.AuditStatsResponse;
import ec.com.finflow.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Controlador REST para auditoría.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    /**
     * Busca eventos por correlationId.
     */
    @GetMapping("/events/correlation/{correlationId}")
    public ResponseEntity<Page<AuditEventResponse>> findByCorrelationId(
            @PathVariable String correlationId,
            @PageableDefault(size = 20) Pageable pageable) {

        log.debug("Finding events by correlationId: {}", correlationId);
        Page<AuditEventResponse> events = auditService.findByCorrelationId(correlationId, pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Busca eventos por entidad.
     */
    @GetMapping("/events/entity/{entityType}/{entityId}")
    public ResponseEntity<Page<AuditEventResponse>> findByEntity(
            @PathVariable String entityType,
            @PathVariable String entityId,
            @PageableDefault(size = 20) Pageable pageable) {

        log.debug("Finding events for entity: {} {}", entityType, entityId);
        Page<AuditEventResponse> events = auditService.findByEntity(entityType, entityId, pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Busca eventos por usuario.
     */
    @GetMapping("/events/user/{userId}")
    public ResponseEntity<Page<AuditEventResponse>> findByUser(
            @PathVariable String userId,
            @PageableDefault(size = 20) Pageable pageable) {

        log.debug("Finding events for user: {}", userId);
        Page<AuditEventResponse> events = auditService.findByUser(userId, pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Busca eventos por rango de fechas.
     */
    @GetMapping("/events/date-range")
    public ResponseEntity<Page<AuditEventResponse>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end,
            @PageableDefault(size = 20) Pageable pageable) {

        log.debug("Finding events between {} and {}", start, end);
        Page<AuditEventResponse> events = auditService.findByDateRange(start, end, pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Busca eventos por categoría.
     */
    @GetMapping("/events/category/{category}")
    public ResponseEntity<Page<AuditEventResponse>> findByCategory(
            @PathVariable String category,
            @PageableDefault(size = 20) Pageable pageable) {

        log.debug("Finding events for category: {}", category);
        Page<AuditEventResponse> events = auditService.findByCategory(category, pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Obtiene estadísticas de auditoría.
     */
    @GetMapping("/stats")
    public ResponseEntity<AuditStatsResponse> getStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.debug("Getting audit stats from {} to {}", startDate, endDate);
        AuditStatsResponse stats = auditService.getStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene resumen diario.
     */
    @GetMapping("/summary/{date}")
    public ResponseEntity<List<AuditDailySummary>> getDailySummary(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.debug("Getting daily summary for {}", date);
        List<AuditDailySummary> summary = auditService.getDailySummary(date);
        return ResponseEntity.ok(summary);
    }

    /**
     * Obtiene alertas abiertas.
     */
    @GetMapping("/alerts")
    public ResponseEntity<Page<AlertResponse>> getOpenAlerts(
            @PageableDefault(size = 20) Pageable pageable) {

        log.debug("Getting open alerts");
        Page<AlertResponse> alerts = auditService.getOpenAlerts(pageable);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Reconoce una alerta.
     */
    @PostMapping("/alerts/{alertId}/acknowledge")
    public ResponseEntity<AlertResponse> acknowledgeAlert(
            @PathVariable Long alertId,
            @RequestHeader(value = "X-User-ID") String userId) {

        log.info("Acknowledging alert {} by {}", alertId, userId);
        AlertResponse alert = auditService.acknowledgeAlert(alertId, userId);
        return ResponseEntity.ok(alert);
    }

    /**
     * Resuelve una alerta.
     */
    @PostMapping("/alerts/{alertId}/resolve")
    public ResponseEntity<AlertResponse> resolveAlert(
            @PathVariable Long alertId,
            @RequestParam String notes,
            @RequestHeader(value = "X-User-ID") String userId) {

        log.info("Resolving alert {} by {}: {}", alertId, userId, notes);
        AlertResponse alert = auditService.resolveAlert(alertId, userId, notes);
        return ResponseEntity.ok(alert);
    }

    /**
     * Health check.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Audit service is healthy");
    }
}
