package com.glowsky.post.controller;

import com.glowsky.post.dto.CreatePostRequest;
import com.glowsky.post.dto.PostResponse;
import com.glowsky.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestHeader("X-User-Id") String userIdStr,
            @Valid @RequestBody CreatePostRequest request) {
        UUID userId = UUID.fromString(userIdStr);
        PostResponse response = postService.createPost(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable String postId) {
        UUID postUuid = UUID.fromString(postId);
        PostResponse response = postService.getPostById(postUuid);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UUID userUuid = UUID.fromString(userId);
        List<PostResponse> posts = postService.getPostsByUserId(userUuid, page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/public/user/{userId}")
    public ResponseEntity<List<PostResponse>> getPublicPostsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UUID userUuid = UUID.fromString(userId);
        List<PostResponse> posts = postService.getPublicPostsByUserId(userUuid, page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<PostResponse>> getPostsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PostResponse> posts = postService.getPostsByTag(tag, page, size);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable String postId,
            @Valid @RequestBody CreatePostRequest request) {
        UUID postUuid = UUID.fromString(postId);
        PostResponse response = postService.updatePost(postUuid, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable String postId) {
        UUID postUuid = UUID.fromString(postId);
        postService.deletePost(postUuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostResponse> likePost(@PathVariable String postId) {
        UUID postUuid = UUID.fromString(postId);
        PostResponse response = postService.incrementLikes(postUuid);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<PostResponse> commentPost(@PathVariable String postId) {
        UUID postUuid = UUID.fromString(postId);
        PostResponse response = postService.incrementComments(postUuid);
        return ResponseEntity.ok(response);
    }
}