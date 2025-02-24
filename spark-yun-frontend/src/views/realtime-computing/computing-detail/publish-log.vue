<template>
  <div id="content" class="publish-log">
    <LoadingPage :visible="loading">
      <LogContainer v-if="logMsg" :logMsg="logMsg" :status="status"></LogContainer>
      <EmptyPage v-else />
    </LoadingPage>
    <span v-if="runId" class="zqy-log-refrash" @click="refrashEvent">刷新</span>
  </div>
</template>

<script lang="ts" setup>
import { nextTick, onUnmounted, ref, defineExpose, computed } from 'vue'
import EmptyPage from '@/components/empty-page/index.vue'
import { GetRealSubLog } from '@/services/realtime-computing.service';
import LoadingPage from '@/components/loading/index.vue'

const logMsg = ref('')
const timer = ref(null)
const runId = ref('')
const status = ref(false)
const isRequest = ref(false)
const loading = ref<boolean>(false)

function initData(id: string, flag: boolean): void {
  if (flag) {
    status.value = false
  }

  runId.value = id
  loading.value = true
  getLogData(runId.value)
  if (!timer.value) {
    timer.value = setInterval(() => {
      !isRequest.value && getLogData(runId.value)
    }, 3000)
  }
}

function refrashEvent() {
  loading.value = true
  getLogData(runId.value)
}

// 获取日志
function getLogData(id: string) {
  if (!id || status.value) {
    loading.value = false
    return
  }
  isRequest.value = true
  GetRealSubLog({
      id: id
  }).then((res: any) => {
      status.value = ['FAIL', 'STOP'].includes(res.data.status) ? true : false
      logMsg.value = res.data.submitLog
      isRequest.value = false
      loading.value = false
  }).catch((err: any) => {
      if (timer.value) {
          clearInterval(timer.value)
      }
      console.log('err', err)
      logMsg.value = ''
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

