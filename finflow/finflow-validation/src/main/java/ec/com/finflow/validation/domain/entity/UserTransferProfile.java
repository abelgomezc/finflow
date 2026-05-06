package ec.com.finflow.validation.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Perfil de transferencias de un usuario.
 * Usado para detección de anomalías.
 */
@Entity
@Table(name = "user_transfer_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTransferProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 100)
    private String userId;

    @Column(name = "avg_transfer_amount", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal avgTransferAmount = BigDecimal.ZERO;

    @Column(name = "max_transfer_amount", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal maxTransferAmount = BigDecimal.ZERO;

    @Column(name = "min_transfer_amount", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal minTransferAmount = BigDecimal.ZERO;

    @Column(name = "total_transfer_count")
    @Builder.Default
    private Integer totalTransferCount = 0;

    @Column(name = "transfers_last_30_days")
    @Builder.Default
    private Integer transfersLast30Days = 0;

    @Column(name = "amount_last_30_days", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal amountLast30Days = BigDecimal.ZERO;

    @Column(name = "usual_hours", columnDefinition = "TEXT")
    private String usualHours;

    @Column(name = "usual_days", columnDefinition = "TEXT")
    private String usualDays;

    @Column(name = "frequent_recipients", columnDefinition = "TEXT")
    private String frequentRecipients;

    @Column(name = "failed_transfers_last_30_days")
    @Builder.Default
    private Integer failedTransfersLast30Days = 0;

    @Column(name = "fraud_alerts_count")
    @Builder.Default
    private Integer fraudAlertsCount = 0;

    @Column(name = "last_fraud_alert")
    private OffsetDateTime lastFraudAlert;

    @Column(name = "has_sufficient_history", nullable = false)
    @Builder.Default
    private Boolean hasSufficientHistory = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
