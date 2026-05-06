package ec.com.finflow.accounts.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Entidad que registra la actividad del usuario.
 * Incluye logins, acciones en transferencias, cambios de perfil, etc.
 */
@Entity
@Table(name = "user_activity", indexes = {
    @Index(name = "idx_user_activity_user_id", columnList = "user_id"),
    @Index(name = "idx_user_activity_type", columnList = "activity_type"),
    @Index(name = "idx_user_activity_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "activity_type", nullable = false, length = 50)
    private String activityType;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "entity_type", length = 50)
    private String entityType;

    @Column(name = "entity_id", length = 100)
    private String entityId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    // Metadata field omitted for simplicity - JSONB requires special handling

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    /**
     * Tipos de actividad comunes
     */
    public static class ActivityType {
        public static final String LOGIN = "LOGIN";
        public static final String LOGOUT = "LOGOUT";
        public static final String LOGIN_FAILED = "LOGIN_FAILED";
        public static final String TRANSFER_INITIATED = "TRANSFER_INITIATED";
        public static final String TRANSFER_COMPLETED = "TRANSFER_COMPLETED";
        public static final String TRANSFER_FAILED = "TRANSFER_FAILED";
        public static final String PROFILE_UPDATED = "PROFILE_UPDATED";
        public static final String PASSWORD_CHANGED = "PASSWORD_CHANGED";
        public static final String ACCOUNT_VIEWED = "ACCOUNT_VIEWED";
    }
}
