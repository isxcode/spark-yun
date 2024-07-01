<template>
    <BlockModal :model-config="modelConfig">
        <div class="preview-report">
            <div class="charts-container" id="currentChartId"></div>
        </div>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, computed, nextTick } from 'vue'
import * as echarts from 'echarts'

let myChart: any = null
const loading = ref<boolean>(false)

const modelConfig = reactive({
    title: '预览',
    visible: false,
    width: '60%',
    cancelConfig: {
        title: '关闭',
        cancel: closeEvent
    },
    needScale: false,
    zIndex: 1100,
    closeOnClickModal: false
})

function showModal(echartsData: any): void {
    setTimeout(() => {
        myChart = echarts.init(document.getElementById('currentChartId'))
        myChart.setOption(echartsData)
        window.addEventListener('resize', resizeChart)
    }, 500);
    modelConfig.visible = true
}

function resizeChart() {
    myChart.resize()
}

function closeEvent() {
    window.removeEventListener('resize', resizeChart)
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.preview-report {
    width: 100%;
    height: calc(55vh - 24px);
    position: relative;
    padding: 12px;
    box-sizing: border-box;
    
    .charts-container {
        height: 100%;
    }
}
</style>
