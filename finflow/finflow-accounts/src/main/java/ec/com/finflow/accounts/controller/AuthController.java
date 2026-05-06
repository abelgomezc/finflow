package ec.com.finflow.accounts.controller;

import ec.com.finflow.accounts.dto.request.AuthenticateRequest;
import ec.com.finflow.accounts.dto.response.AuthenticateResponse;
import ec.com.finflow.accounts.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador interno de autenticación.
 * Este endpoint es llamado por el Gateway para validar credenciales.
 */
@Slf4j
@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Valida credenciales contra la base de datos.
     * Solo debe ser accesible internamente por el Gateway.
     */
    @PostMapping("/validate")
    public ResponseEntity<AuthenticateResponse> validateCredentials(
            @Valid @RequestBody AuthenticateRequest request,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
            HttpServletRequest httpRequest) {

        String ipAddress = forwardedFor != null ? forwardedFor.split(",")[0].trim() : httpRequest.getRemoteAddr();

        log.debug("Credential validation request for user: {} from IP: {}", request.getUsername(), ipAddress);

        AuthenticateResponse response = authService.authenticate(request, ipAddress);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * Crea un nuevo usuario (solo para testing/setup).
     * En producción, esto debería estar protegido o deshabilitado.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticateResponse> registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String fullName,
            @RequestParam(defaultValue = "USER") String role) {

        log.info("User registration request for: {}", username);

        AuthenticateResponse response = authService.createUser(username, password, email, fullName, role);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
