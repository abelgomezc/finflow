package ec.com.finflow.transfers.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Client HTTP para comunicación con finflow-accounts.
 * Usa RestTemplate para llamadas HTTP síncronas.
 */
@Slf4j
@Component
public class AccountsClient {

    private final RestTemplate restTemplate;
    private final String accountsUrl;

    public AccountsClient(@Value("${finflow.services.accounts.url:http://localhost:8081}") String accountsUrl) {
        this.restTemplate = new RestTemplate();
        this.accountsUrl = accountsUrl;
    }

    /**
     * Bloquea un monto en una cuenta.
     * @return blockId o null si falla
     */
    public Long blockAmount(Long accountId, BigDecimal amount, String reason,
                            String executedBy, String correlationId) {
        log.debug("Blocking {} on account {} for reason: {}", amount, accountId, reason);

        try {
            Map<String, Object> request = Map.of(
                    "accountId", accountId,
                    "amount", amount,
                    "reason", reason != null ? reason : "Transfer block",
                    "expirationMinutes", 30,
                    "executedBy", executedBy != null ? executedBy : "SYSTEM"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (correlationId != null) {
                headers.set("X-Correlation-ID", correlationId);
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    accountsUrl + "/api/v1/accounts/blocks",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map body = response.getBody();
                if (body.get("blockId") != null) {
                    Long blockId = ((Number) body.get("blockId")).longValue();
                    log.info("Block created: {} for account: {}", blockId, accountId);
                    return blockId;
                }
            }

            log.warn("Failed to block amount on account: {} - response: {}", accountId, response);
            return null;

        } catch (Exception e) {
            log.error("Error blocking amount on account {}: {}", accountId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Libera un bloqueo.
     */
    public void releaseBlock(Long blockId, String reason, String executedBy) {
        log.debug("Releasing block: {}", blockId);

        try {
            HttpHeaders headers = new HttpHeaders();
            if (executedBy != null) {
                headers.set("X-User-ID", executedBy);
            }

            String url = UriComponentsBuilder.fromHttpUrl(accountsUrl + "/api/v1/accounts/blocks/" + blockId)
                    .queryParam("reason", reason != null ? reason : "Block released")
                    .build()
                    .toUriString();

            HttpEntity<?> entity = new HttpEntity<>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Map.class);

            log.info("Block {} released", blockId);

        } catch (Exception e) {
            log.error("Error releasing block {}: {}", blockId, e.getMessage(), e);
        }
    }

    /**
     * Ejecuta un débito directo en una cuenta.
     * @return transactionId o null si falla
     */
    public Long executeDebit(Long accountId, BigDecimal amount, String transferId,
                             String description, String executedBy, String correlationId) {
        log.debug("Executing debit on account {} amount {} for transfer {}", accountId, amount, transferId);

        try {
            Map<String, Object> request = Map.of(
                    "accountId", accountId,
                    "amount", amount,
                    "transferId", transferId,
                    "description", description != null ? description : "Transfer debit",
                    "idempotencyKey", "DEBIT-" + transferId,
                    "executedBy", executedBy != null ? executedBy : "SYSTEM",
                    "correlationId", correlationId != null ? correlationId : ""
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (correlationId != null) {
                headers.set("X-Correlation-ID", correlationId);
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    accountsUrl + "/api/v1/accounts/debit",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map body = response.getBody();
                if (body.get("transactionId") != null) {
                    Long txId = ((Number) body.get("transactionId")).longValue();
                    log.info("Debit executed: {} for transfer: {}", txId, transferId);
                    return txId;
                }
            }

            log.warn("Failed to execute debit on account: {} - response: {}", accountId, response);
            return null;

        } catch (Exception e) {
            log.error("Error executing debit on account {}: {}", accountId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Ejecuta un crédito en una cuenta.
     * @return transactionId o null si falla
     */
    public Long executeCredit(Long accountId, BigDecimal amount, String transferId,
                              String description, String executedBy, String correlationId) {
        log.debug("Executing credit on account {} amount {} for transfer {}", accountId, amount, transferId);

        try {
            Map<String, Object> request = Map.of(
                    "accountId", accountId,
                    "amount", amount,
                    "transferId", transferId,
                    "description", description != null ? description : "Transfer credit",
                    "idempotencyKey", "CREDIT-" + transferId,
                    "executedBy", executedBy != null ? executedBy : "SYSTEM",
                    "correlationId", correlationId != null ? correlationId : ""
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (correlationId != null) {
                headers.set("X-Correlation-ID", correlationId);
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    accountsUrl + "/api/v1/accounts/credit",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map body = response.getBody();
                if (body.get("transactionId") != null) {
                    Long txId = ((Number) body.get("transactionId")).longValue();
                    log.info("Credit executed: {} for transfer: {}", txId, transferId);
                    return txId;
                }
            }

            log.warn("Failed to execute credit on account: {} - response: {}", accountId, response);
            return null;

        } catch (Exception e) {
            log.error("Error executing credit on account {}: {}", accountId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Revierte un débito previo.
     * @return reversal transactionId o null si falla
     */
    public Long reverseDebit(Long transactionId, String transferId, String reason,
                             String executedBy, String correlationId) {
        log.debug("Reversing transaction {} for transfer {}", transactionId, transferId);

        try {
            HttpHeaders headers = new HttpHeaders();
            if (executedBy != null) {
                headers.set("X-User-ID", executedBy);
            }
            if (correlationId != null) {
                headers.set("X-Correlation-ID", correlationId);
            }

            String url = UriComponentsBuilder.fromHttpUrl(accountsUrl + "/api/v1/accounts/reverse/" + transactionId)
                    .queryParam("transferId", transferId)
                    .queryParam("reason", reason != null ? reason : "Transfer failed")
                    .build()
                    .toUriString();

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map body = response.getBody();
                if (body.get("transactionId") != null) {
                    Long txId = ((Number) body.get("transactionId")).longValue();
                    log.info("Debit reversed: {} for original tx: {}", txId, transactionId);
                    return txId;
                }
            }

            log.warn("Failed to reverse debit transaction: {} - response: {}", transactionId, response);
            return null;

        } catch (Exception e) {
            log.error("Error reversing debit {}: {}", transactionId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Valida si una cuenta existe y está activa.
     */
    public boolean validateAccount(Long accountId) {
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                    accountsUrl + "/api/v1/accounts/{accountId}/validate",
                    Boolean.class,
                    accountId
            );

            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            log.error("Error validating account {}: {}", accountId, e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el número de cuenta por su ID.
     */
    public String getAccountNumber(Long accountId) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    accountsUrl + "/api/v1/accounts/{accountId}",
                    Map.class,
                    accountId
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("accountNumber");
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting account number for {}: {}", accountId, e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene el saldo actual de una cuenta.
     */
    public BigDecimal getAccountBalance(Long accountId) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    accountsUrl + "/api/v1/accounts/{accountId}",
                    Map.class,
                    accountId
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // El campo puede ser "balance" o "currentBalance" dependiendo del endpoint
                Object balance = response.getBody().get("balance");
                if (balance == null) {
                    balance = response.getBody().get("currentBalance");
                }
                if (balance instanceof Number) {
                    return new BigDecimal(balance.toString());
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting account balance for {}: {}", accountId, e.getMessage());
            return null;
        }
    }
}
