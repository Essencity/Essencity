package com.xiaohongshu.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AIService 单元测试 - 使用 Mock 隔离外部 API
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AIServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private AIService aiService;

    @Test
    void testGenerateSummary_Success() {
        // 手动创建 AIService 并注入 mock RestTemplate
        aiService = new AIService(restTemplate, "test-api-key", "https://api.deepseek.com", "deepseek-v4-flash");

        Map<String, Object> mockResponse = Map.of(
            "choices", java.util.List.of(
                Map.of(
                    "message", Map.of(
                        "role", "assistant",
                        "content", "这是一篇关于旅行的笔记，介绍了作者在云南的所见所闻。"
                    )
                )
            )
        );

        when(restTemplate.exchange(
            anyString(),
            any(HttpMethod.class),
            any(),
            eq(Map.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        String result = aiService.generateSummary("云南旅行", "我在云南玩了7天...");

        assertNotNull(result);
        assertTrue(result.contains("云南") || result.contains("旅行"));
    }

    @Test
    void testGenerateSummary_WithEmptyContent() {
        aiService = new AIService(restTemplate, "test-api-key", "https://api.deepseek.com", "deepseek-v4-flash");

        Map<String, Object> mockResponse = Map.of(
            "choices", java.util.List.of(
                Map.of(
                    "message", Map.of(
                        "role", "assistant",
                        "content", "AI summary"
                    )
                )
            )
        );

        when(restTemplate.exchange(
            anyString(),
            any(HttpMethod.class),
            any(),
            eq(Map.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        String result = aiService.generateSummary("Test Title", "");

        assertNotNull(result);
    }

    @Test
    void testGenerateSummary_APIError() {
        aiService = new AIService(restTemplate, "test-api-key", "https://api.deepseek.com", "deepseek-v4-flash");

        when(restTemplate.exchange(
            anyString(),
            any(HttpMethod.class),
            any(),
            eq(Map.class)
        )).thenThrow(new RestClientException("Connection refused"));

        assertThrows(RuntimeException.class, () -> aiService.generateSummary("Test", "Content"));
    }

    @Test
    void testGenerateSummary_NonOKResponse() {
        aiService = new AIService(restTemplate, "test-api-key", "https://api.deepseek.com", "deepseek-v4-flash");

        when(restTemplate.exchange(
            anyString(),
            any(HttpMethod.class),
            any(),
            eq(Map.class)
        )).thenReturn(new ResponseEntity<>(Map.of(), HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(RuntimeException.class, () -> aiService.generateSummary("Test", "Content"));
    }
}