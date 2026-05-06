package ec.com.finflow.accounts.repository;

import ec.com.finflow.accounts.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones con clientes.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Busca un cliente por email (case insensitive).
     */
    Optional<Customer> findByEmailIgnoreCase(String email);

    /**
     * Busca un cliente por tipo y número de documento.
     */
    Optional<Customer> findByDocumentTypeAndDocumentNumber(String documentType, String documentNumber);

    /**
     * Verifica si existe un cliente con el email dado.
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Verifica si existe un cliente con el documento dado.
     */
    boolean existsByDocumentTypeAndDocumentNumber(String documentType, String documentNumber);

    /**
     * Busca clientes activos por nombre (parcial, case insensitive).
     */
    @Query("SELECT c FROM Customer c WHERE c.isActive = true " +
           "AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Customer> findActiveByNameContaining(@Param("name") String name);

    /**
     * Obtiene todos los clientes activos.
     */
    List<Customer> findByIsActiveTrue();
}
