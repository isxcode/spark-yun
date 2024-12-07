<template>
  <div id="content" class="running-log">
    <LoadingPage :visible="loading">
      <LogContainer v-if="logMsg" :logMsg="logMsg" :status="status"></LogContainer>
      <EmptyPage v-else />
    </LoadingPage>
  </div>
</template>

<script lang="ts" setup>
import { nextTick, onUnmounted, computed, ref, defineExpose } from 'vue'
import EmptyPage from '@/components/empty-page/index.vue'
import { GetRealSubRunningLog } from '@/services/realtime-computing.service';
import LoadingPage from '@/components/loading/index.vue'

const logMsg = ref('')
const timer = ref(null)
const pubId = ref('')
const status = ref(false)
const isRequest = ref(false)
const loading = ref<boolean>(false)

function initData(id: string): void {
  pubId.value = id
  loading.value = true
  getLogData(pubId.value)
  if (!timer.value) {
    timer.value = setInterval(() => {
      !isRequest.value && getLogData(pubId.value)
    }, 1000)
  }
}

// 获取日志
function getLogData(id: string) {
  if (!id || status.value) {
    return
  }
  isRequest.value = true
  GetRealSubRunningLog({
    id: id
  })
    .then((res: any) => {
      status.value = ['FAIL', 'STOP'].includes(res.data.status) ? true : false
      logMsg.value = res.data.runningLog
      isRequest.value = false
      loading.value = false
    })
    .catch(() => {
      logMsg.value = ''
      if (timer.value) {
          clearInterval(timer.value)
      }
      timer.value = null
      isRequest.value = false
      loading.value = false
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
.running-log {
  .empty-page {
    height: 100%;
  }
}
</style>
