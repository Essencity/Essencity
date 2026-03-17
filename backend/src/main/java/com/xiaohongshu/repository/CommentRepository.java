package com.xiaohongshu.repository;

import com.xiaohongshu.entity.Comment;
import com.xiaohongshu.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    List<Comment> findByPostAndParentIdIsNullOrderByCreatedAtDesc(Post post);

    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    List<Comment> findByPostAuthorOrderByCreatedAtDesc(com.xiaohongshu.entity.User author);

    long countByPost(Post post);

    // 查询回复某用户评论的子评论（用于通知）
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Comment c WHERE c.parentId IN (SELECT pc.id FROM Comment pc WHERE pc.user = :user) ORDER BY c.createdAt DESC")
    List<Comment> findRepliesOfUserComments(
            @org.springframework.data.repository.query.Param("user") com.xiaohongshu.entity.User user);

    List<Comment> findByReplyToUserOrderByCreatedAtDesc(com.xiaohongshu.entity.User replyToUser);
}
