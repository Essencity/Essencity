

API.md
======

Essencity API 文档
================

Version: v1.1
日期: 2026-03-25
Base URL:
    http://localhost:8080/api

Swagger UI:
    http://localhost:8080/swagger-ui.html

OpenAPI 规范:
    docs/api.yaml

* * *

1 API 设计规范
==========

1.1 RESTful 规范
--------------

| 操作  | 方法     | 示例          |
| --- | ------ | ----------- |
| 查询  | GET    | /posts      |
| 新增  | POST   | /posts      |
| 更新  | PUT    | /posts/{id} |
| 删除  | DELETE | /posts/{id} |

资源命名使用 **复数名词**：
    /posts
    /comments
    /notifications
    /auth

* * *

1.2 数据格式
========

所有接口使用 JSON。

请求头：
    Content-Type: application/json

* * *

1.3 统一响应结构
==========

所有 API 返回统一结构（部分接口直接返回数据）：
    {
      "code": 200,
      "message": "success",
      "data": {}
    }

字段说明：

| 字段      | 说明   |
| ------- | ---- |
| code    | 状态码  |
| message | 提示信息 |
| data    | 返回数据 |

* * *

1.4 分页规范
========

分页接口统一参数：

| 参数   | 说明       |
| ---- | -------- |
| page | 页码（从1开始） |
| size | 每页数量     |

响应结构：
    {
      "list": [],
      "page": 1,
      "size": 10,
      "total": 100
    }

* * *

2 认证机制
======

系统使用 **JWT Token 认证**。

实际实现：登录成功后前端存储用户信息，后续请求通过用户ID参数传递。

登录成功返回用户信息：
    {
      "id": 1,
      "username": "test",
      "nickname": "测试用户",
      "avatar": "/uploads/avatar.png",
      "bio": "",
      "gender": "",
      "token": "jwt_token"
    }

* * *

3 错误码
=====

| Code | 含义    |
| ---- | ----- |
| 200  | 成功    |
| 400  | 参数错误  |
| 401  | 未登录   |
| 403  | 权限不足  |
| 404  | 资源不存在 |
| 409  | 数据冲突  |
| 500  | 服务器错误 |

示例：
    {
      "code":401,
      "message":"Unauthorized",
      "data":null
    }

* * *

4 认证模块
======

路径前缀：
    /api/auth

* * *

4.1 用户登录
========

### POST

    /auth/login

### Request

    {
      "username": "test",
      "password": "123456"
    }

### Response

    {
      "id": 1,
      "username": "test",
      "nickname": "测试用户",
      "avatar": "/uploads/avatar.png",
      "bio": "",
      "gender": "",
      "token": "jwt_token"
    }

### curl 示例

    curl -X POST http://localhost:8080/api/auth/login \
      -H "Content-Type: application/json" \
      -d '{"username":"test","password":"123456"}'

* * *

4.2 用户注册
========

### POST

    /auth/register

### Request

    {
      "username": "test",
      "password": "123456"
    }

### Response

    返回用户信息（同登录）

* * *

4.3 更新用户资料
============

### PUT

    /auth/profile

### Request

    {
      "nickname": "新昵称",
      "bio": "个人简介",
      "gender": "男"
    }

### Response

    返回更新后的用户信息

* * *

4.4 获取关注状态
============

### GET

    /auth/following-status

### Parameters

| 参数 | 说明 |
|------|------|
| followerId | 粉丝ID |
| followingId | 被关注者ID |

### Response

    {
      "success": true,
      "isFollowing": true
    }

* * *

4.5 关注用户
========

### POST

    /auth/follow

### Request

    {
      "followerId": 1,
      "followingId": 2
    }

### Response

    {
      "success": true
    }

* * *

4.6 取消关注
========

### POST

    /auth/unfollow

### Request

    {
      "followerId": 1,
      "followingId": 2
    }

* * *

4.7 获取关注数
============

### GET

    /auth/following-count/{userId}

### Response

    {
      "count": 100
    }

