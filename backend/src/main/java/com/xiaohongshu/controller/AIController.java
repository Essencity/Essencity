package com.xiaohongshu.controller;

import com.xiaohongshu.entity.Post;
import com.xiaohongshu.repository.PostRepository;
import com.xiaohongshu.service.AIService;
import com.xiaohongshu.service.PostService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 获取帖子的AI总结
     */
    @GetMapping("/summary/{postId}")
    public ResponseEntity<?> getAiSummary(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("ai_summary", post.getAiSummary() != null ? post.getAiSummary() : ""));
    }

    /**
     * 生成AI总结
     */
    @PostMapping("/summary")
    @Transactional
    public ResponseEntity<?> generateAiSummary(@RequestBody Map<String, Object> request) {
        try {
            Long postId = ((Number) request.get("postId")).longValue();
            String title = (String) request.get("title");
            String content = (String) request.get("content");

            Post post = postService.getPostById(postId);
            if (post == null) {
                return ResponseEntity.notFound().build();
            }

            // 调用AI生成总结
            String summary = aiService.generateSummary(title, content);

            // 使用EntityManager直接更新
            post.setAiSummary(summary);
            entityManager.merge(post);

            return ResponseEntity.ok(Map.of("ai_summary", summary));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "AI总结生成失败: " + e.getMessage()));
        }
    }
}