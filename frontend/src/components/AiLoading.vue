<script setup>
defineProps({
  size: {
    type: String,
    default: 'medium',
    validator: (v) => ['small', 'medium', 'large'].includes(v)
  },
  text: {
    type: String,
    default: '加载中...'
  }
})
</script>

<template>
  <div class="ai-loading" :class="size">
    <div class="loading-dots">
      <span class="dot"></span>
      <span class="dot"></span>
      <span class="dot"></span>
    </div>
    <p v-if="text" class="loading-text">{{ text }}</p>
  </div>
</template>

<style scoped>
.ai-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.ai-loading.small {
  gap: 8px;
}

.ai-loading.small .loading-dots {
  gap: 4px;
}

.ai-loading.small .dot {
  width: 6px;
  height: 6px;
}

.ai-loading.medium .dot {
  width: 8px;
  height: 8px;
}

.ai-loading.large .dot {
  width: 12px;
  height: 12px;
}

.loading-dots {
  display: flex;
  gap: 6px;
}

.dot {
  background: var(--primary-color);
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.dot:nth-child(1) {
  animation-delay: -0.32s;
}

.dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.loading-text {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.ai-loading.small .loading-text {
  font-size: 12px;
}

.ai-loading.large .loading-text {
  font-size: 16px;
}
</style>
