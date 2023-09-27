<template>
  <el-config-provider :locale="locale">
    <router-view />
  </el-config-provider>
</template>
<script lang="ts" setup>
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { reactive } from 'vue'

const { locale } = reactive({
  locale: zhCn
})
</script>
