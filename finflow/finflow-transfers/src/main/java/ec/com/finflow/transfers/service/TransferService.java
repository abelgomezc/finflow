package ec.com.finflow.transfers.service;

import ec.com.finflow.transfers.dto.request.InitiateTransferRequest;
import ec.com.finflow.transfers.dto.response.TransferResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio de transferencias.
 * Orquesta el flujo completo implementando el patrón Saga.
 */
public interface TransferService {

    /**
     * Inicia una nueva transferencia.
     * Ejecuta el flujo completo: validación → bloqueo → débito → crédito.
     *
     * @param request Datos de la transferencia
     * @return Respuesta con el estado de la transferencia
     */
    TransferResponse initiateTransfer(InitiateTransferRequest request);

    /**
     * Obtiene una transferencia por ID.
     */
    TransferResponse getTransfer(Long transferId);

    /**
     * Obtiene una transferencia por número de referencia.
     */
    TransferResponse getTransferByReference(String referenceNumber);

    /**
     * Obtiene el historial de transferencias de un usuario.
     */
    Page<TransferResponse> getTransferHistory(String userId, Pageable pageable);

    /**
     * Obtiene transferencias de una cuenta.
     */
    Page<TransferResponse> getAccountTransfers(Long accountId, Pageable pageable);

    /**
     * Cancela una transferencia pendiente.
     */
    TransferResponse cancelTransfer(Long transferId, String userId, String reason);

    /**
     * Revierte una transferencia completada (compensating transaction).
     */
    TransferResponse reverseTransfer(Long transferId, String userId, String reason);
}
