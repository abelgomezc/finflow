package ec.com.finflow.accounts.controller;

import ec.com.finflow.accounts.dto.request.CreateCustomerRequest;
import ec.com.finflow.accounts.dto.response.CustomerResponse;
import ec.com.finflow.accounts.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones de clientes.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Crea un nuevo cliente.
     */
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("[{}] Creating customer: {}", correlationId, request.getEmail());
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene un cliente por ID.
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomer(
            @PathVariable Long customerId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.debug("[{}] Getting customer: {}", correlationId, customerId);
        CustomerResponse response = customerService.getCustomer(customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un cliente por email.
     */
    @GetMapping("/by-email")
    public ResponseEntity<CustomerResponse> getCustomerByEmail(
            @RequestParam String email,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.debug("[{}] Getting customer by email: {}", correlationId, email);
        CustomerResponse response = customerService.getCustomerByEmail(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca clientes por nombre.
     */
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponse>> searchByName(
            @RequestParam String name,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.debug("[{}] Searching customers by name: {}", correlationId, name);
        List<CustomerResponse> response = customerService.searchByName(name);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todos los clientes activos.
     */
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllActiveCustomers(
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.debug("[{}] Getting all active customers", correlationId);
        List<CustomerResponse> response = customerService.getAllActiveCustomers();
        return ResponseEntity.ok(response);
    }
}
