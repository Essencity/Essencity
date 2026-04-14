# 林烨澄 - 前端功能迭代贡献说明

## 基本信息

- **姓名**: 林烨澄
- **学号**: 2412190630
- **日期**: 2026-04-12

---

## 贡献内容

### 1. AI 总结功能修复

**问题**：点击前端"AI 总结"按钮时，后端报错 `java.lang.RuntimeException: 无法解析AI响应格式`。

**原因**：`AIService.java` 的 `parseResponse` 方法期望的响应格式为 `choices[0].messages[0].content`，但 MiniMax API 实际返回的是 `choices[0].message.content`。

**修复**：通知后端同学修改 `backend/src/main/java/com/xiaohongshu/service/AIService.java` 的 `parseResponse` 方法。

### 2. 帖子编辑功能实现

实现了完整的帖子编辑功能，用户可以编辑自己发布的帖子。

**修改文件**：

- `frontend/src/App.vue`
  - 新增 `editingPost` ref 和 `handleEditPost` 方法
  - 处理编辑帖子后的导航和数据传递

- `frontend/src/components/PostDetailModal.vue`
  - 添加编辑按钮到菜单
  - 点击编辑时关闭弹窗并跳转到发布页

- `frontend/src/components/CreationPage.vue`
  - 添加 `editingPost` prop 接收编辑数据
  - 通过 `watch` 监听 `editingPost` 变化，自动填充表单
  - 根据帖子类型（视频/图片）切换对应 Tab
  - 编辑模式下隐藏上传区域，直接显示图片网格
  - 发布按钮文字改为"更新"

**核心代码**：

```javascript
// watch with immediate: true 确保编辑数据能正确填充
watch(() => props.editingPost, (post) => {
  if (post) {
    title.value = post.title || ''
    content.value = post.description || ''
    selectedTag.value = post.tag || ''

    if (post.type === 'video') {
      activeTab.value = 'video'
      videoUrl.value = post.url || ''
      coverUrl.value = post.coverUrl || ''
    } else {
      activeTab.value = 'image'
      const imgUrl = post.image || post.url || post.imageUrl
      if (imgUrl) {
        imageUrls.value = [imgUrl]
      }
    }
  }
}, { immediate: true })
```

### 3. 页面交互问题修复

**问题一：个人主页显示关注自己的按钮**

**修复**：在 `ProfilePage.vue` 中添加显式检查，防止显示对自己主页的关注按钮：

```vue
<template v-else-if="currentUser && userId && currentUser.id !== userId">
```

**问题二：点击编辑后弹窗未关闭**

**修复**：在 `handleEditPost` 中使用 `setTimeout` 延迟设置编辑数据，确保弹窗先关闭再跳转：

```javascript
const handleEditPost = (post) => {
  showPostDetail.value = false
  selectedPost.value = null
  setTimeout(() => {
    editingPost.value = post
    currentView.value = 'publish'
  }, 100)
}
```

**问题三：编辑模式下图片不显示**

**原因**：模板条件 `imageFiles.length === 0` 在编辑模式下为 true（因为 `imageFiles` 是空的），导致显示上传区域而非图片网格。

**修复**：将条件改为 `imageFiles.length === 0 && imageUrls.length === 0`：

```vue
<template v-if="imageFiles.length === 0 && imageUrls.length === 0">
```

**问题四：Vue 3 `<script setup>` 变量声明顺序问题**

**现象**：`Cannot access 'imageUrls' before initialization` 错误。

**原因**：`watch` 的 `{ immediate: true }` 在 setup 阶段执行，此时 `imageUrls` 尚未初始化。

**修复**：将 `imageUrls` 等 ref 的声明放在 watch 之前：

```javascript
// Image handling - MUST be declared before watch with immediate: true
const imageFiles = ref([])
const imageUrls = ref([])
const imageInfos = ref([])
const imageInputRef = ref(null)

// Editing mode
const isEditMode = computed(() => !!props.editingPost)

watch(() => props.editingPost, (post) => { ... }, { immediate: true })
```

### 4. 其他交互优化

- 帖子详情弹窗添加 `@edit` 事件绑定
- 将 `editingPost` prop 传递给 `CreationPage` 组件
- 模板中图片计数从 `imageFiles.length` 改为 `imageUrls.length`

---

## 技术要点

### Vue 3 Composition API 最佳实践

1. **`watch` 与 `{ immediate: true }`**：在 `<script setup>` 中使用 `watch` 监听 prop 变化时，若要立即执行，需确保被监听的 reactive 依赖在声明顺序上位于 watch 之前。

2. **组件通信**：使用 `defineProps` 和 `defineEmits` 进行类型安全的父子组件通信。

3. **条件渲染**：编辑模式使用 `isEditMode` computed 属性判断，避免重复逻辑。

### 弹窗关闭与导航时序

使用 `setTimeout` 分离关闭弹窗和设置编辑数据的执行时机，避免 Vue 动画未完成导致的视觉问题。

---

## 相关文件

| 文件路径 | 说明 |
| --- | --- |
| frontend/src/App.vue | 主组件，添加编辑状态管理 |
| frontend/src/components/PostDetailModal.vue | 帖子详情弹窗，添加编辑按钮 |
| frontend/src/components/CreationPage.vue | 发布/编辑页面，核心编辑功能 |
| frontend/src/components/ProfilePage.vue | 个人主页，修复关注按钮显示 |

---

## 遇到的问题和解决

### 问题一：编辑时图片不显示

**现象**：进入编辑模式后，上传区域仍然显示，没有显示原帖子图片。

**解决**：修改模板条件判断 `imageFiles.length === 0` 为 `imageFiles.length === 0 && imageUrls.length === 0`，在 `imageUrls` 有值时显示图片网格。

### 问题二：Cannot access 'imageUrls' before initialization

**现象**：刷新页面后报错，编辑功能无法使用。

**解决**：调整变量声明顺序，将 `imageUrls` 等 ref 移动到 watch 声明之前。

### 问题三：405 Method Not Allowed

**现象**：点击更新按钮后 PUT 请求返回 405 错误。

**解决**：后端需要确保 `@CrossOrigin` 注解包含 `PUT` 方法，并在 `SecurityFilterChain` 中放行所有请求。

---

## 心得体会

1. **Vue 3 `<script setup>` 的执行顺序**：所有 `ref` 和 `reactive` 声明会在 setup 函数开始时完成初始化，但 `watch` 的 `immediate: true` 回调会在声明点立即执行，需要注意依赖的声明顺序。

2. **父子组件数据传递**：编辑功能涉及多个组件之间的数据传递，需要确保 prop 和 emit 的类型定义正确。

3. **用户体验细节**：编辑和发布是两种不同操作，UI 上需要区分（如按钮文字、标题显示、是否显示上传区域等）。
