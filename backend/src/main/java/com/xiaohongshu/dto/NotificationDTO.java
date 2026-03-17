package com.xiaohongshu.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class NotificationDTO {
    private Long id;
    private String type; // "LIKE", "COLLECTION", "COMMENT", "FOLLOW"
    private Map<String, Object> actor; // Who triggered the notification (liker, commenter, etc.)
    private Map<String, Object> post; // Related post (null for FOLLOW type)
    private String content; // Comment content or other text
    private LocalDateTime createdAt;

    public NotificationDTO(Long id, String type, Map<String, Object> actor, Map<String, Object> post, String content,
            LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.actor = actor;
        this.post = post;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getActor() {
        return actor;
    }

    public void setActor(Map<String, Object> actor) {
        this.actor = actor;
    }

    public Map<String, Object> getPost() {
        return post;
    }

    public void setPost(Map<String, Object> post) {
        this.post = post;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
