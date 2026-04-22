# 后端单元测试贡献文档

## 概述

本项目后端采用 **Spring Boot 3 + JUnit 5 + Mockito** 技术栈，通过 Mock 机制隔离数据库和外部依赖，实现核心业务逻辑的单元测试覆盖。

## 测试策略

### 1. 测试分层

```
┌─────────────────────────────────────┐
│         Controller Layer             │  ← 集成测试 (需要完整 Spring Context)
├─────────────────────────────────────┤
│          Service Layer              │  ← 单元测试 (已实现)
├─────────────────────────────────────┤
│        Repository Layer             │  ← Mock 隔离
├─────────────────────────────────────┤
│           External API              │  ← Mock 隔离
└─────────────────────────────────────┘
```

### 2. 测试原则

- **单一职责**：每个测试方法只验证一个行为
- **独立性**：测试之间无依赖，可并行执行
- **可重复性**：测试结果稳定，不受外部环境影响
- **Mock 隔离**：数据库、外部 API 调用全部 Mock

### 3. 技术选型

| 技术      | 用途      |
| ------- | ------- |
| JUnit 5 | 测试框架    |
| Mockito | Mock 框架 |
| JaCoCo  | 覆盖率统计   |

## 测试文件结构

```
backend/src/test/java/com/xiaohongshu/
├── service/
│   ├── UserServiceTest.java          # 用户服务测试 (27 用例)
│   ├── PostServiceTest.java           # 帖子服务测试 (44 用例)
│   ├── AIServiceTest.java            # AI 服务测试 (4 用例)
│   └── NotificationServiceTest.java   # 通知服务测试 (5 用例)
└── controller/                       # (待完善)
```

## 已完成测试用例

### UserServiceTest (27 个测试)

| 测试类别     | 测试用例                            | 覆盖方法                         |
| -------- | ------------------------------- | ---------------------------- |
| **注册**   | testRegister_Success            | register()                   |
|          | testRegister_DuplicateUsername  | register() - 重复用户名           |
|          | testRegister_WithEmptyNickname  | register() - 自动设置昵称          |
| **登录**   | testLogin_Success               | login()                      |
|          | testLogin_UserNotFound          | login() - 用户不存在              |
|          | testLogin_WrongPassword         | login() - 密码错误               |
| **用户查询** | testGetUserById_Success         | getUserById()                |
|          | testGetUserById_NotFound        | getUserById() - 用户不存在        |
| **用户更新** | testUpdateUser_Success          | updateUser()                 |
|          | testUpdateUser_NotFound         | updateUser() - 用户不存在         |
| **关注**   | testFollowUser_Success          | followUser()                 |
|          | testFollowUser_SelfFollow       | followUser() - 不能关注自己        |
|          | testFollowUser_AlreadyFollowing | followUser() - 已关注           |
|          | testFollowUser_UserNotFound     | followUser() - 用户不存在         |
| **取关**   | testUnfollowUser_Success        | unfollowUser()               |
|          | testUnfollowUser_NotFollowing   | unfollowUser() - 未关注         |
| **关注状态** | testIsFollowing_True            | isFollowing()                |
|          | testIsFollowing_False           | isFollowing()                |
| **粉丝列表** | testGetFollowers                | getFollowers()               |
|          | testGetFollowers_Empty          | getFollowers() - 无粉丝         |
| **关注列表** | testGetFollowing                | getFollowing()               |
|          | testGetFollowing_Empty          | getFollowing() - 未关注任何人      |
| **粉丝数**  | testGetFollowersCount           | getFollowersCount()          |
|          | testGetFollowersCount_Zero      | getFollowersCount() - 零粉丝    |
| **关注数**  | testGetFollowingCount           | getFollowingCount()          |
|          | testGetFollowingCount_Zero      | getFollowingCount() - 未关注任何人 |
| **工具方法** | testUserToMap                   | userToMap()                  |

### PostServiceTest (44 个测试)

