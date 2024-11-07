<template>
  <div
    id="content"
    class="running-log"
  >
    <LogContainer
      v-if="logMsg"
      :logMsg="logMsg"
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

const logMsg = ref('')
const pubId = ref('')

const props = defineProps<{
  showParse: boolean
}>()

function initData(id: string): void {
  pubId.value = id
  getLogData(pubId.value)
}

// 获取日志
function getLogData(id: string) {
  if (!id) {
    logMsg.value = ''
    return
  }
  GetYarnLogData({
    instanceId: id
  })
    .then((res: any) => {
      logMsg.value = res.data.yarnLog
    })
    .catch(() => {
      logMsg.value = ''
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
