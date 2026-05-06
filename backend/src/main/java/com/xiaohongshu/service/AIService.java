package com.xiaohongshu.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import org.springframework.boot.web.client.RestTemplateBuilder;

@Service
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    @Value("${minimax.api-key}")
    private String apiKey;

    @Value("${minimax.base-url}")
    private String baseUrl;

    @Value("${minimax.model}")
    private String model;

    private final RestTemplate restTemplate;

    public AIService() {
        this.restTemplate = new RestTemplateBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }

    // 测试用构造函数
    public AIService(RestTemplate restTemplate, String apiKey, String baseUrl, String model) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
    }

    public String generateSummary(String title, String content) {
        String url = baseUrl + "/v1/text/chatcompletion_v2";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String prompt = buildPrompt(title, content);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 500);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return parseResponse(response.getBody());
        }

        throw new RuntimeException("AI API returned non-OK status: " + response.getStatusCode());
    }

    private String buildPrompt(String title, String content) {
        String actualContent = (content == null || content.trim().isEmpty()) ? "（无正文内容）" : content;
        return "请根据以下小红书笔记的标题和内容，用一段简洁的文字（100字以内）总结其核心内容。\n\n" +
                "标题：" + title + "\n\n" +
                "内容：" + actualContent;
    }

    private String parseResponse(Map responseBody) {
        try {
            log.debug("AI 响应: {}", responseBody);

            List<?> choices = (List<?>) responseBody.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                Map<?, ?> message = (Map<?, ?>) choice.get("message");
                if (message != null) {
                    return (String) message.get("content");
                }
            }
            throw new RuntimeException("无法解析AI响应格式");
        } catch (Exception e) {
            log.error("解析AI响应失败: {}", e.getMessage());
            throw new RuntimeException("解析AI响应失败: " + e.getMessage());
        }
    }
}
