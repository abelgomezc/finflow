package ec.com.finflow.transfers.repository;

import ec.com.finflow.transfers.domain.entity.Transfer;
import ec.com.finflow.transfers.domain.enums.TransferStatus;
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
 * Repositorio para operaciones con transferencias.
 */
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<Transfer> findByReferenceNumber(String referenceNumber);

    Page<Transfer> findByInitiatedByOrderByInitiatedAtDesc(String initiatedBy, Pageable pageable);

    Page<Transfer> findBySourceAccountIdOrderByInitiatedAtDesc(Long sourceAccountId, Pageable pageable);

    @Query("SELECT t FROM Transfer t " +
           "WHERE (t.sourceAccountId = :accountId OR t.targetAccountId = :accountId) " +
           "ORDER BY t.initiatedAt DESC")
    Page<Transfer> findByAccountId(@Param("accountId") Long accountId, Pageable pageable);

    List<Transfer> findByStatus(TransferStatus status);

    @Query("SELECT t FROM Transfer t " +
           "WHERE t.status = :status " +
           "AND t.initiatedAt < :before")
    List<Transfer> findStuckTransfers(
            @Param("status") TransferStatus status,
            @Param("before") OffsetDateTime before
    );

    /**
     * Cuenta transferencias de un usuario en un período.
     */
    @Query("SELECT COUNT(t) FROM Transfer t " +
           "WHERE t.initiatedBy = :userId " +
           "AND t.initiatedAt >= :since")
    int countByUserSince(@Param("userId") String userId, @Param("since") OffsetDateTime since);

    /**
     * Verifica si ya existe una transferencia con el mismo correlation ID.
     */
    boolean existsByCorrelationId(String correlationId);
}
