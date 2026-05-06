package ec.com.finflow.validation.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Log de validaciones ejecutadas.
 */
@Entity
@Table(name = "validation_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transfer_id", nullable = false, length = 100)
    private String transferId;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "source_account_id", nullable = false)
    private Long sourceAccountId;

    @Column(name = "target_account_id", nullable = false)
    private Long targetAccountId;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "approved", nullable = false)
    private Boolean approved;

    @Column(name = "rejection_reason", length = 100)
    private String rejectionReason;

    @Column(name = "rejection_message", columnDefinition = "TEXT")
    private String rejectionMessage;

    @Column(name = "fraud_score", nullable = false)
    @Builder.Default
    private Integer fraudScore = 0;

    @Column(name = "fraud_indicators", columnDefinition = "TEXT")
    private String fraudIndicators;

    @Column(name = "validation_time_ms")
    private Long validationTimeMs;

    @Column(name = "account_check_time_ms")
    private Long accountCheckTimeMs;

    @Column(name = "fraud_check_time_ms")
    private Long fraudCheckTimeMs;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "device_fingerprint", length = 100)
    private String deviceFingerprint;

    @Column(name = "validated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime validatedAt = OffsetDateTime.now();
}
