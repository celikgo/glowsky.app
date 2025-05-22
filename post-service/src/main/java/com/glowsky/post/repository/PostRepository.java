package com.glowsky.post.repository;

import com.glowsky.post.model.Post;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends CassandraRepository<Post, UUID> {
    Optional<Post> findById(UUID id);
}