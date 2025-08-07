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
let forceFinishTimer: NodeJS.Timeout | null = null;

// 开始loading
const start = () => {
  reset();

  // 隐藏预加载loading条
  if (process.client && (window as any).__preloadLoading) {
    (window as any).__preloadLoading.hide();
  }

  state.isVisible = true;
  state.isLoading = true;
  state.progress = 0;

  // 不再使用模拟进度，等待真实进度更新
  // 设置强制完成定时器，防止loading卡住
  forceFinishTimer = setTimeout(() => {
    if (state.isLoading && !state.isComplete) {
      finish();
    }
  }, 10000); // 10秒后强制完成
};

// 设置进度
const setProgress = (progress: number) => {
  if (state.isLoading && !state.isComplete) {
    state.progress = Math.min(Math.max(progress, 0), 100);
  }
};

// 完成loading
const finish = () => {
  // 清理所有定时器
  if (progressTimer) {
    clearInterval(progressTimer);
    progressTimer = null;
  }
  if (forceFinishTimer) {
    clearTimeout(forceFinishTimer);
    forceFinishTimer = null;
  }

  // 如果已经完成，避免重复执行
  if (state.isComplete) {
    return;
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
  // 清理所有定时器
  if (progressTimer) {
    clearInterval(progressTimer);
    progressTimer = null;
  }
  if (forceFinishTimer) {
    clearTimeout(forceFinishTimer);
    forceFinishTimer = null;
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
  // 清理所有定时器
  if (progressTimer) {
    clearInterval(progressTimer);
    progressTimer = null;
  }
  if (hideTimer) {
    clearTimeout(hideTimer);
    hideTimer = null;
  }
  if (forceFinishTimer) {
    clearTimeout(forceFinishTimer);
    forceFinishTimer = null;
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
  reset,
  setProgress
});

// 组件卸载时清理定时器
onBeforeUnmount(() => {
  reset();
});
</script>

<style lang="scss" scoped>
.top-loading-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 9999;
  height: 4px;
  background-color: transparent;
  transition: opacity 0.3s ease;

  &__progress {
    height: 100%;
    background: linear-gradient(90deg, #e25a1b 0%, #d4461a 50%, #c73e1d 100%);
    transition: width 0.3s ease;
    border-radius: 0 3px 3px 0;
    box-shadow: 0 0 12px rgba(226, 90, 27, 0.6);
  }

  &--loading {
    .top-loading-bar__progress {
      background: linear-gradient(90deg, #e25a1b 0%, #d4461a 100%);
      animation: loading-shimmer 2s infinite;
    }
  }

  &--complete {
    .top-loading-bar__progress {
      background: #d4461a;
      transition: width 0.1s ease;
    }
  }

  &--error {
    .top-loading-bar__progress {
      background: #f56c6c;
    }
  }
}

@keyframes loading-shimmer {
  0% {
    background-position: -200px 0;
  }
  100% {
    background-position: calc(200px + 100%) 0;
  }
}

/* 深橙色主题色 */
.top-loading-bar {
  &__progress {
    background: linear-gradient(90deg, rgba(226, 90, 27, 0.95) 0%, rgba(212, 70, 26, 0.95) 50%, rgba(199, 62, 29, 0.95) 100%);
  }

  &--loading {
    .top-loading-bar__progress {
      background: linear-gradient(90deg, rgba(226, 90, 27, 0.95) 0%, rgba(212, 70, 26, 0.95) 100%);
    }
  }

  &--complete {
    .top-loading-bar__progress {
      background: rgba(212, 70, 26, 0.95);
    }
  }

  &--error {
    .top-loading-bar__progress {
      background: rgba(245, 108, 108, 0.9);
    }
  }
}
</style>