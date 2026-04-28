package com.xiaohongshu.service;

import com.xiaohongshu.entity.*;
import com.xiaohongshu.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.xiaohongshu.entity.Collection;

/**
 * PostService 单元测试 - 使用 Mock 隔离数据库
 */
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setNickname("Test User");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setDescription("Test Description");
        testPost.setType("image");
        testPost.setAuthor(testUser);
        testPost.setCreatedAt(LocalDateTime.now());
    }

    // ==================== createPost 测试 ====================

    @Test
    void testCreatePost_Success() {
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Post result = postService.createPost(testPost);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(postRepository).save(testPost);
    }

    // ==================== getPostById 测试 ====================

    @Test
    void testGetPostById_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        Post result = postService.getPostById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetPostById_NotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> postService.getPostById(999L));
    }

    // ==================== getAllPosts 测试 ====================

    @Test
    void testGetAllPosts() {
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(posts);

        List<Post> result = postService.getAllPosts();

        assertEquals(1, result.size());
        verify(postRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void testGetAllPosts_Empty() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());

        List<Post> result = postService.getAllPosts();

        assertTrue(result.isEmpty());
    }

    // ==================== searchPosts 测试 ====================

    @Test
    void testSearchPosts() {
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findByTitleContainingOrderByCreatedAtDesc("Test")).thenReturn(posts);

        List<Post> result = postService.searchPosts("Test");

        assertEquals(1, result.size());
        verify(postRepository).findByTitleContainingOrderByCreatedAtDesc("Test");
    }

    @Test
    void testSearchPosts_NoResults() {
        when(postRepository.findByTitleContainingOrderByCreatedAtDesc("Nonexistent")).thenReturn(Collections.emptyList());

        List<Post> result = postService.searchPosts("Nonexistent");

        assertTrue(result.isEmpty());
    }

    // ==================== getPostsByTag 测试 ====================

    @Test
    void testGetPostsByTag() {
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findByTagOrderByCreatedAtDesc("travel")).thenReturn(posts);

        List<Post> result = postService.getPostsByTag("travel");

        assertEquals(1, result.size());
    }

    @Test
    void testGetPostsByTag_NoResults() {
        when(postRepository.findByTagOrderByCreatedAtDesc("nonexistent")).thenReturn(Collections.emptyList());

        List<Post> result = postService.getPostsByTag("nonexistent");

        assertTrue(result.isEmpty());
    }

    // ==================== searchPostsByTag 测试 ====================

    @Test
    void testSearchPostsByTag() {
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findByTagAndTitleContainingOrderByCreatedAtDesc("travel", "Test")).thenReturn(posts);

        List<Post> result = postService.searchPostsByTag("travel", "Test");

        assertEquals(1, result.size());
    }

    // ==================== updatePost 测试 ====================

    @Test
    void testUpdatePost_Success() {
        Post updates = new Post();
        updates.setTitle("Updated Title");
        updates.setDescription("Updated Description");

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        Post result = postService.updatePost(1L, updates);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
    }

    @Test
    void testUpdatePost_PartialUpdate() {
        Post updates = new Post();
        updates.setTitle("Updated Title");
        // description 不更新

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        Post result = postService.updatePost(1L, updates);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Test Description", result.getDescription()); // 保持原值
    }

    @Test
    void testUpdatePost_UpdateType() {
        Post updates = new Post();
        updates.setType("video");

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        Post result = postService.updatePost(1L, updates);

        assertEquals("video", result.getType());
    }

    @Test
    void testUpdatePost_UpdateUrl() {
        Post updates = new Post();
        updates.setUrl("/new-url.jpg");

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        Post result = postService.updatePost(1L, updates);

        assertEquals("/new-url.jpg", result.getUrl());
    }

    @Test
    void testUpdatePost_UpdateCoverUrl() {
        Post updates = new Post();
        updates.setCoverUrl("/cover.jpg");

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        Post result = postService.updatePost(1L, updates);

        assertEquals("/cover.jpg", result.getCoverUrl());
    }

    @Test
    void testUpdatePost_UpdateTag() {
        Post updates = new Post();
        updates.setTag("food");

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        Post result = postService.updatePost(1L, updates);

        assertEquals("food", result.getTag());
    }

    // ==================== deletePost 测试 ====================

    @Test
    void testDeletePost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(commentRepository.findByPostOrderByCreatedAtDesc(testPost)).thenReturn(Collections.emptyList());
        when(likeRepository.findByPost(testPost)).thenReturn(Collections.emptyList());
        when(collectionRepository.findByPost(testPost)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> postService.deletePost(1L));

        verify(postRepository).delete(testPost);
    }

    @Test
    void testDeletePost_WithRelatedData() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setPost(testPost);
        comment.setUser(testUser);

        Like like = new Like();
        like.setId(1L);
        like.setPost(testPost);
        like.setUser(testUser);

        Collection collection = new Collection();
        collection.setId(1L);
        collection.setPost(testPost);
        collection.setUser(testUser);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(commentRepository.findByPostOrderByCreatedAtDesc(testPost)).thenReturn(Arrays.asList(comment));
        when(likeRepository.findByPost(testPost)).thenReturn(Arrays.asList(like));
        when(collectionRepository.findByPost(testPost)).thenReturn(Arrays.asList(collection));

        assertDoesNotThrow(() -> postService.deletePost(1L));

        verify(commentRepository).deleteAll(anyList());
        verify(likeRepository).deleteAll(anyList());
        verify(collectionRepository).deleteAll(anyList());
        verify(postRepository).delete(testPost);
    }

    // ==================== getPostsByAuthor 测试 ====================

    @Test
    void testGetPostsByAuthor() {
        List<Post> posts = Arrays.asList(testPost);
        when(postRepository.findByAuthorOrderByCreatedAtDesc(testUser)).thenReturn(posts);

        List<Post> result = postService.getPostsByAuthor(testUser);

        assertEquals(1, result.size());
    }

    // ==================== toggleLike 测试 ====================

    @Test
    void testToggleLike_AddLike() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(likeRepository.findByUserAndPost(testUser, testPost)).thenReturn(Optional.empty());

        postService.toggleLike(testUser, 1L);

        verify(likeRepository).save(any());
    }

    @Test
    void testToggleLike_RemoveLike() {
        com.xiaohongshu.entity.Like existingLike = new com.xiaohongshu.entity.Like();
        existingLike.setUser(testUser);
        existingLike.setPost(testPost);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(likeRepository.findByUserAndPost(testUser, testPost)).thenReturn(Optional.of(existingLike));

        postService.toggleLike(testUser, 1L);

        verify(likeRepository).delete(existingLike);
    }

    // ==================== likePost 测试 ====================

    @Test
    void testLikePost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(likeRepository.existsByUserAndPost(testUser, testPost)).thenReturn(false);
        when(likeRepository.save(any())).thenReturn(new com.xiaohongshu.entity.Like());

        postService.likePost(testUser, 1L);

        verify(likeRepository).save(any());
    }

    @Test
    void testLikePost_AlreadyLiked() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(likeRepository.existsByUserAndPost(testUser, testPost)).thenReturn(true);

        postService.likePost(testUser, 1L);

        verify(likeRepository, never()).save(any());
    }

    // ==================== unlikePost 测试 ====================

    @Test
    void testUnlikePost_Success() {
        com.xiaohongshu.entity.Like existingLike = new com.xiaohongshu.entity.Like();
        existingLike.setUser(testUser);
        existingLike.setPost(testPost);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(likeRepository.findByUserAndPost(testUser, testPost)).thenReturn(Optional.of(existingLike));

        postService.unlikePost(testUser, 1L);

        verify(likeRepository).delete(existingLike);
    }

    @Test
    void testUnlikePost_NotLiked() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(likeRepository.findByUserAndPost(testUser, testPost)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> postService.unlikePost(testUser, 1L));

        verify(likeRepository, never()).delete(any());
    }

    // ==================== toggleCollection 测试 ====================

    @Test
    void testToggleCollection_AddCollection() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(collectionRepository.findByUserAndPost(testUser, testPost)).thenReturn(Optional.empty());

        postService.toggleCollection(testUser, 1L);

        verify(collectionRepository).save(any());
    }

    @Test
    void testToggleCollection_RemoveCollection() {
        com.xiaohongshu.entity.Collection existingCollection = new com.xiaohongshu.entity.Collection();
        existingCollection.setUser(testUser);
        existingCollection.setPost(testPost);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(collectionRepository.findByUserAndPost(testUser, testPost)).thenReturn(Optional.of(existingCollection));

        postService.toggleCollection(testUser, 1L);

        verify(collectionRepository).delete(existingCollection);
    }

    // ==================== collectPost 测试 ====================

    @Test
    void testCollectPost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(collectionRepository.existsByUserAndPost(testUser, testPost)).thenReturn(false);
        when(collectionRepository.save(any())).thenReturn(new com.xiaohongshu.entity.Collection());

        postService.collectPost(testUser, 1L);

        verify(collectionRepository).save(any());
    }

    @Test
    void testCollectPost_AlreadyCollected() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(collectionRepository.existsByUserAndPost(testUser, testPost)).thenReturn(true);

        postService.collectPost(testUser, 1L);

        verify(collectionRepository, never()).save(any());
    }

    // ==================== uncollectPost 测试 ====================

    @Test
    void testUncollectPost_Success() {
        com.xiaohongshu.entity.Collection existingCollection = new com.xiaohongshu.entity.Collection();
        existingCollection.setUser(testUser);
        existingCollection.setPost(testPost);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(collectionRepository.findByUserAndPost(testUser, testPost)).thenReturn(Optional.of(existingCollection));

        postService.uncollectPost(testUser, 1L);

        verify(collectionRepository).delete(existingCollection);
    }

    @Test
    void testUncollectPost_NotCollected() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(collectionRepository.findByUserAndPost(testUser, testPost)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> postService.uncollectPost(testUser, 1L));

        verify(collectionRepository, never()).delete(any());
    }

    // ==================== getLikeCount 测试 ====================

    @Test
    void testGetLikeCount() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(likeRepository.countByPost(testPost)).thenReturn(5L);

        long result = postService.getLikeCount(1L);

        assertEquals(5L, result);
    }

    @Test
    void testGetLikeCount_Zero() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(likeRepository.countByPost(testPost)).thenReturn(0L);

        long result = postService.getLikeCount(1L);

        assertEquals(0L, result);
    }

    // ==================== isLikedBy 测试 ====================

    @Test
    void testIsLikedBy_True() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(likeRepository.existsByUserAndPost(testUser, testPost)).thenReturn(true);

        boolean result = postService.isLikedBy(testUser, 1L);

        assertTrue(result);
    }

    @Test
    void testIsLikedBy_False() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(likeRepository.existsByUserAndPost(testUser, testPost)).thenReturn(false);

        boolean result = postService.isLikedBy(testUser, 1L);

        assertFalse(result);
    }

    // ==================== getCollectionCount 测试 ====================

    @Test
    void testGetCollectionCount() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(collectionRepository.countByPost(testPost)).thenReturn(3L);

        long result = postService.getCollectionCount(1L);

        assertEquals(3L, result);
    }

    // ==================== isCollectedBy 测试 ====================

    @Test
    void testIsCollectedBy_True() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(collectionRepository.existsByUserAndPost(testUser, testPost)).thenReturn(true);

        boolean result = postService.isCollectedBy(testUser, 1L);

        assertTrue(result);
    }

    @Test
    void testIsCollectedBy_False() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(collectionRepository.existsByUserAndPost(testUser, testPost)).thenReturn(false);

        boolean result = postService.isCollectedBy(testUser, 1L);

        assertFalse(result);
    }

    // ==================== getCommentCount 测试 ====================

    @Test
    void testGetCommentCount() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(commentRepository.countByPost(testPost)).thenReturn(10L);

        long result = postService.getCommentCount(1L);

        assertEquals(10L, result);
    }

    // ==================== deleteComment 测试 ====================

    @Test
    void testDeleteComment_Success() {
        com.xiaohongshu.entity.Comment comment = new com.xiaohongshu.entity.Comment();
        comment.setId(1L);
        comment.setUser(testUser);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        boolean result = postService.deleteComment(1L, 1L);

        assertTrue(result);
        verify(commentRepository).delete(comment);
    }

    @Test
    void testDeleteComment_NotOwner() {
        com.xiaohongshu.entity.Comment comment = new com.xiaohongshu.entity.Comment();
        comment.setId(1L);
        comment.setUser(testUser);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        boolean result = postService.deleteComment(1L, 999L); // 不同的 userId

        assertFalse(result);
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void testDeleteComment_NotFound() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        boolean result = postService.deleteComment(999L, 1L);

        assertFalse(result);
    }

    // ==================== getUserStats 测试 ====================

    @Test
    void testGetUserStats() {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setAuthor(testUser);
        Post post2 = new Post();
        post2.setId(2L);
        post2.setAuthor(testUser);

        when(postRepository.findByAuthorOrderByCreatedAtDesc(testUser)).thenReturn(Arrays.asList(post1, post2));
        when(likeRepository.countByPost(post1)).thenReturn(5L);
        when(likeRepository.countByPost(post2)).thenReturn(3L);
        when(collectionRepository.countByPost(post1)).thenReturn(2L);
        when(collectionRepository.countByPost(post2)).thenReturn(1L);

        Map<String, Long> result = postService.getUserStats(1L, testUser);

        assertEquals(8L, result.get("totalLikes")); // 5 + 3
        assertEquals(3L, result.get("totalCollections")); // 2 + 1
    }

    @Test
    void testGetUserStats_NoPosts() {
        when(postRepository.findByAuthorOrderByCreatedAtDesc(testUser)).thenReturn(Collections.emptyList());

        Map<String, Long> result = postService.getUserStats(1L, testUser);

        assertEquals(0L, result.get("totalLikes"));
        assertEquals(0L, result.get("totalCollections"));
    }
}