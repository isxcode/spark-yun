<!--
 * @Author: fanciNate
 * @Date: 2023-05-26 16:35:28
 * @LastEditTime: 2023-06-18 15:48:24
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /spark-yun/spark-yun-website/src/views/workflow/work-item/running-log.vue
-->
<template>
  <div
    id="content"
    class="running-log"
  >
    <pre
      v-if="logMsg"
      ref="preContentRef"
      @mousewheel="mousewheelEvent"
    >{{ logMsg + loadingMsg }}</pre>
    <EmptyPage v-else />
  </div>
</template>

<script lang="ts" setup>
import { nextTick, onUnmounted, computed, ref, defineExpose } from 'vue'
import EmptyPage from '@/components/empty-page/index.vue'
import { GetRealSubRunningLog } from '@/services/realtime-computing.service';

const logMsg = ref('')
const position = ref(true)
const timer = ref(null)
const preContentRef = ref(null)
const pubId = ref('')
const status = ref(false)
const loadingPoint = ref('.')
const loadingTimer = ref()
const isRequest = ref(false)

const loadingMsg = computed(() => {
  const str = !status.value ? `加载中${loadingPoint.value}` : ''
  return str
})

function initData(id: string): void {
  loadingTimer.value = setInterval(() => {
    if (loadingPoint.value.length < 5) {
      loadingPoint.value = loadingPoint.value + '.'
    } else {
      loadingPoint.value = '.'
    }
  }, 1000)

  pubId.value = id
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
      if (position.value) {
        nextTick(() => {
          scrollToButtom()
        })
      }
      isRequest.value = false
    })
    .catch(() => {
      logMsg.value = ''
      if (timer.value) {
          clearInterval(timer.value)
      }
      timer.value = null
      isRequest.value = false
    })
}

function scrollToButtom() {
  if (preContentRef.value) {
    document.getElementById('content').scrollTop = preContentRef.value?.scrollHeight // 滚动高度
  }
}

function mousewheelEvent(e: any) {
  if (!(e.deltaY > 0)) {
    position.value = false
  }
}

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
  timer.value = null

  if (loadingTimer.value) {
    clearInterval(loadingTimer.value)
  }
  loadingTimer.value = null
})

defineExpose({
  initData
})
</script>

<style lang="scss">
.running-log {
  pre {
    color: getCssVar('text-color', 'primary');
    font-size: 12px;
    line-height: 21px;
    margin: 0;
  }
  .empty-page {
    height: 50%;
  }
}
</style>
