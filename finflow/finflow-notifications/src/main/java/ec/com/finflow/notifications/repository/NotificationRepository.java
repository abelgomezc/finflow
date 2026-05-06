package ec.com.finflow.notifications.repository;

import ec.com.finflow.notifications.domain.entity.Notification;
import ec.com.finflow.notifications.domain.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio para notificaciones.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    Page<Notification> findByStatusOrderByCreatedAtDesc(NotificationStatus status, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.status = 'RETRY' AND n.nextRetryAt <= :now")
    List<Notification> findNotificationsToRetry(@Param("now") OffsetDateTime now);

    @Query("SELECT n FROM Notification n WHERE n.entityType = :entityType AND n.entityId = :entityId " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findByEntity(
            @Param("entityType") String entityType,
            @Param("entityId") UUID entityId,
            Pageable pageable);

    Page<Notification> findByCorrelationIdOrderByCreatedAtDesc(String correlationId, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.status = :status")
    long countByStatus(@Param("status") NotificationStatus status);

    @Query("SELECT n.status, COUNT(n) FROM Notification n " +
           "WHERE n.createdAt >= :since GROUP BY n.status")
    List<Object[]> countByStatusSince(@Param("since") OffsetDateTime since);
}
