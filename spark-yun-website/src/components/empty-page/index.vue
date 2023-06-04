<!--
 * @Author: fanciNate
 * @Date: 2023-06-04 22:27:08
 * @LastEditTime: 2023-06-04 22:32:31
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /spark-yun/spark-yun-website/src/components/empty-page/index.vue
-->
<template>
  <div class="empty-page">
    <img class="empty-icon" src="./empty-page.png" alt="暂无数据" />
    <template v-if="$slots.default">
      <slot />
    </template>
    <span v-else class="empty-text"> {{ label }} </span>
  </div>
</template>

<script lang="ts" setup>
import { computed, defineProps, withDefaults } from "vue";

withDefaults(
  defineProps<{
    label?: string;
  }>(),
  {
    label: "暂无数据",
  }
);
</script>

<style lang="scss">
.empty-page {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin: 0;
  width: 120px;
  position: absolute !important;
  right: calc(50% - 60px);
  height: 100%;

  .empty-icon {
    width: auto;
    height: 72px;
    cursor: auto;
  }
  .empty-text {
    font-size: 12px;
    color: #b2b2b2;
    margin-top: 4px;
    line-height: normal;
  }
}
</style>
