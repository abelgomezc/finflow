package ec.com.finflow.notifications.repository;

import ec.com.finflow.notifications.domain.entity.UserNotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para preferencias de notificación de usuario.
 */
@Repository
public interface UserNotificationPreferencesRepository extends JpaRepository<UserNotificationPreferences, Long> {

    Optional<UserNotificationPreferences> findByUserId(String userId);

    Optional<UserNotificationPreferences> findByEmail(String email);
}
