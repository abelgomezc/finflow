package ec.com.finflow.accounts.service;

import ec.com.finflow.accounts.dto.request.AuthenticateRequest;
import ec.com.finflow.accounts.dto.response.AuthenticateResponse;

/**
 * Servicio de autenticación.
 */
public interface AuthService {

    /**
     * Autentica un usuario contra la base de datos.
     * @param request Credenciales del usuario
     * @param ipAddress IP del cliente para registro de auditoría
     * @return Respuesta con datos del usuario si es exitoso
     */
    AuthenticateResponse authenticate(AuthenticateRequest request, String ipAddress);

    /**
     * Crea un nuevo usuario con password encriptado.
     */
    AuthenticateResponse createUser(String username, String password, String email, String fullName, String role);
}
