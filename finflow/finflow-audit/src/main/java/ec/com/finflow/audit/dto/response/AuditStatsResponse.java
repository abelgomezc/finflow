package ec.com.finflow.audit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para estadísticas de auditoría.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditStatsResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private long totalEvents;
    private long successCount;
    private long failureCount;
    private BigDecimal totalTransferAmount;
    private long openAlerts;

    public double getSuccessRate() {
        if (totalEvents == 0) return 0.0;
        return (double) successCount / totalEvents * 100;
    }

    public double getFailureRate() {
        if (totalEvents == 0) return 0.0;
        return (double) failureCount / totalEvents * 100;
    }
}
