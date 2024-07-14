<!--
 * @Author: fanciNate
 * @Date: 2023-05-26 16:35:28
 * @LastEditTime: 2023-06-22 21:29:48
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /spark-yun/spark-yun-website/src/views/workflow/work-item/publish-log.vue
-->
<template>
  <div
    id="content"
    class="publish-log"
  >
    <LogContainer v-if="logMsg" :logMsg="logMsg" :status="status"></LogContainer>
    <EmptyPage v-else />
  </div>
</template>

<script lang="ts" setup>
import { nextTick, onUnmounted, ref, defineExpose, computed } from 'vue'
import { GetSubmitLogData } from '@/services/workflow.service'
import EmptyPage from '@/components/empty-page/index.vue'

const logMsg = ref('')
const timer = ref(null)
const runId = ref('')
const status = ref(false)
const callback = ref()

function initData(id: string, cb: any): void {
  runId.value = id
  callback.value = cb
  getLogData(runId.value)
  if (!timer.value) {
    timer.value = setInterval(() => {
      getLogData(runId.value)
    }, 3000)
  }
}

// 获取日志
function getLogData(id: string) {
  if (!id) {
    return
  }
  GetSubmitLogData({
    instanceId: id
  })
    .then((res: any) => {
      logMsg.value = res.data.log
      status.value = ['FAIL', 'SUCCESS','ABORT'].includes(res.data.status) ? true : false
      if (status.value && callback.value) {
        callback.value(res.data.status)
        if (timer.value) {
          clearInterval(timer.value)
        }
        timer.value = null
      }
      if (['SUCCESS', 'FAIL'].includes(res.data.status)) {
          if (timer.value) {
              clearInterval(timer.value)
          }
          timer.value = null
      }
    })
    .catch((err: any) => {
      console.log('err', err)
      logMsg.value = ''
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
.publish-log {
  height: 100%;
  .empty-page {
    height: 100%;
  }
}
</style>
