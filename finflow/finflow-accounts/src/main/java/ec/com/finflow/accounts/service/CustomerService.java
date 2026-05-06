package ec.com.finflow.accounts.service;

import ec.com.finflow.accounts.dto.request.CreateCustomerRequest;
import ec.com.finflow.accounts.dto.response.CustomerResponse;

import java.util.List;

/**
 * Interfaz del servicio de clientes.
 */
public interface CustomerService {

    /**
     * Crea un nuevo cliente.
     */
    CustomerResponse createCustomer(CreateCustomerRequest request);

    /**
     * Obtiene un cliente por ID.
     */
    CustomerResponse getCustomer(Long customerId);

    /**
     * Obtiene un cliente por email.
     */
    CustomerResponse getCustomerByEmail(String email);

    /**
     * Busca clientes por nombre.
     */
    List<CustomerResponse> searchByName(String name);

    /**
     * Obtiene todos los clientes activos.
     */
    List<CustomerResponse> getAllActiveCustomers();
}
