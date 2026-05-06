package ec.com.finflow.accounts.dto.response;

import lombok.*;

import java.time.OffsetDateTime;

/**
 * DTO para respuesta de usuarios en panel de administración.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private Boolean isActive;
    private Boolean isLocked;
    private Integer failedAttempts;
    private OffsetDateTime lastLoginAt;
    private String lastLoginIp;
    private String profilePhotoUrl;

    // Información del cliente asociado
    private Long customerId;
    private String customerName;
    private String documentType;
    private String documentNumber;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
