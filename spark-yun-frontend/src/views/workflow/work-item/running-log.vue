<template>
  <div id="content" class="running-log">
    <LogContainer
      v-if="logMsg || loading"
      :logMsg="logMsg || ''"
      :status="true"
      :showResult="false"
    ></LogContainer>
    <EmptyPage v-else />
  </div>
</template>

<script lang="ts" setup>
import { ref, defineExpose } from 'vue'
import EmptyPage from '@/components/empty-page/index.vue'
import { GetYarnLogData } from '@/services/schedule.service'
import LoadingPage from '@/components/loading/index.vue'

const logMsg = ref('')
const pubId = ref('')
const loading = ref<boolean>(false)

const props = defineProps<{
  showParse: boolean
}>()

function initData(id: string): void {
  pubId.value = id
  loading.value = true

  // 立即显示加载状态，不等待日志返回
  if (id) {
    // 如果有 id，立即获取日志
    getLogData(pubId.value)
  } else {
    // 如果没有 id，显示加载中状态
    logMsg.value = ''
  }
}

// 获取日志
function getLogData(id: string) {
  if (!id) {
    // 没有 id 时保持加载状态，不关闭 loading
    return
  }
  GetYarnLogData({ instanceId: id}).then((res: any) => {
    logMsg.value = res.data.yarnLog
    // 只有在有日志数据时才关闭 loading
    if (res.data.yarnLog) {
      loading.value = false
    }
  }).catch(() => {
    // 出错时不清空日志，保持加载状态
    loading.value = false
  })
}

defineExpose({
  initData
})
</script>

<style lang="scss">
.running-log {
  height: 100%;
  .empty-page {
    height: 100%;
  }
  .zqy-loading {
    position: static;
    height: 100% !important;
    padding: 0 !important;
    margin-top: 0 !important;
    overflow: auto;
  }
}
.zqy-json-parse {
  font-size: 12px;
  color: getCssVar('color', 'primary');
  cursor: pointer;
  position: absolute;
  right: 40px;
  top: 12px;
  &.zqy-json-parse__log {
    right: 98px;
  }
  &:hover {
      text-decoration: underline;
  }
}
</style>
