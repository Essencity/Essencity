package com.xiaohongshu.controller;

import com.xiaohongshu.dto.NotificationDTO;
import com.xiaohongshu.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    void getNotifications_ShouldReturnList() throws Exception {
        NotificationDTO dto = new NotificationDTO(
                1L, "LIKE",
                Map.of("id", 2L, "nickname", "User2", "avatar", "/av.png", "username", "user2"),
                Map.of("id", 1L, "title", "Post", "url", "/p.jpg", "coverUrl", "/c.jpg", "type", "image"),
                null, LocalDateTime.now());

        List<NotificationDTO> list = Arrays.asList(dto);
        when(notificationService.getNotifications(anyLong(), anyString())).thenReturn(list);

        mockMvc.perform(get("/notifications")
                        .param("userId", "1")
                        .param("type", "likes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("LIKE"))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getNotifications_ShouldReturnEmptyList() throws Exception {
        when(notificationService.getNotifications(anyLong(), anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/notifications")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
