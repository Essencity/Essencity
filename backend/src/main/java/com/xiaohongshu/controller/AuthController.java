package com.xiaohongshu.controller;

import com.xiaohongshu.config.JwtUtil;
import com.xiaohongshu.entity.User;
import com.xiaohongshu.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
                user.setAvatar("/uploads/default_avatar.png");
            }
            User registered = userService.register(user);
            String token = jwtUtil.generateToken(registered.getId(), registered.getUsername());

            Map<String, Object> response = userToMap(registered);
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            User user = userService.login(
                    loginRequest.get("username"),
                    loginRequest.get("password"));
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());

            Map<String, Object> response = userToMap(user);
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Login failed for user {}: {}", loginRequest.get("username"), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "用户名或密码错误"));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> updates) {
        try {
            User currentUser = getCurrentUser();
            User user = userService.getUserById(currentUser.getId());

            if (updates.containsKey("nickname")) {
                user.setNickname((String) updates.get("nickname"));
            }
            if (updates.containsKey("gender")) {
                user.setGender((String) updates.get("gender"));
            }
            if (updates.containsKey("bio")) {
                user.setBio((String) updates.get("bio"));
            }
            if (updates.containsKey("avatar")) {
                user.setAvatar((String) updates.get("avatar"));
            }

            User updated = userService.updateUser(user);
            return ResponseEntity.ok(userToMap(updated));
        } catch (Exception e) {
            log.error("Failed to update profile: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "更新个人信息失败"));
        }
    }

    @PostMapping("/follow")
    public ResponseEntity<?> followUser(@RequestBody Map<String, Object> body) {
        try {
            User currentUser = getCurrentUser();
            Long followingId = ((Number) body.get("followingId")).longValue();
            boolean success = userService.followUser(currentUser.getId(), followingId);
            return ResponseEntity.ok(Map.of("success", success));
        } catch (Exception e) {
            log.error("Follow failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "操作失败"));
        }
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(@RequestBody Map<String, Object> body) {
        try {
            User currentUser = getCurrentUser();
            Long followingId = ((Number) body.get("followingId")).longValue();
            boolean success = userService.unfollowUser(currentUser.getId(), followingId);
            return ResponseEntity.ok(Map.of("success", success));
        } catch (Exception e) {
            log.error("Unfollow failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "操作失败"));
        }
    }

    @GetMapping("/following-status")
    public ResponseEntity<?> getFollowingStatus(@RequestParam Long followingId) {
        try {
            User currentUser = getCurrentUser();
            boolean isFollowing = userService.isFollowing(currentUser.getId(), followingId);
            return ResponseEntity.ok(Map.of("success", true, "isFollowing", isFollowing));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", true, "isFollowing", false));
        }
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<?> getFollowers(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userService.getFollowers(userId));
        } catch (Exception e) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<?> getFollowing(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userService.getFollowing(userId));
        } catch (Exception e) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
    }

    @GetMapping("/followers-count/{userId}")
    public ResponseEntity<?> getFollowersCount(@PathVariable Long userId) {
        try {
            long count = userService.getFollowersCount(userId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("count", 0));
        }
    }

    @GetMapping("/following-count/{userId}")
    public ResponseEntity<?> getFollowingCount(@PathVariable Long userId) {
        try {
            long count = userService.getFollowingCount(userId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("count", 0));
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }
        throw new RuntimeException("未登录");
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("nickname", user.getNickname());
        map.put("avatar", user.getAvatar());
        map.put("bio", user.getBio());
        map.put("gender", user.getGender());
        return map;
    }
}
