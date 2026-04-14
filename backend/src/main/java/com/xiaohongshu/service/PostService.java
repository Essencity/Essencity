package com.xiaohongshu.service;

import com.xiaohongshu.entity.Collection;
import com.xiaohongshu.entity.Comment;
import com.xiaohongshu.entity.Like;
import com.xiaohongshu.entity.Post;
import com.xiaohongshu.entity.User;
import com.xiaohongshu.repository.CollectionRepository;
import com.xiaohongshu.repository.CommentRepository;
import com.xiaohongshu.repository.LikeRepository;
import com.xiaohongshu.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CommentRepository commentRepository;

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Long id, Post updatedPost) {
        Post post = getPostById(id);
        if (updatedPost.getTitle() != null) {
            post.setTitle(updatedPost.getTitle());
        }
        if (updatedPost.getDescription() != null) {
            post.setDescription(updatedPost.getDescription());
        }
        if (updatedPost.getType() != null) {
            post.setType(updatedPost.getType());
        }
        if (updatedPost.getUrl() != null) {
            post.setUrl(updatedPost.getUrl());
        }
        if (updatedPost.getCoverUrl() != null) {
            post.setCoverUrl(updatedPost.getCoverUrl());
        }
        if (updatedPost.getTag() != null) {
            post.setTag(updatedPost.getTag());
        }
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> searchPosts(String keyword) {
        return postRepository.findByTitleContainingOrderByCreatedAtDesc(keyword);
    }

    public List<Post> getPostsByTag(String tag) {
        return postRepository.findByTagOrderByCreatedAtDesc(tag);
    }

    public List<Post> searchPostsByTag(String tag, String keyword) {
        return postRepository.findByTagAndTitleContainingOrderByCreatedAtDesc(tag, keyword);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = getPostById(id);
        // 删除关联的评论、点赞和收藏
        commentRepository.deleteAll(commentRepository.findByPostOrderByCreatedAtDesc(post));
        likeRepository.deleteAll(likeRepository.findByPost(post));
        collectionRepository.deleteAll(collectionRepository.findByPost(post));
        postRepository.delete(post);
    }

    public List<Post> getPostsByAuthor(User author) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    // Like methods
    @Transactional
    public void toggleLike(User user, Long postId) {
        Post post = getPostById(postId);
        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
        }
    }

    @Transactional
    public void likePost(User user, Long postId) {
        Post post = getPostById(postId);
        if (!likeRepository.existsByUserAndPost(user, post)) {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
        }
    }

    @Transactional
    public void unlikePost(User user, Long postId) {
        Post post = getPostById(postId);
        likeRepository.findByUserAndPost(user, post).ifPresent(likeRepository::delete);
    }

    // Collection methods
    @Transactional
    public void toggleCollection(User user, Long postId) {
        Post post = getPostById(postId);
        Optional<Collection> existingCollection = collectionRepository.findByUserAndPost(user, post);
        if (existingCollection.isPresent()) {
            collectionRepository.delete(existingCollection.get());
        } else {
            Collection collection = new Collection();
            collection.setUser(user);
            collection.setPost(post);
            collectionRepository.save(collection);
        }
    }

    @Transactional
    public void collectPost(User user, Long postId) {
        Post post = getPostById(postId);
        if (!collectionRepository.existsByUserAndPost(user, post)) {
            Collection collection = new Collection();
            collection.setUser(user);
            collection.setPost(post);
            collectionRepository.save(collection);
        }
    }

    @Transactional
    public void uncollectPost(User user, Long postId) {
        Post post = getPostById(postId);
        collectionRepository.findByUserAndPost(user, post).ifPresent(collectionRepository::delete);
    }

    public long getLikeCount(Long postId) {
        Post post = getPostById(postId);
        return likeRepository.countByPost(post);
    }

    public boolean isLikedBy(User user, Long postId) {
        Post post = getPostById(postId);
        return likeRepository.existsByUserAndPost(user, post);
    }

    public long getCollectionCount(Long postId) {
        Post post = getPostById(postId);
        return collectionRepository.countByPost(post);
    }

    public boolean isCollectedBy(User user, Long postId) {
        Post post = getPostById(postId);
        return collectionRepository.existsByUserAndPost(user, post);
    }

    // Comment methods
    public List<Map<String, Object>> getCommentsByPostId(Long postId) {
        Post post = getPostById(postId);
        List<Comment> topLevelComments = commentRepository.findByPostAndParentIdIsNullOrderByCreatedAtDesc(post);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Comment comment : topLevelComments) {
            Map<String, Object> commentMap = commentToMap(comment);
            List<Comment> subComments = commentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId());
            List<Map<String, Object>> subCommentList = new ArrayList<>();
            for (Comment sub : subComments) {
                subCommentList.add(commentToMap(sub));
            }
            commentMap.put("subComments", subCommentList);
            result.add(commentMap);
        }
        return result;
    }

    private Map<String, Object> commentToMap(Comment comment) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", comment.getId());
        map.put("post_id", comment.getPost().getId());
        map.put("user_id", comment.getUser().getId());
        map.put("content", comment.getContent());
        map.put("parent_id", comment.getParentId());
        map.put("created_at", comment.getCreatedAt());

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", comment.getUser().getId());
        userMap.put("nickname", comment.getUser().getNickname());
        userMap.put("avatar", comment.getUser().getAvatar());
        map.put("user", userMap);

        if (comment.getReplyToUser() != null) {
            Map<String, Object> replyToUserMap = new HashMap<>();
            replyToUserMap.put("id", comment.getReplyToUser().getId());
            replyToUserMap.put("nickname", comment.getReplyToUser().getNickname());
            map.put("replyToUser", replyToUserMap);
        }

        return map;
    }

    @Transactional
    public Comment createComment(Long postId, User user, String content, Long parentId) {
        Post post = getPostById(postId);
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);

        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));

            // Set replyToUser and replyToCommentId
            comment.setReplyToUser(parent.getUser());
            comment.setReplyToCommentId(parent.getId());

            // Flattening: If parent has a parentId, it means it's a sub-comment.
            // We want to attach the new comment to the ROOT parent.
            if (parent.getParentId() != null) {
                comment.setParentId(parent.getParentId());
            } else {
                comment.setParentId(parentId);
            }
        } else {
            comment.setParentId(null);
        }
        return commentRepository.save(comment);
    }

    @Transactional
    public boolean deleteComment(Long commentId, Long userId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isPresent() && commentOpt.get().getUser().getId().equals(userId)) {
            commentRepository.delete(commentOpt.get());
            return true;
        }
        return false;
    }

    public long getCommentCount(Long postId) {
        Post post = getPostById(postId);
        return commentRepository.countByPost(post);
    }

    // User stats methods
    public Map<String, Long> getUserStats(Long userId, User user) {
        List<Post> userPosts = postRepository.findByAuthorOrderByCreatedAtDesc(user);
        long totalLikes = 0;
        long totalCollections = 0;
        for (Post post : userPosts) {
            totalLikes += likeRepository.countByPost(post);
            totalCollections += collectionRepository.countByPost(post);
        }
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalLikes", totalLikes);
        stats.put("totalCollections", totalCollections);
        return stats;
    }

    public List<Map<String, Object>> getUserCollections(Long userId, User user) {
        List<Collection> collections = collectionRepository.findByUser(user);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Collection c : collections) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", c.getId());
            item.put("post_id", c.getPost().getId());

            Map<String, Object> postMap = new HashMap<>();
            Post post = c.getPost();
            postMap.put("id", post.getId());
            postMap.put("title", post.getTitle());
            postMap.put("type", post.getType());
            postMap.put("url", post.getUrl());
        postMap.put("imageUrl", post.getUrl());
        postMap.put("coverUrl", post.getCoverUrl());
            postMap.put("author", post.getAuthor().getNickname());
            postMap.put("author_avatar", post.getAuthor().getAvatar());
            item.put("post", postMap);

            result.add(item);
        }
        return result;
    }

    public List<Map<String, Object>> getUserLikedPosts(Long userId, User user) {
        List<Like> likes = likeRepository.findByUser(user);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Like l : likes) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", l.getId());
            item.put("post_id", l.getPost().getId());

            Map<String, Object> postMap = new HashMap<>();
            Post post = l.getPost();
            postMap.put("id", post.getId());
            postMap.put("title", post.getTitle());
            postMap.put("type", post.getType());
            postMap.put("url", post.getUrl());
        postMap.put("imageUrl", post.getUrl());
        postMap.put("coverUrl", post.getCoverUrl());
            postMap.put("author", post.getAuthor().getNickname());
            postMap.put("author_avatar", post.getAuthor().getAvatar());
            item.put("post", postMap);

            result.add(item);
        }
        return result;
    }
}
