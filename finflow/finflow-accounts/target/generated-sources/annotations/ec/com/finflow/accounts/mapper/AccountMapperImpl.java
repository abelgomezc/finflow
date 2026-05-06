package ec.com.finflow.accounts.mapper;

import ec.com.finflow.accounts.domain.entity.Customer;
import ec.com.finflow.accounts.dto.request.CreateCustomerRequest;
import ec.com.finflow.accounts.dto.response.CustomerResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-02T13:48:45-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public CustomerResponse toResponse(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerResponse.CustomerResponseBuilder customerResponse = CustomerResponse.builder();

        customerResponse.id( customer.getId() );
        customerResponse.documentType( customer.getDocumentType() );
        customerResponse.documentNumber( customer.getDocumentNumber() );
        customerResponse.firstName( customer.getFirstName() );
        customerResponse.lastName( customer.getLastName() );
        customerResponse.email( customer.getEmail() );
        customerResponse.phone( customer.getPhone() );
        customerResponse.addressLine1( customer.getAddressLine1() );
        customerResponse.addressLine2( customer.getAddressLine2() );
        customerResponse.city( customer.getCity() );
        customerResponse.state( customer.getState() );
        customerResponse.country( customer.getCountry() );
        customerResponse.postalCode( customer.getPostalCode() );
        customerResponse.isActive( customer.getIsActive() );
        customerResponse.createdAt( customer.getCreatedAt() );
        customerResponse.updatedAt( customer.getUpdatedAt() );

        customerResponse.fullName( customer.getFullName() );

        return customerResponse.build();
    }

    @Override
    public Customer toEntity(CreateCustomerRequest request) {
        if ( request == null ) {
            return null;
        }

        Customer.CustomerBuilder<?, ?> customer = Customer.builder();

        customer.documentType( request.getDocumentType() );
        customer.documentNumber( request.getDocumentNumber() );
        customer.firstName( request.getFirstName() );
        customer.lastName( request.getLastName() );
        customer.email( request.getEmail() );
        customer.phone( request.getPhone() );
        customer.addressLine1( request.getAddressLine1() );
        customer.addressLine2( request.getAddressLine2() );
        customer.city( request.getCity() );
        customer.state( request.getState() );
        customer.country( request.getCountry() );
        customer.postalCode( request.getPostalCode() );

        customer.isActive( true );

        return customer.build();
    }
}