| 测试类别     | 测试用例                                  | 覆盖方法                      |
| -------- | ------------------------------------- | ------------------------- |
| **创建帖子** | testCreatePost_Success                | createPost()              |
| **查询帖子** | testGetPostById_Success               | getPostById()             |
|          | testGetPostById_NotFound              | getPostById() - 不存在       |
| **帖子列表** | testGetAllPosts                       | getAllPosts()             |
|          | testGetAllPosts_Empty                 | getAllPosts() - 空列表       |
| **搜索帖子** | testSearchPosts                       | searchPosts()             |
|          | testSearchPosts_NoResults             | searchPosts() - 无结果       |
| **标签搜索** | testGetPostsByTag                     | getPostsByTag()           |
|          | testGetPostsByTag_NoResults           | getPostsByTag() - 无结果     |
|          | testSearchPostsByTag                  | searchPostsByTag()        |
| **更新帖子** | testUpdatePost_Success                | updatePost() - 全部更新       |
|          | testUpdatePost_PartialUpdate          | updatePost() - 部分更新       |
|          | testUpdatePost_UpdateType             | updatePost() - 更新类型       |
|          | testUpdatePost_UpdateUrl              | updatePost() - 更新 URL     |
|          | testUpdatePost_UpdateCoverUrl         | updatePost() - 更新封面       |
|          | testUpdatePost_UpdateTag              | updatePost() - 更新标签       |
| **删除帖子** | testDeletePost_Success                | deletePost()              |
|          | testDeletePost_WithRelatedData        | deletePost() - 含关联数据      |
| **作者帖子** | testGetPostsByAuthor                  | getPostsByAuthor()        |
| **点赞**   | testToggleLike_AddLike                | toggleLike() - 添加点赞       |
|          | testToggleLike_RemoveLike             | toggleLike() - 取消点赞       |
|          | testLikePost_Success                  | likePost()                |
|          | testLikePost_AlreadyLiked             | likePost() - 已点赞          |
|          | testUnlikePost_Success                | unlikePost()              |
|          | testUnlikePost_NotLiked               | unlikePost() - 未点赞        |
|          | testGetLikeCount                      | getLikeCount()            |
|          | testGetLikeCount_Zero                 | getLikeCount() - 零点赞      |
|          | testIsLikedBy_True                    | isLikedBy()               |
|          | testIsLikedBy_False                   | isLikedBy()               |
| **收藏**   | testToggleCollection_AddCollection    | toggleCollection() - 添加收藏 |
|          | testToggleCollection_RemoveCollection | toggleCollection() - 取消收藏 |
|          | testCollectPost_Success               | collectPost()             |
|          | testCollectPost_AlreadyCollected      | collectPost() - 已收藏       |
|          | testUncollectPost_Success             | uncollectPost()           |
|          | testUncollectPost_NotCollected        | uncollectPost() - 未收藏     |
|          | testGetCollectionCount                | getCollectionCount()      |
|          | testIsCollectedBy_True                | isCollectedBy()           |
|          | testIsCollectedBy_False               | isCollectedBy()           |
| **评论数**  | testGetCommentCount                   | getCommentCount()         |
| **删除评论** | testDeleteComment_Success             | deleteComment()           |
|          | testDeleteComment_NotOwner            | deleteComment() - 非所有者    |
|          | testDeleteComment_NotFound            | deleteComment() - 不存在     |
| **用户统计** | testGetUserStats                      | getUserStats()            |
|          | testGetUserStats_NoPosts              | getUserStats() - 无帖子      |

### AIServiceTest (4 个测试)

| 测试用例                                 | 说明          |
| ------------------------------------ | ----------- |
| testGenerateSummary_Success          | AI 总结生成成功   |
| testGenerateSummary_WithEmptyContent | 空内容处理       |
| testGenerateSummary_APIError         | API 连接错误处理  |
| testGenerateSummary_NonOKResponse    | 非 200 状态码处理 |

