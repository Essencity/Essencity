package com.xiaohongshu.controller;

import com.xiaohongshu.entity.User;
import com.xiaohongshu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
                user.setAvatar("http://localhost:3000/uploads/default_avatar.png");
            }
            return ResponseEntity.ok(userService.register(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            return ResponseEntity.ok(userService.login(
                    loginRequest.get("username"),
                    loginRequest.get("password")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> updates) {
        try {
            Long userId = ((Number) updates.get("userId")).longValue();
            User user = userService.getUserById(userId);

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

            return ResponseEntity.ok(userService.updateUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to update profile: " + e.getMessage()));
        }
    }

    // Follow endpoints
    @PostMapping("/follow")
    public ResponseEntity<?> followUser(@RequestBody Map<String, Object> body) {
        try {
            Long followerId = ((Number) body.get("followerId")).longValue();
            Long followingId = ((Number) body.get("followingId")).longValue();
            boolean success = userService.followUser(followerId, followingId);
            return ResponseEntity.ok(Map.of("success", success));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(@RequestBody Map<String, Object> body) {
        try {
            Long followerId = ((Number) body.get("followerId")).longValue();
            Long followingId = ((Number) body.get("followingId")).longValue();
            boolean success = userService.unfollowUser(followerId, followingId);
            return ResponseEntity.ok(Map.of("success", success));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/following-status")
    public ResponseEntity<?> getFollowingStatus(@RequestParam Long followerId, @RequestParam Long followingId) {
        try {
            boolean isFollowing = userService.isFollowing(followerId, followingId);
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
}
