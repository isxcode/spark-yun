<template>
  <div>
    <!-- 顶部Loading条 - 优先渲染 -->
    <TopLoadingBar ref="topLoadingBarRef" />

    <!-- 主要内容 -->
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </div>
</template>

<script setup lang="ts">
// 优先导入TopLoadingBar组件
import TopLoadingBar from '~/components/loading/TopLoadingBar.vue';

// 设置TopLoadingBar实例
const topLoadingBarRef = ref<any>(null);
const { setInstance } = useTopLoading();

// 立即设置实例，不等待mounted
nextTick(() => {
  if (topLoadingBarRef.value) {
    setInstance(topLoadingBarRef.value);
  }
});

onMounted(() => {

  // 打印当前版本号
  console.log( '当前版本号: GH-2139' )

  // 确保实例已设置
  if (topLoadingBarRef.value) {
    setInstance(topLoadingBarRef.value);
  }
});
</script>

<style>
/* 全局样式，确保loading条在最顶层 */
body {
  margin: 0;
  padding: 0;
}

/* 确保loading条不会被其他元素遮挡 */
.top-loading-bar {
  z-index: 9999 !important;
}

/* 内联关键CSS - 确保loading条立即可见 */
.top-loading-bar {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  z-index: 9999 !important;
  height: 4px !important;
  background-color: transparent !important;
  transition: opacity 0.3s ease !important;
}

.top-loading-bar__progress {
  height: 100% !important;
  background: linear-gradient(90deg, #e25a1b 0%, #d4461a 50%, #c73e1d 100%) !important;
  transition: width 0.3s ease !important;
  border-radius: 0 3px 3px 0 !important;
  box-shadow: 0 0 12px rgba(226, 90, 27, 0.6) !important;
}

/* 预加载动画 */
@keyframes loading-shimmer {
  0% {
    background-position: -200px 0;
  }
  100% {
    background-position: calc(200px + 100%) 0;
  }
}

.top-loading-bar--loading .top-loading-bar__progress {
  background: linear-gradient(90deg, #e25a1b 0%, #d4461a 100%) !important;
  animation: loading-shimmer 2s infinite !important;
}
</style>
