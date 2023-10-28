<template>
  <div class="main-default">
    <LayoutHeader />
    <div class="content">
      <slot />
    </div>
  </div>
</template>

<script lang="ts" setup></script>

<style scope lang="scss">
.main-default {
  .content {
    width: 1200px;
    margin: 0 auto;
    padding-top: 60px;
  }
}

// 媒体查询
@media screen and (max-width: 768px) {
  .main {
    width: 100%;
  }
  .content {
    width: 100%;
  }
}
</style>
