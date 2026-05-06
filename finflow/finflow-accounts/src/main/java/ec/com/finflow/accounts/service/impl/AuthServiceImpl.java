package ec.com.finflow.accounts.service.impl;

import ec.com.finflow.accounts.domain.entity.User;
import ec.com.finflow.accounts.dto.request.AuthenticateRequest;
import ec.com.finflow.accounts.dto.response.AuthenticateResponse;
import ec.com.finflow.accounts.repository.UserRepository;
import ec.com.finflow.accounts.service.AuthService;
import ec.com.finflow.accounts.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de autenticación.
 * Usa BCrypt para encriptar y validar passwords.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserActivityService userActivityService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Override
    @Transactional
    public AuthenticateResponse authenticate(AuthenticateRequest request, String ipAddress) {
        log.info("Authentication attempt for user: {}", request.getUsername());

        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            log.warn("User not found: {}", request.getUsername());
            return AuthenticateResponse.builder()
                    .success(false)
                    .message("Credenciales inválidas")
                    .build();
        }

        User user = userOpt.get();

        // Verificar si la cuenta está bloqueada
        if (user.getIsLocked()) {
            log.warn("Account locked for user: {}", request.getUsername());
            return AuthenticateResponse.builder()
                    .success(false)
                    .message("Cuenta bloqueada. Contacte al administrador.")
                    .build();
        }

        // Verificar si la cuenta está activa
        if (!user.getIsActive()) {
            log.warn("Account inactive for user: {}", request.getUsername());
            return AuthenticateResponse.builder()
                    .success(false)
                    .message("Cuenta inactiva")
                    .build();
        }

        // Validar password con BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            user.incrementFailedAttempts();
            userRepository.save(user);
            log.warn("Invalid password for user: {}. Attempts: {}", request.getUsername(), user.getFailedAttempts());

            // Registrar intento fallido
            userActivityService.logLoginFailed(request.getUsername(), ipAddress);

            if (user.getIsLocked()) {
                return AuthenticateResponse.builder()
                        .success(false)
                        .message("Cuenta bloqueada por múltiples intentos fallidos")
                        .build();
            }

            return AuthenticateResponse.builder()
                    .success(false)
                    .message("Credenciales inválidas")
                    .build();
        }

        // Login exitoso
        user.recordSuccessfulLogin(ipAddress);
        userRepository.save(user);

        // Registrar login exitoso
        userActivityService.logLogin(user.getId(), ipAddress);

        log.info("Authentication successful for user: {}", request.getUsername());

        return AuthenticateResponse.builder()
                .success(true)
                .message("Autenticación exitosa")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(List.of(user.getRole()))
                .customerId(user.getCustomer() != null ? user.getCustomer().getId() : null)
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .build();
    }

    @Override
    @Transactional
    public AuthenticateResponse createUser(String username, String password, String email, String fullName, String role) {
        log.info("Creating new user: {}", username);

        // Verificar si ya existe
        if (userRepository.existsByUsername(username)) {
            return AuthenticateResponse.builder()
                    .success(false)
                    .message("Username ya existe")
                    .build();
        }

        if (userRepository.existsByEmail(email)) {
            return AuthenticateResponse.builder()
                    .success(false)
                    .message("Email ya existe")
                    .build();
        }

        // Encriptar password con BCrypt
        String passwordHash = passwordEncoder.encode(password);

        User user = User.builder()
                .username(username)
                .passwordHash(passwordHash)
                .email(email)
                .fullName(fullName)
                .role(role != null ? role : "USER")
                .isActive(true)
                .isLocked(false)
                .failedAttempts(0)
                .build();

        user = userRepository.save(user);

        log.info("User created successfully: {}", username);

        return AuthenticateResponse.builder()
                .success(true)
                .message("Usuario creado exitosamente")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(List.of(user.getRole()))
                .build();
    }
}
