package com.xiaohongshu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaohongshu.config.JwtAuthenticationFilter;
import com.xiaohongshu.config.JwtUtil;
import com.xiaohongshu.entity.Comment;
import com.xiaohongshu.entity.Post;
import com.xiaohongshu.entity.User;
import com.xiaohongshu.service.PostService;
import com.xiaohongshu.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final Path UPLOAD_DIR = Paths.get("./test-uploads").toAbsolutePath().normalize();

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() throws Exception {
        Files.createDirectories(UPLOAD_DIR);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setNickname("Test User");
        testUser.setAvatar("/avatar.png");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setDescription("Test description");
        testPost.setType("image");
        testPost.setUrl("/test.jpg");
        testPost.setCoverUrl("/cover.jpg");
        testPost.setTag("美食");
        testPost.setAuthor(testUser);
        testPost.setCreatedAt(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() throws Exception {
        if (Files.exists(UPLOAD_DIR)) {
            Files.list(UPLOAD_DIR).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (Exception ignored) {
                }
            });
        }
    }

    // ==================== getAllPosts ====================

    @Test
    void getAllPosts_NoParams_ShouldReturnAll() throws Exception {
        when(postService.getAllPosts()).thenReturn(Arrays.asList(testPost));
        when(postService.getLikeCount(1L)).thenReturn(5L);
        when(postService.getCollectionCount(1L)).thenReturn(3L);

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Post"))
                .andExpect(jsonPath("$[0].likeCount").value(5))
                .andExpect(jsonPath("$[0].collectionCount").value(3));
    }

    @Test
    void getAllPosts_ByTag_ShouldFilterByTag() throws Exception {
        when(postService.getPostsByTag("美食")).thenReturn(Arrays.asList(testPost));
        when(postService.getLikeCount(1L)).thenReturn(5L);
        when(postService.getCollectionCount(1L)).thenReturn(3L);

        mockMvc.perform(get("/posts").param("tag", "美食"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tag").value("美食"));
    }

    @Test
    void getAllPosts_ByTitle_ShouldSearch() throws Exception {
        when(postService.searchPosts("Test")).thenReturn(Arrays.asList(testPost));
        when(postService.getLikeCount(1L)).thenReturn(5L);
        when(postService.getCollectionCount(1L)).thenReturn(3L);

        mockMvc.perform(get("/posts").param("title", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Post"));
    }

    @Test
    void getAllPosts_ByTagAndTitle_ShouldSearchByBoth() throws Exception {
        when(postService.searchPostsByTag("美食", "Test")).thenReturn(Arrays.asList(testPost));
        when(postService.getLikeCount(1L)).thenReturn(2L);
        when(postService.getCollectionCount(1L)).thenReturn(1L);

        mockMvc.perform(get("/posts")
                        .param("tag", "美食")
                        .param("title", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Post"));
    }

    @Test
    void getAllPosts_RecommendedTag_ShouldReturnAll() throws Exception {
        when(postService.getAllPosts()).thenReturn(Arrays.asList(testPost));
        when(postService.getLikeCount(1L)).thenReturn(0L);
        when(postService.getCollectionCount(1L)).thenReturn(0L);

        mockMvc.perform(get("/posts").param("tag", "推荐"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Post"));

        verify(postService, never()).getPostsByTag(anyString());
        verify(postService).getAllPosts();
    }

    @Test
    void getAllPosts_Empty_ShouldReturnEmptyList() throws Exception {
        when(postService.getAllPosts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllPosts_VideoType_ShouldSetImageAndVideoUrls() throws Exception {
        Post videoPost = new Post();
        videoPost.setId(2L);
        videoPost.setTitle("Video Post");
        videoPost.setDescription("Video desc");
        videoPost.setType("video");
        videoPost.setUrl("/video.mp4");
        videoPost.setCoverUrl("/video-cover.jpg");
        videoPost.setAuthor(testUser);
        videoPost.setCreatedAt(LocalDateTime.now());

        when(postService.getAllPosts()).thenReturn(Arrays.asList(videoPost));
        when(postService.getLikeCount(2L)).thenReturn(0L);
        when(postService.getCollectionCount(2L)).thenReturn(0L);

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imageUrl").value("/video-cover.jpg"))
                .andExpect(jsonPath("$[0].videoUrl").value("/video.mp4"));
    }

    // ==================== getPostById ====================

    @Test
    void getPostById_Success() throws Exception {
        when(postService.getPostById(1L)).thenReturn(testPost);
        when(postService.getLikeCount(1L)).thenReturn(10L);
        when(postService.getCollectionCount(1L)).thenReturn(5L);

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.likeCount").value(10))
                .andExpect(jsonPath("$.collectionCount").value(5));
    }

    @Test
    void getPostById_NotFound_ShouldReturn404() throws Exception {
        when(postService.getPostById(999L)).thenReturn(null);

        mockMvc.perform(get("/posts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPostById_Exception_ShouldReturn400() throws Exception {
        when(postService.getPostById(1L)).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isBadRequest());
    }

    // ==================== deletePost ====================

    @Test
    void deletePost_Success() throws Exception {
        doNothing().when(postService).deletePost(1L);

        mockMvc.perform(delete("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deletePost_Error_ShouldReturn400() throws Exception {
        doThrow(new RuntimeException("Post not found")).when(postService).deletePost(999L);

        mockMvc.perform(delete("/posts/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== createPost ====================

    @Test
    void createPost_Success() throws Exception {
        Map<String, Object> postData = new HashMap<>();
        postData.put("title", "New Post");
        postData.put("description", "New desc");
        postData.put("type", "image");
        postData.put("url", "/new.jpg");
        postData.put("cover_url", "/new-cover.jpg");
        postData.put("tag", "旅行");
        postData.put("author_id", 1);

        when(userService.getUserById(1L)).thenReturn(testUser);
        when(postService.createPost(any(Post.class))).thenReturn(testPost);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Post"));
    }

    @Test
    void createPost_Error_ShouldReturn400() throws Exception {
        Map<String, Object> postData = Map.of(
                "title", "New Post",
                "description", "desc",
                "type", "image",
                "url", "/new.jpg",
                "author_id", 1);

        when(userService.getUserById(1L)).thenReturn(testUser);
        when(postService.createPost(any(Post.class))).thenThrow(new RuntimeException("Create failed"));

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postData)))
                .andExpect(status().isBadRequest());
    }

    // ==================== updatePost ====================

    @Test
    void updatePost_Success() throws Exception {
        Map<String, Object> postData = new HashMap<>();
        postData.put("title", "Updated Title");
        postData.put("description", "Updated desc");
        postData.put("type", "image");
        postData.put("url", "/updated.jpg");
        postData.put("cover_url", "/updated-cover.jpg");
        postData.put("tag", "美食");

        Post updated = testPost;
        updated.setTitle("Updated Title");
        when(postService.updatePost(eq(1L), any(Post.class))).thenReturn(updated);

        mockMvc.perform(put("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void updatePost_Error_ShouldReturn400() throws Exception {
        Map<String, Object> postData = Map.of(
                "title", "Updated",
                "description", "desc",
                "type", "image",
                "url", "/updated.jpg");

        when(postService.updatePost(eq(1L), any(Post.class)))
                .thenThrow(new RuntimeException("Post not found"));

        mockMvc.perform(put("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postData)))
                .andExpect(status().isBadRequest());
    }

    // ==================== uploadFile ====================

    @Test
    void uploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, "image data".getBytes());

        mockMvc.perform(multipart("/posts/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(org.hamcrest.Matchers.startsWith("/uploads/")));
    }

    // ==================== like status & toggle ====================

    @Test
    void getLikeStatus_Liked() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(postService.isLikedBy(testUser, 1L)).thenReturn(true);
        when(postService.getLikeCount(1L)).thenReturn(10L);

        mockMvc.perform(get("/posts/1/like/status").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").value(10));
    }

    @Test
    void getLikeStatus_NotLiked() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(postService.isLikedBy(testUser, 1L)).thenReturn(false);
        when(postService.getLikeCount(1L)).thenReturn(0L);

        mockMvc.perform(get("/posts/1/like/status").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(false));
    }

    @Test
    void likePost_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(postService.isLikedBy(testUser, 1L)).thenReturn(true);
        when(postService.getLikeCount(1L)).thenReturn(1L);

        mockMvc.perform(post("/posts/1/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").value(1));
    }

    @Test
    void likePost_Error_ShouldReturn500() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        doThrow(new RuntimeException("Like failed")).when(postService).likePost(testUser, 1L);

        mockMvc.perform(post("/posts/1/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void unlikePost_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(postService.isLikedBy(testUser, 1L)).thenReturn(false);
        when(postService.getLikeCount(1L)).thenReturn(0L);

        mockMvc.perform(post("/posts/1/unlike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.likeCount").value(0));
    }

    // ==================== collect status & toggle ====================

    @Test
    void getCollectStatus_Collected() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(postService.isCollectedBy(testUser, 1L)).thenReturn(true);
        when(postService.getCollectionCount(1L)).thenReturn(5L);

        mockMvc.perform(get("/posts/1/collect/status").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collected").value(true))
                .andExpect(jsonPath("$.collectionCount").value(5));
    }

    @Test
    void collectPost_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(postService.isCollectedBy(testUser, 1L)).thenReturn(true);
        when(postService.getCollectionCount(1L)).thenReturn(1L);

        mockMvc.perform(post("/posts/1/collect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collected").value(true));
    }

    @Test
    void uncollectPost_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(postService.isCollectedBy(testUser, 1L)).thenReturn(false);
        when(postService.getCollectionCount(1L)).thenReturn(0L);

        mockMvc.perform(post("/posts/1/uncollect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collected").value(false));
    }

    // ==================== comments ====================

    @Test
    void getComments_WithResults() throws Exception {
        Map<String, Object> comment = Map.of(
                "id", 1, "content", "Great post!", "user_id", 2,
                "created_at", LocalDateTime.now().toString());
        when(postService.getCommentsByPostId(1L)).thenReturn(Arrays.asList(comment));

        mockMvc.perform(get("/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Great post!"));
    }

    @Test
    void getComments_Empty_ShouldReturnEmptyList() throws Exception {
        when(postService.getCommentsByPostId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getComments_Error_ShouldReturnEmptyList() throws Exception {
        when(postService.getCommentsByPostId(1L)).thenThrow(new RuntimeException("Table not found"));

        mockMvc.perform(get("/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void createComment_Success() throws Exception {
        User commentUser = new User();
        commentUser.setId(2L);
        commentUser.setNickname("Commenter");

        Comment comment = new Comment();
        comment.setId(10L);
        comment.setPost(testPost);
        comment.setUser(commentUser);
        comment.setContent("Nice post!");
        comment.setCreatedAt(LocalDateTime.now());

        when(userService.getUserById(2L)).thenReturn(commentUser);
        when(postService.createComment(eq(1L), eq(commentUser), eq("Nice post!"), isNull()))
                .thenReturn(comment);

        Map<String, Object> body = new HashMap<>();
        body.put("userId", 2);
        body.put("content", "Nice post!");

        mockMvc.perform(post("/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.content").value("Nice post!"));
    }

    @Test
    void createComment_WithReplyToUser() throws Exception {
        User commentUser = new User();
        commentUser.setId(2L);
        commentUser.setNickname("Commenter");

        User replyToUser = new User();
        replyToUser.setId(1L);
        replyToUser.setNickname("OriginalAuthor");

        Comment comment = new Comment();
        comment.setId(11L);
        comment.setPost(testPost);
        comment.setUser(commentUser);
        comment.setContent("Reply message");
        comment.setParentId(10L);
        comment.setReplyToUser(replyToUser);
        comment.setCreatedAt(LocalDateTime.now());

        when(userService.getUserById(2L)).thenReturn(commentUser);
        when(postService.createComment(eq(1L), eq(commentUser), eq("Reply message"), eq(10L)))
                .thenReturn(comment);

        Map<String, Object> body = new HashMap<>();
        body.put("userId", 2);
        body.put("content", "Reply message");
        body.put("parent_id", 10);

        mockMvc.perform(post("/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.replyToUser.nickname").value("OriginalAuthor"));
    }

    @Test
    void createComment_Error_ShouldReturn400() throws Exception {
        when(userService.getUserById(2L)).thenReturn(testUser);
        when(postService.createComment(anyLong(), any(), anyString(), any()))
                .thenThrow(new RuntimeException("Comment failed"));

        Map<String, Object> body = Map.of("userId", 2, "content", "Bad comment");

        mockMvc.perform(post("/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    // ==================== deleteComment ====================

    @Test
    void deleteComment_Success() throws Exception {
        when(postService.deleteComment(10L, 1L)).thenReturn(true);

        mockMvc.perform(delete("/posts/comments/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteComment_NotOwner_ShouldReturn400() throws Exception {
        when(postService.deleteComment(10L, 2L)).thenReturn(false);

        mockMvc.perform(delete("/posts/comments/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== user stats ====================

    @Test
    void getUserStats_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(postService.getUserStats(eq(1L), eq(testUser)))
                .thenReturn(Map.of("totalLikes", 100L, "totalCollections", 50L));

        mockMvc.perform(get("/posts/user/1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLikes").value(100))
                .andExpect(jsonPath("$.totalCollections").value(50));
    }

    // ==================== user collections ====================

    @Test
    void getUserCollections_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        Map<String, Object> collected = Map.of("id", 5, "title", "Collected Post");
        when(postService.getUserCollections(eq(1L), eq(testUser)))
                .thenReturn(Arrays.asList(collected));

        mockMvc.perform(get("/posts/user/1/collections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Collected Post"));
    }

    @Test
    void getUserCollections_Empty() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(postService.getUserCollections(eq(1L), eq(testUser)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/posts/user/1/collections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ==================== user likes ====================

    @Test
    void getUserLikes_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        Map<String, Object> liked = Map.of("id", 3, "title", "Liked Post");
        when(postService.getUserLikedPosts(eq(1L), eq(testUser)))
                .thenReturn(Arrays.asList(liked));

        mockMvc.perform(get("/posts/user/1/likes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Liked Post"));
    }
}
