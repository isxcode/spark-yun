<template>
    <BlockModal :model-config="modelConfig" @close="closeEvent">
        <LoadingPage class="log-loading" :visible="loading">
            <div id="content" class="content-box">
                <LogContainer v-if="logMsg" :logMsg="logMsg" :status="status"></LogContainer>
            </div>
        </LoadingPage>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, onUnmounted, nextTick, computed } from 'vue'
import { GetSparkContainerkDetail, GetSparkContainerkRunningLog } from '@/services/spark-container.service'
import LoadingPage from '@/components/loading/index.vue'


const logMsg = ref('')
const timer = ref(null)
const loading = ref<boolean>(false)
const isRequest = ref(false)

const status = ref(false)

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

function showModal(data: string, type?: string): void {
    logMsg.value = ''
    loading.value = true
    timer.value = null
    getLogData(data, type)
    if (!timer.value && type !== 'runningLog') {
        timer.value = setInterval(() => {
            !isRequest.value && getLogData(data, type)
        }, 3000)
    }
    modelConfig.visible = true
}
// 获取日志
function getLogData(data: any, type?: string) {
    isRequest.value = true
    if (type === 'runningLog') {
        modelConfig.title = '运行日志'
        GetSparkContainerkRunningLog({ id: data.id }).then((res: any) => {
            logMsg.value = res.data.runningLog
            status.value = true
            isRequest.value = false
            loading.value = false
        }).catch(() => {
            logMsg.value = ''
            isRequest.value = false
            loading.value = false
        })
    } else {
        modelConfig.title = '提交日志'
        GetSparkContainerkDetail({
            id: data.id
        }).then((res: any) => {
            status.value = ['FAIL', 'RUNNING'].includes(res.data.status) ? true : false
            logMsg.value = res.data.submitLog
            if (['RUNNING', 'FAIL'].includes(res.data.status)) {
                if (timer.value) {
                    clearInterval(timer.value)
                }
                timer.value = null
            }
            loading.value = false
            isRequest.value = false
        }).catch((err: any) => {
            loading.value = false
            console.log('err', err)
            logMsg.value = ''
            isRequest.value = false
        })
    }
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
