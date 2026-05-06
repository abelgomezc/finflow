package ec.com.finflow.gateway.controller;

import ec.com.finflow.gateway.dto.request.LoginRequest;
import ec.com.finflow.gateway.dto.response.AuthResponse;
import ec.com.finflow.gateway.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Controlador de autenticación.
 * Valida credenciales contra el servicio de accounts y genera JWT.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final WebClient webClient;

    public AuthController(JwtUtil jwtUtil,
                          @Value("${finflow.services.accounts.url:http://localhost:8081}") String accountsUrl) {
        this.jwtUtil = jwtUtil;
        this.webClient = WebClient.builder()
                .baseUrl(accountsUrl)
                .build();
    }

    /**
     * Login con validación real contra base de datos.
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(
            @RequestBody LoginRequest request,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
            @RequestHeader(value = "X-Real-IP", required = false) String realIp) {

        log.info("Login attempt for user: {}", request.getUsername());

        // Validación básica
        if (request.getUsername() == null || request.getUsername().isBlank() ||
            request.getPassword() == null || request.getPassword().length() < 6) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Username y password (mín 6 caracteres) son requeridos")
                            .build()));
        }

        String clientIp = forwardedFor != null ? forwardedFor.split(",")[0].trim() :
                          realIp != null ? realIp : "unknown";

        // Llamar al servicio de accounts para validar credenciales
        return webClient.post()
                .uri("/internal/auth/validate")
                .header("X-Forwarded-For", clientIp)
                .bodyValue(Map.of(
                        "username", request.getUsername(),
                        "password", request.getPassword()
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    Boolean success = (Boolean) response.get("success");

                    if (success == null || !success) {
                        String message = (String) response.get("message");
                        log.warn("Authentication failed for user: {} - {}", request.getUsername(), message);
                        return Mono.just(ResponseEntity.status(401)
                                .body(AuthResponse.builder()
                                        .success(false)
                                        .message(message != null ? message : "Credenciales inválidas")
                                        .build()));
                    }

                    // Autenticación exitosa - generar JWT
                    // userId viene como Integer/Long del JSON, convertir a String
                    Object userIdObj = response.get("userId");
                    String userId = userIdObj != null ? userIdObj.toString() : null;
                    String username = (String) response.get("username");
                    String fullName = (String) response.get("fullName");
                    String profilePhotoUrl = (String) response.get("profilePhotoUrl");
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) response.get("roles");

                    String token = jwtUtil.generateToken(userId, username, roles);

                    log.info("Login successful for user: {}", username);

                    return Mono.just(ResponseEntity.ok(AuthResponse.builder()
                            .success(true)
                            .token(token)
                            .userId(userId)
                            .username(username)
                            .fullName(fullName)
                            .roles(roles)
                            .profilePhotoUrl(profilePhotoUrl)
                            .message("Login exitoso")
                            .build()));
                })
                .onErrorResume(error -> {
                    log.error("Error during authentication: {}", error.getMessage());
                    return Mono.just(ResponseEntity.status(503)
                            .body(AuthResponse.builder()
                                    .success(false)
                                    .message("Servicio de autenticación no disponible")
                                    .build()));
                });
    }

    /**
     * Validar token.
     */
    @GetMapping("/validate")
    public Mono<ResponseEntity<AuthResponse>> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.just(ResponseEntity.status(401)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Token no proporcionado")
                            .build()));
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
            return Mono.just(ResponseEntity.status(401)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Token inválido o expirado")
                            .build()));
        }

        String userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        List<String> roles = jwtUtil.getRoles(token);

        return Mono.just(ResponseEntity.ok(AuthResponse.builder()
                .success(true)
                .userId(userId)
                .username(username)
                .roles(roles)
                .message("Token válido")
                .build()));
    }

    /**
     * Registro de nuevo usuario.
     * Crea usuario con rol USER por defecto.
     */
    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String email = request.get("email");
        String fullName = request.get("fullName");

        log.info("Registration attempt for user: {}", username);

        // Validaciones
        if (username == null || username.length() < 3 || username.length() > 50) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Username debe tener entre 3 y 50 caracteres")
                            .build()));
        }

        if (password == null || password.length() < 6) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Password debe tener al menos 6 caracteres")
                            .build()));
        }

        if (email == null || !email.contains("@")) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Email inválido")
                            .build()));
        }

        if (fullName == null || fullName.length() < 2) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Nombre completo es requerido")
                            .build()));
        }

        // Llamar al servicio de accounts para crear usuario
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/auth/register")
                        .queryParam("username", username)
                        .queryParam("password", password)
                        .queryParam("email", email)
                        .queryParam("fullName", fullName)
                        .queryParam("role", "USER")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    Boolean success = (Boolean) response.get("success");
                    String message = (String) response.get("message");

                    if (success == null || !success) {
                        log.warn("Registration failed for user: {} - {}", username, message);
                        return Mono.just(ResponseEntity.badRequest()
                                .body(AuthResponse.builder()
                                        .success(false)
                                        .message(message != null ? message : "Error al registrar usuario")
                                        .build()));
                    }

                    log.info("Registration successful for user: {}", username);

                    return Mono.just(ResponseEntity.ok(AuthResponse.builder()
                            .success(true)
                            .message("Usuario registrado exitosamente. Por favor inicie sesión.")
                            .build()));
                })
                .onErrorResume(error -> {
                    log.error("Error during registration: {}", error.getMessage());
                    return Mono.just(ResponseEntity.status(503)
                            .body(AuthResponse.builder()
                                    .success(false)
                                    .message("Servicio de registro no disponible")
                                    .build()));
                });
    }

    /**
     * Refresh token.
     */
    @PostMapping("/refresh")
    public Mono<ResponseEntity<AuthResponse>> refreshToken(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.just(ResponseEntity.status(401)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Token no proporcionado")
                            .build()));
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return Mono.just(ResponseEntity.status(401)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Token inválido")
                            .build()));
        }

        String userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        List<String> roles = jwtUtil.getRoles(token);

        String newToken = jwtUtil.generateToken(userId, username, roles);

        return Mono.just(ResponseEntity.ok(AuthResponse.builder()
                .success(true)
                .token(newToken)
                .userId(userId)
                .username(username)
                .roles(roles)
                .message("Token renovado exitosamente")
                .build()));
    }
}
