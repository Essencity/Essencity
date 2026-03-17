package com.xiaohongshu.service;

import com.xiaohongshu.dto.NotificationDTO;
import com.xiaohongshu.entity.Collection;
import com.xiaohongshu.entity.Comment;
import com.xiaohongshu.entity.Follow;
import com.xiaohongshu.entity.Like;
import com.xiaohongshu.entity.Post;
import com.xiaohongshu.entity.User;
import com.xiaohongshu.repository.CollectionRepository;
import com.xiaohongshu.repository.CommentRepository;
import com.xiaohongshu.repository.FollowRepository;
import com.xiaohongshu.repository.LikeRepository;
import com.xiaohongshu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    // 将 User 实体转换为简单 Map
    private Map<String, Object> userToMap(User user) {
        if (user == null)
            return null;
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("nickname", user.getNickname());
        map.put("avatar", user.getAvatar());
        map.put("username", user.getUsername());
        return map;
    }

    // 将 Post 实体转换为简单 Map
    private Map<String, Object> postToMap(Post post) {
        if (post == null)
            return null;
        Map<String, Object> map = new HashMap<>();
        map.put("id", post.getId());
        map.put("title", post.getTitle());
        map.put("url", post.getUrl());
        map.put("coverUrl", post.getCoverUrl());
        map.put("type", post.getType());
        return map;
    }

    public List<NotificationDTO> getNotifications(Long userId, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<NotificationDTO> notifications = new ArrayList<>();

        if ("likes".equals(type)) {
            // Fetch Likes
            List<Like> likes = likeRepository.findByPostAuthorOrderByCreatedAtDesc(user);
            for (Like like : likes) {
                // Filter out self-likes
                if (!like.getUser().getId().equals(userId)) {
                    notifications.add(new NotificationDTO(
                            like.getId(),
                            "LIKE",
                            userToMap(like.getUser()),
                            postToMap(like.getPost()),
                            null,
                            like.getCreatedAt()));
                }
            }

            // Fetch Collections
            List<Collection> collections = collectionRepository.findByPostAuthorOrderByCreatedAtDesc(user);
            for (Collection collection : collections) {
                if (!collection.getUser().getId().equals(userId)) {
                    notifications.add(new NotificationDTO(
                            collection.getId(),
                            "COLLECTION",
                            userToMap(collection.getUser()),
                            postToMap(collection.getPost()),
                            null,
                            collection.getCreatedAt()));
                }
            }
        } else if ("comments".equals(type)) {
            // Fetch Comments on user's posts
            List<Comment> comments = commentRepository.findByPostAuthorOrderByCreatedAtDesc(user);
            for (Comment comment : comments) {
                if (!comment.getUser().getId().equals(userId)) {
                    notifications.add(new NotificationDTO(
                            comment.getId(),
                            "COMMENT",
                            userToMap(comment.getUser()),
                            postToMap(comment.getPost()),
                            comment.getContent(),
                            comment.getCreatedAt()));
                }
            }

            // Fetch Replies to user's comments
            // Fetch Replies to user's comments (Old way - keeping for backward
            // compatibility)
            List<Comment> replies = commentRepository.findRepliesOfUserComments(user);

            // Fetch targeted replies (New way - using replyToUser field)
            List<Comment> targetedReplies = commentRepository.findByReplyToUserOrderByCreatedAtDesc(user);

            // Merge and deduplicate
            List<Comment> allReplies = new ArrayList<>(replies);
            allReplies.addAll(targetedReplies);

            java.util.Set<Long> processedReplyIds = new java.util.HashSet<>();

            for (Comment reply : allReplies) {
                if (!processedReplyIds.contains(reply.getId()) && !reply.getUser().getId().equals(userId)) {
                    notifications.add(new NotificationDTO(
                            reply.getId(),
                            "REPLY",
                            userToMap(reply.getUser()),
                            postToMap(reply.getPost()),
                            reply.getContent(),
                            reply.getCreatedAt()));
                    processedReplyIds.add(reply.getId());
                }
            }
        } else if ("follows".equals(type)) {
            // Fetch Followers
            List<Follow> follows = followRepository.findByFollowingOrderByCreatedAtDesc(user);
            for (Follow follow : follows) {
                notifications.add(new NotificationDTO(
                        follow.getId(),
                        "FOLLOW",
                        userToMap(follow.getFollower()),
                        null,
                        null,
                        follow.getCreatedAt()));
            }
        }

        // Sort combined results by createdAt desc
        return notifications.stream()
                .sorted(Comparator.comparing(NotificationDTO::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }
}
