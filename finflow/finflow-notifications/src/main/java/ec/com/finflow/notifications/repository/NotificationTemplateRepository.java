package ec.com.finflow.notifications.repository;

import ec.com.finflow.notifications.domain.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para templates de notificación.
 */
@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    Optional<NotificationTemplate> findByTemplateCodeAndIsActiveTrue(String templateCode);

    List<NotificationTemplate> findByIsActiveTrue();

    Optional<NotificationTemplate> findByTemplateCode(String templateCode);
}
