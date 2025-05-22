package com.glowsky.post.dto;

import com.glowsky.post.model.Post;
import com.glowsky.post.model.PostByUser;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class PostResponse {
    private UUID id;
    private UUID userId;
    private String content;
    private List<String> mediaUrls;
    private List<String> tags;
    private int likesCount;
    private int commentsCount;
    private boolean isPublic;
    private Instant createdAt;
    private Instant updatedAt;

    // Constructor from entity
    public PostResponse(Post post) {
        this.id = post.getId();
        this.userId = post.getUserId();
        this.content = post.getContent();
        this.mediaUrls = post.getMediaUrls();
        this.tags = post.getTags();
        this.likesCount = post.getLikesCount();
        this.commentsCount = post.getCommentsCount();
        this.isPublic = post.isPublic();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    // Alternatively, construct from PostByUser for more efficient queries
    public PostResponse(PostByUser postByUser) {
        this.id = postByUser.getPostId();
        this.userId = postByUser.getUserId();
        this.content = postByUser.getContent();
        this.mediaUrls = postByUser.getMediaUrls();
        this.tags = postByUser.getTags();
        this.likesCount = postByUser.getLikesCount();
        this.commentsCount = postByUser.getCommentsCount();
        this.isPublic = postByUser.isPublic();
        this.createdAt = postByUser.getCreatedAt();
        // Updated at is not in PostByUser, would need to get from main post table if needed
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public List<String> getTags() {
        return tags;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}