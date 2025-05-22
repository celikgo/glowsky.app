package com.glowsky.post.repository;

import com.glowsky.post.model.PostByTag;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface PostByTagRepository extends CassandraRepository<PostByTag, String> {
    Slice<PostByTag> findByTagAndIsPublicTrue(String tag, Pageable pageable);
}