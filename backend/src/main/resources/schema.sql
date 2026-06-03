-- Essencity (仿小红书) 数据库结构
-- 数据库: xiaohongshu
-- 字符集: utf8mb4

CREATE DATABASE IF NOT EXISTS xiaohongshu
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE xiaohongshu;

-- ============================================================
-- 用户表
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(250) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(255),
    avatar VARCHAR(255),
    bio TEXT,
    gender VARCHAR(10)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 帖子表
-- ============================================================
CREATE TABLE IF NOT EXISTS posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL COMMENT 'image 或 video',
    url VARCHAR(255) NOT NULL COMMENT '资源 URL',
    cover_url VARCHAR(255) COMMENT '封面图 URL',
    author_id BIGINT NOT NULL,
    tag VARCHAR(50) COMMENT '标签：美食/穿搭/旅行/健身/家居/影视等',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ai_summary TEXT COMMENT 'AI 智能摘要',
    image_urls TEXT COMMENT '多图模式下的图片 URL 列表（JSON 数组）',
    FOREIGN KEY (author_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 点赞表
-- ============================================================
CREATE TABLE IF NOT EXISTS likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_post (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (post_id) REFERENCES posts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 收藏表
-- ============================================================
CREATE TABLE IF NOT EXISTS collections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_post_collection (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (post_id) REFERENCES posts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 评论表
-- ============================================================
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    parent_id BIGINT COMMENT '父评论 ID（回复专用）',
    reply_to_user_id BIGINT COMMENT '被回复用户 ID',
    reply_to_comment_id BIGINT COMMENT '被回复评论 ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 关注表
-- ============================================================
CREATE TABLE IF NOT EXISTS follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT NOT NULL COMMENT '关注者',
    following_id BIGINT NOT NULL COMMENT '被关注者',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_follow (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(id),
    FOREIGN KEY (following_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
