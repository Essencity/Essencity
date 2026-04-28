package com.xiaohongshu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaohongshu.entity.Post;
import com.xiaohongshu.entity.User;
import com.xiaohongshu.repository.PostRepository;
import com.xiaohongshu.service.AIService;
import com.xiaohongshu.service.PostService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AIControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AIService aiService;

    @Mock
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        AIController controller = new AIController();
        ReflectionTestUtils.setField(controller, "aiService", aiService);
        ReflectionTestUtils.setField(controller, "postService", postService);
        ReflectionTestUtils.setField(controller, "postRepository", postRepository);
        ReflectionTestUtils.setField(controller, "entityManager", entityManager);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private Post createTestPost() {
        User author = new User();
        author.setId(1L);
        author.setUsername("author");
        author.setNickname("Author");

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setDescription("Test description content");
        post.setType("image");
        post.setUrl("/test.jpg");
        post.setAuthor(author);
        return post;
    }

    // ==================== getAiSummary ====================

    @Test
    void getAiSummary_ExistingSummary_ShouldReturnIt() throws Exception {
        Post post = createTestPost();
        post.setAiSummary("This is an AI summary");
        when(postService.getPostById(1L)).thenReturn(post);

        mockMvc.perform(get("/ai/summary/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ai_summary").value("This is an AI summary"));
    }

    @Test
    void getAiSummary_NoSummary_ShouldReturnEmptyString() throws Exception {
        Post post = createTestPost();
        post.setAiSummary(null);
        when(postService.getPostById(1L)).thenReturn(post);

        mockMvc.perform(get("/ai/summary/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ai_summary").value(""));
    }

    @Test
    void getAiSummary_PostNotFound_ShouldReturn404() throws Exception {
        when(postService.getPostById(999L)).thenReturn(null);

        mockMvc.perform(get("/ai/summary/999"))
                .andExpect(status().isNotFound());
    }

    // ==================== generateAiSummary ====================

    @Test
    void generateAiSummary_Success() throws Exception {
        Post post = createTestPost();
        when(postService.getPostById(1L)).thenReturn(post);
        when(aiService.generateSummary("Test Post", "Test description content"))
                .thenReturn("Generated AI summary");

        Map<String, Object> request = Map.of(
                "postId", 1,
                "title", "Test Post",
                "content", "Test description content");

        mockMvc.perform(post("/ai/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ai_summary").value("Generated AI summary"));
    }

    @Test
    void generateAiSummary_PostNotFound_ShouldReturn404() throws Exception {
        when(postService.getPostById(999L)).thenReturn(null);

        Map<String, Object> request = Map.of(
                "postId", 999,
                "title", "Test",
                "content", "Content");

        mockMvc.perform(post("/ai/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void generateAiSummary_ServiceThrowsException_ShouldReturn400() throws Exception {
        Post post = createTestPost();
        when(postService.getPostById(1L)).thenReturn(post);
        when(aiService.generateSummary(anyString(), anyString()))
                .thenThrow(new RuntimeException("AI service unavailable"));

        Map<String, Object> request = Map.of(
                "postId", 1,
                "title", "Test Post",
                "content", "Content");

        mockMvc.perform(post("/ai/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
