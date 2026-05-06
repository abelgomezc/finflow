package ec.com.finflow.accounts.service;

import ec.com.finflow.accounts.domain.entity.User;
import ec.com.finflow.accounts.domain.entity.UserActivity;
import ec.com.finflow.accounts.repository.UserActivityRepository;
import ec.com.finflow.accounts.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final UserActivityRepository activityRepository;
    private final UserRepository userRepository;

    /**
     * Registra una actividad de usuario de forma asíncrona.
     */
    @Async
    @Transactional
    public void logActivity(Long userId, String activityType, String description,
                           String entityType, String entityId, String ipAddress) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warn("Cannot log activity: User {} not found", userId);
                return;
            }

            UserActivity activity = UserActivity.builder()
                    .user(userOpt.get())
                    .activityType(activityType)
                    .description(description)
                    .entityType(entityType)
                    .entityId(entityId)
                    .ipAddress(ipAddress)
                    .build();

            activityRepository.save(activity);
            log.debug("Activity logged: {} for user {}", activityType, userId);
        } catch (Exception e) {
            log.error("Error logging activity for user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Registra un login exitoso.
     */
    public void logLogin(Long userId, String ipAddress) {
        logActivity(userId, UserActivity.ActivityType.LOGIN,
                "Inicio de sesión exitoso", null, null, ipAddress);
    }

    /**
     * Registra un intento de login fallido.
     */
    public void logLoginFailed(String username, String ipAddress) {
        // Para login fallido, buscamos el usuario por username
        userRepository.findByUsername(username).ifPresent(user ->
            logActivity(user.getId(), UserActivity.ActivityType.LOGIN_FAILED,
                    "Intento de inicio de sesión fallido", null, null, ipAddress)
        );
    }

    /**
     * Registra inicio de transferencia.
     */
    public void logTransferInitiated(Long userId, String transferRef, String ipAddress) {
        logActivity(userId, UserActivity.ActivityType.TRANSFER_INITIATED,
                "Transferencia iniciada: " + transferRef, "TRANSFER", transferRef, ipAddress);
    }

    /**
     * Registra actualización de perfil.
     */
    public void logProfileUpdated(Long userId, String ipAddress) {
        logActivity(userId, UserActivity.ActivityType.PROFILE_UPDATED,
                "Perfil actualizado", "USER", userId.toString(), ipAddress);
    }

    /**
     * Obtiene el historial de actividad de un usuario.
     */
    @Transactional(readOnly = true)
    public Page<UserActivity> getActivityHistory(Long userId, int page, int size) {
        return activityRepository.findByUserIdOrderByCreatedAtDesc(
                userId, PageRequest.of(page, size));
    }

    /**
     * Obtiene las últimas 10 actividades de un usuario.
     */
    @Transactional(readOnly = true)
    public List<UserActivity> getRecentActivity(Long userId) {
        return activityRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
    }
}
