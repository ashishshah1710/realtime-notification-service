package com.ashish.notification.consumer;

import com.ashish.notification.dto.NotificationEvent;
import com.ashish.notification.model.Notification;
import com.ashish.notification.model.NotificationStatus;
import com.ashish.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeadLetterConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterConsumer.class);

    private final NotificationRepository repository;

    @KafkaListener(topics = "${notification.kafka.dead-letter-topic}", groupId = "${spring.kafka.consumer.group-id}-dlt",
        containerFactory = "dltListenerContainerFactory")
    public void consume(NotificationEvent event) {
        log.warn("Processing dead-letter notification for userId={}, channel={}, title={}",
                event.getUserId(), event.getChannel(), event.getTitle());

        Notification notification = Notification.builder()
                .userId(event.getUserId())
                .channel(event.getChannel())
                .title(event.getTitle())
                .message(event.getMessage())
                .status(NotificationStatus.FAILED)
                .retryCount(3)
                .build();

        repository.save(notification);
        log.info("Persisted failed notification to database: userId={}", event.getUserId());
    }
}
