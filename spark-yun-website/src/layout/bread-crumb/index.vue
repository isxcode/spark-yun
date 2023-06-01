<!--
 * @Author: fanciNate
 * @Date: 2023-04-17 10:33:04
 * @LastEditTime: 2023-04-27 17:01:21
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/layout/bread-crumb/index.vue
-->
<template>
  <el-breadcrumb class="zqy-breadcrumb" :separator-icon="ArrowRight">
    <template v-for="bread in props.breadCrumbList" :key="bread.code">
      <el-breadcrumb-item v-if="!bread.hidden" :replace="true" :to="{ name: bread.code, query: bread.query ? bread.query : null }">
        {{ bread.name }}
      </el-breadcrumb-item>
    </template>
  </el-breadcrumb>
</template>

<script lang="ts" setup>
import { ArrowRight } from "@element-plus/icons-vue";
import { defineProps, withDefaults } from "vue";

interface BreadCrumb {
  name: string;
  code: string;
  hidden?: boolean;
  query?: any;
}

const props: any = withDefaults(
  defineProps<{
    breadCrumbList: any;
  }>(),
  {
    breadCrumbList: [],
  }
);
</script>

<style lang="scss">
.zqy-breadcrumb {
  height: 54px;
  display: flex;
  align-items: center;
  padding-left: 20px;
  background-color: $--app-light-color;
  border-bottom: 1px solid $--app-border-color;
}
</style>
