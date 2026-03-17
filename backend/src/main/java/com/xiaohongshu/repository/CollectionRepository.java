package com.xiaohongshu.repository;

import com.xiaohongshu.entity.Collection;
import com.xiaohongshu.entity.Post;
import com.xiaohongshu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    Optional<Collection> findByUserAndPost(User user, Post post);

    long countByPost(Post post);

    boolean existsByUserAndPost(User user, Post post);

    List<Collection> findByUser(User user);

    List<Collection> findByPost(Post post);

    List<Collection> findByPostAuthorOrderByCreatedAtDesc(User author);
}
