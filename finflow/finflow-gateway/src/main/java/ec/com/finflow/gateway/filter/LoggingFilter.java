package ec.com.finflow.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filtro para logging de requests y responses.
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpMethod method = request.getMethod();
        String path = request.getPath().value();
        String correlationId = request.getHeaders().getFirst("X-Correlation-ID");
        String userId = request.getHeaders().getFirst("X-User-ID");

        long startTime = System.currentTimeMillis();

        log.info("[{}] {} {} - User: {}",
                correlationId,
                method,
                path,
                userId != null ? userId : "anonymous");

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    int statusCode = exchange.getResponse().getStatusCode() != null ?
                            exchange.getResponse().getStatusCode().value() : 0;

                    log.info("[{}] {} {} - Status: {} - Duration: {}ms",
                            correlationId,
                            method,
                            path,
                            statusCode,
                            duration);
                }));
    }

    @Override
    public int getOrder() {
        return -150; // Entre correlation y JWT
    }
}
