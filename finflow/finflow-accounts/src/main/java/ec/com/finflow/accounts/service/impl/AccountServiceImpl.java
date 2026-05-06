package ec.com.finflow.accounts.service.impl;

import ec.com.finflow.accounts.domain.entity.Account;
import ec.com.finflow.accounts.domain.entity.BalanceHistory;
import ec.com.finflow.accounts.domain.entity.BlockedAmount;
import ec.com.finflow.accounts.domain.entity.Customer;
import ec.com.finflow.accounts.domain.enums.AccountStatus;
import ec.com.finflow.accounts.domain.enums.TransactionType;
import ec.com.finflow.accounts.domain.exception.AccountException;
import ec.com.finflow.accounts.domain.exception.BlockException;
import ec.com.finflow.accounts.dto.request.*;
import ec.com.finflow.accounts.dto.response.*;
import ec.com.finflow.accounts.mapper.AccountMapper;
import ec.com.finflow.accounts.repository.AccountRepository;
import ec.com.finflow.accounts.repository.BalanceHistoryRepository;
import ec.com.finflow.accounts.repository.BlockedAmountRepository;
import ec.com.finflow.accounts.repository.CustomerRepository;
import ec.com.finflow.accounts.repository.UserRepository;
import ec.com.finflow.accounts.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Implementación del servicio de cuentas.
 * Usa JPA para operaciones financieras.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final BlockedAmountRepository blockedAmountRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    @Value("${finflow.accounts.default-daily-limit:10000.00}")
    private BigDecimal defaultDailyLimit;

    @Value("${finflow.accounts.default-per-transfer-limit:5000.00}")
    private BigDecimal defaultPerTransferLimit;

    @Value("${finflow.accounts.block-expiration-minutes:15}")
    private int defaultBlockExpirationMinutes;

    // ============================================================
    // Operaciones CRUD
    // ============================================================

    @Override
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Creating account for customer: {}", request.getCustomerId());

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found: " + request.getCustomerId()));

        Account account = Account.builder()
                .customer(customer)
                .accountType(request.getAccountType())
                .currency(request.getCurrency())
                .balance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO)
                .dailyTransferLimit(request.getDailyTransferLimit() != null ?
                        request.getDailyTransferLimit() : defaultDailyLimit)
                .perTransferLimit(request.getPerTransferLimit() != null ?
                        request.getPerTransferLimit() : defaultPerTransferLimit)
                .status(AccountStatus.ACTIVE)
                .build();

        account = accountRepository.save(account);
        log.info("Account created: {} for customer: {}", account.getId(), customer.getId());

        return accountMapper.toResponse(account, BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> AccountException.notFound(accountId));

        BigDecimal blockedAmount = blockedAmountRepository.sumActiveBlockedAmounts(accountId);
        return accountMapper.toResponse(account, blockedAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> AccountException.notFoundByNumber(accountNumber));

        BigDecimal blockedAmount = blockedAmountRepository.sumActiveBlockedAmounts(account.getId());
        return accountMapper.toResponse(account, blockedAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByCustomer(Long customerId) {
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        return accounts.stream()
                .map(account -> {
                    BigDecimal blocked = blockedAmountRepository.sumActiveBlockedAmounts(account.getId());
                    return accountMapper.toResponse(account, blocked);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByUserId(Long userId) {
        log.debug("Getting accounts for user ID: {}", userId);

        return userRepository.findById(userId)
                .map(user -> {
                    if (user.getCustomer() == null) {
                        log.warn("User {} has no associated customer", userId);
                        return List.<AccountResponse>of();
                    }
                    Long customerId = user.getCustomer().getId();
                    log.debug("User {} has customer ID: {}", userId, customerId);
                    return getAccountsByCustomer(customerId);
                })
                .orElseGet(() -> {
                    log.warn("User not found: {}", userId);
                    return List.of();
                });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .map(Account::isActive)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(Account::isActive)
                .orElse(false);
    }

    // ============================================================
    // Operaciones de Balance
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> AccountException.notFound(accountId));

        BigDecimal blockedAmount = blockedAmountRepository.sumActiveBlockedAmounts(accountId);
        BigDecimal availableBalance = account.getBalance().subtract(blockedAmount);

        return BalanceResponse.builder()
                .accountId(accountId)
                .accountNumber(account.getAccountNumber())
                .currency(account.getCurrency())
                .currentBalance(account.getBalance())
                .availableBalance(availableBalance)
                .blockedAmount(blockedAmount)
                .asOf(OffsetDateTime.now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAvailableBalance(Long accountId) {
        return accountRepository.getAvailableBalance(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getDailyTransferTotal(Long accountId) {
        return accountRepository.getDailyTransferTotal(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public int countRecentTransfers(Long accountId, int hours) {
        Integer count = accountRepository.countRecentTransfers(accountId, hours);
        return count != null ? count : 0;
    }

    // ============================================================
    // Operaciones de Bloqueo
    // ============================================================

    @Override
    @Transactional
    public BlockResponse blockAmount(BlockAmountRequest request) {
        log.info("Blocking amount {} for account {}", request.getAmount(), request.getAccountId());

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> AccountException.notFound(request.getAccountId()));

        BigDecimal availableBalance = account.getBalance()
                .subtract(blockedAmountRepository.sumActiveBlockedAmounts(account.getId()));

        if (availableBalance.compareTo(request.getAmount()) < 0) {
            throw AccountException.insufficientFunds(request.getAccountId(), request.getAmount(), availableBalance);
        }

        int expirationMinutes = request.getExpirationMinutes() != null ?
                request.getExpirationMinutes() : defaultBlockExpirationMinutes;

        BlockedAmount block = BlockedAmount.builder()
                .account(account)
                .amount(request.getAmount())
                .reason(request.getReason())
                .status("ACTIVE")
                .expiresAt(OffsetDateTime.now().plusMinutes(expirationMinutes))
                .createdBy(request.getExecutedBy())
                .build();

        block = blockedAmountRepository.save(block);

        BigDecimal newAvailableBalance = availableBalance.subtract(request.getAmount());

        log.info("Block created: {} for account: {}", block.getId(), request.getAccountId());

        return BlockResponse.builder()
                .blockId(block.getId())
                .accountId(request.getAccountId())
                .blockedAmount(request.getAmount())
                .newAvailableBalance(newAvailableBalance)
                .expiresAt(block.getExpiresAt())
                .createdAt(block.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public BlockResponse releaseBlock(Long blockId, String reason, String executedBy) {
        log.info("Releasing block: {} with reason: {}", blockId, reason);

        BlockedAmount block = blockedAmountRepository.findById(blockId)
                .orElseThrow(() -> BlockException.notFound(blockId));

        if (!"ACTIVE".equals(block.getStatus())) {
            throw BlockException.alreadyReleased(blockId);
        }

        block.setStatus("RELEASED");
        block.setReleasedAt(OffsetDateTime.now());
        blockedAmountRepository.save(block);

        Account account = block.getAccount();
        BigDecimal newAvailableBalance = account.getBalance()
                .subtract(blockedAmountRepository.sumActiveBlockedAmounts(account.getId()));

        log.info("Block released: {} amount: {} for account: {}", blockId, block.getAmount(), account.getId());

        return BlockResponse.builder()
                .blockId(blockId)
                .accountId(account.getId())
                .blockedAmount(block.getAmount())
                .newAvailableBalance(newAvailableBalance)
                .build();
    }

    @Override
    @Transactional
    public BlockResponse releaseBlockByReference(String blockReference, String reason, String executedBy) {
        // This method is no longer supported - throw exception
        throw new UnsupportedOperationException("releaseBlockByReference is no longer supported");
    }

    // ============================================================
    // Operaciones Financieras
    // ============================================================

    @Override
    @Transactional
    public TransactionResponse executeBlockedDebit(Long blockId, String transferId,
                                                   String description, String executedBy,
                                                   String correlationId) {
        log.info("Executing blocked debit for block: {} transfer: {}", blockId, transferId);

        BlockedAmount block = blockedAmountRepository.findById(blockId)
                .orElseThrow(() -> BlockException.notFound(blockId));

        if (!"ACTIVE".equals(block.getStatus())) {
            throw BlockException.alreadyReleased(blockId);
        }

        Account account = block.getAccount();
        BigDecimal amount = block.getAmount();
        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(amount);

        // Update account balance
        account.setBalance(balanceAfter);
        accountRepository.save(account);

        // Mark block as executed
        block.setStatus("EXECUTED");
        blockedAmountRepository.save(block);

        // Create balance history
        BalanceHistory history = BalanceHistory.builder()
                .account(account)
                .transactionType(TransactionType.DEBIT)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .referenceId(transferId)
                .description(description)
                .createdBy(executedBy)
                .build();

        history = balanceHistoryRepository.save(history);

        log.info("Blocked debit executed: {} amount: {} account: {}", history.getId(), amount, account.getId());

        return TransactionResponse.builder()
                .transactionId(history.getId())
                .accountId(account.getId())
                .transferId(transferId)
                .type(TransactionType.DEBIT)
                .amount(amount)
                .currency(account.getCurrency())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .executedAt(OffsetDateTime.now())
                .wasDuplicate(false)
                .build();
    }

    @Override
    @Transactional
    public TransactionResponse executeDebit(ExecuteDebitRequest request) {
        log.info("Executing direct debit for account: {} amount: {}",
                request.getAccountId(), request.getAmount());

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> AccountException.notFound(request.getAccountId()));

        BigDecimal balanceBefore = account.getBalance();

        if (balanceBefore.compareTo(request.getAmount()) < 0) {
            throw AccountException.insufficientFunds(request.getAccountId(), request.getAmount(), balanceBefore);
        }

        BigDecimal balanceAfter = balanceBefore.subtract(request.getAmount());

        // Update account balance
        account.setBalance(balanceAfter);
        accountRepository.save(account);

        // Create balance history
        BalanceHistory history = BalanceHistory.builder()
                .account(account)
                .transactionType(TransactionType.DEBIT)
                .amount(request.getAmount())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .referenceId(request.getTransferId())
                .description(request.getDescription())
                .createdBy(request.getExecutedBy())
                .build();

        history = balanceHistoryRepository.save(history);

        log.info("Debit executed: {} amount: {} account: {}",
                history.getId(), request.getAmount(), request.getAccountId());

        return TransactionResponse.builder()
                .transactionId(history.getId())
                .accountId(request.getAccountId())
                .transferId(request.getTransferId())
                .type(TransactionType.DEBIT)
                .amount(request.getAmount())
                .currency(account.getCurrency())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .executedAt(OffsetDateTime.now())
                .wasDuplicate(false)
                .build();
    }

    @Override
    @Transactional
    public TransactionResponse executeCredit(ExecuteCreditRequest request) {
        log.info("Executing credit for account: {} amount: {}",
                request.getAccountId(), request.getAmount());

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> AccountException.notFound(request.getAccountId()));

        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(request.getAmount());

        // Update account balance
        account.setBalance(balanceAfter);
        accountRepository.save(account);

        // Create balance history
        BalanceHistory history = BalanceHistory.builder()
                .account(account)
                .transactionType(TransactionType.CREDIT)
                .amount(request.getAmount())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .referenceId(request.getTransferId())
                .description(request.getDescription())
                .createdBy(request.getExecutedBy())
                .build();

        history = balanceHistoryRepository.save(history);

        log.info("Credit executed: {} amount: {} account: {}",
                history.getId(), request.getAmount(), request.getAccountId());

        return TransactionResponse.builder()
                .transactionId(history.getId())
                .accountId(request.getAccountId())
                .transferId(request.getTransferId())
                .type(TransactionType.CREDIT)
                .amount(request.getAmount())
                .currency(account.getCurrency())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .executedAt(OffsetDateTime.now())
                .wasDuplicate(false)
                .build();
    }

    @Override
    @Transactional
    public TransactionResponse reverseDebit(Long originalTransactionId, String transferId,
                                            String reason, String executedBy, String correlationId) {
        log.info("Reversing debit: {} for transfer: {}", originalTransactionId, transferId);

        BalanceHistory originalTx = balanceHistoryRepository.findById(originalTransactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + originalTransactionId));

        Account account = originalTx.getAccount();
        BigDecimal amount = originalTx.getAmount();
        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);

        // Update account balance (credit back)
        account.setBalance(balanceAfter);
        accountRepository.save(account);

        // Create reversal history
        BalanceHistory reversal = BalanceHistory.builder()
                .account(account)
                .transactionType(TransactionType.CREDIT)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .referenceId(transferId)
                .description("Reversal: " + reason)
                .createdBy(executedBy)
                .build();

        reversal = balanceHistoryRepository.save(reversal);

        log.info("Debit reversed: {} original: {} amount: {}", reversal.getId(), originalTransactionId, amount);

        return TransactionResponse.builder()
                .transactionId(reversal.getId())
                .accountId(account.getId())
                .transferId(transferId)
                .type(TransactionType.CREDIT)
                .amount(amount)
                .currency(account.getCurrency())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .executedAt(OffsetDateTime.now())
                .wasDuplicate(false)
                .originalTransactionId(originalTransactionId)
                .build();
    }
}
