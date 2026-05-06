package ec.com.finflow.accounts.domain.entity;

import ec.com.finflow.accounts.domain.enums.AccountStatus;
import ec.com.finflow.accounts.domain.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una cuenta bancaria.
 * Almacena el saldo y configuración de límites.
 */
@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Account extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    @Builder.Default
    private AccountType accountType = AccountType.CHECKING;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "balance", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "daily_transfer_limit", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal dailyTransferLimit = new BigDecimal("10000.00");

    @Column(name = "per_transfer_limit", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal perTransferLimit = new BigDecimal("5000.00");

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @Builder.Default
    private List<BlockedAmount> blockedAmounts = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @Builder.Default
    private List<BalanceHistory> balanceHistory = new ArrayList<>();

    /**
     * Verifica si la cuenta está activa y puede operar.
     */
    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    /**
     * Verifica si la cuenta puede recibir fondos.
     */
    public boolean canReceiveFunds() {
        return status == AccountStatus.ACTIVE || status == AccountStatus.INACTIVE;
    }
}
