<template>
  <el-breadcrumb
    class="zqy-breadcrumb"
    :separator-icon="ArrowRight"
  >
    <template
      v-for="bread in props.breadCrumbList"
      :key="bread.code"
    >
      <el-breadcrumb-item
        v-if="!bread.hidden"
        :replace="true"
        :to="{ name: bread.code, query: bread.query ? bread.query : null }"
      >
        {{ bread.name }}
      </el-breadcrumb-item>
    </template>
  </el-breadcrumb>
</template>

<script lang="ts" setup>
import { ArrowRight } from '@element-plus/icons-vue'
import { defineProps, withDefaults } from 'vue'

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
    breadCrumbList: []
  }
)
</script>

<style lang="scss">
.zqy-breadcrumb {
  height: 55px;
  display: flex;
  align-items: center;
  padding-left: 20px;
  background-color: getCssVar('color', 'white');
  border-bottom: 1px solid getCssVar('border-color');
}
</style>