* * *

4.8 获取粉丝数
============

### GET

    /auth/followers-count/{userId}

### Response

    {
      "count": 50
    }

* * *

4.9 获取粉丝列表
============

### GET

    /auth/followers/{userId}

### Response

    [
      {
        "id": 1,
        "username": "user1",
        "nickname": "用户1",
        "avatar": "/uploads/avatar.png"
      }
    ]

* * *

4.10 获取关注列表
=============

### GET

    /auth/following/{userId}

### Response

    同粉丝列表格式

* * *

5 帖子模块
======

路径：
    /api/posts

* * *

5.1 获取帖子列表
============

### GET

    /posts

### Parameters

| 参数   | 说明     |
|------|----------|
| title | 搜索标题关键词 |
| tag   | 标签筛选   |
| page  | 页码     |
| size  | 每页数量   |

### Response

    [
      {
        "id": 1,
        "title": "标题",
        "description": "描述内容",
        "type": "image",
        "url": "/uploads/1.jpg",
        "coverUrl": "/uploads/cover.jpg",
        "author": {
          "id": 1,
          "nickname": "作者",
          "avatar": "/uploads/avatar.png"
        },
        "tag": "穿搭",
        "likeCount": 10,
        "collectionCount": 5,
        "createdAt": "2026-03-25T10:00:00Z"
      }
    ]

* * *

5.2 创建帖子
=========

### POST

    /posts

### Request

    {
      "title": "今日穿搭",
      "description": "分享一下今日穿搭",
      "type": "image",
      "url": "/uploads/1.jpg",
      "coverUrl": "/uploads/cover.jpg",
      "author_id": 1,
      "tag": "穿搭"
    }

### type 说明

| 值 | 说明 |
|----|------|
| video | 视频 |
| image | 图片 |
| article | 文章 |

* * *

5.3 上传媒体文件
=============

### POST

    /posts/upload

### Content-Type

    multipart/form-data

### Request

    form-data: file (文件)

### Response

    {
      "url": "/uploads/2026/03/video_abc123.mp4"
    }

### 限制

| 项目   | 限制               |
| ---- | ---------------- |
| 视频大小 | ≤20GB            |
| 图片大小 | ≤20MB            |
| 格式   | mp4/mov/jpg/png/webp |

* * *

5.4 获取帖子详情
============

### GET

    /posts/{id}

### Response

    {
      "id": 1,
      "title": "标题",
      "description": "描述",
      "type": "video",
      "url": "/uploads/video.mp4",
      "coverUrl": "/uploads/cover.jpg",
      "videoUrl": "/uploads/video.mp4",
      "imageUrl": "/uploads/image.jpg",
      "imageUrls": ["/uploads/1.jpg", "/uploads/2.jpg"],
      "author": {...},
      "authorId": 1,
      "authorNickname": "作者",
      "authorAvatar": "/uploads/avatar.png",
      "likeCount": 20,
      "collectionCount": 10,
      "createdAt": "2026-03-25T10:00:00Z"
    }

* * *

5.5 获取用户帖子统计
================

### GET

    /posts/user/{userId}/stats

### Response

    {
      "postCount": 10,
      "likeCount": 100,
      "collectCount": 50
    }

* * *

5.6 获取用户收藏列表
================

### GET

    /posts/user/{userId}/collections

### Response

    [
      {
        "id": 1,
        "post": {
          "id": 100,
          "title": "帖子标题",
          "coverUrl": "/uploads/cover.jpg"
        }
      }
    ]

* * *

5.7 获取用户点赞列表
================

### GET

    /posts/user/{userId}/likes

### Response

    [
      {
        "id": 1,
        "post": {
          "id": 100,
          "title": "帖子标题",
          "coverUrl": "/uploads/cover.jpg"
        }
      }
    ]

* * *

6 点赞模块
======

路径：
    /api/posts

* * *

6.1 点赞帖子
=========

### POST

    /posts/{id}/like

