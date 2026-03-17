package com.xiaohongshu.service;

import com.xiaohongshu.entity.Follow;
import com.xiaohongshu.entity.User;
import com.xiaohongshu.repository.FollowRepository;
import com.xiaohongshu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        if (user.getNickname() == null) {
            user.setNickname("");
        }
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user.get();
        }
        throw new RuntimeException("Invalid username or password");
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException("User not found");
        }
        return userRepository.save(user);
    }

    // Follow methods
    @Transactional
    public boolean followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            return false;
        }
        User follower = getUserById(followerId);
        User following = getUserById(followingId);

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            return false;
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        followRepository.save(follow);
        return true;
    }

    @Transactional
    public boolean unfollowUser(Long followerId, Long followingId) {
        User follower = getUserById(followerId);
        User following = getUserById(followingId);

        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowing(follower, following);
        if (existingFollow.isPresent()) {
            followRepository.delete(existingFollow.get());
            return true;
        }
        return false;
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        User follower = getUserById(followerId);
        User following = getUserById(followingId);
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }

    public List<Map<String, Object>> getFollowers(Long userId) {
        User user = getUserById(userId);
        List<Follow> follows = followRepository.findByFollowingOrderByCreatedAtDesc(user);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Follow f : follows) {
            result.add(userToMap(f.getFollower()));
        }
        return result;
    }

    public List<Map<String, Object>> getFollowing(Long userId) {
        User user = getUserById(userId);
        List<Follow> follows = followRepository.findByFollower(user);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Follow f : follows) {
            result.add(userToMap(f.getFollowing()));
        }
        return result;
    }

    public long getFollowersCount(Long userId) {
        User user = getUserById(userId);
        return followRepository.countByFollowing(user);
    }

    public long getFollowingCount(Long userId) {
        User user = getUserById(userId);
        return followRepository.countByFollower(user);
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
