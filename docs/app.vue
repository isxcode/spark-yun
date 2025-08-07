<template>
  <div>
    <!-- 顶部Loading条 -->
    <TopLoadingBar ref="topLoadingBarRef" />
    
    <!-- 主要内容 -->
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </div>
</template>

<script setup lang="ts">
// 导入TopLoadingBar组件
import TopLoadingBar from '~/components/loading/TopLoadingBar.vue';

// 设置TopLoadingBar实例
const topLoadingBarRef = ref<any>(null);
const { setInstance } = useTopLoading();

onMounted(() => {
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
</style>
