package ec.com.finflow.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Filtro CORS de alta prioridad que se ejecuta antes de todos los GlobalFilters.
 * Maneja las solicitudes OPTIONS (preflight) y agrega headers CORS a todas las respuestas.
 */
@Slf4j
@Component
public class CorsWebFilter implements WebFilter, Ordered {

    private static final String ALLOWED_ORIGINS = "http://localhost:5173,http://localhost:3000";
    private static final String ALLOWED_METHODS = "GET,POST,PUT,DELETE,OPTIONS,PATCH";
    private static final String ALLOWED_HEADERS = "*";
    private static final String EXPOSED_HEADERS = "X-Correlation-ID,X-Request-Id,Authorization";
    private static final String MAX_AGE = "3600";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String origin = request.getHeaders().getOrigin();

        // Add CORS headers to all responses
        if (origin != null && isAllowedOrigin(origin)) {
            HttpHeaders headers = response.getHeaders();
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS);
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ALLOWED_HEADERS);
            headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, EXPOSED_HEADERS);
            headers.set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
        }

        // Handle preflight (OPTIONS) requests
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            log.debug("Handling CORS preflight request for path: {}", request.getPath());
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean isAllowedOrigin(String origin) {
        for (String allowed : ALLOWED_ORIGINS.split(",")) {
            if (allowed.trim().equals(origin)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        // Execute BEFORE all GlobalFilters (which start at HIGHEST_PRECEDENCE)
        // WebFilters with lower order run first
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
