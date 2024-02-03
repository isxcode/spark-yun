<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <router-view />
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const breadOtherList = [
  {
    name: '表单查询',
    code: 'form-query'
  },
  {
    name: '表单配置',
    code: 'form-setting'
  }
]

const breadCrumbList = ref([
  {
    name: '分享表单',
    code: 'custom-form'
  }
])
watch(() => route.name, (e) => {
  breadCrumbList.value = [{
    name: '分享表单',
    code: 'custom-form'
  }, ...breadOtherList.filter(r => r.code === e)]
})

</script>

<style lang="scss">
.costom-form {
  // &.zqy-form-engine {
  //   height: calc(100vh - 55px);
  // }
  // .form-card-container {
  //   padding: 0 20px;
  //   box-sizing: border-box;
  //   .form-card-list {
  //     width: 100%;
  //     .form-card-item {
  //       width: 24%;
  //       height: 84px;
  //       border: 1px solid getCssVar('border-color');
  //       border-radius: 6px;
  //       background-color: getCssVar('color', 'white');
  //       box-shadow: getCssVar('box-shadow', 'lighter');
  //       transition: transform 0.15s linear;
  //       cursor: pointer;
  //       display: flex;
  //       flex-direction: column;
  //       justify-content: space-between;
  //       padding: 12px;
  //       box-sizing: border-box;
  //       font-size: getCssVar('font-size', 'extra-small');
  //       &:hover {
  //         transition: transform 0.15s linear;
  //         transform: scale(1.03);
  //       }
  //     }
  //   }
  // }
}
</style>