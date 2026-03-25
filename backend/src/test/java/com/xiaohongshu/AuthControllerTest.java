package com.xiaohongshu;

import com.xiaohongshu.entity.User;
import com.xiaohongshu.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证模块接口测试 - 简化版
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static final String BASE_URL = "/auth";
    private static final String CONTENT_TYPE = "application/json";

    // ==================== 用户注册测试 ====================

    @Test
    void testRegister_Success() throws Exception {
        String uniqueUsername = "newuser_" + System.currentTimeMillis();
        String requestBody = """
            {
                "username": "%s",
                "password": "password123",
                "nickname": "新用户"
            }
            """.formatted(uniqueUsername);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(CONTENT_TYPE)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value(uniqueUsername))
                .andExpect(jsonPath("$.nickname").value("新用户"));
    }

    @Test
    void testRegister_DuplicateUsername() throws Exception {
        // 先注册一个用户
        String uniqueUsername = "dupuser_" + System.currentTimeMillis();
        String requestBody1 = """
            {
                "username": "%s",
                "password": "password123"
            }
            """.formatted(uniqueUsername);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(CONTENT_TYPE)
                        .content(requestBody1))
                .andExpect(status().isOk());

        // 尝试重复注册
        String requestBody2 = """
            {
                "username": "%s",
                "password": "password123"
            }
            """.formatted(uniqueUsername);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(CONTENT_TYPE)
                        .content(requestBody2))
                .andExpect(status().isBadRequest());
    }

    // ==================== 用户登录测试 ====================

    @Test
    void testLogin_Success() throws Exception {
        // 先注册
        String uniqueUsername = "logintest_" + System.currentTimeMillis();
        String registerBody = """
            {
                "username": "%s",
                "password": "password123"
            }
            """.formatted(uniqueUsername);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(CONTENT_TYPE)
                        .content(registerBody))
                .andExpect(status().isOk());

        // 登录
        String loginBody = """
            {
                "username": "%s",
                "password": "password123"
            }
            """.formatted(uniqueUsername);

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(CONTENT_TYPE)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value(uniqueUsername));
    }

    @Test
    void testLogin_WrongPassword() throws Exception {
        // 先注册
        String uniqueUsername = "loginfail_" + System.currentTimeMillis();
        String registerBody = """
            {
                "username": "%s",
                "password": "password123"
            }
            """.formatted(uniqueUsername);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(CONTENT_TYPE)
                        .content(registerBody))
                .andExpect(status().isOk());

        // 错误密码登录
        String loginBody = """
            {
                "username": "%s",
                "password": "wrongpassword"
            }
            """.formatted(uniqueUsername);

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(CONTENT_TYPE)
                        .content(loginBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        String requestBody = """
            {
                "username": "nonexistent123456",
                "password": "password123"
            }
            """;

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(CONTENT_TYPE)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    // ==================== 关注状态测试 ====================

    @Test
    void testGetFollowingStatus_NotFollowing() throws Exception {
        mockMvc.perform(get(BASE_URL + "/following-status")
                        .param("followerId", "1")
                        .param("followingId", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFollowers_Empty() throws Exception {
        mockMvc.perform(get(BASE_URL + "/followers/999"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFollowing_Empty() throws Exception {
        mockMvc.perform(get(BASE_URL + "/following/999"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFollowersCount() throws Exception {
        mockMvc.perform(get(BASE_URL + "/followers-count/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists());
    }

    @Test
    void testGetFollowingCount() throws Exception {
        mockMvc.perform(get(BASE_URL + "/following-count/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists());
    }

    // ==================== 更新用户资料测试 ====================

    @Test
    void testUpdateProfile() throws Exception {
        // 先创建用户
        String uniqueUsername = "updateuser_" + System.currentTimeMillis();
        String registerBody = """
            {
                "username": "%s",
                "password": "password123",
                "nickname": "原昵称"
            }
            """.formatted(uniqueUsername);

        var result = mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(CONTENT_TYPE)
                        .content(registerBody))
                .andExpect(status().isOk())
                .andReturn();

        // 获取用户ID
        String response = result.getResponse().getContentAsString();
        Long userId = extractIdFromResponse(response);

        // 更新资料
        String updateBody = """
            {
                "userId": %d,
                "nickname": "新昵称",
                "bio": "这是我的简介",
                "gender": "男"
            }
            """.formatted(userId);

        mockMvc.perform(put(BASE_URL + "/profile")
                        .contentType(CONTENT_TYPE)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("新昵称"));
    }

    // 辅助方法：从JSON响应中提取ID
    private Long extractIdFromResponse(String response) {
        // 简单的ID提取
        String idKey = "\"id\":";
        int index = response.indexOf(idKey);
        if (index != -1) {
            int start = index + idKey.length();
            int end = response.indexOf(",", start);
            if (end == -1) end = response.indexOf("}", start);
            String idStr = response.substring(start, end).trim();
            return Long.parseLong(idStr);
        }
        return 1L;
    }
}
