package com.xiaohongshu.service;

import com.xiaohongshu.entity.Follow;
import com.xiaohongshu.entity.User;
import com.xiaohongshu.repository.FollowRepository;
import com.xiaohongshu.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试 - 使用 Mock 隔离数据库
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private UserService userService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User createTestUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setNickname("Test User");
        user.setAvatar("/avatar.png");
        user.setBio("Test bio");
        user.setGender("男");
        return user;
    }

    // ==================== register 测试 ====================

    @Test
    void testRegister_Success() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setNickname("New User");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        User result = userService.register(newUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("newuser", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_DuplicateUsername() {
        User existingUser = createTestUser(1L, "existinguser");

        User newUser = new User();
        newUser.setUsername("existinguser");
        newUser.setPassword("password123");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        assertThrows(RuntimeException.class, () -> userService.register(newUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_WithEmptyNickname_SetsDefaultNickname() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setNickname("");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        User result = userService.register(newUser);

        assertEquals("newuser", result.getNickname()); // 默认使用 username
    }

    // ==================== login 测试 ====================

    @Test
    void testLogin_Success() {
        User user = createTestUser(1L, "testuser");
        // 密码已经是 BCrypt 加密的
        user.setPassword(passwordEncoder.encode("password123"));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.login("testuser", "password123");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.login("nonexistent", "password123"));
    }

    @Test
    void testLogin_WrongPassword() {
        User user = createTestUser(1L, "testuser");
        user.setPassword(passwordEncoder.encode("correctpassword"));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> userService.login("testuser", "wrongpassword"));
    }

    // ==================== getUserById 测试 ====================

    @Test
    void testGetUserById_Success() {
        User user = createTestUser(1L, "testuser");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(999L));
    }

    // ==================== updateUser 测试 ====================

    @Test
    void testUpdateUser_Success() {
        User user = createTestUser(1L, "testuser");
        user.setNickname("Updated Nickname");

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(user);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUser_NotFound() {
        User user = createTestUser(999L, "nonexistent");

        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.updateUser(user));
    }

    // ==================== followUser 测试 ====================

    @Test
    void testFollowUser_Success() {
        User follower = createTestUser(1L, "follower");
        User following = createTestUser(2L, "following");

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(false);
        when(followRepository.save(any())).thenReturn(new Follow());

        boolean result = userService.followUser(1L, 2L);

        assertTrue(result);
        verify(followRepository).save(any());
    }

    @Test
    void testFollowUser_SelfFollow() {
        boolean result = userService.followUser(1L, 1L);

        assertFalse(result);
        verify(followRepository, never()).save(any());
    }

    @Test
    void testFollowUser_AlreadyFollowing() {
        User follower = createTestUser(1L, "follower");
        User following = createTestUser(2L, "following");

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(true);

        boolean result = userService.followUser(1L, 2L);

        assertFalse(result);
        verify(followRepository, never()).save(any());
    }

    @Test
    void testFollowUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.followUser(1L, 2L));
    }

    // ==================== unfollowUser 测试 ====================

    @Test
    void testUnfollowUser_Success() {
        User follower = createTestUser(1L, "follower");
        User following = createTestUser(2L, "following");
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.of(follow));

        boolean result = userService.unfollowUser(1L, 2L);

        assertTrue(result);
        verify(followRepository).delete(follow);
    }

    @Test
    void testUnfollowUser_NotFollowing() {
        User follower = createTestUser(1L, "follower");
        User following = createTestUser(2L, "following");

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.empty());

        boolean result = userService.unfollowUser(1L, 2L);

        assertFalse(result);
        verify(followRepository, never()).delete(any());
    }

    // ==================== isFollowing 测试 ====================

    @Test
    void testIsFollowing_True() {
        User follower = createTestUser(1L, "follower");
        User following = createTestUser(2L, "following");

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(true);

        boolean result = userService.isFollowing(1L, 2L);

        assertTrue(result);
    }

    @Test
    void testIsFollowing_False() {
        User follower = createTestUser(1L, "follower");
        User following = createTestUser(2L, "following");

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(false);

        boolean result = userService.isFollowing(1L, 2L);

        assertFalse(result);
    }

    // ==================== getFollowers 测试 ====================

    @Test
    void testGetFollowers() {
        User user = createTestUser(1L, "user");
        User follower1 = createTestUser(2L, "follower1");
        User follower2 = createTestUser(3L, "follower2");

        Follow follow1 = new Follow();
        follow1.setFollower(follower1);
        follow1.setFollowing(user);

        Follow follow2 = new Follow();
        follow2.setFollower(follower2);
        follow2.setFollowing(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(followRepository.findByFollowingOrderByCreatedAtDesc(user)).thenReturn(Arrays.asList(follow1, follow2));

        List<Map<String, Object>> result = userService.getFollowers(1L);

        assertEquals(2, result.size());
    }

    @Test
    void testGetFollowers_Empty() {
        User user = createTestUser(1L, "user");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(followRepository.findByFollowingOrderByCreatedAtDesc(user)).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = userService.getFollowers(1L);

        assertTrue(result.isEmpty());
    }

    // ==================== getFollowing 测试 ====================

    @Test
    void testGetFollowing() {
        User user = createTestUser(1L, "user");
        User following1 = createTestUser(2L, "following1");
        User following2 = createTestUser(3L, "following2");

        Follow follow1 = new Follow();
        follow1.setFollower(user);
        follow1.setFollowing(following1);

        Follow follow2 = new Follow();
        follow2.setFollower(user);
        follow2.setFollowing(following2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(followRepository.findByFollower(user)).thenReturn(Arrays.asList(follow1, follow2));

        List<Map<String, Object>> result = userService.getFollowing(1L);

        assertEquals(2, result.size());
    }

    @Test
    void testGetFollowing_Empty() {
        User user = createTestUser(1L, "user");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(followRepository.findByFollower(user)).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = userService.getFollowing(1L);

        assertTrue(result.isEmpty());
    }

    // ==================== getFollowersCount 测试 ====================

    @Test
    void testGetFollowersCount() {
        User user = createTestUser(1L, "user");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(followRepository.countByFollowing(user)).thenReturn(10L);

        long result = userService.getFollowersCount(1L);

        assertEquals(10L, result);
    }

    @Test
    void testGetFollowersCount_Zero() {
        User user = createTestUser(1L, "user");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(followRepository.countByFollowing(user)).thenReturn(0L);

        long result = userService.getFollowersCount(1L);

        assertEquals(0L, result);
    }

    // ==================== getFollowingCount 测试 ====================

    @Test
    void testGetFollowingCount() {
        User user = createTestUser(1L, "user");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(followRepository.countByFollower(user)).thenReturn(5L);

        long result = userService.getFollowingCount(1L);

        assertEquals(5L, result);
    }

    @Test
    void testGetFollowingCount_Zero() {
        User user = createTestUser(1L, "user");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(followRepository.countByFollower(user)).thenReturn(0L);

        long result = userService.getFollowingCount(1L);

        assertEquals(0L, result);
    }

    // ==================== userToMap 测试 ====================

    @Test
    void testUserToMap() {
        User user = createTestUser(1L, "testuser");
        User follower = createTestUser(2L, "follower");

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(user);
        follow.setCreatedAt(LocalDateTime.now());

        // 通过调用其他方法间接测试 userToMap
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(followRepository.findByFollowingOrderByCreatedAtDesc(user)).thenReturn(Arrays.asList(follow));

        List<Map<String, Object>> followers = userService.getFollowers(1L);

        assertEquals(1, followers.size());
        Map<String, Object> userMap = followers.get(0);
        assertEquals(2L, userMap.get("id"));
        assertEquals("follower", userMap.get("username"));
        assertEquals("Test User", userMap.get("nickname"));
    }
}