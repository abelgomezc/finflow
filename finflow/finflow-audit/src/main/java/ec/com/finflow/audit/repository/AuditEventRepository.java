package ec.com.finflow.audit.repository;

import ec.com.finflow.audit.domain.entity.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para eventos de auditoría.
 */
@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {

    Optional<AuditEvent> findByEventId(String eventId);

    boolean existsByEventId(String eventId);

    Page<AuditEvent> findByCorrelationIdOrderByEventTimestampDesc(
            String correlationId, Pageable pageable);

    Page<AuditEvent> findByEntityTypeAndEntityIdOrderByEventTimestampDesc(
            String entityType, String entityId, Pageable pageable);

    Page<AuditEvent> findByUserIdOrderByEventTimestampDesc(
            String userId, Pageable pageable);

    Page<AuditEvent> findByEventCategoryOrderByEventTimestampDesc(
            String eventCategory, Pageable pageable);

    Page<AuditEvent> findByEventTypeOrderByEventTimestampDesc(
            String eventType, Pageable pageable);

    @Query("SELECT ae FROM AuditEvent ae WHERE ae.eventTimestamp BETWEEN :start AND :end " +
           "ORDER BY ae.eventTimestamp DESC")
    Page<AuditEvent> findByDateRange(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            Pageable pageable);

    @Query("SELECT ae FROM AuditEvent ae WHERE ae.eventCategory = :category " +
           "AND ae.eventTimestamp BETWEEN :start AND :end " +
           "ORDER BY ae.eventTimestamp DESC")
    Page<AuditEvent> findByCategoryAndDateRange(
            @Param("category") String category,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            Pageable pageable);

    @Query("SELECT COUNT(ae) FROM AuditEvent ae WHERE ae.eventCategory = :category " +
           "AND ae.eventTimestamp >= :since")
    long countByCategorySince(
            @Param("category") String category,
            @Param("since") OffsetDateTime since);

    @Query("SELECT ae.eventType, COUNT(ae) FROM AuditEvent ae " +
           "WHERE ae.eventTimestamp >= :since " +
           "GROUP BY ae.eventType ORDER BY COUNT(ae) DESC")
    List<Object[]> countByEventTypeSince(@Param("since") OffsetDateTime since);

    @Query(value = "SELECT * FROM audit_events " +
           "WHERE event_data @> :jsonFilter::jsonb " +
           "ORDER BY event_timestamp DESC",
           nativeQuery = true)
    Page<AuditEvent> findByEventDataContaining(
            @Param("jsonFilter") String jsonFilter,
            Pageable pageable);
}
