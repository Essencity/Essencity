package com.xiaohongshu.controller;

import com.xiaohongshu.dto.NotificationDTO;
import com.xiaohongshu.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<NotificationDTO> getNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "comments") String type) {
        return notificationService.getNotifications(userId, type);
    }
}
