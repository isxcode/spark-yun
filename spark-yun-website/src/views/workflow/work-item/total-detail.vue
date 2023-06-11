<!--
 * @Author: fanciNate
 * @Date: 2023-05-26 16:35:28
 * @LastEditTime: 2023-05-27 15:31:20
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/views/workflow/work-item/total-detail.vue
-->
<template>
  <div
    id="content"
    class="total-detail"
  >
    <div class="detail-container">
      <span>作业运行地址: {{ detailData.trackingUrl }}</span>
      <span>作业当前状态: {{ detailData.finalApplicationStatus }}</span>
      <span>Yarn容器状态: {{ detailData.yarnApplicationState }}</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, onUnmounted, ref, defineExpose } from 'vue'
import { GetResultItemDetail } from '@/services/workflow.service'

const timer = ref(null)

let detailData = reactive({
  finalApplicationStatus: '',
  trackingUrl: '',
  yarnApplicationState: ''
})

function initData(id: string): void {
  getData(id)
  if (!timer.value) {
    timer.value = setInterval(() => {
      getData(id)
    }, 3000)
  }
}

// 获取日志
function getData(id: string) {
  if (!id) {
    return
  }
  GetResultItemDetail({
    instanceId: id
  })
    .then((res: any) => {
      detailData.finalApplicationStatus = res.data.finalApplicationStatus
      detailData.trackingUrl = res.data.trackingUrl
      detailData.yarnApplicationState = res.data.yarnApplicationState
    })
    .catch((error: any) => {
      console.error(error)
    })
}

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
  timer.value = null
})

defineExpose({
  initData
})
</script>

<style lang="scss">
.detail-container {
  display: flex;
  flex-direction: column;
  color: $vxe-font-color;
  font-size: $--app-small-font-size;
  span {
    margin-bottom: 12px;
  }
}
</style>
