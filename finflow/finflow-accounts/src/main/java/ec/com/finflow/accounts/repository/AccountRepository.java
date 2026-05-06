package ec.com.finflow.accounts.repository;

import ec.com.finflow.accounts.domain.entity.Account;
import ec.com.finflow.accounts.domain.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones con cuentas.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Busca una cuenta por número.
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Busca cuentas de un cliente.
     */
    List<Account> findByCustomerId(Long customerId);

    /**
     * Busca cuentas activas de un cliente.
     */
    List<Account> findByCustomerIdAndStatus(Long customerId, AccountStatus status);

    /**
     * Verifica si existe una cuenta con el número dado.
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * Calcula el saldo disponible (balance - bloqueados activos).
     * Usa la función PL/pgSQL fn_available_balance.
     */
    @Query(value = "SELECT fn_available_balance(:accountId)", nativeQuery = true)
    BigDecimal getAvailableBalance(@Param("accountId") Long accountId);

    /**
     * Obtiene el total transferido en el día.
     * Usa la función PL/pgSQL fn_daily_transfer_total.
     */
    @Query(value = "SELECT fn_daily_transfer_total(:accountId, CURRENT_DATE)", nativeQuery = true)
    BigDecimal getDailyTransferTotal(@Param("accountId") Long accountId);

    /**
     * Cuenta transferencias recientes.
     * Retorna el count de transferencias en las últimas N horas.
     */
    @Query(value = "SELECT transfer_count FROM fn_count_recent_transfers(:accountId, :hours)", nativeQuery = true)
    Integer countRecentTransfers(@Param("accountId") Long accountId, @Param("hours") int hours);

    /**
     * Suma de montos de transferencias recientes.
     */
    @Query(value = "SELECT total_amount FROM fn_count_recent_transfers(:accountId, :hours)", nativeQuery = true)
    BigDecimal getRecentTransfersTotal(@Param("accountId") Long accountId, @Param("hours") int hours);

    /**
     * Busca cuentas con saldo mayor a un monto.
     */
    @Query("SELECT a FROM Account a WHERE a.balance > :minBalance AND a.status = :status")
    List<Account> findByBalanceGreaterThanAndStatus(
            @Param("minBalance") BigDecimal minBalance,
            @Param("status") AccountStatus status
    );
}
