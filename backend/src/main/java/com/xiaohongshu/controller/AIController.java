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

import java.util.List;
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

    /**
     * AI标签推荐
     */
    @PostMapping("/recommend-tags")
    public ResponseEntity<?> recommendTags(@RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            String content = (String) request.get("content");

            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "标题不能为空"));
            }

            List<String> tags = aiService.recommendTags(title, content);
            return ResponseEntity.ok(Map.of("tags", tags));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "标签推荐失败: " + e.getMessage()));
        }
    }

    /**
     * AI内容创作助手
     */
    @PostMapping("/assist")
    public ResponseEntity<?> assistContent(@RequestBody Map<String, Object> request) {
        try {
            String mode = (String) request.get("mode");
            String title = (String) request.get("title");
            String content = (String) request.get("content");

            if (mode == null || mode.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "模式不能为空"));
            }

            String result = aiService.generateContent(mode, title, content);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "内容生成失败: " + e.getMessage()));
        }
    }

    /**
     * AI帖子问答
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(@RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            String content = (String) request.get("content");
            String question = (String) request.get("question");
            List<Map<String, String>> history = (List<Map<String, String>>) request.get("history");

            if (question == null || question.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "问题不能为空"));
            }

            String answer = aiService.answerQuestion(title, content, question, history);
            return ResponseEntity.ok(Map.of("answer", answer));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "问答失败: " + e.getMessage()));
        }
    }
}