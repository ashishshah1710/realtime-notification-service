package com.ashish.notification.service;

import com.ashish.notification.dto.NotificationEvent;
import com.ashish.notification.model.Notification;
import com.ashish.notification.model.NotificationChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final WebSocketNotificationService webSocketService;
    private final EmailNotificationService emailService;
    private final SmsNotificationService smsService;

    public void dispatch(Notification notification, NotificationEvent event) {
        switch (notification.getChannel()) {
            case WEBSOCKET -> webSocketService.send(notification, event);
            case EMAIL -> emailService.send(notification, event);
            case SMS -> smsService.send(notification, event);
            default -> throw new IllegalArgumentException("Unsupported channel: " + notification.getChannel());
        }
    }
}
