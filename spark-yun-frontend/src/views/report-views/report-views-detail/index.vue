<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="report-views">
        <div class="report-button-container" v-if="renderSence === 'edit'">
            <el-button type="primary" :loading="saveLoading" @click="saveData">保存</el-button>
            <el-button type="primary" :loading="saveLoading" @click="publishEvent">发布</el-button>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData">
            <ZChartsEngine
                ref="ZChartsEngineRef"
                :chartsList="chartsList"
                :renderSence="renderSence"
                :componentList="componentList"
                :getPreviewOption="getPreviewOption"
                :getRealDataOption="getRealDataOption"
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
import { ElMessage } from 'element-plus'
import PreviewReport from '../preview-report/index.vue'
import {
    QueryReportComponent,
    GetReportComponentData,
    GetReportViewDetail,
    RefreshReportViewItemData,
    SaveReportViewDetail,
    PublishReportViewData
} from '@/services/report-echarts.service'
import { useRoute, useRouter } from 'vue-router'
import { ChartTypeList } from '../../report-components/report-item/report-item.config'

const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()

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
const chartsList = ref([])
const saveLoading = ref(false)
const renderSence = ref('edit')
const componentList = ref([])
const chartTypeList = ref(ChartTypeList)
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
    getChartComponents(e)
}

function previewChatEvent(e: any) {
    GetReportComponentData({
        id: e.id,
    }).then((res: any) => {
        previewReportRef.value.showModal(res.data.cardInfo.exampleData)
    }).catch(() => {
    })
}
// 获取所有配置信息
function initData() {
    if (!route.query.id) {
        return;
    }
    loading.value = true
    GetReportViewDetail({
        id: route.query.id
    }).then((res: any) => {
        loading.value = false
        componentList.value = res.data?.webConfig?.cardList || []
    }).catch(() => {
        loading.value = false
    })
}
// 获取可拖拽的报表组件
function getChartComponents(e: string) {
    QueryReportComponent({
        page: 0,
        pageSize: 99999,
        searchKeyWord: e
    }).then((res: any) => {
        chartsList.value = (res.data.content || []).filter(item => item.status === 'PUBLISHED').map((item: any) => {
            const typeName = chartTypeList.value.find(t => t.value === item.type)
            return {
                chartName: item.name,
                id: item.id,
                type: item.type.toLowerCase(),
                typeName: typeName?.label,
                uuid: guid(),
                x: 0,
                y: 0,
                w: maxWidth,
                h: 16,
                i: guid(),
                option: {}
            }
        })
    }).catch(() => {

    })
}

function saveData(): void {
    if (!route.query.id) {
        return;
    }
    saveLoading.value = true
    componentList.value = ZChartsEngineRef.value.getComponentList()
    SaveReportViewDetail({
        id: route.query.id,
        webConfig: {
            cardList: componentList.value
        },
        cardList: componentList.value.map((item: any) => item.id)
    }).then((res: any) => {
        saveLoading.value = false
        ElMessage.success(res.msg)
    }).catch(() => {
        saveLoading.value = false
    })
}

// 拖拽时获取预览的效果
function getPreviewOption(config: any) {
    return new Promise((resolve, reject) => {
        // console.log('拿到预览数据')
        GetReportComponentData({
            id: config.id
        }).then((res: any) => {
            resolve(res.data.cardInfo)
        }).catch(() => {})
    })
}
// 真实场景下获取真实数据
function getRealDataOption(config: any) {
    return new Promise((resolve, reject) => {
        // console.log('获取真实数据')
        RefreshReportViewItemData({
            id: config.id
        }).then((res: any) => {
            resolve(res.data.viewData)
        }).catch(() => {})
    })
}

function publishEvent() {
    if (!route.query.id) {
        return;
    }
    saveLoading.value = true

    componentList.value = ZChartsEngineRef.value.getComponentList()
    SaveReportViewDetail({
        id: route.query.id,
        webConfig: {
            cardList: componentList.value
        },
        cardList: componentList.value.map((item: any) => item.id)
    }).then((res: any) => {
        PublishReportViewData({
            id: route.query.id
        }).then((res: any) => {
            saveLoading.value = false
            ElMessage.success(res.msg)
            router.push({
                name: 'report-views'
            })
        }).catch(() => {
            saveLoading.value = false
        })
    }).catch(() => {
        saveLoading.value = false
    })
}

onMounted(() => {
    if (route.query.type === 'edit') {
        renderSence.value = 'edit'
        getChartComponents('')
    } else {
        renderSence.value = 'readonly'
    }
    initData()
})
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
