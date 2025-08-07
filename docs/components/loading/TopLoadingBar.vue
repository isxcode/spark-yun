<template>
  <div
    v-show="isVisible"
    :class="[
      'top-loading-bar',
      {
        'top-loading-bar--loading': isLoading,
        'top-loading-bar--complete': isComplete,
        'top-loading-bar--error': isError
      }
    ]"
  >
    <div
      class="top-loading-bar__progress"
      :style="{ width: progress + '%' }"
    ></div>
  </div>
</template>

<script setup lang="ts">
interface LoadingBarState {
  isVisible: boolean;
  isLoading: boolean;
  isComplete: boolean;
  isError: boolean;
  progress: number;
}

const state = reactive<LoadingBarState>({
  isVisible: false,
  isLoading: false,
  isComplete: false,
  isError: false,
  progress: 0
});

const { isVisible, isLoading, isComplete, isError, progress } = toRefs(state);

let progressTimer: NodeJS.Timeout | null = null;
let hideTimer: NodeJS.Timeout | null = null;

// 开始loading
const start = () => {
  reset();
  state.isVisible = true;
  state.isLoading = true;
  state.progress = 0;

  // 模拟进度增长
  progressTimer = setInterval(() => {
    if (state.progress < 90) {
      const increment = Math.random() * 15;
      state.progress = Math.min(state.progress + increment, 90);
    }
  }, 200);
};

// 完成loading
const finish = () => {
  if (progressTimer) {
    clearInterval(progressTimer);
    progressTimer = null;
  }

  state.progress = 100;
  state.isLoading = false;
  state.isComplete = true;

  // 延迟隐藏
  hideTimer = setTimeout(() => {
    hide();
  }, 300);
};

// 错误状态
const error = () => {
  if (progressTimer) {
    clearInterval(progressTimer);
    progressTimer = null;
  }

  state.isLoading = false;
  state.isError = true;

  // 延迟隐藏
  hideTimer = setTimeout(() => {
    hide();
  }, 1000);
};

// 隐藏loading条
const hide = () => {
  state.isVisible = false;
  setTimeout(() => {
    reset();
  }, 300);
};

// 重置状态
const reset = () => {
  if (progressTimer) {
    clearInterval(progressTimer);
    progressTimer = null;
  }
  if (hideTimer) {
    clearTimeout(hideTimer);
    hideTimer = null;
  }

  state.isLoading = false;
  state.isComplete = false;
  state.isError = false;
  state.progress = 0;
};

// 暴露方法给父组件
defineExpose({
  start,
  finish,
  error,
  hide,
  reset
});

// 组件卸载时清理定时器
onBeforeUnmount(() => {
  reset();
});
</script>