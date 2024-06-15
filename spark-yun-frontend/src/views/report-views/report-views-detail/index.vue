<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="report-views">
        <div class="report-button-container" v-if="renderSence === 'edit'">
            <el-button type="primary" @click="saveData">保存</el-button>
            <el-button type="primary">预览</el-button>
            <el-button type="primary">发布</el-button>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData">
            <ZChartsEngine
                ref="ZChartsEngineRef"
                :chartsList="chartsList"
                :renderSence="renderSence"
                :componentList="componentList"
                @getChartListEvent="getChartListEvent"
                @previewChatEvent="previewChatEvent"
            ></ZChartsEngine>
        </LoadingPage>
        <PreviewReport ref="previewReportRef"></PreviewReport>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { BreadCrumbList } from './views-detail.config'
import ZChartsEngine from '@/lib/packages/z-charts-engine/index.vue'
import { useAuthStore } from '@/store/useAuth'
import PreviewReport from '../preview-report/index.vue'

const authStore = useAuthStore()

const guid = function () {
    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
    }
    return S4() + S4() + '-' + S4() + '-' + S4() + '-' + S4() + '-' + S4() + S4() + S4()
}
const maxWidth = 150
const ZChartsEngineRef = ref()
const previewReportRef = ref()
const loading = ref(false)
const networkError = ref(false)
const chartsList = ref([
    {
        chartName: '测试柱形图',
        chartId: '2',
        type: 'line',
        typeName: '柱形图',

        uuid: guid(),
        x: 0,
        y: 0,
        w: maxWidth,
        h: 16,
        i: guid(),

        option: {
            title: {
                text: '柱形图',
                textStyle: {
                    fontSize: '12px'
                },
                left: '2%',
                top: '14px'
            },
            xAxis: {
                type: 'category',
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
            },
            yAxis: {
                type: 'value'
            },
            series: [
                {
                    data: [150, 230, 224, 218, 135, 147, 260],
                    type: 'bar'
                }
            ]
        }
    },
    {
        chartName: '测试折线图',
        chartId: '1',
        type: 'line',
        typeName: '折线图',
        uuid: guid(),
        x: 0,
        y: 0,
        w: maxWidth,
        h: 16,
        i: guid(),

        option: {
            title: {
                text: '折线图',
                textStyle: {
                    fontSize: '12px'
                },
                left: '2%',
                top: '14px'
            },
            toolbox: {
                show: true
            },
            xAxis: {
                type: 'category',
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
            },
            yAxis: {
                type: 'value'
            },
            series: [
                {
                    data: [150, 230, 224, 218, 135, 147, 260],
                    type: 'line'
                }
            ]
        }
    },
    {
        chartName: '测试饼图',
        chartId: '3',
        type: 'line',
        typeName: '饼图',
        uuid: guid(),
        x: 0,
        y: 0,
        w: maxWidth,
        h: 16,
        i: guid(),

        option: {
            title: {
                text: '测试饼图',
                // subtext: 'Fake Data',
                left: 'center'
            },
            tooltip: {
                trigger: 'item'
            },
            legend: {
                bottom: 10,
                left: 'center'
            },
            series: [
                {
                    name: 'Access From',
                    type: 'pie',
                    radius: '50%',
                    data: [
                        { value: 1048, name: 'Search Engine' },
                        { value: 735, name: 'Direct' },
                        { value: 580, name: 'Email' },
                        { value: 484, name: 'Union Ads' },
                        { value: 300, name: 'Video Ads' }
                    ],
                    emphasis: {
                        itemStyle: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ]
        }
    }
])
// const renderSence = ref('readonly')
const renderSence = ref('edit')
const componentList = ref([
    {
            chartName: '测试饼图',
            chartId: '3',
            type: 'line',
            typeName: '饼图',
            uuid: '9578ca7d-0f27-1025-64a2-5ae7fd60080a',
            x: 0,
            y: 0,
            w: 150,
            h: 16,
            i: 'e86635fc-0721-118e-41f2-f364e7c9fbb2',
            option: {
                title: { text: '测试饼图', textStyle: { fontSize: '14px' }, left: '2%', top: '14px'  },
                tooltip: { trigger: 'item' },
                legend: { bottom: 10, left: 'center' },
                series: [
                    {
                        name: 'Access From',
                        type: 'pie',
                        radius: '50%',
                        data: [
                            { value: 1048, name: 'Search Engine' },
                            { value: 735, name: 'Direct' },
                            { value: 580, name: 'Email' },
                            { value: 484, name: 'Union Ads' },
                            { value: 300, name: 'Video Ads' }
                        ],
                        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
                    }
                ]
            },
            moved: false
    },
    {
        chartName: '测试柱形图',
        chartId: '2',
        type: 'line',
        uuid: '08648238-0e28-4ea1-2ce2-66218e24ef71',
        typeName: '柱形图',
        x: 150,
        y: 0,
        w: 150,
        h: 16,
        i: 'ec63c299-4931-f59c-8f52-29c8552690d0',
        option: {
            tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
            title: { text: '柱形图', textStyle: { fontSize: '14px' }, left: '2%', top: '14px' },
            xAxis: { type: 'category', data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'] },
            yAxis: { type: 'value' },
            series: [{ data: [150, 230, 224, 218, 135, 147, 260], type: 'bar' }]
        },
        moved: false
    },
    {
        chartName: '测试折线图',
        chartId: '1',
        type: 'line',
        typeName: '折线图',
        uuid: 'bdf709db-6688-00a5-c1c6-260ccaa947f0',
        x: 0,
        y: 16,
        w: 300,
        h: 16,
        i: 'd455c75f-c57f-0ee8-201f-0c015010a9cd',
        option: {
            tooltip: {
                trigger: 'axis'
            },
            title: { text: '折线图', textStyle: { fontSize: '14px' }, left: '2%', top: '14px' },
            toolbox: { show: true },
            xAxis: { type: 'category', data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'] },
            yAxis: { type: 'value' },
            series: [{ data: [150, 230, 224, 218, 135, 147, 260], type: 'line' }]
        },
        moved: false
    }
])
const breadCrumbList = reactive(BreadCrumbList)

// 后续迁出组件
watch(
    () => authStore.isCollapse,
    (newVal) => {
        setTimeout(() => {
            ZChartsEngineRef.value.resizeAllCharts()
        }, 320)
    }
)

// 获取可以拖拽的报表组件
function getChartListEvent(e: string) {
    console.log('条件', e)
}

function previewChatEvent(e: any) {
    console.log('预览', e)
    previewReportRef.value.showModal(e.option)
}

function initData() {}

function saveData(): void {
    componentList.value = ZChartsEngineRef.value.getComponentList()
    console.log('componentList', componentList.value)
}

onMounted(() => {})
</script>

<style lang="scss">
.report-views {
    position: relative;
    height: 100%;
    .report-button-container {
        position: absolute;
        top: -56px;
        right: 0;
        display: flex;
        height: 56px;
        align-items: center;
        padding-right: 20px;
    }
}
</style>
