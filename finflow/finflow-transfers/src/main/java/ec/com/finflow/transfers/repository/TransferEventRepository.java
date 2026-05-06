package ec.com.finflow.transfers.repository;

import ec.com.finflow.transfers.domain.entity.TransferEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para eventos de transferencia.
 */
@Repository
public interface TransferEventRepository extends JpaRepository<TransferEvent, Long> {

    List<TransferEvent> findByTransferIdOrderByCreatedAtAsc(Long transferId);
}
