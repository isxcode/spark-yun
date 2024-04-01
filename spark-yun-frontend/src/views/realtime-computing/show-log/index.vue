<template>
    <BlockModal :model-config="modelConfig" @close="closeEvent">
        <div id="content" class="content-box">
            <pre v-if="logMsg" @mousewheel="mousewheelEvent" ref="preContentRef">{{ logMsg + loadingMsg  }}</pre>
        </div>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, onUnmounted, nextTick, computed } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { GetRealSubLog, GetRealSubRunningLog } from '@/services/realtime-computing.service';

const logMsg = ref('')
const timer = ref(null)
const preContentRef = ref(null)
const position = ref(true)
const isRequest = ref(false)

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

const status = ref(false)
const loadingPoint = ref('.')
const loadingTimer = ref()
const loadingMsg = computed(() => {
  const str = !status.value ? `加载中${loadingPoint.value}` : ''
  return str
})

function showModal(clusterNodeId: string, type?: string): void {
    position.value = true
    // 日志添加loading
    loadingTimer.value = setInterval(() => {
        if (loadingPoint.value.length < 5) {
            loadingPoint.value = loadingPoint.value + '.'
        } else {
            loadingPoint.value = '.'
        }
    }, 1000)

    getLogData(clusterNodeId, type)
    if (!timer.value) {
        timer.value = setInterval(() => {
            !isRequest.value && getLogData(clusterNodeId, type)
        }, 1000)
    }
    modelConfig.visible = true
}
// 获取日志
function getLogData(id: string, type?: string) {
    if (!id) {
        return
    }
    isRequest.value = true
    if (type === 'runningLog') {
        modelConfig.title = '运行日志'
        GetRealSubRunningLog({
            id: id
        }).then((res: any) => {
            status.value = ['FAIL', 'STOP'].includes(res.data.status) ? true : false
            logMsg.value = res.data.runningLog
            if (position.value) {
                nextTick(() => {
                    scrollToButtom()
                })
            }
            isRequest.value = false
        }).catch((err: any) => {
            console.log('err', err)
            logMsg.value = ''
            isRequest.value = false
        })
    } else {
        GetRealSubLog({
            id: id
        }).then((res: any) => {
            status.value = ['FAIL', 'STOP'].includes(res.data.status) ? true : false
            logMsg.value = res.data.submitLog
            if (position.value) {
                nextTick(() => {
                    scrollToButtom()
                })
            }
            isRequest.value = false
        }).catch((err: any) => {
            console.log('err', err)
            logMsg.value = ''
            isRequest.value = false
        })
    }
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

function closeEvent() {
    if (loadingTimer.value) {
        clearInterval(loadingTimer.value)
    }
    loadingTimer.value = null
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
    if (loadingTimer.value) {
        clearInterval(loadingTimer.value)
    }
    loadingTimer.value = null
})


defineExpose({
    showModal
})
</script>

<style lang="scss">
.zqy-log-modal {
    .modal-content {
        .content-box {
            min-height: 60vh;
            max-height: 60vh;
            padding: 12px 20px;
            box-sizing: border-box;
            overflow: auto;

            pre {
                color: getCssVar('text-color', 'primary');
                font-size: 12px;
                line-height: 21px;
                margin: 0;
            }
        }
    }
}
</style>
  