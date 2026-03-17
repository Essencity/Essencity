package com.xiaohongshu.controller;

import com.xiaohongshu.entity.Comment;
import com.xiaohongshu.entity.Post;
import com.xiaohongshu.entity.User;
import com.xiaohongshu.service.PostService;
import com.xiaohongshu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    private final Path fileStorageLocation;

    public PostController(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

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

        // 为每个帖子添加 likeCount
        return posts.stream().map(post -> {
            Map<String, Object> postMap = new java.util.HashMap<>();
            postMap.put("id", post.getId());
            postMap.put("title", post.getTitle());
            postMap.put("description", post.getDescription());
            postMap.put("type", post.getType());
            postMap.put("url", post.getUrl());
            postMap.put("coverUrl", post.getCoverUrl());
            postMap.put("imageUrl", post.getType().equals("video") ? post.getCoverUrl() : post.getUrl());
            postMap.put("videoUrl", post.getType().equals("video") ? post.getUrl() : null);
            postMap.put("author", post.getAuthor());
            postMap.put("createdAt", post.getCreatedAt());
            postMap.put("tag", post.getTag());
            postMap.put("likeCount", postService.getLikeCount(post.getId()));
            postMap.put("collectionCount", postService.getCollectionCount(post.getId()));
            return postMap;
        }).collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        try {
            Post post = postService.getPostById(id);
            if (post == null) {
                return ResponseEntity.notFound().build();
            }

            java.util.HashMap<String, Object> postMap = new java.util.HashMap<>();
            postMap.put("id", post.getId());
            postMap.put("title", post.getTitle());
            postMap.put("content", post.getDescription());
            postMap.put("description", post.getDescription());
            postMap.put("type", post.getType());
            postMap.put("url", post.getUrl());
            postMap.put("coverUrl", post.getCoverUrl());
            postMap.put("imageUrl", post.getType().equals("video") ? post.getCoverUrl() : post.getUrl());
            postMap.put("videoUrl", post.getType().equals("video") ? post.getUrl() : null);
            postMap.put("author", post.getAuthor());
            postMap.put("createdAt", post.getCreatedAt());
            postMap.put("tag", post.getTag());
            postMap.put("likeCount", postService.getLikeCount(post.getId()));
            postMap.put("collectionCount", postService.getCollectionCount(post.getId()));

            return ResponseEntity.ok(postMap);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Post deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Map<String, Object> postData) {
        try {
            Post post = new Post();
            post.setTitle((String) postData.get("title"));
            post.setDescription((String) postData.get("description"));
            post.setType((String) postData.get("type"));
            post.setUrl((String) postData.get("url"));
            post.setCoverUrl((String) postData.get("cover_url"));
            post.setTag((String) postData.get("tag"));

            Long authorId = ((Number) postData.get("author_id")).longValue();
            User author = userService.getUserById(authorId);
            post.setAuthor(author);

            return ResponseEntity.ok(postService.createPost(post));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to create post"));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            String fileUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to upload file: " + e.getMessage()));
        }
    }

    // Like endpoints
    @GetMapping("/{id}/like/status")
    public ResponseEntity<?> getLikeStatus(@PathVariable Long id, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        boolean liked = postService.isLikedBy(user, id);
        long count = postService.getLikeCount(id);
        return ResponseEntity.ok(Map.of("liked", liked, "likeCount", count));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Long userId = ((Number) body.get("userId")).longValue();
            User user = userService.getUserById(userId);
            postService.likePost(user, id);
            boolean liked = postService.isLikedBy(user, id);
            long count = postService.getLikeCount(id);
            return ResponseEntity.ok(Map.of("liked", liked, "likeCount", count));
        } catch (Exception e) {
            e.printStackTrace(); // PRINT STACK TRACE
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Long userId = ((Number) body.get("userId")).longValue();
            User user = userService.getUserById(userId);
            postService.unlikePost(user, id);
            boolean liked = postService.isLikedBy(user, id);
            long count = postService.getLikeCount(id);
            return ResponseEntity.ok(Map.of("liked", liked, "likeCount", count));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    // Collect endpoints
    @GetMapping("/{id}/collect/status")
    public ResponseEntity<?> getCollectStatus(@PathVariable Long id, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        boolean collected = postService.isCollectedBy(user, id);
        long count = postService.getCollectionCount(id);
        return ResponseEntity.ok(Map.of("collected", collected, "collectionCount", count));
    }

    @PostMapping("/{id}/collect")
    public ResponseEntity<?> collectPost(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = ((Number) body.get("userId")).longValue();
        User user = userService.getUserById(userId);
        postService.collectPost(user, id);
        boolean collected = postService.isCollectedBy(user, id);
        long count = postService.getCollectionCount(id);
        return ResponseEntity.ok(Map.of("collected", collected, "collectionCount", count));
    }

    @PostMapping("/{id}/uncollect")
    public ResponseEntity<?> uncollectPost(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = ((Number) body.get("userId")).longValue();
        User user = userService.getUserById(userId);
        postService.uncollectPost(user, id);
        boolean collected = postService.isCollectedBy(user, id);
        long count = postService.getCollectionCount(id);
        return ResponseEntity.ok(Map.of("collected", collected, "collectionCount", count));
    }

    // Comment endpoints
    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(postService.getCommentsByPostId(id));
        } catch (Exception e) {
            // 如果评论表不存在等情况，返回空数组
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> createComment(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            System.out.println("Processing createComment for post: " + id);
            Long userId = ((Number) body.get("userId")).longValue();
            String content = (String) body.get("content");
            Long parentId = body.get("parent_id") != null ? ((Number) body.get("parent_id")).longValue() : null;

            System.out.println("User: " + userId + ", ParentId: " + parentId);

            User user = userService.getUserById(userId);
            System.out.println("User found: " + user.getNickname());

            Comment comment = postService.createComment(id, user, content, parentId);
            System.out.println("Comment created: " + comment.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("id", comment.getId());
            response.put("post_id", comment.getPost().getId());
            response.put("user_id", comment.getUser().getId());
            response.put("content", comment.getContent());
            response.put("created_at", comment.getCreatedAt());
            response.put("parent_id", comment.getParentId());

            if (comment.getReplyToUser() != null) {
                System.out.println("Adding replyToUser info: " + comment.getReplyToUser().getNickname());
                Map<String, Object> rUser = new HashMap<>();
                rUser.put("id", comment.getReplyToUser().getId());
                rUser.put("nickname", comment.getReplyToUser().getNickname());
                response.put("replyToUser", rUser);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in createComment: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to create comment: " + e.getMessage()));
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, @RequestBody Map<String, Object> body) {
        Long userId = ((Number) body.get("userId")).longValue();
        boolean deleted = postService.deleteComment(commentId, userId);
        if (deleted) {
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Cannot delete comment"));
    }

    // User stats endpoints
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
}
