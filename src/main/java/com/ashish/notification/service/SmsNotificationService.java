package com.ashish.notification.service;

import com.ashish.notification.dto.NotificationEvent;
import com.ashish.notification.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

    public void send(Notification notification, NotificationEvent event) {
        String phoneNumber = extractPhoneFromMetadata(event);
        if (phoneNumber == null || phoneNumber.isBlank()) {
            phoneNumber = "N/A";
        }

        log.info("SMS delivery [PLACEHOLDER] - userId={}, phone={}, title={}, message={}",
                notification.getUserId(), phoneNumber, notification.getTitle(), notification.getMessage());
    }

    private String extractPhoneFromMetadata(NotificationEvent event) {
        if (event.getMetadata() != null && event.getMetadata().containsKey("phone")) {
            Object phone = event.getMetadata().get("phone");
            return phone != null ? phone.toString() : null;
        }
        return null;
    }
}
