package com.xiaohongshu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AIService {

    @Value("${minimax.api-key}")
    private String apiKey;

    @Value("${minimax.base-url}")
    private String baseUrl;

    @Value("${minimax.model}")
    private String model;

    private RestTemplate restTemplate;

    public AIService() {
        this.restTemplate = new RestTemplate();
    }

    // 构造函数用于测试
    public AIService(RestTemplate restTemplate, String apiKey, String baseUrl, String model) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
    }

    public String generateSummary(String title, String content) {
        try {
            String url = baseUrl + "/v1/text/chatcompletion_v2";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // 构建提示词
            String prompt = buildPrompt(title, content);

            // 构建请求体 - MiniMax API 格式
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of(
                    "role", "user",
                    "content", prompt
            ));
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 500);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseResponse(response.getBody());
            }

            throw new RuntimeException("AI API returned non-OK status: " + response.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("调用AI服务失败: " + e.getMessage());
        }
    }

    private String buildPrompt(String title, String content) {
        String actualContent = (content == null || content.trim().isEmpty()) ? "（无正文内容）" : content;
        return "请根据以下小红书笔记的标题和内容，用一段简洁的文字（100字以内）总结其核心内容。\n\n" +
                "标题：" + title + "\n\n" +
                "内容：" + actualContent;
    }

    private String parseResponse(Map responseBody) {
        try {
            // MiniMax API 响应格式
            // {"id":"xxx","choices":[{"message":{"role":"assistant","content":"xxx"}}]}
            System.out.println("AI 响应: " + responseBody);

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
            e.printStackTrace();
            throw new RuntimeException("解析AI响应失败: " + e.getMessage());
        }
    }

    // ==================== 标签推荐 ====================

    public List<String> recommendTags(String title, String content) {
        try {
            String url = baseUrl + "/v1/text/chatcompletion_v2";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String prompt = buildTagRecommendationPrompt(title, content);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.3);
            requestBody.put("max_tokens", 100);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String aiResponse = parseResponse(response.getBody());
                return parseTagRecommendations(aiResponse);
            }

            throw new RuntimeException("AI API returned non-OK status: " + response.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("标签推荐失败: " + e.getMessage());
        }
    }

    private String buildTagRecommendationPrompt(String title, String content) {
        String actualContent = (content == null || content.trim().isEmpty()) ? "（无正文内容）" : content;
        return "你是一个小红书标签推荐助手。请根据以下笔记的标题和内容，从预设标签中推荐1-3个最相关的标签。\n\n" +
                "预设标签列表：['穿搭', '美食', '彩妆', '影视', '职场', '情感', '家居', '游戏', '旅行', '健身']\n\n" +
                "要求：\n" +
                "1. 只能从预设标签中选择\n" +
                "2. 推荐1-3个最相关的标签\n" +
                "3. 返回JSON数组格式，例如：[\"美食\", \"旅行\"]\n" +
                "4. 不要返回其他任何文字说明\n\n" +
                "标题：" + title + "\n\n" +
                "内容：" + actualContent;
    }

    private List<String> parseTagRecommendations(String aiResponse) {
        List<String> tags = new ArrayList<>();
        try {
            // 尝试直接解析JSON数组
            String jsonStr = aiResponse.trim();
            // 如果包含非JSON内容，尝试提取方括号部分
            if (!jsonStr.startsWith("[")) {
                int start = jsonStr.indexOf("[");
                int end = jsonStr.lastIndexOf("]");
                if (start >= 0 && end > start) {
                    jsonStr = jsonStr.substring(start, end + 1);
                }
            }
            // 简单解析JSON数组
            jsonStr = jsonStr.replaceAll("[\\[\\]\"]", "");
            String[] parts = jsonStr.split(",");
            for (String part : parts) {
                String tag = part.trim();
                if (!tag.isEmpty()) {
                    tags.add(tag);
                }
            }
        } catch (Exception e) {
            // 解析失败，返回空列表
            e.printStackTrace();
        }
        return tags;
    }

    // ==================== 内容创作助手 ====================

    public String generateContent(String mode, String title, String content) {
        switch (mode) {
            case "expand":
                return expandContent(title, content);
            case "polish":
                return polishContent(title, content);
            case "title":
                return generateTitle(content);
            default:
                throw new IllegalArgumentException("不支持的模式: " + mode);
        }
    }

    private String expandContent(String title, String content) {
        try {
            String url = baseUrl + "/v1/text/chatcompletion_v2";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String prompt = buildExpandPrompt(title, content);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.8);
            requestBody.put("max_tokens", 600);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseResponse(response.getBody());
            }

            throw new RuntimeException("AI API returned non-OK status: " + response.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("内容扩写失败: " + e.getMessage());
        }
    }

    private String buildExpandPrompt(String title, String content) {
        return "你是一个小红书文案创作助手。请将以下简短内容扩展为更丰富、更有吸引力的小红书风格文案。\n\n" +
                "要求：\n" +
                "1. 保持核心信息不变\n" +
                "2. 扩展到200-300字\n" +
                "3. 使用小红书风格的语言（亲切、生动、有感染力）\n" +
                "4. 可以适当添加表情符号\n" +
                "5. 只返回扩展后的文案，不要返回其他说明\n\n" +
                "标题：" + title + "\n\n" +
                "原始内容：" + content;
    }

    private String polishContent(String title, String content) {
        try {
            String url = baseUrl + "/v1/text/chatcompletion_v2";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String prompt = buildPolishPrompt(title, content);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.6);
            requestBody.put("max_tokens", 600);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseResponse(response.getBody());
            }

            throw new RuntimeException("AI API returned non-OK status: " + response.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("内容润色失败: " + e.getMessage());
        }
    }

    private String buildPolishPrompt(String title, String content) {
        return "你是一个小红书文案润色助手。请润色以下文案，使其更加流畅、生动，适合小红书平台风格。\n\n" +
                "要求：\n" +
                "1. 保持原意不变\n" +
                "2. 优化语言表达，使其更流畅\n" +
                "3. 适当使用小红书风格的表达方式\n" +
                "4. 只返回润色后的文案，不要返回其他说明\n\n" +
                "标题：" + title + "\n\n" +
                "原始内容：" + content;
    }

    private String generateTitle(String content) {
        try {
            String url = baseUrl + "/v1/text/chatcompletion_v2";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String prompt = buildTitlePrompt(content);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.9);
            requestBody.put("max_tokens", 200);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseResponse(response.getBody());
            }

            throw new RuntimeException("AI API returned non-OK status: " + response.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("标题生成失败: " + e.getMessage());
        }
    }

    private String buildTitlePrompt(String content) {
        return "你是一个小红书标题创作助手。请根据以下内容生成3个吸引人的标题选项。\n\n" +
                "要求：\n" +
                "1. 每个标题不超过20字\n" +
                "2. 标题要吸引人、有点击欲望\n" +
                "3. 符合小红书平台风格\n" +
                "4. 返回JSON数组格式，例如：[\"标题1\", \"标题2\", \"标题3\"]\n" +
                "5. 不要返回其他任何文字说明\n\n" +
                "内容：" + content;
    }

    // ==================== 帖子问答 ====================

    public String answerQuestion(String title, String content, String question, List<Map<String, String>> history) {
        try {
            String url = baseUrl + "/v1/text/chatcompletion_v2";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String prompt = buildQAPrompt(title, content, question, history);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.5);
            requestBody.put("max_tokens", 400);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseResponse(response.getBody());
            }

            throw new RuntimeException("AI API returned non-OK status: " + response.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("问答失败: " + e.getMessage());
        }
    }

    private String buildQAPrompt(String title, String content, String question, List<Map<String, String>> history) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个小红书帖子问答助手。请基于以下帖子的标题和内容回答用户的问题。\n\n");
        sb.append("帖子标题：").append(title).append("\n\n");
        sb.append("帖子内容：").append(content != null ? content : "（无内容）").append("\n\n");

        if (history != null && !history.isEmpty()) {
            sb.append("对话历史：\n");
            for (Map<String, String> msg : history) {
                String role = msg.get("role");
                String msgContent = msg.get("content");
                if ("user".equals(role)) {
                    sb.append("用户：").append(msgContent).append("\n");
                } else if ("assistant".equals(role)) {
                    sb.append("AI：").append(msgContent).append("\n");
                }
            }
            sb.append("\n");
        }

        sb.append("用户问题：").append(question).append("\n\n");
        sb.append("要求：\n");
        sb.append("1. 基于帖子内容回答问题\n");
        sb.append("2. 如果问题与帖子内容无关，礼貌地说明只能回答与帖子相关的问题\n");
        sb.append("3. 回答控制在200字以内\n");
        sb.append("4. 只返回回答内容，不要返回其他说明");

        return sb.toString();
    }
}