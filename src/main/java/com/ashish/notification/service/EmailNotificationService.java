package com.ashish.notification.service;

import com.ashish.notification.dto.NotificationEvent;
import com.ashish.notification.model.Notification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    private final JavaMailSender mailSender;

    public void send(Notification notification, NotificationEvent event) {
        try {
            String recipient = extractEmailFromMetadata(event);
            if (recipient == null || recipient.isBlank()) {
                throw new IllegalArgumentException("Email recipient not found in metadata");
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient);
            message.setSubject(notification.getTitle());
            message.setText(notification.getMessage());

            mailSender.send(message);
            log.debug("Email notification sent to {}", recipient);
        } catch (Exception e) {
            log.error("Failed to send email notification for userId={}: {}", notification.getUserId(), e.getMessage());
            throw new RuntimeException("Email delivery failed", e);
        }
    }

    private String extractEmailFromMetadata(NotificationEvent event) {
        if (event.getMetadata() != null && event.getMetadata().containsKey("email")) {
            Object email = event.getMetadata().get("email");
            return email != null ? email.toString() : null;
        }
        return null;
    }
}
