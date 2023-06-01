<!--
 * @Author: fanciNate
 * @Date: 2023-05-23 07:25:46
 * @LastEditTime: 2023-05-28 22:19:07
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/App.vue
-->

<template>
  <router-view />
</template>
<!--<template>-->
<!--  &lt;!&ndash; <router-view /> &ndash;&gt;-->
<!--  <div>-->
<!--    &lt;!&ndash; <el-button type="primary" @click="showModal">测试</el-button>-->
<!--    <BlockDrawer :drawer-config="drawerConfig">-->
<!--      测试内容-->
<!--    </BlockDrawer> &ndash;&gt;-->
<!--    <ZSvgIcon iconClass="webui"></ZSvgIcon>-->
<!--    <bpmn></bpmn>-->
<!--  </div>-->
<!--</template>-->

<script lang="ts" setup>
import { onMounted, reactive, ref } from "vue";
import ZSvgIcon from "@/components/z-svg-icon/index.vue";
import bpmn from "@/lib/packages/bpmn.vue";

// import BlockDrawer from '@/components/block-drawer/index.vue'

// const addModalRef = ref()

// const drawerConfig = reactive({
//   title: '添加数据源',
//   visible: false,
//   width: '500',
//   okConfig: {
//     title: '确定',
//     ok: okEvent,
//     disabled: false,
//     loading: false
//   },
//   cancelConfig: {
//     title: '取消',
//     cancel: closeEvent,
//     disabled: false
//   },
//   zIndex: 1100,
//   closeOnClickModal: false
// })

// function showModal() {
//   drawerConfig.visible = true
// }

// function okEvent() {
//   console.log('点击确定')
//   drawerConfig.visible = false
// }

// function closeEvent() {
//   console.log('点击取消')
//   drawerConfig.visible = false
// }

onMounted(() => {
  const linkIcon: any = document.getElementById("icon");
  if (linkIcon) {
    linkIcon.href = "./assets/icons/favicon.ico";
  }
});
</script>
