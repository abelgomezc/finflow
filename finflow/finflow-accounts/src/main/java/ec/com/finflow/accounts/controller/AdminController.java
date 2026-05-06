package ec.com.finflow.accounts.controller;

import ec.com.finflow.accounts.domain.entity.Account;
import ec.com.finflow.accounts.domain.entity.BalanceHistory;
import ec.com.finflow.accounts.domain.entity.Customer;
import ec.com.finflow.accounts.domain.entity.User;
import ec.com.finflow.accounts.domain.enums.AccountStatus;
import ec.com.finflow.accounts.domain.enums.TransactionType;
import ec.com.finflow.accounts.dto.response.AdminUserResponse;
import ec.com.finflow.accounts.dto.response.SystemStatsResponse;
import ec.com.finflow.accounts.repository.AccountRepository;
import ec.com.finflow.accounts.repository.BalanceHistoryRepository;
import ec.com.finflow.accounts.repository.CustomerRepository;
import ec.com.finflow.accounts.repository.UserRepository;
import ec.com.finflow.accounts.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador REST para operaciones de administración.
 * Solo accesible por usuarios con rol ADMIN.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;
    private final UserActivityService userActivityService;

    /**
     * Verifica que el usuario sea administrador.
     */
    private void checkAdmin(String userRole) {
        if (!"ADMIN".equals(userRole)) {
            throw new SecurityException("Acceso denegado: se requiere rol ADMIN");
        }
    }

    /**
     * Obtiene lista paginada de todos los usuarios.
     */
    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserResponse>> getAllUsers(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin getting all users, role: {}", correlationId, userRole);
        checkAdmin(userRole);

        Page<AdminUserResponse> response = userRepository.findAll(pageable)
                .map(this::toAdminUserResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un usuario específico.
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<AdminUserResponse> getUser(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin getting user: {}", correlationId, userId);
        checkAdmin(userRole);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));

        return ResponseEntity.ok(toAdminUserResponse(user));
    }

    /**
     * Bloquea un usuario.
     */
    @PutMapping("/users/{userId}/lock")
    public ResponseEntity<AdminUserResponse> lockUser(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-ID", required = false) String adminUserId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin {} locking user: {}", correlationId, adminUserId, userId);
        checkAdmin(userRole);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));

        user.setIsLocked(true);
        userRepository.save(user);

        log.info("User {} locked by admin {}", userId, adminUserId);
        return ResponseEntity.ok(toAdminUserResponse(user));
    }

    /**
     * Desbloquea un usuario.
     */
    @PutMapping("/users/{userId}/unlock")
    public ResponseEntity<AdminUserResponse> unlockUser(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-ID", required = false) String adminUserId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin {} unlocking user: {}", correlationId, adminUserId, userId);
        checkAdmin(userRole);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));

        user.setIsLocked(false);
        user.setFailedAttempts(0);
        userRepository.save(user);

        log.info("User {} unlocked by admin {}", userId, adminUserId);
        return ResponseEntity.ok(toAdminUserResponse(user));
    }

    /**
     * Activa un usuario.
     */
    @PutMapping("/users/{userId}/activate")
    public ResponseEntity<AdminUserResponse> activateUser(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-ID", required = false) String adminUserId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin {} activating user: {}", correlationId, adminUserId, userId);
        checkAdmin(userRole);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));

        user.setIsActive(true);
        userRepository.save(user);

        log.info("User {} activated by admin {}", userId, adminUserId);
        return ResponseEntity.ok(toAdminUserResponse(user));
    }

    /**
     * Desactiva un usuario.
     */
    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<AdminUserResponse> deactivateUser(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-ID", required = false) String adminUserId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin {} deactivating user: {}", correlationId, adminUserId, userId);
        checkAdmin(userRole);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));

        user.setIsActive(false);
        userRepository.save(user);

        log.info("User {} deactivated by admin {}", userId, adminUserId);
        return ResponseEntity.ok(toAdminUserResponse(user));
    }

    /**
     * Cambia el rol de un usuario.
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<AdminUserResponse> changeUserRole(
            @PathVariable Long userId,
            @RequestParam String newRole,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-ID", required = false) String adminUserId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin {} changing role of user {} to {}", correlationId, adminUserId, userId, newRole);
        checkAdmin(userRole);

        if (!List.of("USER", "ADMIN").contains(newRole.toUpperCase())) {
            throw new IllegalArgumentException("Rol inválido: " + newRole);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));

        user.setRole(newRole.toUpperCase());
        userRepository.save(user);

        log.info("User {} role changed to {} by admin {}", userId, newRole, adminUserId);
        return ResponseEntity.ok(toAdminUserResponse(user));
    }

    /**
     * Obtiene estadísticas del sistema.
     */
    @GetMapping("/stats")
    public ResponseEntity<SystemStatsResponse> getSystemStats(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin getting system stats", correlationId);
        checkAdmin(userRole);

        List<User> allUsers = userRepository.findAll();
        List<Customer> allCustomers = customerRepository.findAll();
        List<Account> allAccounts = accountRepository.findAll();

        long totalUsers = allUsers.size();
        long activeUsers = allUsers.stream().filter(u -> Boolean.TRUE.equals(u.getIsActive())).count();
        long lockedUsers = allUsers.stream().filter(u -> Boolean.TRUE.equals(u.getIsLocked())).count();
        long adminUsers = allUsers.stream().filter(u -> "ADMIN".equals(u.getRole())).count();

        long totalCustomers = allCustomers.size();
        long activeCustomers = allCustomers.stream().filter(c -> Boolean.TRUE.equals(c.getIsActive())).count();

        long totalAccounts = allAccounts.size();
        long activeAccounts = allAccounts.stream().filter(a -> a.getStatus() == AccountStatus.ACTIVE).count();
        BigDecimal totalBalance = allAccounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        SystemStatsResponse stats = SystemStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .lockedUsers(lockedUsers)
                .adminUsers(adminUsers)
                .totalCustomers(totalCustomers)
                .activeCustomers(activeCustomers)
                .totalAccounts(totalAccounts)
                .activeAccounts(activeAccounts)
                .totalBalance(totalBalance)
                // Transfer stats se llenarán desde el gateway combinando datos
                .totalTransfers(0L)
                .pendingTransfers(0L)
                .completedTransfers(0L)
                .failedTransfers(0L)
                .totalTransferAmount(BigDecimal.ZERO)
                .build();

        return ResponseEntity.ok(stats);
    }

    /**
     * Convierte User a AdminUserResponse.
     */
    private AdminUserResponse toAdminUserResponse(User user) {
        AdminUserResponse.AdminUserResponseBuilder builder = AdminUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .isLocked(user.getIsLocked())
                .failedAttempts(user.getFailedAttempts())
                .lastLoginAt(user.getLastLoginAt())
                .lastLoginIp(user.getLastLoginIp())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt());

        Customer customer = user.getCustomer();
        if (customer != null) {
            builder.customerId(customer.getId())
                    .customerName(customer.getFirstName() + " " + customer.getLastName())
                    .documentType(customer.getDocumentType())
                    .documentNumber(customer.getDocumentNumber());
        }

        return builder.build();
    }

    // ==================== GESTIÓN DE CUENTAS ====================

    /**
     * Obtiene todas las cuentas del sistema (paginadas).
     */
    @GetMapping("/accounts")
    public ResponseEntity<Page<Map<String, Object>>> getAllAccounts(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin getting all accounts", correlationId);
        checkAdmin(userRole);

        Page<Map<String, Object>> response = accountRepository.findAll(pageable)
                .map(this::toAccountMap);

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene una cuenta específica.
     */
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<Map<String, Object>> getAccount(
            @PathVariable Long accountId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin getting account: {}", correlationId, accountId);
        checkAdmin(userRole);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada: " + accountId));

        return ResponseEntity.ok(toAccountMap(account));
    }

    /**
     * Ajusta el saldo de una cuenta (depósito o retiro por admin).
     * type: DEPOSIT (agregar dinero) o WITHDRAW (quitar dinero)
     */
    @PostMapping("/accounts/{accountId}/adjust")
    @Transactional
    public ResponseEntity<Map<String, Object>> adjustAccountBalance(
            @PathVariable Long accountId,
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-User-ID", required = false) String adminUserId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin {} adjusting balance of account {}", correlationId, adminUserId, accountId);
        checkAdmin(userRole);

        // Validar request
        String type = (String) request.get("type");
        Object amountObj = request.get("amount");
        String description = (String) request.get("description");

        if (type == null || (!type.equals("DEPOSIT") && !type.equals("WITHDRAW"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tipo inválido. Use DEPOSIT o WITHDRAW"));
        }

        BigDecimal amount;
        try {
            if (amountObj instanceof Number) {
                amount = new BigDecimal(amountObj.toString());
            } else {
                amount = new BigDecimal((String) amountObj);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Monto inválido"));
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "El monto debe ser mayor a 0"));
        }

        // Obtener cuenta
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada: " + accountId));

        // Calcular nuevo saldo
        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balanceAfter;
        TransactionType transactionType;

        if (type.equals("DEPOSIT")) {
            balanceAfter = balanceBefore.add(amount);
            transactionType = TransactionType.CREDIT;
        } else {
            if (balanceBefore.compareTo(amount) < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Saldo insuficiente para el retiro"));
            }
            balanceAfter = balanceBefore.subtract(amount);
            transactionType = TransactionType.DEBIT;
        }

        // Actualizar saldo de cuenta
        account.setBalance(balanceAfter);
        accountRepository.save(account);

        // Registrar en historial de balance
        String referenceId = "ADJ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String desc = description != null ? description :
                (type.equals("DEPOSIT") ? "Depósito administrativo" : "Retiro administrativo");

        BalanceHistory history = BalanceHistory.builder()
                .account(account)
                .transactionType(transactionType)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .referenceId(referenceId)
                .description(desc + " (Admin ID: " + adminUserId + ")")
                .createdBy("ADMIN:" + adminUserId)
                .build();
        balanceHistoryRepository.save(history);

        // Registrar en actividad del admin
        if (adminUserId != null) {
            try {
                Long adminId = Long.parseLong(adminUserId);
                String activityDesc = type.equals("DEPOSIT")
                        ? "Depósito de $" + amount + " a cuenta " + account.getAccountNumber()
                        : "Retiro de $" + amount + " de cuenta " + account.getAccountNumber();
                userActivityService.logActivity(
                        adminId,
                        type.equals("DEPOSIT") ? "ADMIN_DEPOSIT" : "ADMIN_WITHDRAW",
                        activityDesc,
                        "Account",
                        accountId.toString(),
                        null
                );
            } catch (Exception e) {
                log.warn("Error logging admin activity: {}", e.getMessage());
            }
        }

        log.info("Account {} balance adjusted: {} -> {} by admin {}",
                accountId, balanceBefore, balanceAfter, adminUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("accountId", accountId);
        response.put("accountNumber", account.getAccountNumber());
        response.put("type", type);
        response.put("amount", amount);
        response.put("balanceBefore", balanceBefore);
        response.put("balanceAfter", balanceAfter);
        response.put("referenceId", referenceId);
        response.put("message", type.equals("DEPOSIT") ? "Depósito realizado exitosamente" : "Retiro realizado exitosamente");

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene el historial de movimientos de una cuenta.
     */
    @GetMapping("/accounts/{accountId}/history")
    public ResponseEntity<Page<Map<String, Object>>> getAccountHistory(
            @PathVariable Long accountId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Admin getting history for account: {}", correlationId, accountId);
        checkAdmin(userRole);

        // Verificar que existe la cuenta
        accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada: " + accountId));

        Page<BalanceHistory> history = balanceHistoryRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable);

        Page<Map<String, Object>> response = history.map(h -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", h.getId());
            map.put("accountId", accountId);
            map.put("transactionType", h.getTransactionType().name());
            map.put("amount", h.getAmount());
            map.put("balanceBefore", h.getBalanceBefore());
            map.put("balanceAfter", h.getBalanceAfter());
            map.put("referenceId", h.getReferenceId());
            map.put("description", h.getDescription());
            map.put("createdAt", h.getCreatedAt());
            map.put("createdBy", h.getCreatedBy());
            return map;
        });

        return ResponseEntity.ok(response);
    }

    /**
     * Convierte Account a Map para respuesta.
     */
    private Map<String, Object> toAccountMap(Account account) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", account.getId());
        map.put("accountNumber", account.getAccountNumber());
        map.put("accountType", account.getAccountType().name());
        map.put("status", account.getStatus().name());
        map.put("currency", account.getCurrency());
        map.put("balance", account.getBalance());
        map.put("availableBalance", account.getBalance()); // Por ahora igual al balance
        map.put("dailyTransferLimit", account.getDailyTransferLimit());
        map.put("perTransferLimit", account.getPerTransferLimit());
        map.put("createdAt", account.getCreatedAt());
        map.put("updatedAt", account.getUpdatedAt());

        Customer customer = account.getCustomer();
        if (customer != null) {
            map.put("customerId", customer.getId());
            map.put("customerName", customer.getFirstName() + " " + customer.getLastName());
            map.put("documentNumber", customer.getDocumentNumber());

            // Buscar el usuario asociado al customer
            User user = userRepository.findByCustomerId(customer.getId()).orElse(null);
            if (user != null) {
                map.put("username", user.getUsername());
            }
        }

        return map;
    }
}
