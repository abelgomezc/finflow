package ec.com.finflow.transfers.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Productor Kafka para eventos de transferencia.
 * Versión stub que solo registra logs (Kafka deshabilitado).
 */
@Slf4j
@Component
public class TransferKafkaProducer {

    /**
     * Publica evento de transferencia iniciada.
     */
    public void publishInitiated(TransferEvent event) {
        log.info("[KAFKA STUB] Would publish INITIATED event for transfer: {}", event.getTransferId());
    }

    /**
     * Publica evento de transferencia completada.
     */
    public void publishCompleted(TransferEvent event) {
        log.info("[KAFKA STUB] Would publish COMPLETED event for transfer: {}", event.getTransferId());
    }

    /**
     * Publica evento de transferencia fallida.
     */
    public void publishFailed(TransferEvent event) {
        log.info("[KAFKA STUB] Would publish FAILED event for transfer: {}", event.getTransferId());
    }

    /**
     * Publica evento de transferencia revertida.
     */
    public void publishReversed(TransferEvent event) {
        log.info("[KAFKA STUB] Would publish REVERSED event for transfer: {}", event.getTransferId());
    }
}
