package com.xiaohongshu;

import com.xiaohongshu.entity.User;
import com.xiaohongshu.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 帖子模块接口测试 - 简化版
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static final String BASE_URL = "/posts";

    @BeforeEach
    void setUp() {
        // 创建测试用户
        User user = new User();
        user.setUsername("testuser_" + System.currentTimeMillis());
        user.setPassword("password123");
        user.setNickname("测试用户");
        user.setAvatar("/uploads/default_avatar.png");
        userRepository.save(user);
    }

    // ==================== 获取帖子列表测试 ====================

    @Test
    void testGetAllPosts() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetAllPosts_WithTitleSearch() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("title", "测试"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllPosts_WithTagFilter() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("tag", "测试"))
                .andExpect(status().isOk());
    }

    // ==================== 获取帖子详情测试 ====================

    @Test
    void testGetPostById_NotFound() throws Exception {
        // 后端对于不存在的帖子返回400而非404
        mockMvc.perform(get(BASE_URL + "/99999"))
                .andExpect(status().isBadRequest());
    }

    // ==================== 上传文件测试 ====================

    @Test
    void testUploadFile_Image() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        mockMvc.perform(multipart(BASE_URL + "/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").exists());
    }

    @Test
    void testUploadFile_Video() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.mp4",
                "video/mp4",
                "test video content".getBytes()
        );

        mockMvc.perform(multipart(BASE_URL + "/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").exists());
    }

    // ==================== 评论测试 ====================

    @Test
    void testGetComments() throws Exception {
        mockMvc.perform(get(BASE_URL + "/999/comments"))
                .andExpect(status().isOk());
    }
}
