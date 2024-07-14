<template>
    <BlockModal :model-config="modelConfig" @close="closeEvent">
        <div id="content" class="content-box">
            <!-- 日志展示 -->
            <template v-if="['log', 'yarnLog'].includes(modalType)">
                <LogContainer v-if="logMsg" :logMsg="logMsg" :status="true"></LogContainer>
            </template>
            <!-- 结果展示 -->
            <template v-else-if="modalType === 'result'">
                <BlockTable :table-config="tableConfig" />
            </template>
        </div>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, ref, onUnmounted } from 'vue'
import BlockModal from '../block-modal/index.vue'
import BlockTable from '../block-table/index.vue'

import { GetLogData, GetYarnLogData, GetResultData } from '@/services/schedule.service'

const callback = ref<any>()
const logMsg = ref('')
const info = ref('')
const timer = ref(null)
const modalType = ref('')

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
    customClass: 'zqy-log-modal',
    closeOnClickModal: false
})

function showModal(cb: () => void, data: any): void {
    callback.value = cb
    info.value = data.id
    modalType.value = data.type
    if (modalType.value === 'log') {
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
            logMsg.value = res.data.log
            if (['SUCCESS', 'FAIL'].includes(res.data.status)) {
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

// 获取yarn日志
function getYarnLogData() {
    GetYarnLogData({
        instanceId: info.value
    })
        .then((res: any) => {
            logMsg.value = res.data.yarnLog
            if (['SUCCESS', 'FAIL'].includes(res.data.status)) {
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

// 获取结果
function getResultDatalist() {
  tableConfig.loading = true
  GetResultData({
    instanceId: info.value
  })
    .then((res: any) => {
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
.zqy-log-modal {
    .modal-content {
        position: relative;
        .content-box {
            min-height: 59vh;
            max-height: 60vh;
            padding: 12px 20px;
            box-sizing: border-box;
            overflow: auto;
            .zqy-download-log {
                top: 6px;
                right: 20px;
            }
        }
    }
}
</style>
  