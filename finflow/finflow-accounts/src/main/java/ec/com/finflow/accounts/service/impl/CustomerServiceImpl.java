package ec.com.finflow.accounts.service.impl;

import ec.com.finflow.accounts.domain.entity.Customer;
import ec.com.finflow.accounts.domain.exception.FinFlowException;
import ec.com.finflow.accounts.dto.request.CreateCustomerRequest;
import ec.com.finflow.accounts.dto.response.CustomerResponse;
import ec.com.finflow.accounts.mapper.AccountMapper;
import ec.com.finflow.accounts.repository.CustomerRepository;
import ec.com.finflow.accounts.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de clientes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        log.info("Creating customer with email: {}", request.getEmail());

        // Validar que no exista con el mismo email
        if (customerRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new FinFlowException("Customer with email already exists: " + request.getEmail(),
                    "DUPLICATE_EMAIL");
        }

        // Validar que no exista con el mismo documento
        if (customerRepository.existsByDocumentTypeAndDocumentNumber(
                request.getDocumentType(), request.getDocumentNumber())) {
            throw new FinFlowException("Customer with document already exists",
                    "DUPLICATE_DOCUMENT");
        }

        Customer customer = accountMapper.toEntity(request);
        customer = customerRepository.save(customer);

        log.info("Customer created: {}", customer.getId());
        return accountMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new FinFlowException(
                        "Customer not found: " + customerId, "CUSTOMER_NOT_FOUND"));
        return accountMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new FinFlowException(
                        "Customer not found with email: " + email, "CUSTOMER_NOT_FOUND"));
        return accountMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> searchByName(String name) {
        return customerRepository.findActiveByNameContaining(name).stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllActiveCustomers() {
        return customerRepository.findByIsActiveTrue().stream()
                .map(accountMapper::toResponse)
                .toList();
    }
}
