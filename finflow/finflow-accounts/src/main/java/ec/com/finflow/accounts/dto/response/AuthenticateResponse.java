package ec.com.finflow.accounts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response de autenticación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateResponse {

    private boolean success;
    private String message;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private List<String> roles;
    private Long customerId;
    private String profilePhotoUrl;
}
