package ec.com.finflow.notifications.kafka;

import ec.com.finflow.notifications.service.NotificationService;
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

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${finflow.notifications.topics.transfers-initiated}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTransferInitiated(ConsumerRecord<String, TransferNotificationEvent> record,
                                         Acknowledgment ack) {
        processEvent(record, "TRANSFER_INITIATED", ack);
    }

    @KafkaListener(
            topics = "${finflow.notifications.topics.transfers-completed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTransferCompleted(ConsumerRecord<String, TransferNotificationEvent> record,
                                         Acknowledgment ack) {
        processEvent(record, "TRANSFER_COMPLETED", ack);
    }

    @KafkaListener(
            topics = "${finflow.notifications.topics.transfers-failed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTransferFailed(ConsumerRecord<String, TransferNotificationEvent> record,
                                      Acknowledgment ack) {
        processEvent(record, "TRANSFER_FAILED", ack);
    }

    @KafkaListener(
            topics = "${finflow.notifications.topics.transfers-reversed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTransferReversed(ConsumerRecord<String, TransferNotificationEvent> record,
                                        Acknowledgment ack) {
        processEvent(record, "TRANSFER_REVERSED", ack);
    }

    private void processEvent(ConsumerRecord<String, TransferNotificationEvent> record,
                               String eventType,
                               Acknowledgment ack) {
        TransferNotificationEvent event = record.value();
        try {
            log.info("[{}] Processing {} notification: transferId={}, topic={}, partition={}, offset={}",
                    event.getCorrelationId(),
                    eventType,
                    event.getTransferId(),
                    record.topic(),
                    record.partition(),
                    record.offset());

            notificationService.sendTransferNotification(
                    event,
                    eventType,
                    record.topic(),
                    record.partition(),
                    record.offset()
            );

            ack.acknowledge();
            log.debug("[{}] Notification {} processed successfully",
                    event.getCorrelationId(), eventType);

        } catch (Exception e) {
            log.error("[{}] Error processing {} notification: {}",
                    event.getCorrelationId(), eventType, e.getMessage(), e);
            // Acknowledge to prevent infinite retry - notification will be saved with FAILED status
            ack.acknowledge();
        }
    }
}
