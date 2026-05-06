package ec.com.finflow.notifications.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Entidad que representa las preferencias de notificación de un usuario.
 */
@Entity
@Table(name = "user_notification_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 100)
    private String userId;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "email_enabled", nullable = false)
    @Builder.Default
    private Boolean emailEnabled = true;

    @Column(name = "transfer_initiated_enabled", nullable = false)
    @Builder.Default
    private Boolean transferInitiatedEnabled = true;

    @Column(name = "transfer_completed_enabled", nullable = false)
    @Builder.Default
    private Boolean transferCompletedEnabled = true;

    @Column(name = "transfer_failed_enabled", nullable = false)
    @Builder.Default
    private Boolean transferFailedEnabled = true;

    @Column(name = "transfer_reversed_enabled", nullable = false)
    @Builder.Default
    private Boolean transferReversedEnabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public boolean isNotificationEnabled(String eventType) {
        if (!emailEnabled) return false;

        return switch (eventType) {
            case "TRANSFER_INITIATED" -> transferInitiatedEnabled;
            case "TRANSFER_COMPLETED" -> transferCompletedEnabled;
            case "TRANSFER_FAILED" -> transferFailedEnabled;
            case "TRANSFER_REVERSED" -> transferReversedEnabled;
            default -> true;
        };
    }
}
