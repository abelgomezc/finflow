package ec.com.finflow.accounts.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Entidad que representa un monto bloqueado (reservado) en una cuenta.
 * Usado para el patrón Saga en transferencias.
 */
@Entity
@Table(name = "blocked_amounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "transfer_id")
    private Long transferId;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "reason", length = 200)
    private String reason;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "released_at")
    private OffsetDateTime releasedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    /**
     * Verifica si el bloqueo está activo.
     */
    public boolean isActive() {
        return "ACTIVE".equals(status) && (expiresAt == null || expiresAt.isAfter(OffsetDateTime.now()));
    }

    /**
     * Verifica si el bloqueo ha expirado.
     */
    public boolean isExpired() {
        return "ACTIVE".equals(status) && expiresAt != null && expiresAt.isBefore(OffsetDateTime.now());
    }
}
