<template>
  <div id="content" class="running-log">
    <LogContainer v-if="logMsg || loading" :logMsg="logMsg || ''" :status="status"></LogContainer>
    <EmptyPage v-else />
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

  // 立即显示加载状态，不等待日志返回
  if (id) {
    // 如果有 id，立即开始轮询获取日志
    getLogData(pubId.value)
    if (!timer.value) {
      timer.value = setInterval(() => {
        !isRequest.value && getLogData(pubId.value)
      }, 1000)
    }
  } else {
    // 如果没有 id，显示加载中状态
    logMsg.value = ''
    status.value = false
  }
}

// 获取日志
function getLogData(id: string) {
  if (!id) {
    // 没有 id 时保持加载状态，不关闭 loading
    return
  }
  if (status.value) {
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
      // 只有在有日志数据或任务完成时才关闭 loading
      if (res.data.runningLog || status.value) {
        loading.value = false
      }
    })
    .catch(() => {
      // 出错时不清空日志，保持加载状态继续重试
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