### Request

    {
      "userId": 1
    }

* * *

6.2 取消点赞
=========

### POST

    /posts/{id}/unlike

### Request

    {
      "userId": 1
    }

* * *

6.3 获取点赞状态
=============

### GET

    /posts/{id}/like/status?userId=1

### Response

    {
      "liked": true,
      "likeCount": 100
    }

* * *

7 收藏模块
======

7.1 收藏帖子
=========

### POST

    /posts/{id}/collect

### Request

    {
      "userId": 1
    }

* * *

7.2 取消收藏
=========

### POST

    /posts/{id}/uncollect

### Request

    {
      "userId": 1
    }

* * *

7.3 获取收藏状态
=============

### GET

    /posts/{id}/collect/status?userId=1

### Response

    {
      "collected": true,
      "collectionCount": 50
    }

* * *

8 评论模块
======

路径：
    /api/posts/{postId}/comments

* * *

8.1 获取评论列表
============

### GET

    /posts/{postId}/comments

### Response

    [
      {
        "id": 1,
        "postId": 100,
        "userId": 1,
        "user": {
          "id": 1,
          "nickname": "用户",
          "avatar": "/uploads/avatar.png"
        },
        "content": "评论内容",
        "parentId": null,
        "subComments": [
          {
            "id": 2,
            "user": {...},
            "content": "回复内容",
            "replyToUser": {"nickname": "被回复者"},
            "createdAt": "2026-03-25T10:00:00Z"
          }
        ],
        "createdAt": "2026-03-25T10:00:00Z"
      }
    ]

* * *

8.2 添加评论
=========

### POST

    /posts/{postId}/comments

### Request

    {
      "userId": 1,
      "content": "评论内容",
      "parent_id": null
    }

### parent_id 说明

- 不传或传 null：顶级评论
- 传评论ID：回复该评论

* * *

8.3 删除评论
=========

### DELETE

    /posts/comments/{commentId}

### Request

    {
      "userId": 1
    }

* * *

9 通知模块
======

路径：
    /api/notifications

* * *

9.1 获取通知列表
============

### GET

    /notifications?userId=1&type=comments

### Parameters

| 参数   | 说明         |
|------|--------------|
| userId | 用户ID       |
| type  | 通知类型      |

### type 值

| 值 | 说明 |
|----|------|
| comments | 评论和回复 |
| likes | 赞和收藏 |
| follows | 新增关注 |

### Response

    [
      {
        "id": 1,
        "type": "LIKE",
        "actor": {
          "id": 2,
          "nickname": "用户",
          "avatar": "/uploads/avatar.png"
        },
        "content": "评论内容（如果有）",
        "createdAt": "2026-03-25T10:00:00Z"
      }
    ]

### type 显示说明

| 实际type值 | 前端显示 |
|-----------|----------|
| LIKE | 赞了你的笔记 |
| COLLECTION | 收藏了你的笔记 |
| COMMENT | 评论了你的笔记 |
| REPLY | 回复了你的评论 |
| FOLLOW | 开始关注你了 |

* * *

10 文件上传
=======

通用上传接口：
    /api/uploads

Content-Type：
    multipart/form-data

* * *

11 接口调用流程示例
============

用户发布帖子流程：
    1. 登录获取用户信息
    2. POST /posts/upload 上传媒体文件
    3. POST /posts/upload 上传封面图（如需要）
    4. POST /posts 创建帖子
    5. 返回帖子详情

用户点赞流程：
    1. 检查登录状态
    2. POST /posts/{id}/like 点赞
    3. 更新本地状态

用户评论流程：
    1. 检查登录状态
    2. POST /posts/{id}/comments 添加评论
    3. 更新评论列表

* * *

12 附录：未实现功能
==============

以下功能在当前版本中暂未实现：

- 话题模块 (topics)
- 搜索模块 (search)
- 草稿模块 (drafts)
- AI 模块 (ai)
- 分页功能（部分接口）

如需使用这些功能，需要单独开发后端实现。
