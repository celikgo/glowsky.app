package com.glowsky.notification.service;

import com.glowsky.common.kafka.KafkaTopics;
import com.glowsky.common.kafka.events.PostEvent;
import com.glowsky.common.kafka.events.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {
    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaTopics.POST_EVENTS, groupId = "notification-service-group")
    public void consumePostEvent(PostEvent event) {
        log.info("Notification service received post event: {}", event);

        switch (event.getEventType()) {
            case LIKED:
                // Create a notification for the post author that someone liked their post
                notificationService.createLikeNotification(event.getPostId(), event.getUserId());
                break;
            case CREATED:

                break;
            // Handle other event types
        }
    }

    @KafkaListener(topics = KafkaTopics.USER_EVENTS, groupId = "notification-service-group")
    public void consumeUserEvent(UserEvent event) {
        log.info("Notification service received user event: {}", event);

        // Handle user events that might trigger notifications
    }
}