package ec.com.finflow.audit.kafka;

import ec.com.finflow.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Consumer de Kafka para eventos de transferencias.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransferEventConsumer {

    private final AuditService auditService;

    @KafkaListener(
            topics = "${finflow.audit.topics.transfers-initiated}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTransferInitiated(ConsumerRecord<String, AuditEvent> record, Acknowledgment ack) {
        processEvent(record, "TRANSFER", "TRANSFER_INITIATED", ack);
    }

    @KafkaListener(
            topics = "${finflow.audit.topics.transfers-completed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTransferCompleted(ConsumerRecord<String, AuditEvent> record, Acknowledgment ack) {
        processEvent(record, "TRANSFER", "TRANSFER_COMPLETED", ack);
    }

    @KafkaListener(
            topics = "${finflow.audit.topics.transfers-failed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTransferFailed(ConsumerRecord<String, AuditEvent> record, Acknowledgment ack) {
        processEvent(record, "TRANSFER", "TRANSFER_FAILED", ack);
    }

    @KafkaListener(
            topics = "${finflow.audit.topics.transfers-reversed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTransferReversed(ConsumerRecord<String, AuditEvent> record, Acknowledgment ack) {
        processEvent(record, "TRANSFER", "TRANSFER_REVERSED", ack);
    }

    private void processEvent(ConsumerRecord<String, AuditEvent> record,
                              String category,
                              String eventType,
                              Acknowledgment ack) {
        try {
            AuditEvent event = record.value();
            log.info("[{}] Processing {} event: transferId={}, topic={}, partition={}, offset={}",
                    event.getCorrelationId(),
                    eventType,
                    event.getTransferId(),
                    record.topic(),
                    record.partition(),
                    record.offset());

            auditService.saveTransferEvent(
                    event,
                    category,
                    eventType,
                    record.topic(),
                    record.partition(),
                    record.offset()
            );

            ack.acknowledge();
            log.debug("[{}] Event {} processed successfully", event.getCorrelationId(), eventType);

        } catch (Exception e) {
            log.error("Error processing {} event: {}", eventType, e.getMessage(), e);
            // No hacer ack para que el mensaje se reprocese
            throw e;
        }
    }
}
