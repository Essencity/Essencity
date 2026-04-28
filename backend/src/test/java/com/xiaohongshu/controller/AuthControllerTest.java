package com.xiaohongshu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaohongshu.entity.User;
import com.xiaohongshu.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setNickname("Test User");
        user.setAvatar("/default.png");
        user.setBio("Hello");
        user.setGender("male");
        return user;
    }

    // ==================== register ====================

    @Test
    void register_Success() throws Exception {
        User input = new User();
        input.setUsername("newuser");
        input.setPassword("password123");
        input.setNickname("New User");
        input.setAvatar("/custom.png");

        User saved = createTestUser();
        saved.setUsername("newuser");
        saved.setNickname("New User");

        when(userService.register(any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void register_DefaultAvatar_WhenEmpty() throws Exception {
        User input = new User();
        input.setUsername("newuser");
        input.setPassword("password123");
        input.setAvatar("");

        User saved = createTestUser();
        saved.setUsername("newuser");
        saved.setAvatar("http://localhost:3000/uploads/default_avatar.png");

        when(userService.register(any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    void register_DuplicateUsername_ShouldReturn400() throws Exception {
        User input = new User();
        input.setUsername("existing");
        input.setPassword("password123");

        when(userService.register(any(User.class))).thenThrow(new RuntimeException("Username already exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    // ==================== login ====================

    @Test
    void login_Success() throws Exception {
        Map<String, String> loginRequest = Map.of("username", "testuser", "password", "password123");
        User user = createTestUser();
        when(userService.login("testuser", "password123")).thenReturn(user);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void login_WrongPassword_ShouldReturn400() throws Exception {
        Map<String, String> loginRequest = Map.of("username", "testuser", "password", "wrong");
        when(userService.login("testuser", "wrong")).thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void login_UserNotFound_ShouldReturn400() throws Exception {
        Map<String, String> loginRequest = Map.of("username", "nobody", "password", "pass");
        when(userService.login("nobody", "pass")).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    // ==================== updateProfile ====================

    @Test
    void updateProfile_Success() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("userId", 1);
        updates.put("nickname", "Updated Name");
        updates.put("bio", "New bio");

        User existing = createTestUser();
        User updated = createTestUser();
        updated.setNickname("Updated Name");
        updated.setBio("New bio");

        when(userService.getUserById(1L)).thenReturn(existing);
        when(userService.updateUser(any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/auth/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("Updated Name"));
    }

    @Test
    void updateProfile_PartialUpdate() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("userId", 1);
        updates.put("gender", "female");

        User existing = createTestUser();
        User updated = createTestUser();
        updated.setGender("female");

        when(userService.getUserById(1L)).thenReturn(existing);
        when(userService.updateUser(any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/auth/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gender").value("female"));
    }

    @Test
    void updateProfile_UserNotFound_ShouldReturn400() throws Exception {
        Map<String, Object> updates = Map.of("userId", 999);
        when(userService.getUserById(999L)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(put("/auth/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isBadRequest());
    }

    // ==================== follow ====================

    @Test
    void followUser_Success() throws Exception {
        Map<String, Object> body = Map.of("followerId", 1, "followingId", 2);
        when(userService.followUser(1L, 2L)).thenReturn(true);

        mockMvc.perform(post("/auth/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void followUser_SelfFollow_ShouldReturn400() throws Exception {
        Map<String, Object> body = Map.of("followerId", 1, "followingId", 1);
        when(userService.followUser(1L, 1L)).thenThrow(new RuntimeException("Cannot follow yourself"));

        mockMvc.perform(post("/auth/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== unfollow ====================

    @Test
    void unfollowUser_Success() throws Exception {
        Map<String, Object> body = Map.of("followerId", 1, "followingId", 2);
        when(userService.unfollowUser(1L, 2L)).thenReturn(true);

        mockMvc.perform(post("/auth/unfollow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void unfollowUser_Error_ShouldReturn400() throws Exception {
        Map<String, Object> body = Map.of("followerId", 1, "followingId", 2);
        when(userService.unfollowUser(1L, 2L)).thenThrow(new RuntimeException("Not following"));

        mockMvc.perform(post("/auth/unfollow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== following-status ====================

    @Test
    void getFollowingStatus_True() throws Exception {
        when(userService.isFollowing(1L, 2L)).thenReturn(true);

        mockMvc.perform(get("/auth/following-status")
                        .param("followerId", "1")
                        .param("followingId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFollowing").value(true));
    }

    @Test
    void getFollowingStatus_False() throws Exception {
        when(userService.isFollowing(1L, 2L)).thenReturn(false);

        mockMvc.perform(get("/auth/following-status")
                        .param("followerId", "1")
                        .param("followingId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFollowing").value(false));
    }

    @Test
    void getFollowingStatus_Error_ReturnsFalse() throws Exception {
        when(userService.isFollowing(1L, 2L)).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/auth/following-status")
                        .param("followerId", "1")
                        .param("followingId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFollowing").value(false));
    }

    // ==================== followers ====================

    @Test
    void getFollowers_WithResults() throws Exception {
        Map<String, Object> follower = Map.of("id", 2, "nickname", "Follower1");
        when(userService.getFollowers(1L)).thenReturn(Arrays.asList(follower));

        mockMvc.perform(get("/auth/followers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nickname").value("Follower1"));
    }

    @Test
    void getFollowers_Empty() throws Exception {
        when(userService.getFollowers(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/auth/followers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getFollowers_Error_ReturnsEmpty() throws Exception {
        when(userService.getFollowers(1L)).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/auth/followers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ==================== following ====================

    @Test
    void getFollowing_WithResults() throws Exception {
        Map<String, Object> following = Map.of("id", 2, "nickname", "Following1");
        when(userService.getFollowing(1L)).thenReturn(Arrays.asList(following));

        mockMvc.perform(get("/auth/following/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nickname").value("Following1"));
    }

    // ==================== followers-count ====================

    @Test
    void getFollowersCount_ReturnsCount() throws Exception {
        when(userService.getFollowersCount(1L)).thenReturn(42L);

        mockMvc.perform(get("/auth/followers-count/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(42));
    }

    @Test
    void getFollowersCount_Error_ReturnsZero() throws Exception {
        when(userService.getFollowersCount(1L)).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/auth/followers-count/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0));
    }

    // ==================== following-count ====================

    @Test
    void getFollowingCount_ReturnsCount() throws Exception {
        when(userService.getFollowingCount(1L)).thenReturn(10L);

        mockMvc.perform(get("/auth/following-count/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(10));
    }
}
