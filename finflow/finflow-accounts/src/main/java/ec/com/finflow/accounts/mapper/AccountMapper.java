package ec.com.finflow.accounts.mapper;

import ec.com.finflow.accounts.domain.entity.Account;
import ec.com.finflow.accounts.domain.entity.Customer;
import ec.com.finflow.accounts.dto.request.CreateCustomerRequest;
import ec.com.finflow.accounts.dto.response.AccountResponse;
import ec.com.finflow.accounts.dto.response.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.math.BigDecimal;

/**
 * Mapper para conversión entre entidades y DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    /**
     * Convierte Customer entity a CustomerResponse DTO.
     */
    @Mapping(target = "fullName", expression = "java(customer.getFullName())")
    CustomerResponse toResponse(Customer customer);

    /**
     * Convierte CreateCustomerRequest a Customer entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Customer toEntity(CreateCustomerRequest request);

    /**
     * Convierte Account entity a AccountResponse DTO.
     * Requiere el monto bloqueado para calcular available balance.
     */
    default AccountResponse toResponse(Account account, BigDecimal blockedAmount) {
        if (account == null) {
            return null;
        }

        BigDecimal available = account.getBalance().subtract(
                blockedAmount != null ? blockedAmount : BigDecimal.ZERO);

        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .customerId(account.getCustomer().getId())
                .customerName(account.getCustomer().getFullName())
                .customerDocumentType(account.getCustomer().getDocumentType())
                .customerDocumentNumber(account.getCustomer().getDocumentNumber())
                .accountType(account.getAccountType())
                .status(account.getStatus())
                .currency(account.getCurrency())
                .balance(account.getBalance())
                .availableBalance(available)
                .blockedAmount(blockedAmount != null ? blockedAmount : BigDecimal.ZERO)
                .dailyTransferLimit(account.getDailyTransferLimit())
                .perTransferLimit(account.getPerTransferLimit())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
