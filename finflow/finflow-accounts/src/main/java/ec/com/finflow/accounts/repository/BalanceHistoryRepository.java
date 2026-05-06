package ec.com.finflow.accounts.repository;

import ec.com.finflow.accounts.domain.entity.BalanceHistory;
import ec.com.finflow.accounts.domain.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones con historial de balance.
 */
@Repository
public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, Long> {

    /**
     * Busca historial de una cuenta, ordenado por fecha descendente.
     */
    Page<BalanceHistory> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    /**
     * Busca historial por referencia.
     */
    List<BalanceHistory> findByReferenceIdOrderByCreatedAtAsc(String referenceId);

    /**
     * Suma de débitos de una cuenta en un rango de fechas.
     */
    @Query("SELECT COALESCE(SUM(h.amount), 0) FROM BalanceHistory h " +
           "WHERE h.account.id = :accountId " +
           "AND h.transactionType = 'DEBIT' " +
           "AND h.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumDebitsByAccountAndDateRange(
            @Param("accountId") Long accountId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    /**
     * Cuenta transacciones de débito en las últimas N horas.
     */
    @Query("SELECT COUNT(h) FROM BalanceHistory h " +
           "WHERE h.account.id = :accountId " +
           "AND h.transactionType = 'DEBIT' " +
           "AND h.createdAt >= :since")
    int countDebitsAccountSince(
            @Param("accountId") Long accountId,
            @Param("since") OffsetDateTime since
    );

    /**
     * Busca historial por tipo de transacción.
     */
    Page<BalanceHistory> findByAccountIdAndTransactionTypeOrderByCreatedAtDesc(
            Long accountId, TransactionType transactionType, Pageable pageable);

    /**
     * Obtiene las últimas N transacciones de una cuenta.
     */
    List<BalanceHistory> findTop10ByAccountIdOrderByCreatedAtDesc(Long accountId);
}
