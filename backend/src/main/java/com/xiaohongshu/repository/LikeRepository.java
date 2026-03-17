package com.xiaohongshu.repository;

import com.xiaohongshu.entity.Like;
import com.xiaohongshu.entity.Post;
import com.xiaohongshu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);

    long countByPost(Post post);

    boolean existsByUserAndPost(User user, Post post);

    java.util.List<Like> findByPost(Post post);

    java.util.List<Like> findByUser(User user);

    java.util.List<Like> findByPostAuthorOrderByCreatedAtDesc(User author);
}
