<template>
  <div id="content" class="publish-log">
    <LoadingPage :visible="loading">
      <LogContainer v-if="logMsg" :logMsg="logMsg" :status="status" :showResult="false"></LogContainer>
      <EmptyPage v-else />
    </LoadingPage>
    <span v-if="runId" class="zqy-log-refrash" @click="refrashEvent">刷新</span>
  </div>
</template>

<script lang="ts" setup>
import { nextTick, onUnmounted, ref, defineExpose } from 'vue'
import { GetSubmitLogData } from '@/services/workflow.service'
import EmptyPage from '@/components/empty-page/index.vue'
import LoadingPage from '@/components/loading/index.vue'

const logMsg = ref('')
const timer = ref(null)
const runId = ref('')
const status = ref(false)
const callback = ref()
const loading = ref<boolean>(false)

function initData(id: string, cb: any): void {
  runId.value = id
  callback.value = cb
  loading.value = true
  getLogData(runId.value)
  if (!timer.value) {
    timer.value = setInterval(() => {
      getLogData(runId.value)
    }, 3000)
  }
}

function refrashEvent() {
  loading.value = true
  getLogData(runId.value)
}

// 获取日志
function getLogData(id: string) {
  if (!id) {
    loading.value = false
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
      setTimeout(() => {
        loading.value = false
      }, 300)
    })
    .catch((err: any) => {
      console.log('err', err)
      logMsg.value = ''
      setTimeout(() => {
        loading.value = false
      }, 300)
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
  position: static;
  .zqy-loading {
    position: static;
    height: 100% !important;
    padding: 0 !important;
    margin-top: 0 !important;
    overflow: auto;
  }
  .empty-page {
    height: 100%;
  }
  .zqy-log-refrash {
    font-size: 12px;
    color: getCssVar('color', 'primary');
    cursor: pointer;
    position: absolute;
    right: 100px;
    top: 12px;
  }
}
</style>
