package com.xiaohongshu.controller;

import com.xiaohongshu.entity.Comment;
import com.xiaohongshu.entity.Post;
import com.xiaohongshu.entity.User;
import com.xiaohongshu.service.PostService;
import com.xiaohongshu.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/posts")
<<<<<<< Updated upstream
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
=======
@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST,
        RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowCredentials = "true")
>>>>>>> Stashed changes
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml");
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4", "video/webm", "video/ogg");
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    private final Path fileStorageLocation;

    public PostController(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @GetMapping
    public List<Map<String, Object>> getAllPosts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String tag) {
        List<Post> posts;
        if (tag != null && !tag.isEmpty() && !"推荐".equals(tag)) {
            if (title != null && !title.isEmpty()) {
                posts = postService.searchPostsByTag(tag, title);
            } else {
                posts = postService.getPostsByTag(tag);
            }
        } else if (title != null && !title.isEmpty()) {
            posts = postService.searchPosts(title);
        } else {
            posts = postService.getAllPosts();
        }
        return posts.stream().map(this::postToMap).collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        try {
            Post post = postService.getPostById(id);
            if (post == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(postToMap(post));
        } catch (Exception e) {
            log.error("Error getting post {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "获取帖子失败"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            Post post = postService.getPostById(id);
            if (post == null) {
                return ResponseEntity.notFound().build();
            }
            if (!post.getAuthor().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "无权删除"));
            }
            postService.deletePost(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Post deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting post {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "删除失败"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Map<String, Object> postData) {
        try {
            User currentUser = getCurrentUser();
            Post post = new Post();
            post.setTitle((String) postData.get("title"));
            post.setDescription((String) postData.get("description"));
            post.setType((String) postData.get("type"));
            post.setUrl((String) postData.get("url"));
            post.setCoverUrl((String) postData.get("cover_url"));
            post.setTag((String) postData.get("tag"));
            post.setAuthor(currentUser);

            return ResponseEntity.ok(postService.createPost(post));
        } catch (Exception e) {
            log.error("Failed to create post: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "创建帖子失败"));
        }
    }

<<<<<<< Updated upstream
=======
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody Map<String, Object> postData) {
        try {
            User currentUser = getCurrentUser();
            Post existing = postService.getPostById(id);
            if (existing == null) {
                return ResponseEntity.notFound().build();
            }
            if (!existing.getAuthor().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(Map.of("message", "无权修改"));
            }

            Post post = new Post();
            post.setTitle((String) postData.get("title"));
            post.setDescription((String) postData.get("description"));
            post.setType((String) postData.get("type"));
            post.setUrl((String) postData.get("url"));
            post.setCoverUrl((String) postData.get("cover_url"));
            post.setTag((String) postData.get("tag"));

            Post updated = postService.updatePost(id, post);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Failed to update post {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "更新失败"));
        }
    }

>>>>>>> Stashed changes
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "文件为空"));
            }
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body(Map.of("message", "文件大小不能超过50MB"));
            }

            // 校验文件类型
            String contentType = file.getContentType();
            String originalName = file.getOriginalFilename();
            if (!isAllowedFileType(contentType, originalName)) {
                return ResponseEntity.badRequest().body(Map.of("message", "不支持的文件类型"));
            }

            // 仅使用 UUID 作为文件名，防止路径遍历
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
                // 二次校验扩展名
                String lowerExt = ext.toLowerCase();
                if (!lowerExt.matches("\\.(jpg|jpeg|png|gif|webp|svg|mp4|webm|ogg)")) {
                    return ResponseEntity.badRequest().body(Map.of("message", "不支持的文件格式"));
                }
            }
            String fileName = UUID.randomUUID().toString() + ext;
            Path targetLocation = fileStorageLocation.resolve(fileName);

            // 确保路径在 upload 目录内
            if (!targetLocation.normalize().startsWith(fileStorageLocation)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid file path"));
            }

            Files.copy(file.getInputStream(), targetLocation);
            String fileUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (IOException e) {
            log.error("Failed to upload file: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", "文件上传失败"));
        }
    }

    // Like endpoints - 从 SecurityContext 获取当前用户
    @GetMapping("/{id}/like/status")
    public ResponseEntity<?> getLikeStatus(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            boolean liked = postService.isLikedBy(currentUser, id);
            long count = postService.getLikeCount(id);
            return ResponseEntity.ok(Map.of("liked", liked, "likeCount", count));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("liked", false, "likeCount", postService.getLikeCount(id)));
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            postService.likePost(currentUser, id);
            boolean liked = postService.isLikedBy(currentUser, id);
            long count = postService.getLikeCount(id);
            return ResponseEntity.ok(Map.of("liked", liked, "likeCount", count));
        } catch (Exception e) {
            log.error("Like failed for post {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "操作失败"));
        }
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            postService.unlikePost(currentUser, id);
            boolean liked = postService.isLikedBy(currentUser, id);
            long count = postService.getLikeCount(id);
            return ResponseEntity.ok(Map.of("liked", liked, "likeCount", count));
        } catch (Exception e) {
            log.error("Unlike failed for post {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "操作失败"));
        }
    }

    // Collect endpoints
    @GetMapping("/{id}/collect/status")
    public ResponseEntity<?> getCollectStatus(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            boolean collected = postService.isCollectedBy(currentUser, id);
            long count = postService.getCollectionCount(id);
            return ResponseEntity.ok(Map.of("collected", collected, "collectionCount", count));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("collected", false, "collectionCount",
                    postService.getCollectionCount(id)));
        }
    }

    @PostMapping("/{id}/collect")
    public ResponseEntity<?> collectPost(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            postService.collectPost(currentUser, id);
            boolean collected = postService.isCollectedBy(currentUser, id);
            long count = postService.getCollectionCount(id);
            return ResponseEntity.ok(Map.of("collected", collected, "collectionCount", count));
        } catch (Exception e) {
            log.error("Collect failed for post {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "操作失败"));
        }
    }

    @PostMapping("/{id}/uncollect")
    public ResponseEntity<?> uncollectPost(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            postService.uncollectPost(currentUser, id);
            boolean collected = postService.isCollectedBy(currentUser, id);
            long count = postService.getCollectionCount(id);
            return ResponseEntity.ok(Map.of("collected", collected, "collectionCount", count));
        } catch (Exception e) {
            log.error("Uncollect failed for post {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "操作失败"));
        }
    }

    // Comment endpoints
    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(postService.getCommentsByPostId(id));
        } catch (Exception e) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> createComment(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            User currentUser = getCurrentUser();
            String content = (String) body.get("content");
            Long parentId = body.get("parent_id") != null ? ((Number) body.get("parent_id")).longValue() : null;

            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "评论内容不能为空"));
            }
            if (content.length() > 2000) {
                return ResponseEntity.badRequest().body(Map.of("message", "评论内容不能超过2000字"));
            }

            Comment comment = postService.createComment(id, currentUser, content, parentId);

            Map<String, Object> response = new HashMap<>();
            response.put("id", comment.getId());
            response.put("post_id", comment.getPost().getId());
            response.put("user_id", comment.getUser().getId());
            response.put("content", comment.getContent());
            response.put("created_at", comment.getCreatedAt());
            response.put("parent_id", comment.getParentId());

            if (comment.getReplyToUser() != null) {
                Map<String, Object> rUser = new HashMap<>();
                rUser.put("id", comment.getReplyToUser().getId());
                rUser.put("nickname", comment.getReplyToUser().getNickname());
                response.put("replyToUser", rUser);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating comment for post {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "评论发送失败"));
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            User currentUser = getCurrentUser();
            boolean deleted = postService.deleteComment(commentId, currentUser.getId());
            if (deleted) {
                return ResponseEntity.ok(Map.of("success", true));
            }
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Cannot delete comment"));
        } catch (Exception e) {
            log.error("Error deleting comment {}: {}", commentId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "删除失败"));
        }
    }

    // User stats endpoints (public read)
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<?> getUserStats(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(postService.getUserStats(userId, user));
    }

    @GetMapping("/user/{userId}/collections")
    public ResponseEntity<?> getUserCollections(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(postService.getUserCollections(userId, user));
    }

    @GetMapping("/user/{userId}/likes")
    public ResponseEntity<?> getUserLikes(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(postService.getUserLikedPosts(userId, user));
    }

    // Helpers
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }
        throw new RuntimeException("未登录");
    }

    private Map<String, Object> postToMap(Post post) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", post.getId());
        map.put("title", post.getTitle());
        map.put("description", post.getDescription());
        map.put("type", post.getType());
        map.put("url", post.getUrl());
        map.put("coverUrl", post.getCoverUrl());
        map.put("imageUrl", post.getType().equals("video") ? post.getCoverUrl() : post.getUrl());
        map.put("videoUrl", post.getType().equals("video") ? post.getUrl() : null);
        map.put("author", post.getAuthor());
        map.put("createdAt", post.getCreatedAt());
        map.put("tag", post.getTag());
        map.put("likeCount", postService.getLikeCount(post.getId()));
        map.put("collectionCount", postService.getCollectionCount(post.getId()));
        return map;
    }

    private boolean isAllowedFileType(String contentType, String fileName) {
        if (contentType != null) {
            return ALLOWED_IMAGE_TYPES.contains(contentType)
                    || ALLOWED_VIDEO_TYPES.contains(contentType);
        }
        if (fileName == null) return false;
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")
                || lower.endsWith(".gif") || lower.endsWith(".webp") || lower.endsWith(".svg")
                || lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".ogg");
    }
}
