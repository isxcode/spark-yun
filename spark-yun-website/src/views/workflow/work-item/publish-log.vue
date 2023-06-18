<!--
 * @Author: fanciNate
 * @Date: 2023-05-26 16:35:28
 * @LastEditTime: 2023-06-18 16:25:52
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /spark-yun/spark-yun-website/src/views/workflow/work-item/publish-log.vue
-->
<template>
  <div
    id="content"
    class="publish-log"
  >
    <pre
      v-if="logMsg"
      ref="preContentRef"
      @mousewheel="mousewheelEvent"
    >{{ logMsg }}</pre>
    <EmptyPage v-else />
  </div>
</template>

<script lang="ts" setup>
import { nextTick, onUnmounted, ref, defineExpose } from 'vue'
import { GetSubmitLogData } from '@/services/workflow.service'
import EmptyPage from '@/components/empty-page/index.vue'

const logMsg = ref('')
const position = ref(true)
const timer = ref(null)
const preContentRef = ref(null)
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
      status.value = res.data.status === 'SUCCESS' ? true : false
      if (position.value) {
        nextTick(() => {
          scrollToButtom()
        })
      }
      if (status.value) {
        callback.value()
        if (timer.value) {
          clearInterval(timer.value)
        }
        timer.value = null
      }
    })
    .catch(() => {
      logMsg.value = ''
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
})

defineExpose({
  initData
})
</script>

<style lang="scss">
.publish-log {
  pre {
    color: $--app-base-font-color;
    font-size: $--app-small-font-size;
    line-height: 21px;
    margin: 0;
  }
  .empty-page {
    height: 50%;
  }
}
</style>
