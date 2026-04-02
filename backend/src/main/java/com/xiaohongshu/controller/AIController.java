package com.xiaohongshu.controller;

import com.xiaohongshu.entity.Post;
import com.xiaohongshu.service.AIService;
import com.xiaohongshu.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

            // 保存到数据库
            post.setAiSummary(summary);
            postService.createPost(post);

            return ResponseEntity.ok(Map.of("ai_summary", summary));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "AI总结生成失败: " + e.getMessage()));
        }
    }
}