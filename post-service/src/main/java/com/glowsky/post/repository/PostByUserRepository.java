package com.glowsky.post.repository;

import com.glowsky.post.model.PostByUser;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostByUserRepository extends CassandraRepository<PostByUser, UUID> {
    Slice<PostByUser> findByUserId(UUID userId, Pageable pageable);
    Slice<PostByUser> findByUserIdAndIsPublicTrue(UUID userId, Pageable pageable);
}