package com.ashish.notification.dto;

import com.ashish.notification.model.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String userId;
    private NotificationChannel channel;
    private String title;
    private String message;
    private Map<String, Object> metadata;
}
