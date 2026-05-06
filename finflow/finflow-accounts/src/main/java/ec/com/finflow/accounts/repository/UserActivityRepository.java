package ec.com.finflow.accounts.repository;

import ec.com.finflow.accounts.domain.entity.UserActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    /**
     * Obtiene actividades de un usuario ordenadas por fecha descendente.
     */
    Page<UserActivity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Obtiene actividades de un usuario filtradas por tipo.
     */
    Page<UserActivity> findByUserIdAndActivityTypeOrderByCreatedAtDesc(
            Long userId, String activityType, Pageable pageable);

    /**
     * Obtiene las últimas N actividades de un usuario.
     */
    List<UserActivity> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Cuenta actividades por tipo en un rango de fechas.
     */
    @Query("SELECT ua.activityType, COUNT(ua) FROM UserActivity ua " +
           "WHERE ua.user.id = :userId AND ua.createdAt >= :since " +
           "GROUP BY ua.activityType")
    List<Object[]> countByActivityTypeSince(
            @Param("userId") Long userId,
            @Param("since") OffsetDateTime since);
}
