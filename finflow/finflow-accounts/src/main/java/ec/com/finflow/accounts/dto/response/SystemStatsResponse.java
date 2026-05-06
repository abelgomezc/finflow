package ec.com.finflow.accounts.dto.response;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para estadísticas del sistema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemStatsResponse {

    // Usuarios
    private Long totalUsers;
    private Long activeUsers;
    private Long lockedUsers;
    private Long adminUsers;

    // Clientes
    private Long totalCustomers;
    private Long activeCustomers;

    // Cuentas
    private Long totalAccounts;
    private Long activeAccounts;
    private BigDecimal totalBalance;

    // Transferencias (se llenará desde el servicio de transfers)
    private Long totalTransfers;
    private Long pendingTransfers;
    private Long completedTransfers;
    private Long failedTransfers;
    private BigDecimal totalTransferAmount;
}
