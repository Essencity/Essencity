package com.xiaohongshu.controller;

import com.xiaohongshu.entity.Post;
import com.xiaohongshu.service.AIService;
import com.xiaohongshu.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AIController {

    private static final Logger log = LoggerFactory.getLogger(AIController.class);

    @Autowired
    private AIService aiService;

    @Autowired
    private PostService postService;

    @GetMapping("/summary/{postId}")
    public ResponseEntity<?> getAiSummary(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("ai_summary", post.getAiSummary() != null ? post.getAiSummary() : ""));
    }

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

            String summary = aiService.generateSummary(title, content);
            post.setAiSummary(summary);
            postService.updatePostAiSummary(post);

            return ResponseEntity.ok(Map.of("ai_summary", summary));
        } catch (Exception e) {
            log.error("AI summary generation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "AI总结生成失败，请稍后重试"));
        }
    }
}
