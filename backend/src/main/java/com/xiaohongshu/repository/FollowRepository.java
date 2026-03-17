package com.xiaohongshu.repository;

import com.xiaohongshu.entity.Follow;
import com.xiaohongshu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    boolean existsByFollowerAndFollowing(User follower, User following);

    List<Follow> findByFollowingOrderByCreatedAtDesc(User following);

    List<Follow> findByFollower(User follower);

    long countByFollowing(User following);

    long countByFollower(User follower);
}
