<template>
  <BlockModal :model-config="modelConfig" @close="closeEvent">
    <LoadingPage class="log-loading" :visible="loading">
      <div id="content" class="content-box">
        <!-- 日志展示 -->
        <template v-if="['log', 'yarnLog'].includes(modalType)">
          <LogContainer v-if="logMsg" :logMsg="logMsg" :status="true"></LogContainer>
          <template v-else>
            <empty-page label="暂无日志"></empty-page>
          </template>
        </template>
        <!-- 结果展示 -->
        <template v-else-if="modalType === 'result'">
          <LogContainer v-if="strData || jsonData" :logMsg="strData || jsonData" :showResult="true" :status="true">
          </LogContainer>
          <template v-else>
            <BlockTable :table-config="tableConfig" />
          </template>
        </template>
      </div>
    </LoadingPage>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick, onUnmounted } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { GetLogData, GetYarnLogData, GetResultData } from '@/services/schedule.service'

const callback = ref<any>()
const logMsg = ref('')
const info = ref('')
const modalType = ref('')
const timer = ref(null)

const jsonData = ref()
const strData = ref()
const loading = ref<boolean>(false)

const tableConfig = reactive({
  tableData: [],
  colConfigs: [],
  seqType: 'seq',
  loading: false
})
const modelConfig = reactive({
  title: '日志',
  visible: false,
  width: '820px',
  cancelConfig: {
    title: '关闭',
    cancel: closeEvent,
    disabled: false
  },
  needScale: false,
  zIndex: 1100,
  customClass: 'zqy-log-modal zqy-log-modal__s',
  closeOnClickModal: false
})

function showModal(cb: () => void, data: any, type: string): void {
  callback.value = cb
  info.value = data.id
  modalType.value = type

  logMsg.value = ''
  jsonData.value = null
  strData.value = null
  tableConfig.colConfigs = []
  tableConfig.tableData = []

  if (modalType.value === 'log') {
    loading.value = true
    getLogData()
    if (!timer.value) {
      timer.value = setInterval(() => {
        getLogData()
      }, 3000)
    }
    modelConfig.title = '日志'
  } else if (modalType.value === 'result') {
    getResultDatalist()
    modelConfig.width = '64%'
    modelConfig.title = '结果'
  } else if (modalType.value === 'yarnLog') {
    modelConfig.title = '运行日志'
    getYarnLogData()
  }
  modelConfig.visible = true
}

// 获取日志
function getLogData() {
  GetLogData({
    instanceId: info.value
  })
    .then((res: any) => {
      loading.value = false
      logMsg.value = res.data.log
    })
    .catch(() => {
      logMsg.value = ''
      loading.value = false
    })
}

// 获取yarn日志
function getYarnLogData() {
  loading.value = true
  GetYarnLogData({
    instanceId: info.value
  })
    .then((res: any) => {
      loading.value = false
      logMsg.value = res.data.yarnLog
    })
    .catch(() => {
      loading.value = false
      logMsg.value = ''
    })
}

// 获取结果
function getResultDatalist() {
  loading.value = true
  tableConfig.loading = true
  GetResultData({
    instanceId: info.value
  })
    .then((res: any) => {
      jsonData.value = res.data.jsonData
      strData.value = res.data.strData

      loading.value = false

      const col = res.data.data.slice(0, 1)[0]
      const tableData = res.data.data.slice(1, res.data.data.length)
      tableConfig.colConfigs = col.map((colunm: any) => {
        return {
          prop: colunm,
          title: colunm,
          minWidth: 100,
          showHeaderOverflow: true,
          showOverflowTooltip: true
        }
      })
      tableConfig.tableData = tableData.map((columnData: any) => {
        const dataObj: any = {
        }
        col.forEach((c: any, index: number) => {
          dataObj[c] = columnData[index]
        })
        return dataObj
      })
      tableConfig.loading = false
    })
    .catch(() => {
      tableConfig.colConfigs = []
      tableConfig.tableData = []
      tableConfig.loading = false
      loading.value = false
    })
}

function closeEvent() {
  if (timer.value) {
    clearInterval(timer.value)
  }
  timer.value = null
  modelConfig.visible = false
}

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
  timer.value = null
})

defineExpose({
  showModal
})
</script>

<style lang="scss">
.zqy-log-modal__s {
  .content-box {
    position: static;
  }
}
</style>