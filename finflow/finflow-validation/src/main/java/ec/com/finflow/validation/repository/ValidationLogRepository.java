package ec.com.finflow.validation.repository;

import ec.com.finflow.validation.domain.entity.ValidationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repositorio para logs de validación.
 */
@Repository
public interface ValidationLogRepository extends JpaRepository<ValidationLog, Long> {

    List<ValidationLog> findByTransferId(String transferId);

    Page<ValidationLog> findByUserIdOrderByValidatedAtDesc(String userId, Pageable pageable);

    @Query("SELECT v FROM ValidationLog v " +
           "WHERE (v.sourceAccountId = :accountId OR v.targetAccountId = :accountId) " +
           "ORDER BY v.validatedAt DESC")
    Page<ValidationLog> findByAccountId(@Param("accountId") Long accountId, Pageable pageable);

    /**
     * Cuenta validaciones rechazadas en un período.
     */
    @Query("SELECT COUNT(v) FROM ValidationLog v " +
           "WHERE v.userId = :userId " +
           "AND v.approved = false " +
           "AND v.validatedAt >= :since")
    int countRejectedSince(@Param("userId") String userId, @Param("since") OffsetDateTime since);

    /**
     * Promedio de fraud score de un usuario.
     */
    @Query("SELECT AVG(v.fraudScore) FROM ValidationLog v " +
           "WHERE v.userId = :userId " +
           "AND v.validatedAt >= :since")
    Double avgFraudScoreSince(@Param("userId") String userId, @Param("since") OffsetDateTime since);
}
