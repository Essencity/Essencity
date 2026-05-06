package com.xiaohongshu.controller;

import com.xiaohongshu.dto.NotificationDTO;
import com.xiaohongshu.entity.User;
import com.xiaohongshu.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            @RequestParam(defaultValue = "comments") String type) {
        User currentUser = getCurrentUser();
        return notificationService.getNotifications(currentUser.getId(), type);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }
        throw new RuntimeException("未登录");
    }
}
