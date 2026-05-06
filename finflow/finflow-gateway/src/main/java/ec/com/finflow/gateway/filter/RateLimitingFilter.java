package ec.com.finflow.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Filtro de Rate Limiting en memoria.
 * Limita las peticiones por IP usando el algoritmo Token Bucket simplificado.
 *
 * Para producción, usar Spring Cloud Gateway con Redis.
 */
@Slf4j
@Component
public class RateLimitingFilter implements GlobalFilter, Ordered {

    @Value("${finflow.gateway.ratelimit.enabled:true}")
    private boolean enabled;

    @Value("${finflow.gateway.ratelimit.requests-per-second:10}")
    private int requestsPerSecond;

    @Value("${finflow.gateway.ratelimit.burst-capacity:20}")
    private int burstCapacity;

    // Almacén de contadores por IP
    private final Map<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!enabled) {
            return chain.filter(exchange);
        }

        // Skip CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequest().getMethod().name())) {
            return chain.filter(exchange);
        }

        String clientIp = getClientIp(exchange);
        String path = exchange.getRequest().getPath().value();

        // No aplicar rate limit a health checks
        if (path.contains("/actuator/health")) {
            return chain.filter(exchange);
        }

        RateLimitBucket bucket = buckets.computeIfAbsent(clientIp,
                k -> new RateLimitBucket(burstCapacity, requestsPerSecond));

        if (!bucket.tryConsume()) {
            log.warn("Rate limit exceeded for IP: {} on path: {}", clientIp, path);

            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(requestsPerSecond));
            exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", "0");
            exchange.getResponse().getHeaders().add("Retry-After", "1");

            return exchange.getResponse().setComplete();
        }

        // Agregar headers de rate limit info
        exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(requestsPerSecond));
        exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", String.valueOf(bucket.getRemaining()));

        return chain.filter(exchange);
    }

    private String getClientIp(ServerWebExchange exchange) {
        String forwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }

        return exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1; // Ejecutar temprano, después de logging
    }

    /**
     * Implementación simple de Token Bucket para rate limiting.
     */
    private static class RateLimitBucket {
        private final int capacity;
        private final int refillRate;
        private final AtomicInteger tokens;
        private volatile long lastRefillTime;

        RateLimitBucket(int capacity, int refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.tokens = new AtomicInteger(capacity);
            this.lastRefillTime = Instant.now().toEpochMilli();
        }

        synchronized boolean tryConsume() {
            refill();
            if (tokens.get() > 0) {
                tokens.decrementAndGet();
                return true;
            }
            return false;
        }

        int getRemaining() {
            return Math.max(0, tokens.get());
        }

        private void refill() {
            long now = Instant.now().toEpochMilli();
            long elapsed = now - lastRefillTime;

            if (elapsed >= 1000) { // Refill cada segundo
                int tokensToAdd = (int) (elapsed / 1000) * refillRate;
                int newTokens = Math.min(capacity, tokens.get() + tokensToAdd);
                tokens.set(newTokens);
                lastRefillTime = now;
            }
        }
    }

    /**
     * Limpieza periódica de buckets inactivos (llamar desde un scheduled task).
     */
    public void cleanupInactiveBuckets() {
        long threshold = Instant.now().toEpochMilli() - 300000; // 5 minutos
        buckets.entrySet().removeIf(entry -> entry.getValue().lastRefillTime < threshold);
        log.debug("Cleaned up rate limit buckets. Remaining: {}", buckets.size());
    }
}
