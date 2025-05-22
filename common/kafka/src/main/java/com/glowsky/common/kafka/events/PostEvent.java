package com.glowsky.common.kafka.events;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PostEvent {
    private String postId;
    private String userId;
    private String content;
    private List<String> mediaUrls;
    private List<String> tags;
    private boolean isPublic;
    private Instant createdAt;
    private EventType eventType;

    public enum EventType {
        CREATED,
        UPDATED,
        DELETED,
        LIKED
    }
}
