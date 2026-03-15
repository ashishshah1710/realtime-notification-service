package com.ashish.notification.controller;

import com.ashish.notification.dto.NotificationEvent;
import com.ashish.notification.dto.NotificationResponse;
import com.ashish.notification.model.Notification;
import com.ashish.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification publishing and query API")
public class NotificationController {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final NotificationRepository notificationRepository;

    @Value("${notification.kafka.topic}")
    private String notificationsTopic;

    @PostMapping
    @Operation(summary = "Publish notification", description = "Publishes a notification event to Kafka for async delivery")
    public ResponseEntity<NotificationEvent> publishNotification(@RequestBody NotificationEvent event) {
        kafkaTemplate.send(notificationsTopic, event.getUserId(), event);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(event);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get notification history", description = "Returns all notifications for the given user")
    public ResponseEntity<List<NotificationResponse>> getNotificationHistory(@PathVariable String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<NotificationResponse> responses = notifications.stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Get delivery status", description = "Returns the delivery status of a notification by ID")
    public ResponseEntity<NotificationResponse> getDeliveryStatus(@PathVariable UUID id) {
        return notificationRepository.findById(id)
                .map(NotificationResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
