<template>
    <div class="z-share-form">
        <Header />
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="getReportConfigById">
            <div class="share-form-container">
                <ZChartsEngine
                    ref="ZChartsEngineRef"
                    :chartsList="chartsList"
                    :renderSence="renderSence"
                    :componentList="componentList"
                    :getPreviewOption="getPreviewOption"
                    :getRealDataOption="getRealDataOption"
                ></ZChartsEngine>
            </div>
        </LoadingPage>
    </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, watch, reactive } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { useRoute } from 'vue-router'
import Header from '@/layout/header/index.vue'
import ZChartsEngine from '@/lib/packages/z-charts-engine/index.vue'
import { GetChartsLinkInfoConfig, RefreshReportViewItemData, GetReportComponentData, GetReportViewDetail } from '@/services/report-echarts.service'

interface baseParam {
    tenantId: string
    viewId: string
    viewToken: string
    viewVersion: string
}

const route = useRoute()

const loading = ref(false)
const networkError = ref(false)
const chartsList = ref<any[]>([])
const componentList = ref<any[]>([])
const renderSence = ref('readonly')

const shareLinkId = ref<string>('')
const shareReportConfig = ref<baseParam>({
    viewId: '',
    viewVersion: '',
    tenantId: '',
    viewToken: ''
})

// 真实场景下获取真实数据
function getRealDataOption(config: any) {
    return new Promise((resolve, reject) => {
        // console.log('获取真实数据')
        RefreshReportViewItemData({
            id: config.id
        }, {
            authorization: shareReportConfig.value.viewToken,
            tenant: shareReportConfig.value.tenantId
        }).then((res: any) => {
            resolve(res.data.viewData)
        }).catch(() => {})
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

function getReportConfigById(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    GetReportViewDetail({
        id: shareReportConfig.value.viewId
    }, {
        authorization: shareReportConfig.value.viewToken,
        tenant: shareReportConfig.value.tenantId
    }).then((res: any) => {
        componentList.value = res.data?.webConfig?.cardList || []
        loading.value = false
    }).catch(() => {
        loading.value = false
        networkError.value = true
    })
}

function getlinkParams() {
    return new Promise((resolve, reject) => {
        loading.value = true
        GetChartsLinkInfoConfig({
            viewLinkId: shareLinkId.value
        }).then((res: any) => {
            shareReportConfig.value = res.data
            resolve()
        }).catch((error: any) => {
            loading.value = false
            reject(error)
        })
    })
}

onMounted(() => {
    const params: any = route.params.shareParam
    if (params) {
        shareLinkId.value = params
        getlinkParams().then(() => {
            getReportConfigById()
        })
    }
})
</script>

<style lang="scss">
.z-share-form {
    width: 100%;
    background-color: #ffffff;
    height: 100vh;
    overflow: auto;
    position: relative;

    .zqy-breadcrumb {
        box-shadow: getCssVar('box-shadow', 'lighter');
    }

    .share-form-button {
        position: absolute;
        top: 0;
        right: 0;
        height: 60px;
        display: flex;
        align-items: center;
        z-index: 100;
        padding-right: 20px;
        box-sizing: border-box;
    }

    .share-form-container {
        // padding: 0 8%;
        box-sizing: border-box;
        height: 100%;
        max-height: calc(100vh - 60px);
        position: absolute;
        top: 60px;
        left: 0;
        width: 100%;

        .zqy-form-engine {
            .form-components {
                .el-scrollbar {
                    .el-scrollbar__view {
                        padding: 0 10%;
                    }
                }
            }
        }
    }
}
</style>
