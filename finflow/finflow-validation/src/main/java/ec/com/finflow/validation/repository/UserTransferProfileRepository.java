package ec.com.finflow.validation.repository;

import ec.com.finflow.validation.domain.entity.UserTransferProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para perfiles de usuario.
 */
@Repository
public interface UserTransferProfileRepository extends JpaRepository<UserTransferProfile, Long> {

    Optional<UserTransferProfile> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
