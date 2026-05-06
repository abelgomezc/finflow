package ec.com.finflow.transfers.domain.entity;

import ec.com.finflow.transfers.domain.enums.TransferStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Entidad que representa una transferencia bancaria.
 */
@Entity
@Table(name = "transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "reference_number", nullable = false, unique = true, length = 30)
    private String referenceNumber;

    @Column(name = "source_account_id", nullable = false)
    private Long sourceAccountId;

    @Column(name = "source_account_number", length = 20)
    private String sourceAccountNumber;

    @Column(name = "target_account_id", nullable = false)
    private Long targetAccountId;

    @Column(name = "target_account_number", length = 20)
    private String targetAccountNumber;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private TransferStatus status = TransferStatus.PENDING;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "initiated_by", nullable = false, length = 100)
    private String initiatedBy;

    // IDs de operaciones relacionadas
    @Column(name = "validation_id")
    private Long validationId;

    @Column(name = "block_id")
    private Long blockId;

    @Column(name = "debit_transaction_id")
    private Long debitTransactionId;

    @Column(name = "credit_transaction_id")
    private Long creditTransactionId;

    @Column(name = "reversal_transaction_id")
    private Long reversalTransactionId;

    // Saldos después de la transferencia (para historial)
    @Column(name = "source_balance_after", precision = 18, scale = 2)
    private BigDecimal sourceBalanceAfter;

    @Column(name = "target_balance_after", precision = 18, scale = 2)
    private BigDecimal targetBalanceAfter;

    // Información de error
    @Column(name = "failure_reason", length = 100)
    private String failureReason;

    @Column(name = "failure_message", columnDefinition = "TEXT")
    private String failureMessage;

    // Timestamps
    @Column(name = "initiated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime initiatedAt = OffsetDateTime.now();

    @Column(name = "validated_at")
    private OffsetDateTime validatedAt;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "failed_at")
    private OffsetDateTime failedAt;

    @Column(name = "reversed_at")
    private OffsetDateTime reversedAt;

    // Metadata
    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.initiatedAt == null) {
            this.initiatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * Verifica si la transferencia está en un estado final.
     */
    public boolean isFinal() {
        return status.isFinal();
    }

    /**
     * Verifica si se puede cancelar.
     */
    public boolean canCancel() {
        return status == TransferStatus.PENDING;
    }

    /**
     * Verifica si se puede reversar.
     */
    public boolean canReverse() {
        return status == TransferStatus.COMPLETED;
    }
}
