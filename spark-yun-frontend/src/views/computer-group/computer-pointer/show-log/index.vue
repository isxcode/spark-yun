<template>
    <BlockModal :model-config="modelConfig" @close="closeEvent">
        <div id="content" class="content-box">
            <pre v-if="logMsg" ref="preContentRef">{{ logMsg }}</pre>
        </div>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, onUnmounted, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { GetComputerPointDetailData } from '@/services/computer-group.service';

const logMsg = ref('')
const timer = ref(null)
const preContentRef = ref(null)
const position = ref(true)

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
        if (position.value) {
            nextTick(() => {
                scrollToButtom()
            })
        }
    }).catch((err: any) => {
        console.log('err', err)
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

function closeEvent() {
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
  