### NotificationServiceTest (5 个测试)

| 测试用例                                         | 说明      |
| -------------------------------------------- | ------- |
| testGetNotifications_Likes                   | 获取点赞通知  |
| testGetNotifications_Likes_ExcludesSelfLikes | 排除自点赞   |
| testGetNotifications_Comments                | 获取评论通知  |
| testGetNotifications_Follows                 | 获取关注通知  |
| testGetNotifications_UserNotFound            | 用户不存在处理 |

## 测试覆盖率

### 整体覆盖率

| 模块            | 行覆盖率    | 分支覆盖率   |
| ------------- | ------- | ------- |
| **Service 层** | **70%** | **70%** |
| Controller 层  | 1%      | 0%      |
| Entity 层      | 15%     | 1%      |
| Config 层      | 53%     | n/a     |
| **整体**        | **28%** | **14%** |

### Service 层详细覆盖

| Service             | 覆盖方法数 | 总方法数 | 覆盖率      |
| ------------------- | ----- | ---- | -------- |
| UserService         | 14    | 14   | **100%** |
| PostService         | 24    | 30   | **80%**  |
| AIService           | 4     | 5    | **80%**  |
| NotificationService | 5     | 5    | **100%** |

### 覆盖率目标

- ✅ Service 层覆盖率 > 70%
- ⏳ Controller 层覆盖率 (需要集成测试)
- ⏳ 整体覆盖率 > 50% (待完善)

## 运行测试

### 运行所有 Service 层测试

```bash
cd backend
mvn test -Dtest="*ServiceTest"
```

### 生成覆盖率报告

```bash
cd backend
mvn test jacoco:report
```

报告位置: `backend/target/site/jacoco/index.html`

### 查看 CSV 格式覆盖率数据

```bash
cat backend/target/site/jacoco/jacoco.csv
```

## 测试配置

### 测试数据库 (application-test.properties)

```properties
# H2 内存数据库
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL
spring.jpa.hibernate.ddl-auto=create-drop

# 文件上传测试目录
file.upload-dir=./test-uploads

# AI API 测试配置
minimax.api-key=test-api-key
minimax.base-url=https://api.minimax.chat
```

### JaCoCo 配置 (pom.xml)

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <includes>
                    <include>com.xiaohongshu.service.*</include>
                </includes>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.70</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

## 待完善项

### 1. Controller 层测试

需要解决以下问题：

- Spring Security 配置在 @WebMvcTest 下行为不一致
- 跨域配置需要完整 Context

可选方案：

- 使用 @SpringBootTest + MockMvc
- 添加 @AutoConfigureMockMvc(addFilters = false)

### 2. 集成测试

- 使用 @SpringBootTest 进行端到端测试
- 配合 Testcontainers 测试真实 MySQL

### 3. 边界条件补充

- 空指针边界
- 超长字符串输入
- 并发场景

## 常见问题

### Q: Mock 对象未匹配?

```java
// 错误：使用具体类型
when(userRepository.findById(1L)).thenReturn(Optional.of(user));

// 正确：使用 any() 匹配任意参数
when(userRepository.findById(any())).thenReturn(Optional.of(user));
```

### Q: @InjectMocks 无法注入?

```java
// AIService 没有无参构造函数，需要手动创建
@BeforeEach
void setUp() {
    aiService = new AIService(mockRestTemplate, apiKey, baseUrl, model);
}
```

### Q: 测试通过但覆盖率低?

检查是否：

- 测试了异常路径但未执行正常路径
- 使用了 @Ignore 跳过部分测试
- 反射修改了对象状态

## 参考资料

- [JUnit 5 文档](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito 文档](https://site.mockito.org/)
- [JaCoCo 文档](https://www.jacoco.org/jacoco/trunk/doc/)

---

**贡献者**: 林忠阳
**完成时间**: 2026-04-22
**测试用例数**: 80 个
**Service 层覆盖率**: 70% (分支覆盖率 70%)
