<template>
    <BlockModal :model-config="modelConfig" @close="closeEvent">
        <div id="content" class="content-box">
            <LogContainer v-if="logMsg" :logMsg="logMsg" :status="true"></LogContainer>
        </div>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, onUnmounted, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { GetComputerPointDetailData } from '@/services/computer-group.service';

const logMsg = ref('')
const timer = ref(null)

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

function showModal(clusterNodeId: string, type?: string): void {
    if (type === 'cluster') {
        getLogData(clusterNodeId)
        if (!timer.value) {
            timer.value = setInterval(() => {
                getLogData(clusterNodeId)
            }, 1000)
        }
    } else {
        logMsg.value = clusterNodeId
    }
    modelConfig.visible = true
}
// 获取日志
function getLogData(id: string) {
    if (!id) {
        return
    }
    GetComputerPointDetailData({
        clusterNodeId: id
    }).then((res: any) => {
        logMsg.value = res.data.agentLog
    }).catch((err: any) => {
        console.log('err', err)
        logMsg.value = ''
    })
}

function closeEvent() {
    if (timer.value) {
        clearInterval(timer.value)
    }
    timer.value = null
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>
