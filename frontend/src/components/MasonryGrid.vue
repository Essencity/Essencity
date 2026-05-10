<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import PostCard from './PostCard.vue'

const props = defineProps({
  items: {
    type: Array,
    required: true
  }
})

const columnCount = ref(2)
const columns = ref([])

const updateColumnCount = () => {
  const width = window.innerWidth
  if (width <= 768) {
    columnCount.value = 2
  } else if (width >= 1380) {
    columnCount.value = 5
  } else if (width >= 1120) {
    columnCount.value = 4
  } else if (width >= 800) {
    columnCount.value = 3
  } else {
    columnCount.value = 2
  }
}

const distributeItems = () => {
  // Initialize columns
  const newColumns = Array.from({ length: columnCount.value }, () => [])
  
  // Simple distribution: round robin
  props.items.forEach((item, index) => {
    newColumns[index % columnCount.value].push(item)
  })
  
  columns.value = newColumns
}

watch(() => [props.items, columnCount.value], () => {
  distributeItems()
}, { deep: true })

onMounted(() => {
  updateColumnCount()
  distributeItems()
  window.addEventListener('resize', updateColumnCount)
})

onUnmounted(() => {
  window.removeEventListener('resize', updateColumnCount)
})
</script>

<template>
  <div class="masonry-grid">
    <div 
      v-for="(col, index) in columns" 
      :key="index" 
      class="masonry-column"
    >
      <PostCard 
        v-for="item in col" 
        :key="item.id" 
        :post="item" 
        @open-detail="$emit('open-detail', $event)"
      />
    </div>
  </div>
</template>

<style scoped>
.masonry-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: flex-start;
}

.masonry-column {
  width: 210px;
  display: flex;
  flex-direction: column;
}

@media (max-width: 768px) {
  .masonry-grid {
    gap: 8px;
    justify-content: space-between;
  }

  .masonry-column {
    width: calc((100% - 8px) / 2);
  }
}
</style>
