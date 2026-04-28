package com.xiaohongshu.service;

import com.xiaohongshu.dto.NotificationDTO;
import com.xiaohongshu.entity.*;
import com.xiaohongshu.repository.CollectionRepository;
import com.xiaohongshu.repository.CommentRepository;
import com.xiaohongshu.repository.FollowRepository;
import com.xiaohongshu.repository.LikeRepository;
import com.xiaohongshu.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * NotificationService 单元测试 - 使用 Mock 隔离数据库
 */
@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private User otherUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setNickname("Test User");
        testUser.setAvatar("/avatar.png");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otheruser");
        otherUser.setNickname("Other User");
        otherUser.setAvatar("/avatar2.png");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setType("image");
        testPost.setUrl("/post.jpg");
        testPost.setAuthor(testUser);
    }

    // ==================== getNotifications (likes) 测试 ====================

    @Test
    void testGetNotifications_Likes() {
        Like like = new Like();
        like.setId(1L);
        like.setUser(otherUser);
        like.setPost(testPost);
        like.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(likeRepository.findByPostAuthorOrderByCreatedAtDesc(testUser)).thenReturn(Arrays.asList(like));
        when(collectionRepository.findByPostAuthorOrderByCreatedAtDesc(testUser)).thenReturn(Arrays.asList());

        List<NotificationDTO> result = notificationService.getNotifications(1L, "likes");

        assertFalse(result.isEmpty());
        assertEquals("LIKE", result.get(0).getType());
    }

    @Test
    void testGetNotifications_Likes_ExcludesSelfLikes() {
        Like selfLike = new Like();
        selfLike.setId(1L);
        selfLike.setUser(testUser); // 自己点赞自己
        selfLike.setPost(testPost);
        selfLike.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(likeRepository.findByPostAuthorOrderByCreatedAtDesc(testUser)).thenReturn(Arrays.asList(selfLike));
        when(collectionRepository.findByPostAuthorOrderByCreatedAtDesc(testUser)).thenReturn(Arrays.asList());

        List<NotificationDTO> result = notificationService.getNotifications(1L, "likes");

        assertTrue(result.isEmpty()); // 自己的点赞被排除
    }

    // ==================== getNotifications (comments) 测试 ====================

    @Test
    void testGetNotifications_Comments() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUser(otherUser);
        comment.setPost(testPost);
        comment.setContent("Great post!");
        comment.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(commentRepository.findByPostAuthorOrderByCreatedAtDesc(testUser)).thenReturn(Arrays.asList(comment));
        when(commentRepository.findRepliesOfUserComments(testUser)).thenReturn(Arrays.asList());
        when(commentRepository.findByReplyToUserOrderByCreatedAtDesc(testUser)).thenReturn(Arrays.asList());

        List<NotificationDTO> result = notificationService.getNotifications(1L, "comments");

        assertFalse(result.isEmpty());
        assertEquals("COMMENT", result.get(0).getType());
    }

    // ==================== getNotifications (follows) 测试 ====================

    @Test
    void testGetNotifications_Follows() {
        Follow follow = new Follow();
        follow.setId(1L);
        follow.setFollower(otherUser);
        follow.setFollowing(testUser);
        follow.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(followRepository.findByFollowingOrderByCreatedAtDesc(testUser)).thenReturn(Arrays.asList(follow));

        List<NotificationDTO> result = notificationService.getNotifications(1L, "follows");

        assertFalse(result.isEmpty());
        assertEquals("FOLLOW", result.get(0).getType());
    }

    // ==================== getNotifications 异常测试 ====================

    @Test
    void testGetNotifications_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> notificationService.getNotifications(999L, "likes"));
    }
}