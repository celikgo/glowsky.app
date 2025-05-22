
package com.glowsky.post.service;

import com.glowsky.post.dto.CreatePostRequest;
import com.glowsky.post.dto.PostResponse;
import com.glowsky.post.exception.PostNotFoundException;
import com.glowsky.post.model.Post;
import com.glowsky.post.model.PostByTag;
import com.glowsky.post.model.PostByUser;
import com.glowsky.post.repository.PostByTagRepository;
import com.glowsky.post.repository.PostByUserRepository;
import com.glowsky.post.repository.PostRepository;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostByUserRepository postByUserRepository;
    private final PostByTagRepository postByTagRepository;
    private final PostEventPublisher eventPublisher;

    public PostService(
            PostRepository postRepository,
            PostByUserRepository postByUserRepository,
            PostByTagRepository postByTagRepository,
            PostEventPublisher eventPublisher) {
        this.postRepository = postRepository;
        this.postByUserRepository = postByUserRepository;
        this.postByTagRepository = postByTagRepository;
        this.eventPublisher = eventPublisher;
    }

    public PostResponse createPost(UUID userId, CreatePostRequest request) {
        // Create and save the main post
        Post post = new Post(
                userId,
                request.getContent(),
                request.getMediaUrls(),
                request.getTags(),
                request.isPublic()
        );

        Post savedPost = postRepository.save(post);

        // Create and save post by user
        PostByUser postByUser = new PostByUser(savedPost);
        postByUserRepository.save(postByUser);

        // Create and save post by tag if tags exist
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            List<PostByTag> postsByTag = request.getTags().stream()
                    .map(tag -> new PostByTag(tag, savedPost))
                    .collect(Collectors.toList());
            postsByTag.forEach(postByTagRepository::save);
        }

        // Publish Kafka event
        eventPublisher.publishPostCreated(savedPost);

        return new PostResponse(savedPost);
    }

    public PostResponse getPostById(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));
        return new PostResponse(post);
    }

    public List<PostResponse> getPostsByUserId(UUID userId, int page, int size) {
        Pageable pageable = CassandraPageRequest.of(page, size);
        Slice<PostByUser> postSlice = postByUserRepository.findByUserId(userId, pageable);

        return postSlice.getContent().stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getPublicPostsByUserId(UUID userId, int page, int size) {
        Pageable pageable = CassandraPageRequest.of(page, size);
        Slice<PostByUser> postSlice = postByUserRepository.findByUserIdAndIsPublicTrue(userId, pageable);

        return postSlice.getContent().stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getPostsByTag(String tag, int page, int size) {
        Pageable pageable = CassandraPageRequest.of(page, size);
        Slice<PostByTag> postSlice = postByTagRepository.findByTagAndIsPublicTrue(tag, pageable);

        // For each PostByTag, we need to get the full post information
        List<PostResponse> responses = new ArrayList<>();
        for (PostByTag postByTag : postSlice.getContent()) {
            Optional<Post> postOpt = postRepository.findById(postByTag.getPostId());
            postOpt.ifPresent(post -> responses.add(new PostResponse(post)));
        }

        return responses;
    }

    public PostResponse updatePost(UUID postId, CreatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        // Update post fields
        post.setContent(request.getContent());
        post.setMediaUrls(request.getMediaUrls());
        post.setTags(request.getTags());
        post.setPublic(request.isPublic());
        post.setUpdatedAt(Instant.now());

        Post updatedPost = postRepository.save(post);

        // Delete old entries in secondary tables
        // (In a real app, you might want to manage this more carefully)
        PostByUser postByUser = new PostByUser(updatedPost);
        postByUserRepository.save(postByUser);

        // Publish Kafka event
        eventPublisher.publishPostUpdated(updatedPost);

        return new PostResponse(updatedPost);
    }

    public void deletePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        // Publish Kafka event before deletion
        eventPublisher.publishPostDeleted(post);

        // Delete from main table
        postRepository.delete(post);

        // In a real application, you'd want to also clean up the secondary tables
        // This would require additional queries or a background process
    }

    public PostResponse incrementLikes(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        post.setLikesCount(post.getLikesCount() + 1);
        Post updatedPost = postRepository.save(post);

        // Also update in PostByUser table
        PostByUser postByUser = new PostByUser(updatedPost);
        postByUserRepository.save(postByUser);

        return new PostResponse(updatedPost);
    }

    public PostResponse incrementComments(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        post.setCommentsCount(post.getCommentsCount() + 1);
        Post updatedPost = postRepository.save(post);

        // Also update in PostByUser table
        PostByUser postByUser = new PostByUser(updatedPost);
        postByUserRepository.save(postByUser);

        return new PostResponse(updatedPost);
    }
}