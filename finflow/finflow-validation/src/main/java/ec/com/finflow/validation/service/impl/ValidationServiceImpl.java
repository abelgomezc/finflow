package ec.com.finflow.validation.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.com.finflow.validation.domain.entity.BlacklistedAccount;
import ec.com.finflow.validation.domain.entity.UserTransferProfile;
import ec.com.finflow.validation.domain.entity.ValidationLog;
import ec.com.finflow.validation.domain.enums.RejectionReason;
import ec.com.finflow.validation.dto.request.ValidateTransferRequest;
import ec.com.finflow.validation.dto.response.*;
import ec.com.finflow.validation.repository.BlacklistedAccountRepository;
import ec.com.finflow.validation.repository.UserTransferProfileRepository;
import ec.com.finflow.validation.repository.ValidationLogRepository;
import ec.com.finflow.validation.service.ValidationService;
import ec.com.finflow.validation.service.rules.FraudRule;
import ec.com.finflow.validation.service.rules.FraudRuleContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de validación.
 * Orquesta la validación de cuentas, límites y fraude.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final List<FraudRule> fraudRules;
    private final BlacklistedAccountRepository blacklistRepository;
    private final UserTransferProfileRepository profileRepository;
    private final ValidationLogRepository validationLogRepository;
    private final ObjectMapper objectMapper;

    @Value("${finflow.validation.fraud-score-threshold:70}")
    private int fraudScoreThreshold;

    @Value("${finflow.validation.default-max-amount-per-transfer:5000.00}")
    private BigDecimal defaultMaxPerTransfer;

    @Value("${finflow.validation.default-max-daily-amount:10000.00}")
    private BigDecimal defaultMaxDaily;

    @Value("${finflow.validation.default-max-transfers-per-hour:10}")
    private int defaultMaxTransfersPerHour;

    @Override
    @Transactional
    public ValidationResult validateTransfer(ValidateTransferRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Validating transfer: {} from {} to {} amount: {}",
                request.getTransferId(),
                request.getSourceAccountId(),
                request.getTargetAccountId(),
                request.getAmount());

        try {
            // 1. Validar que no sea la misma cuenta
            if (request.getSourceAccountId().equals(request.getTargetAccountId())) {
                return saveAndReturn(request, ValidationResult.rejected(
                        request.getTransferId(),
                        RejectionReason.SAME_ACCOUNT_TRANSFER,
                        "No se puede transferir a la misma cuenta",
                        0
                ), startTime);
            }

            // 2. Validar cuentas (simulado - en producción llamaría a finflow-accounts via gRPC)
            AccountValidationInfo sourceInfo = validateSourceAccount(request.getSourceAccountId(), request.getAmount());
            AccountValidationInfo targetInfo = validateTargetAccount(request.getTargetAccountId());

            // Si cuenta origen no válida
            if (!sourceInfo.isExists()) {
                return saveAndReturn(request, ValidationResult.rejected(
                        request.getTransferId(),
                        RejectionReason.SOURCE_ACCOUNT_NOT_FOUND,
                        "Cuenta origen no encontrada",
                        0
                ), startTime);
            }

            if (!sourceInfo.isActive()) {
                return saveAndReturn(request, ValidationResult.rejected(
                        request.getTransferId(),
                        RejectionReason.SOURCE_ACCOUNT_INACTIVE,
                        "Cuenta origen no activa",
                        0
                ), startTime);
            }

            // Validar saldo disponible
            if (sourceInfo.getAvailableBalance() != null &&
                sourceInfo.getAvailableBalance().compareTo(request.getAmount()) < 0) {
                return saveAndReturn(request, ValidationResult.rejected(
                        request.getTransferId(),
                        RejectionReason.INSUFFICIENT_AVAILABLE_BALANCE,
                        "Saldo disponible insuficiente",
                        0
                ), startTime);
            }

            // Si cuenta destino no válida
            if (!targetInfo.isExists()) {
                return saveAndReturn(request, ValidationResult.rejected(
                        request.getTransferId(),
                        RejectionReason.TARGET_ACCOUNT_NOT_FOUND,
                        "Cuenta destino no encontrada",
                        0
                ), startTime);
            }

            if (!targetInfo.isActive()) {
                return saveAndReturn(request, ValidationResult.rejected(
                        request.getTransferId(),
                        RejectionReason.TARGET_ACCOUNT_INACTIVE,
                        "Cuenta destino no activa",
                        0
                ), startTime);
            }

            // 3. Verificar lista negra
            Optional<BlacklistedAccount> blacklistEntry = blacklistRepository
                    .findActiveByAccountId(request.getTargetAccountId());
            if (blacklistEntry.isPresent()) {
                return saveAndReturn(request, ValidationResult.rejected(
                        request.getTransferId(),
                        RejectionReason.BLACKLISTED_ACCOUNT,
                        "Cuenta destino en lista negra: " + blacklistEntry.get().getReason(),
                        100
                ), startTime);
            }

            // 4. Verificar límites
            LimitCheckResult limitCheck = checkLimits(request);
            if (!limitCheck.isPerTransferOk()) {
                return saveAndReturn(request, ValidationResult.rejected(
                        request.getTransferId(),
                        RejectionReason.AMOUNT_EXCEEDS_TRANSFER_LIMIT,
                        "Monto excede límite por transferencia",
                        0
                ), startTime);
            }

            if (!limitCheck.isDailyLimitOk()) {
                return saveAndReturn(request, ValidationResult.rejected(
                        request.getTransferId(),
                        RejectionReason.DAILY_LIMIT_EXCEEDED,
                        "Límite diario excedido",
                        0
                ), startTime);
            }

            if (!limitCheck.isFrequencyOk()) {
                return saveAndReturn(request, ValidationResult.rejected(
                        request.getTransferId(),
                        RejectionReason.TRANSFERS_PER_HOUR_EXCEEDED,
                        "Demasiadas transferencias en la última hora",
                        0
                ), startTime);
            }

            // 5. Análisis de fraude (si no se omite)
            List<FraudIndicator> indicators = new ArrayList<>();
            int fraudScore = 0;

            if (!request.isSkipFraudCheck()) {
                FraudRuleContext context = buildFraudContext(request, sourceInfo, targetInfo, limitCheck);

                // Ejecutar reglas ordenadas
                List<FraudRule> sortedRules = fraudRules.stream()
                        .filter(FraudRule::isEnabled)
                        .sorted(Comparator.comparingInt(FraudRule::getOrder))
                        .toList();

                for (FraudRule rule : sortedRules) {
                    try {
                        Optional<FraudIndicator> indicator = rule.evaluate(request, context);
                        if (indicator.isPresent()) {
                            indicators.add(indicator.get());
                            fraudScore += indicator.get().getScoreImpact();
                        }
                    } catch (Exception e) {
                        log.warn("Error evaluating rule {}: {}", rule.getCode(), e.getMessage());
                    }
                }

                // Verificar umbral de fraude
                int threshold = request.getMaxFraudScoreAllowed() != null ?
                        request.getMaxFraudScoreAllowed() : fraudScoreThreshold;

                if (fraudScore >= threshold) {
                    return saveAndReturn(request, ValidationResult.builder()
                            .transferId(request.getTransferId())
                            .approved(false)
                            .rejectionReason(RejectionReason.HIGH_FRAUD_SCORE)
                            .rejectionMessage("Score de fraude elevado: " + fraudScore)
                            .fraudScore(fraudScore)
                            .fraudIndicators(indicators)
                            .sourceAccount(sourceInfo)
                            .targetAccount(targetInfo)
                            .limitCheck(limitCheck)
                            .validatedAt(OffsetDateTime.now())
                            .build(), startTime);
                }
            }

            // 6. Aprobado
            ValidationResult result = ValidationResult.builder()
                    .transferId(request.getTransferId())
                    .approved(true)
                    .fraudScore(fraudScore)
                    .fraudIndicators(indicators)
                    .sourceAccount(sourceInfo)
                    .targetAccount(targetInfo)
                    .limitCheck(limitCheck)
                    .validatedAt(OffsetDateTime.now())
                    .build();

            return saveAndReturn(request, result, startTime);

        } catch (Exception e) {
            log.error("Error validating transfer {}: {}", request.getTransferId(), e.getMessage(), e);
            return saveAndReturn(request, ValidationResult.rejected(
                    request.getTransferId(),
                    RejectionReason.SYSTEM_ERROR,
                    "Error de sistema: " + e.getMessage(),
                    0
            ), startTime);
        }
    }

    /**
     * Valida cuenta origen (simulado).
     * En producción, llamaría a finflow-accounts via gRPC.
     */
    private AccountValidationInfo validateSourceAccount(Long accountId, BigDecimal amount) {
        // Simular validación - en producción usar gRPC client
        return AccountValidationInfo.builder()
                .accountId(accountId)
                .exists(true)
                .isActive(true)
                .status("ACTIVE")
                .availableBalance(new BigDecimal("10000.00"))  // Simulado
                .build();
    }

    /**
     * Valida cuenta destino (simulado).
     */
    private AccountValidationInfo validateTargetAccount(Long accountId) {
        // Verificar lista negra
        Optional<BlacklistedAccount> blacklist = blacklistRepository.findActiveByAccountId(accountId);

        return AccountValidationInfo.builder()
                .accountId(accountId)
                .exists(true)
                .isActive(true)
                .status("ACTIVE")
                .isBlacklisted(blacklist.isPresent())
                .blacklistReason(blacklist.map(BlacklistedAccount::getReason).orElse(null))
                .build();
    }

    /**
     * Verifica límites de transferencia.
     */
    private LimitCheckResult checkLimits(ValidateTransferRequest request) {
        // En producción, obtendría los límites de la cuenta via gRPC
        BigDecimal perTransferLimit = defaultMaxPerTransfer;
        BigDecimal dailyLimit = defaultMaxDaily;
        int maxPerHour = defaultMaxTransfersPerHour;

        // Simular uso diario (en producción vendría de finflow-accounts)
        BigDecimal dailyUsed = new BigDecimal("1500.00");

        return LimitCheckResult.builder()
                .perTransferLimit(perTransferLimit)
                .perTransferOk(request.getAmount().compareTo(perTransferLimit) <= 0)
                .dailyLimit(dailyLimit)
                .dailyUsed(dailyUsed)
                .dailyRemaining(dailyLimit.subtract(dailyUsed))
                .dailyLimitOk(dailyUsed.add(request.getAmount()).compareTo(dailyLimit) <= 0)
                .maxTransfersPerHour(maxPerHour)
                .transfersLastHour(3)  // Simulado
                .frequencyOk(true)
                .build();
    }

    /**
     * Construye el contexto para las reglas de fraude.
     */
    private FraudRuleContext buildFraudContext(ValidateTransferRequest request,
                                               AccountValidationInfo sourceInfo,
                                               AccountValidationInfo targetInfo,
                                               LimitCheckResult limitCheck) {
        // Obtener perfil del usuario
        UserTransferProfile profile = profileRepository.findByUserId(request.getUserId()).orElse(null);

        // Verificar si es primera vez que transfiere a este destino
        boolean isFirstTimeRecipient = true;
        if (profile != null && profile.getFrequentRecipients() != null) {
            isFirstTimeRecipient = !profile.getFrequentRecipients()
                    .contains(request.getTargetAccountId().toString());
        }

        return FraudRuleContext.builder()
                .userProfile(profile)
                .availableBalance(sourceInfo.getAvailableBalance())
                .dailyTransferTotal(limitCheck.getDailyUsed())
                .transfersLastHour(limitCheck.getTransfersLastHour())
                .perTransferLimit(limitCheck.getPerTransferLimit())
                .dailyLimit(limitCheck.getDailyLimit())
                .maxTransfersPerHour(limitCheck.getMaxTransfersPerHour())
                .targetIsBlacklisted(targetInfo.isBlacklisted())
                .blacklistReason(targetInfo.getBlacklistReason())
                .isFirstTimeRecipient(isFirstTimeRecipient)
                .build();
    }

    /**
     * Guarda el log de validación y retorna el resultado.
     */
    private ValidationResult saveAndReturn(ValidateTransferRequest request,
                                           ValidationResult result,
                                           long startTime) {
        long totalTime = System.currentTimeMillis() - startTime;
        result.setValidationTimeMs(totalTime);

        try {
            ValidationLog logEntry = ValidationLog.builder()
                    .transferId(request.getTransferId())
                    .correlationId(request.getCorrelationId())
                    .sourceAccountId(request.getSourceAccountId())
                    .targetAccountId(request.getTargetAccountId())
                    .userId(request.getUserId())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .approved(result.isApproved())
                    .rejectionReason(result.getRejectionReason() != null ?
                            result.getRejectionReason().name() : null)
                    .rejectionMessage(result.getRejectionMessage())
                    .fraudScore(result.getFraudScore())
                    .fraudIndicators(serializeIndicators(result.getFraudIndicators()))
                    .validationTimeMs(totalTime)
                    .ipAddress(request.getIpAddress())
                    .userAgent(request.getUserAgent())
                    .deviceFingerprint(request.getDeviceFingerprint())
                    .validatedAt(OffsetDateTime.now())
                    .build();

            logEntry = validationLogRepository.save(logEntry);
            result.setValidationId(logEntry.getId());

        } catch (Exception e) {
            log.error("Error saving validation log: {}", e.getMessage());
        }

        log.info("Validation {} completed in {}ms - approved: {} score: {}",
                request.getTransferId(), totalTime, result.isApproved(), result.getFraudScore());

        return result;
    }

    private String serializeIndicators(List<FraudIndicator> indicators) {
        if (indicators == null || indicators.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(indicators);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
