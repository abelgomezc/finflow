package ec.com.finflow.accounts.repository;

import ec.com.finflow.accounts.domain.entity.BlockedAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repositorio para operaciones con montos bloqueados.
 */
@Repository
public interface BlockedAmountRepository extends JpaRepository<BlockedAmount, Long> {

    /**
     * Busca bloqueos activos de una cuenta.
     */
    List<BlockedAmount> findByAccountIdAndStatus(Long accountId, String status);

    /**
     * Suma de montos bloqueados activos en una cuenta.
     */
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM BlockedAmount b " +
           "WHERE b.account.id = :accountId AND b.status = 'ACTIVE' " +
           "AND (b.expiresAt IS NULL OR b.expiresAt > CURRENT_TIMESTAMP)")
    BigDecimal sumActiveBlockedAmounts(@Param("accountId") Long accountId);

    /**
     * Cuenta bloqueos activos en una cuenta.
     */
    @Query("SELECT COUNT(b) FROM BlockedAmount b " +
           "WHERE b.account.id = :accountId AND b.status = 'ACTIVE'")
    int countActiveBlocks(@Param("accountId") Long accountId);

    /**
     * Busca bloqueos expirados que aún están activos.
     */
    @Query("SELECT b FROM BlockedAmount b WHERE b.status = 'ACTIVE' AND b.expiresAt < :now")
    List<BlockedAmount> findExpiredBlocks(@Param("now") OffsetDateTime now);

    /**
     * Expira bloqueos vencidos.
     */
    @Modifying
    @Query("UPDATE BlockedAmount b SET b.status = 'EXPIRED' " +
           "WHERE b.status = 'ACTIVE' AND b.expiresAt < CURRENT_TIMESTAMP")
    int expireBlocks();

    /**
     * Busca bloqueos por transferencia.
     */
    List<BlockedAmount> findByTransferId(Long transferId);
}
