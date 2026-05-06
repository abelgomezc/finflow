package ec.com.finflow.accounts.repository;

import ec.com.finflow.accounts.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca usuario por username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca usuario por email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca usuario activo por username.
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.isActive = true AND u.isLocked = false")
    Optional<User> findActiveByUsername(@Param("username") String username);

    /**
     * Verifica si existe un username.
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un email.
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuario por customerId.
     */
    Optional<User> findByCustomerId(Long customerId);
}
