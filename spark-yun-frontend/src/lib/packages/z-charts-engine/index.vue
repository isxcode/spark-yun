<template>
    <div class="z-charts-engine">
        <ChartsChoose
            v-if="renderSence !== 'readonly'"
            :chartsList="chartsList"
            @startMoveEvent="startMoveEvent"
            @endMoveEvent="endMoveEvent"
            @getChartListEvent="getChartListEvent"
            @previewChatEvent="previewChatEvent"
        ></ChartsChoose>
        <ChartsComponents
            ref="chartComponentsRef"
            class="charts-edit-container"
            :renderSence="renderSence"
            :chartList="componentList"
            :getPreviewOption="getPreviewOption"
            :getRealDataOption="getRealDataOption"
        ></ChartsComponents>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineProps, defineEmits } from 'vue'
import ChartsChoose from './charts-choose/index.vue'
import ChartsComponents from './charts-components/charts-grid-layout.vue'

const props = defineProps(['chartsList', 'renderSence', 'componentList', 'getPreviewOption', 'getRealDataOption'])
const emit = defineEmits(['getChartListEvent', 'previewChatEvent'])

const chartComponentsRef = ref()

function startMoveEvent(e: any) {
    chartComponentsRef.value.startMoveEvent(e)
}

function endMoveEvent(e: any) {
    chartComponentsRef.value.endMoveEvent(e)
}

function resizeAllCharts() {
    chartComponentsRef.value.resizeAllCharts()
}

function getChartListEvent(e: string) {
    emit('getChartListEvent', e)
}
function previewChatEvent(e: any) {
    emit('previewChatEvent', e)
}

function downloadLog() {
    const logStr = ''
    const blob = new Blob([logStr], {
        type: "text/plain;charset=utf-8"
    })
    const objectURL = URL.createObjectURL(blob)
    const aTag = document.createElement('a')
    aTag.href = objectURL
    aTag.download = "日志.log"
    aTag.click()
    URL.revokeObjectURL(objectURL)
}

// 获取到组件
function getComponentList() {
    return chartComponentsRef.value.getComponentList()
}

defineExpose({
    resizeAllCharts,
    getComponentList
})
</script>

<style lang="scss">
.z-charts-engine {
    display: flex;
    width: 100%;
    height: 100%;
    .charts-edit-container {
        width: 100%;
    }
    .charts-config-container {
        width: 200px;
        border-left: 1px solid #b2b2b2;
        height: 100%;
    }
}
</style>