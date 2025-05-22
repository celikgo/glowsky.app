package com.glowsky.common.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private UUID userId;
    private String username;
    private EventType eventType;
    private Instant timestamp;

    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
}