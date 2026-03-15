package com.ashish.notification.consumer;

import com.ashish.notification.dto.NotificationEvent;
import com.ashish.notification.model.Notification;
import com.ashish.notification.model.NotificationStatus;
import com.ashish.notification.repository.NotificationRepository;
import com.ashish.notification.service.NotificationDispatcher;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationDispatcher dispatcher;
    private final NotificationRepository repository;

    @KafkaListener(topics = "${notification.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(@Payload NotificationEvent event, ConsumerRecord<String, NotificationEvent> record,
                        org.springframework.kafka.support.Acknowledgment ack) {
        log.debug("Received notification event for userId={}, channel={}", event.getUserId(), event.getChannel());

        Notification notification = Notification.builder()
                .userId(event.getUserId())
                .channel(event.getChannel())
                .title(event.getTitle())
                .message(event.getMessage())
                .status(NotificationStatus.PENDING)
                .retryCount(0)
                .build();

        notification = repository.save(notification);
        try {
            dispatcher.dispatch(notification, event);
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(Instant.now());
            repository.save(notification);
            ack.acknowledge();
            log.info("Notification delivered successfully: id={}, channel={}", notification.getId(), event.getChannel());
        } catch (Exception e) {
            log.error("Failed to deliver notification for userId={}, channel={}, retrying: {}",
                    event.getUserId(), event.getChannel(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
