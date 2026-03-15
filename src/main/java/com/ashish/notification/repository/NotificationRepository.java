package com.ashish.notification.repository;

import com.ashish.notification.model.Notification;
import com.ashish.notification.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Notification> findByStatus(NotificationStatus status);
}
