package ec.com.finflow.accounts.controller;

import ec.com.finflow.accounts.domain.entity.UserActivity;
import ec.com.finflow.accounts.dto.response.UserActivityResponse;
import ec.com.finflow.accounts.repository.UserRepository;
import ec.com.finflow.accounts.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador para gestión del perfil de usuario.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserRepository userRepository;
    private final UserActivityService userActivityService;

    @Value("${finflow.uploads.path:./uploads}")
    private String uploadsPath;

    @Value("${finflow.uploads.base-url:http://localhost:8081/uploads}")
    private String uploadsBaseUrl;

    /**
     * Obtiene el perfil del usuario autenticado.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(
            @RequestHeader(value = "X-User-ID", required = false) String userId) {

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }

        return userRepository.findById(Long.parseLong(userId))
                .map(user -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", user.getId());
                    response.put("username", user.getUsername());
                    response.put("email", user.getEmail());
                    response.put("fullName", user.getFullName());
                    response.put("profilePhotoUrl", user.getProfilePhotoUrl() != null ? user.getProfilePhotoUrl() : "");
                    response.put("role", user.getRole());

                    if (user.getCustomer() != null) {
                        response.put("customerId", user.getCustomer().getId());
                        response.put("customerName", user.getCustomer().getFullName());
                        response.put("documentType", user.getCustomer().getDocumentType());
                        response.put("documentNumber", user.getCustomer().getDocumentNumber());
                    }

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Sube una foto de perfil para el usuario autenticado.
     */
    @PostMapping("/me/photo")
    public ResponseEntity<?> uploadProfilePhoto(
            @RequestHeader(value = "X-User-ID", required = false) String userId,
            @RequestParam("file") MultipartFile file) {

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Archivo vacío"));
        }

        // Validar tipo de archivo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Solo se permiten imágenes"));
        }

        // Validar tamaño (máx 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tamaño máximo: 5MB"));
        }

        return userRepository.findById(Long.parseLong(userId))
                .map(user -> {
                    try {
                        // Crear directorio de uploads si no existe
                        Path uploadDir = Paths.get(uploadsPath, "profiles");
                        Files.createDirectories(uploadDir);

                        // Generar nombre único
                        String extension = getFileExtension(file.getOriginalFilename());
                        String filename = "profile_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

                        // Guardar archivo
                        Path filePath = uploadDir.resolve(filename);
                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        // Actualizar URL en usuario
                        String photoUrl = uploadsBaseUrl + "/profiles/" + filename;
                        user.setProfilePhotoUrl(photoUrl);
                        userRepository.save(user);

                        log.info("Profile photo uploaded for user {}: {}", userId, photoUrl);

                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Foto subida exitosamente");
                        response.put("profilePhotoUrl", photoUrl);
                        return ResponseEntity.ok(response);
                    } catch (IOException e) {
                        log.error("Error uploading profile photo for user {}: {}", userId, e.getMessage());
                        return ResponseEntity.internalServerError().body(Map.of("error", "Error al guardar la imagen"));
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualiza el perfil del usuario.
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(
            @RequestHeader(value = "X-User-ID", required = false) String userId,
            @RequestBody Map<String, String> updates) {

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }

        return userRepository.findById(Long.parseLong(userId))
                .map(user -> {
                    if (updates.containsKey("fullName")) {
                        user.setFullName(updates.get("fullName"));
                    }
                    if (updates.containsKey("email")) {
                        // Verificar que no exista otro usuario con ese email
                        String newEmail = updates.get("email");
                        if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                            return ResponseEntity.badRequest().body(Map.of("error", "Email ya existe"));
                        }
                        user.setEmail(newEmail);
                    }

                    userRepository.save(user);

                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Perfil actualizado");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * Obtiene el historial de actividad del usuario autenticado.
     */
    @GetMapping("/me/activity")
    public ResponseEntity<?> getMyActivity(
            @RequestHeader(value = "X-User-ID", required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }

        try {
            Page<UserActivity> activities = userActivityService.getActivityHistory(
                    Long.parseLong(userId), page, size);

            Page<UserActivityResponse> response = activities.map(activity ->
                    UserActivityResponse.builder()
                            .id(activity.getId())
                            .activityType(activity.getActivityType())
                            .description(activity.getDescription())
                            .entityType(activity.getEntityType())
                            .entityId(activity.getEntityId())
                            .ipAddress(activity.getIpAddress())
                            .createdAt(activity.getCreatedAt())
                            .build()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting activity for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener actividad"));
        }
    }
}
