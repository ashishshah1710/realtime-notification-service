package com.ashish.notification.service;

import com.ashish.notification.dto.NotificationEvent;
import com.ashish.notification.model.Notification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private static final Logger log = LoggerFactory.getLogger(WebSocketNotificationService.class);

    private final SimpMessagingTemplate messagingTemplate;

    public void send(Notification notification, NotificationEvent event) {
        String destination = "/topic/notifications/" + notification.getUserId();

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", notification.getId());
        payload.put("userId", notification.getUserId());
        payload.put("channel", notification.getChannel().name());
        payload.put("title", notification.getTitle());
        payload.put("message", notification.getMessage());
        payload.put("metadata", event.getMetadata());

        messagingTemplate.convertAndSend(destination, payload);
        log.debug("WebSocket notification sent to userId={}", notification.getUserId());
    }
}
