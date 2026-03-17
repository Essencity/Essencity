<script setup>
import { ref } from 'vue'

const categories = [
  '推荐', '穿搭', '美食', '彩妆', '影视', '职场', '情感', '家居', '游戏', '旅行', '健身'
]

const emit = defineEmits(['change'])

const activeCategory = ref('推荐')

const handleCategoryClick = (cat) => {
  activeCategory.value = cat
  emit('change', cat)
}
</script>

<template>
  <div class="category-tabs">
    <button 
      v-for="cat in categories" 
      :key="cat"
      class="tab-btn"
      :class="{ active: activeCategory === cat }"
      @click="handleCategoryClick(cat)"
    >
      {{ cat }}
    </button>
  </div>
</template>

<style scoped>
.category-tabs {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 40px;
  overflow-x: auto;
  white-space: nowrap;
  background: var(--white);
  /* Hide scrollbar but keep functionality */
  scrollbar-width: none;
  -ms-overflow-style: none;
  position: sticky;
  top: var(--header-height);
  z-index: 80;
}

.category-tabs::-webkit-scrollbar {
  display: none;
}

.tab-btn {
  padding: 8px 16px;
  border-radius: var(--radius-full);
  font-size: 16px;
  color: var(--text-secondary);
  transition: all 0.2s;
  background: transparent;
}

.tab-btn:hover {
  background-color: var(--bg-color);
  color: var(--text-primary);
}

.tab-btn.active {
  background-color: var(--bg-color);
  color: var(--text-primary);
  font-weight: 600;
}
</style>
