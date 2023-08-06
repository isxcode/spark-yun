<template>
    <BlockModal :model-config="modelConfig" @close="closeEvent">
        <div id="content" class="content-box">
            <!-- 日志展示 -->
            <pre v-if="logMsg" ref="preContentRef" @mousewheel="mousewheelEvent">{{ logMsg }}</pre>
        </div>
    </BlockModal>
</template>
  
<script lang="ts" setup>
import { reactive, ref, nextTick, onUnmounted } from 'vue'
import BlockModal from '../block-modal/index.vue'

import { GetLogData, GetYarnLogData } from '@/services/schedule.service'

const callback = ref<any>()
const logMsg = ref('')
const info = ref('')
const position = ref(false)
const timer = ref(null)
const preContentRef = ref(null)

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
    position.value = true
    getLogData()
    if (!timer.value) {
        timer.value = setInterval(() => {
            getLogData()
        }, 3000)
    }
    modelConfig.title = '日志'
    modelConfig.visible = true
}

// 获取日志
function getLogData() {
    GetLogData({
        instanceId: info.value
    })
        .then((res: any) => {
            logMsg.value = res.data.log
            if (position.value) {
                nextTick(() => {
                    scrollToButtom()
                })
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
        })
        .catch(() => {
            logMsg.value = ''
        })
}

function scrollToButtom() {
    if (preContentRef.value) {
        document.getElementById('content').scrollTop = preContentRef.value.scrollHeight // 滚动高度
    }
}

function mousewheelEvent(e: any) {
    if (!(e.deltaY > 0)) {
        position.value = false
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
                color: $--app-base-font-color;
                font-size: 12px;
                line-height: 21px;
                margin: 0;
            }
        }
    }
}
</style>
  