package ec.com.finflow.audit.repository;

import ec.com.finflow.audit.domain.entity.AuditAlert;
import ec.com.finflow.audit.domain.enums.AlertSeverity;
import ec.com.finflow.audit.domain.enums.AlertStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repositorio para alertas de auditoría.
 */
@Repository
public interface AuditAlertRepository extends JpaRepository<AuditAlert, Long> {

    Page<AuditAlert> findByStatusOrderByCreatedAtDesc(
            AlertStatus status, Pageable pageable);

    Page<AuditAlert> findBySeverityOrderByCreatedAtDesc(
            AlertSeverity severity, Pageable pageable);

    Page<AuditAlert> findByStatusAndSeverityOrderByCreatedAtDesc(
            AlertStatus status, AlertSeverity severity, Pageable pageable);

    List<AuditAlert> findByStatusInOrderBySeverityDescCreatedAtDesc(
            List<AlertStatus> statuses);

    @Query("SELECT COUNT(aa) FROM AuditAlert aa WHERE aa.status = :status")
    long countByStatus(@Param("status") AlertStatus status);

    @Query("SELECT aa.severity, COUNT(aa) FROM AuditAlert aa " +
           "WHERE aa.status = :status GROUP BY aa.severity")
    List<Object[]> countByStatusGroupedBySeverity(@Param("status") AlertStatus status);

    @Query("SELECT aa FROM AuditAlert aa WHERE aa.status = 'OPEN' " +
           "AND aa.createdAt < :threshold ORDER BY aa.createdAt ASC")
    List<AuditAlert> findStaleOpenAlerts(@Param("threshold") OffsetDateTime threshold);

    Page<AuditAlert> findByAlertTypeOrderByCreatedAtDesc(
            String alertType, Pageable pageable);
}
