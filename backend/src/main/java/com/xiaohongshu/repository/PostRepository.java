package com.xiaohongshu.repository;

import com.xiaohongshu.entity.Post;
import com.xiaohongshu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByAuthorOrderByCreatedAtDesc(User author);

    List<Post> findByTitleContainingOrderByCreatedAtDesc(String title);

    List<Post> findByTagOrderByCreatedAtDesc(String tag);

    List<Post> findByTagAndTitleContainingOrderByCreatedAtDesc(String tag, String title);
}
