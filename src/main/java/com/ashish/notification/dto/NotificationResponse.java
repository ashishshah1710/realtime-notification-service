package com.ashish.notification.dto;

import com.ashish.notification.model.Notification;
import com.ashish.notification.model.NotificationChannel;
import com.ashish.notification.model.NotificationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {

    private UUID id;
    private String userId;
    private NotificationChannel channel;
    private String title;
    private String message;
    private NotificationStatus status;
    private Instant createdAt;
    private Instant sentAt;
    private Integer retryCount;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .channel(notification.getChannel())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .retryCount(notification.getRetryCount())
                .build();
    }
}
