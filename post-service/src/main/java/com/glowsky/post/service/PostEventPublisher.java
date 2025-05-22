package com.glowsky.post.service;

import com.glowsky.common.kafka.KafkaTopics;
import com.glowsky.common.kafka.events.PostEvent;
import com.glowsky.post.model.Post;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PostEventPublisher {
    private final KafkaTemplate<String, PostEvent> kafkaTemplate;

    public PostEventPublisher(KafkaTemplate<String, PostEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPostCreated(Post post) {
        PostEvent event = createPostEvent(post, PostEvent.EventType.CREATED);
        kafkaTemplate.send(KafkaTopics.POST_EVENTS, post.getId().toString(), event);
    }

    public void publishPostUpdated(Post post) {
        PostEvent event = createPostEvent(post, PostEvent.EventType.UPDATED);
        kafkaTemplate.send(KafkaTopics.POST_EVENTS, post.getId().toString(), event);
    }

    public void publishPostDeleted(Post post) {
        PostEvent event = createPostEvent(post, PostEvent.EventType.DELETED);
        kafkaTemplate.send(KafkaTopics.POST_EVENTS, post.getId().toString(), event);
    }

    private PostEvent createPostEvent(Post post, PostEvent.EventType eventType) {
        return PostEvent.builder()
                .postId(post.getId().toString())
                .userId(post.getUserId().toString())
                .content(post.getContent())
                .mediaUrls(post.getMediaUrls())
                .tags(post.getTags())
                .isPublic(post.isPublic())
                .createdAt(post.getCreatedAt())
                .eventType(eventType)
                .build();
    }
}