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
import EmptyPage from '@/components/empty-page/index.vue'
import { GetRealSubLog } from '@/services/realtime-computing.service';

const logMsg = ref('')
const timer = ref(null)
const runId = ref('')
const status = ref(false)
const isRequest = ref(false)

function initData(id: string, flag: boolean): void {
  if (flag) {
    status.value = false
  }

  runId.value = id
  getLogData(runId.value)
  if (!timer.value) {
    timer.value = setInterval(() => {
      !isRequest.value && getLogData(runId.value)
    }, 3000)
  }
}

// 获取日志
function getLogData(id: string) {
  if (!id || status.value) {
    return
  }
  isRequest.value = true
  GetRealSubLog({
      id: id
  }).then((res: any) => {
      status.value = ['FAIL', 'STOP'].includes(res.data.status) ? true : false
      logMsg.value = res.data.submitLog
      isRequest.value = false
  }).catch((err: any) => {
      if (timer.value) {
          clearInterval(timer.value)
      }
      console.log('err', err)
      logMsg.value = ''
      isRequest.value = false
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
  .empty-page {
    height: 100%;
  }
}
</style>
