<template>
    <BlockModal :model-config="modelConfig" @close="closeEvent">
        <div class="zqy-dag-modal__dag">
            <ZqyFlow ref="zqyFlowRef"></ZqyFlow>
        </div>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick, onUnmounted } from 'vue'
import ZqyFlow from '@/lib/packages/zqy-flow/flow.vue'
import { GetScheduleDetail } from '@/services/schedule.service'

const info = ref()
const zqyFlowRef = ref()

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
    info.value = data
    nextTick(() => {
        initFlowData()
    })
    modelConfig.visible = true
}

function initFlowData() {
    GetScheduleDetail({
        workflowInstanceId: info.value.workflowInstanceId
    }).then((res: any) => {
        if (res.data?.webConfig) {
            zqyFlowRef.value.initCellList(JSON.parse(res.data.webConfig))
            zqyFlowRef.value.hideGrid(true)
            nextTick(() => {
                zqyFlowRef.value.locationContentCenter()
            })
        }
    }).catch(() => {
    })
}

function closeEvent() {
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
            .x6-graph {
                height: 100% !important;
                width: 100% !important;
            }
        }
    }
}
</style>
