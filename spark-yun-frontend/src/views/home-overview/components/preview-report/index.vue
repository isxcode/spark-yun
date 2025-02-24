<template>
    <BlockModal :model-config="modelConfig">
        <monitor-chart :monitorData="monitorData" :dateTimeList="dateTimeList" :hideFull="true"></monitor-chart>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, computed, nextTick } from 'vue'
import * as echarts from 'echarts'
import MonitorChart from '../monitor-chart.vue'
import { MonitorInfo } from '../hooks/useMonitor'

let myChart: any = null
const loading = ref<boolean>(false)

const monitorData = ref<MonitorInfo>()
const dateTimeList = ref<string[]>([])

const modelConfig = reactive({
    title: '查看',
    visible: false,
    width: '60%',
    customClass: 'preview-report-chart',
    cancelConfig: {
        title: '关闭',
        cancel: closeEvent
    },
    needScale: false,
    zIndex: 1100,
    closeOnClickModal: false
})

function showModal(monitor: MonitorInfo, dateTime: string[]): void {
    monitorData.value = monitor
    dateTimeList.value = dateTime

    modelConfig.visible = true
}

function closeEvent() {
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.preview-report-chart {
    box-sizing: border-box;
    
    .monitor-chart {
        margin: 0;
        width: 100%;
        box-sizing: border-box;
        height: 400px;
        .monitor-chart__container {
            width: 700px;
            height: 300px;
        }
    }
}
</style>
