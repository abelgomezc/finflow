package ec.com.finflow.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Controlador de fallback para Circuit Breaker.
 */
@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/accounts")
    public Mono<ResponseEntity<Map<String, Object>>> accountsFallback() {
        log.warn("Accounts service fallback triggered");
        return createFallbackResponse("Accounts service is temporarily unavailable");
    }

    @GetMapping("/transfers")
    public Mono<ResponseEntity<Map<String, Object>>> transfersFallback() {
        log.warn("Transfers service fallback triggered");
        return createFallbackResponse("Transfers service is temporarily unavailable");
    }

    @GetMapping("/audit")
    public Mono<ResponseEntity<Map<String, Object>>> auditFallback() {
        log.warn("Audit service fallback triggered");
        return createFallbackResponse("Audit service is temporarily unavailable");
    }

    @GetMapping("/notifications")
    public Mono<ResponseEntity<Map<String, Object>>> notificationsFallback() {
        log.warn("Notifications service fallback triggered");
        return createFallbackResponse("Notifications service is temporarily unavailable");
    }

    private Mono<ResponseEntity<Map<String, Object>>> createFallbackResponse(String message) {
        Map<String, Object> response = Map.of(
                "status", "error",
                "code", "SERVICE_UNAVAILABLE",
                "message", message,
                "timestamp", System.currentTimeMillis()
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
}
