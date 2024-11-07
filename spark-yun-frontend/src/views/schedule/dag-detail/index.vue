<template>
    <BlockModal :model-config="modelConfig" @close="closeEvent" top="10vh">
        <div class="zqy-dag-modal__dag">
            <ZqyFlow ref="zqyFlowRef"></ZqyFlow>
        </div>
    </BlockModal>
    <zqyLog ref="zqyLogRef"></zqyLog>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick, onUnmounted } from 'vue'
import ZqyFlow from '@/lib/packages/zqy-flow/flow.vue'
import { GetScheduleDetail } from '@/services/schedule.service'
import { BreakFlowData, QueryRunWorkInstances, RerunCurrentNodeFlowData, RunAfterFlowData } from '@/services/workflow.service';
import eventBus from '@/utils/eventBus'
import zqyLog from '@/components/zqy-log/index.vue'
import { ElMessage } from 'element-plus';

const info = ref()
const zqyFlowRef = ref()
const workflowInstanceId = ref('')
const timer = ref()
const runningStatus = ref(false)

const zqyLogRef = ref(null)

const modelConfig = reactive({
    title: 'DAG',
    visible: false,
    width: '90%',
    cancelConfig: {
        title: '关闭',
        cancel: closeEvent,
        disabled: false
    },
    needScale: false,
    zIndex: 1100,
    customClass: 'zqy-dag-modal',
    closeOnClickModal: false
})

function showModal(data: any): void {
    eventBus.on('nodeMenuEvent', (e: any) => {
        if (e.type === 'node_log') {
            // 日志
            nodeRunningLog(e, 'log')
        } else if (e.type === 'node_runAfter') {
            // 重跑下游
            nodeRunAfterFlow(e)
        } else if (e.type === 'node_result') {
            // 查看结果
            nodeRunningLog(e, 'result')
        } else if (e.type === 'node_yarnLog') {
            // 运行日志
            nodeRunningLog(e, 'yarnLog')
        } else if (e.type === 'node_break') {
            // 中断
            nodeBreakFlow(e)
        } else if (e.type === 'node_reRun') {
            // 重跑当前
            reRunCurrentNodeFlow(e)
        }
    })
    info.value = data
    nextTick(() => {
        initFlowData()
    })
    modelConfig.visible = true
}

function initFlowData() {
    workflowInstanceId.value = info.value.workflowInstanceId
    GetScheduleDetail({
        workflowInstanceId: info.value.workflowInstanceId
    }).then((res: any) => {
        if (res.data?.webConfig) {
            zqyFlowRef.value.initCellList(JSON.parse(res.data.webConfig))
            zqyFlowRef.value.hideGrid(true)
            nextTick(() => {
                zqyFlowRef.value.locationCenter()

                // 判断是否开始运行
                runningStatus.value = true
                queryRunWorkInstancesEvent()
                if (!timer.value) {
                    timer.value = setInterval(() => {
                        queryRunWorkInstancesEvent()
                    }, 2000)
                }
            })
        }
    }).catch(() => {
    })
}

// 运行作业流后获取节点运行状态
function queryRunWorkInstancesEvent() {
    if (workflowInstanceId.value) {
        QueryRunWorkInstances({
            workflowInstanceId: workflowInstanceId.value
        }).then((res: any) => {
            const statusList = ['SUCCESS', 'FAIL', 'ABORT']
            if (statusList.includes(res.data.flowStatus)) {
                clearInterval(timer.value)
                timer.value = null
                // 这里关闭运行状态
                runningStatus.value = false
            }
            zqyFlowRef.value.updateFlowStatus(res.data.workInstances, runningStatus.value)
        }).catch(() => {

        })
    } else {
        // ElMessage.warning('请先运行作业流')
    }
}


// 节点运行日志
function nodeRunningLog(e: any, type: string) {
    zqyLogRef.value.showModal(() => {
      console.log('关闭')
    }, { id: e.data.workInstanceId, type: type }
  )
}

// 节点重跑下游
function nodeRunAfterFlow(e: any) {
    RunAfterFlowData({
        workInstanceId: e.data.workInstanceId
    }).then((res: any) => {
        ElMessage.success(res.msg)
        zqyFlowRef.value.hideGrid(true)
        // 判断是否开始运行
        runningStatus.value = true
        queryRunWorkInstancesEvent()
        if (!timer.value) {
            timer.value = setInterval(() => {
                queryRunWorkInstancesEvent()
            }, 1000)
        }
    }).catch(() => {
    })
}

// 节点中断
function nodeBreakFlow(e: any) {
    BreakFlowData({
        workInstanceId: e.data.workInstanceId
    }).then((res: any) => {
        ElMessage.success(res.msg)
        queryRunWorkInstancesEvent()
    }).catch(() => {})
}

// 重跑当前节点
function reRunCurrentNodeFlow(e: any) {
    RerunCurrentNodeFlowData({
        workInstanceId: e.data.workInstanceId
    }).then((res: any) => {
        ElMessage.success(res.msg)
        zqyFlowRef.value.hideGrid(true)
        // 判断是否开始运行
        runningStatus.value = true
        queryRunWorkInstancesEvent()
        if (!timer.value) {
            timer.value = setInterval(() => {
                queryRunWorkInstancesEvent()
            }, 1000)
        }
    }).catch(() => {})
}

function closeEvent() {
    clearInterval(timer.value)
    timer.value = null
    eventBus.off('nodeMenuEvent')
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.zqy-dag-modal {
    .zqy-dag-modal__dag {
        width: 100%;
        height: calc(62vh - 32px);
        .zqy-flow {
            height: calc(62vh - 32px);
        }
    }
}
</style>
