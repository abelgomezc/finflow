package ec.com.finflow.validation.repository;

import ec.com.finflow.validation.domain.entity.BlacklistedAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para cuentas en lista negra.
 */
@Repository
public interface BlacklistedAccountRepository extends JpaRepository<BlacklistedAccount, Long> {

    Optional<BlacklistedAccount> findByAccountId(Long accountId);

    /**
     * Verifica si una cuenta está en lista negra activa.
     */
    @Query("SELECT COUNT(b) > 0 FROM BlacklistedAccount b " +
           "WHERE b.accountId = :accountId " +
           "AND b.isActive = true " +
           "AND (b.expiresAt IS NULL OR b.expiresAt > CURRENT_TIMESTAMP)")
    boolean isBlacklisted(@Param("accountId") Long accountId);

    /**
     * Obtiene la entrada de lista negra activa.
     */
    @Query("SELECT b FROM BlacklistedAccount b " +
           "WHERE b.accountId = :accountId " +
           "AND b.isActive = true " +
           "AND (b.expiresAt IS NULL OR b.expiresAt > CURRENT_TIMESTAMP)")
    Optional<BlacklistedAccount> findActiveByAccountId(@Param("accountId") Long accountId);

    /**
     * Lista todas las cuentas activamente bloqueadas.
     */
    @Query("SELECT b FROM BlacklistedAccount b " +
           "WHERE b.isActive = true " +
           "AND (b.expiresAt IS NULL OR b.expiresAt > CURRENT_TIMESTAMP)")
    List<BlacklistedAccount> findAllActive();
}
