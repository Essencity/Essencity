 

API.md
======

Essencity API 文档
================

Version: v1.0  
日期:2026-03-11
Base URL:
    http://localhost:8080/api

Swagger UI:
    http://localhost:8080/swagger-ui.html

* * *

1 API 设计规范
==========

1.1 RESTful 规范
--------------

| 操作  | 方法     | 示例          |
| --- | ------ | ----------- |
| 查询  | GET    | /notes      |
| 新增  | POST   | /notes      |
| 更新  | PUT    | /notes/{id} |
| 删除  | DELETE | /notes/{id} |

资源命名使用 **复数名词**：
    /users
    /notes
    /comments
    /topics

* * *

1.2 数据格式
========

所有接口使用 JSON。

请求头：
    Content-Type: application/json

* * *

1.3 统一响应结构
==========

所有 API 返回统一结构：
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

登录成功返回：
    {
      "token": "jwt_token"
    }

请求 Header：
    Authorization: Bearer {token}

示例：
    Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

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

4 用户模块
======

路径前缀：
    /api/users

* * *

4.1 用户注册
========

### POST

    /users/register

### Request

    {
      "username": "test",
      "password": "123456",
      "nickname": "测试用户"
    }

### Response

    {
      "code":200,
      "message":"success",
      "data":{
        "userId":1
      }
    }

### curl 示例

    curl -X POST http://localhost:8080/api/users/register \
    -H "Content-Type: application/json" \
    -d '{"username":"test","password":"123456","nickname":"测试用户"}'

* * *

4.2 用户登录
========

### POST

    /users/login

### Request

    {
      "username": "test",
      "password": "123456"
    }

### Response

    {
      "code":200,
      "message":"success",
      "data":{
        "token":"jwt_token",
        "user":{
          "id":1,
          "username":"test",
          "nickname":"测试用户",
          "avatar":""
        }
      }
    }

* * *

4.3 获取当前用户信息
============

### GET

    /users/me

Header：
    Authorization: Bearer token

Response：
    {
      "id":1,
      "username":"test",
      "nickname":"测试用户",
      "avatar":"",
      "bio":"",
      "school":"XX大学"
    }

* * *

4.4 更新用户信息
==========

### PUT

    /users/me

Request：
    {
      "nickname":"新昵称",
      "bio":"个人简介",
      "gender":1,
      "school":"XX大学"
    }

* * *

4.5 上传头像
========

### POST

    /users/avatar

Content-Type:
    multipart/form-data

* * *

5 笔记模块
======

路径：
    /api/notes

* * *

5.1 发布笔记
========

### POST

    /notes

Request：
    {
      "title":"今日穿搭",
      "content":"分享一下今日穿搭",
      "images":[
        "https://xxx.com/1.jpg",
        "https://xxx.com/2.jpg"
      ],
      "topics":[1,2]
    }

Response：
    {
      "code":200,
      "message":"success",
      "data":{
        "noteId":1001
      }
    }

* * *

5.2 获取笔记详情
==========

### GET

    /notes/{noteId}

Response：
    {
      "id":1001,
      "title":"今日穿搭",
      "content":"内容",
      "images":[],
      "likeCount":20,
      "collectCount":5,
      "commentCount":3,
      "viewCount":200
    }

* * *

5.3 首页笔记列表
==========

### GET

    /notes

参数：
    page
    size

Response：
    {
      "list":[
        {
          "id":1,
          "title":"标题",
          "cover":"image.jpg",
          "likeCount":10
        }
      ],
      "page":1,
      "size":10,
      "total":200
    }

* * *

5.4 更新笔记
========

### PUT

    /notes/{noteId}

* * *

5.5 删除笔记
========

### DELETE

    /notes/{noteId}

软删除。

* * *

5.6 点赞笔记
========

### POST

    /notes/{noteId}/like

再次调用为取消点赞。

* * *

5.7 收藏笔记
========

### POST

    /notes/{noteId}/collect

* * *

6 评论模块
======

路径：
    /api/comments

* * *

6.1 发表评论
========

### POST

    /comments

Request：
    {
      "noteId":1,
      "content":"评论内容",
      "parentId":null
    }

* * *

6.2 删除评论
========

### DELETE

    /comments/{commentId}

* * *

6.3 获取评论列表
==========

### GET

    /comments/note/{noteId}

Response：
    {
      "list":[
        {
          "id":1,
          "content":"评论",
          "user":{
            "id":1,
            "nickname":"用户"
          }
        }
      ]
    }

* * *

7 话题模块
======

路径：
    /api/topics

* * *

7.1 话题列表
========

### GET

    /topics

* * *

7.2 热门话题
========

### GET

    /topics/hot

* * *

7.3 话题详情
========

### GET

    /topics/{topicId}

* * *

7.4 话题笔记
========

### GET

    /topics/{topicId}/notes

* * *

8 搜索模块
======

路径：
    /api/search

* * *

8.1 搜索笔记
========

### GET

    /search/notes

参数：
    keyword
    page
    size

* * *

8.2 搜索用户
========

### GET

    /search/users

* * *

8.3 搜索话题
========

### GET

    /search/topics

* * *

9 草稿模块
======

路径：
    /api/drafts

* * *

9.1 保存草稿
========

### POST

    /drafts

* * *

9.2 草稿列表
========

### GET

    /drafts

* * *

9.3 草稿详情
========

### GET

    /drafts/{draftId}

* * *

9.4 删除草稿
========

### DELETE

    /drafts/{draftId}

* * *

10 AI 模块
========

路径：
    /api/ai

* * *

10.1 生成 AI 总结
=============

### POST

    /ai/summary/{noteId}

Response：
    {
      "summary":"AI生成的总结"
    }

* * *

11 文件上传
=======

上传接口：
    /upload

Content-Type：
    multipart/form-data

限制：

| 项目   | 限制               |
| ---- | ---------------- |
| 图片大小 | ≤5MB             |
| 图片数量 | ≤9               |
| 格式   | jpg/png/gif/webp |

* * *

12 接口调用流程示例
===========

用户发笔记流程：
    登录
     ↓
    获取 token
     ↓
    上传图片
     ↓
    POST /notes
     ↓
    返回 noteId

AI 总结流程：
    点击 AI 总结
     ↓
    检查数据库是否存在 summary
     ↓
    存在 → 返回
     ↓
    不存在 → 调用 AI API
     ↓
    保存 summary
     ↓
    返回前端 
