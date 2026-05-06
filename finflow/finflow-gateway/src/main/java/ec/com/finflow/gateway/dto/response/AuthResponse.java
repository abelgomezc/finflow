package ec.com.finflow.gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta de autenticación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private boolean success;
    private String token;
    private String userId;
    private String username;
    private String fullName;
    private List<String> roles;
    private String message;
    private String profilePhotoUrl;
}
