<template>
    <pre
        class="zqy-log-container"
        ref="preContentRef"
        @mousewheel="mousewheelEvent"
    >{{ logMsg + loadingMsg }}</pre>
    <span class="zqy-download-log" @click="downloadLog">下载日志</span>
</template>

<script lang="ts" setup>
import { ref, defineProps, onMounted, computed, nextTick, onUnmounted, watch } from 'vue'
import dayjs from 'dayjs'

const props = defineProps<{
    logMsg: string,
    status: boolean
}>()

const position = ref(true)
const loadingTimer = ref()
const loadingPoint = ref('.')
const preContentRef = ref(null)

watch(() => props.logMsg, () => {
    if (position.value) {
        nextTick(() => {
            scrollToButtom()
        })
    }
}, {
    immediate: true
})

const loadingMsg = computed(() => {
  const str = !props.status ? `加载中${loadingPoint.value}` : ''
  return str
})

function downloadLog() {
    const logStr = props.logMsg
    const nowDate = dayjs(new Date()).format('YYYY-MM-DD HH:mm:ss')
    const blob = new Blob([logStr], {
        type: "text/plain;charset=utf-8"
    })
    const objectURL = URL.createObjectURL(blob)
    const aTag = document.createElement('a')
    aTag.href = objectURL
    aTag.download = `日志-${nowDate}.log`
    aTag.click()
    URL.revokeObjectURL(objectURL)
}

// 外部调用
function resetPosition() {
    if (position.value) {
        nextTick(() => {
            scrollToButtom()
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

onMounted(() => {
    position.value = true
    if (loadingTimer.value) {
        clearInterval(loadingTimer.value)
    }
    loadingTimer.value = null
    loadingTimer.value = setInterval(() => {
        if (loadingPoint.value.length < 5) {
            loadingPoint.value = loadingPoint.value + '.'
        } else {
            loadingPoint.value = '.'
        }
    }, 1000)
})

onUnmounted(() => {
  if (loadingTimer.value) {
    clearInterval(loadingTimer.value)
  }
  loadingTimer.value = null
})

defineExpose({
    resetPosition
})
</script>

<style lang="scss">
.zqy-log-container {
    color: getCssVar('text-color', 'primary');
    font-size: getCssVar('font-size', 'extra-small');
    line-height: 21px;
    margin: 0;
}
.zqy-download-log {
    font-size: 12px;
    color: getCssVar('color', 'primary');
    cursor: pointer;
    position: absolute;
    top: 0;
    right: 0;
    &:hover {
        text-decoration: underline;
    }
}
</style>