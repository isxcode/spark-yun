<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="report-item">
        <div class="report-button-container">
            <el-button type="primary" :loading="saveLoading" @click="saveConfigEvent">保存</el-button>
            <el-button type="primary" :loading="saveLoading" @click="publishChartEvent">发布</el-button>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(true)">
            <div v-if="showEcharts" class="charts-container" id="currentChart"></div>

            <div class="report-item-config">
                <el-tabs tab-position="left">
                    <el-tab-pane label="基础配置">
                        <div class="config-item">
                            <div class="item-title">基础配置</div>
                            <el-scrollbar>
                                <el-form
                                    ref="clusterConfigForm"
                                    label-position="top"
                                    :model="baseConfig"
                                    :rules="baseConfigRules"
                                >
                                    <el-form-item label="名称" prop="name">
                                        <el-input
                                            v-model="baseConfig.name"
                                            maxlength="200"
                                            placeholder="请输入"
                                        />
                                    </el-form-item>
                                    <el-form-item label="类型">
                                        <el-select v-model="baseConfig.type" disabled>
                                            <el-option
                                                v-for="item in chartTypeList"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value"
                                            />
                                            </el-select>
                                    </el-form-item>
                                    <el-form-item label="数据源" prop="datasourceId">
                                        <el-select v-model="baseConfig.datasourceId" placeholder="请选择">
                                            <el-option
                                                v-for="item in dataSourceList"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value"
                                            />
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item class="sqls-container" label="聚合Sql" prop="sqls">
                                        <el-icon class="sqls-container-add" @click="addSqlEvent"><CirclePlus /></el-icon>
                                        <div class="sqls-item" v-for="(sql, index) in baseConfig.sqls" :key="index" :class="{ 'show-screen__full': fullScreenArr[index] }">
                                            <div class="sql-option-container">
                                                <span class="sql-option-preview" @click="previewChatEvent(sql)">预览</span>
                                                <div class="sql-option-icon">
                                                    <el-icon v-if="baseConfig.sqls.length > 1" class="remove-sql" @click="removeSqlEvent(index)"><CircleClose /></el-icon>
                                                    <el-icon class="modal-full-screen" @click="fullScreenEvent(index)"><FullScreen v-if="!fullScreenArr[index]" /><Close v-else /></el-icon>
                                                </div>
                                            </div>
                                            <el-input class="shadow-input" disabled v-model="baseConfig.sqls[index]"/>
                                            <code-mirror v-model="baseConfig.sqls[index]" basic :lang="sqlLang"/>
                                        </div>
                                    </el-form-item>
                                    <el-form-item>
                                        <el-button type="primary" @click="refreshDataEvent">刷新数据</el-button>
                                    </el-form-item>
                                </el-form>
                            </el-scrollbar>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="图表设置">
                        <div class="config-item">
                            <div class="item-title">图表设置</div>
                            <el-scrollbar>
                                <el-form
                                    ref="clusterConfigForm"
                                    label-position="top"
                                    :model="chartConfig"
                                    :rules="chartConfigRules"
                                >
                                    <el-form-item label="标题" prop="title">
                                        <el-input
                                            v-model="chartConfig.title"
                                            maxlength="200"
                                            placeholder="请输入"
                                            @blur="updateChartEvent"
                                        />
                                    </el-form-item>
                                    <el-form-item label="刷新间隔（秒）" prop="title">
                                        <el-input-number
                                            v-model="chartConfig.time"
                                            :min="1"
                                            controls-position="right"
                                            placeholder="请输入"
                                        />
                                    </el-form-item>
                                    <el-form-item>
                                        <el-button type="primary" @click="updateChartEvent">更新图表</el-button>
                                    </el-form-item>
                                </el-form>
                            </el-scrollbar>
                        </div>
                    </el-tab-pane>
                </el-tabs>
            </div>
        </LoadingPage>
        <PreviewReport ref="previewReportRef"></PreviewReport>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import * as echarts from 'echarts'
import CodeMirror from 'vue-codemirror6'
import {sql} from '@codemirror/lang-sql'
import PreviewReport from '../preview-report/index.vue'

import { BreadCrumbList, BaseConfigRules, ChartTypeList } from './report-item.config'
import { ConfigReportComponentData, GetReportComponentData, RefreshReportComponentData, PublishReportComponentData } from '@/services/report-echarts.service'
import { useRoute, useRouter } from 'vue-router'
import { GetDatasourceList } from '@/services/datasource.service'

const route = useRoute()
const router = useRouter()

const saveLoading = ref<boolean>(false)
const previewReportRef = ref()
const loading = ref(false)
const showEcharts = ref(true)
const networkError = ref(false)
const chartTypeList = ref(ChartTypeList)
const dataSourceList = ref([])  // 数据源
const fullScreenArr = ref<boolean[]>([])   // 对应sql脚本全屏显示
const sqlLang = ref<any>(sql())
let myChart: any = null
const echartOption = ref<any>()
const breadCrumbList = reactive(BreadCrumbList)
// 基础配置
const baseConfigRules = reactive<FormRules>(BaseConfigRules)
const baseConfig = reactive({
    name: '',           // 名称
    type: '',           // 类型
    datasourceId: '',   // 数据源
    sqls: [''],           // 聚合sql
})

// 图表配置
const chartConfigRules = reactive<FormRules>(BaseConfigRules)
const chartConfig = reactive({
    title: '',          // 标题
    time: 60,           // 图表刷新间隔
})

function initData() {
    myChart = echarts.init(document.getElementById('currentChart'))
    loading.value = true
    GetReportComponentData({
        id: route.query.id
    }).then((res: any) => {
        echartOption.value = res.data.cardInfo.exampleData
        if (!echartOption.value.title) {
            echartOption.value.title = {
                title: res.data.cardInfo.name,
                left: 'center'
            }
            chartConfig.title = res.data.cardInfo.name
        }
        myChart.setOption({...echartOption.value})
        setTimeout(() => {
            myChart.resize()
        })
        window.addEventListener('resize', resizeChart)
        baseConfig.sqls = res.data.cardInfo?.dataSql?.sqlList || ['']
        baseConfig.sqls.forEach(s => {
            fullScreenArr.value.push(false)
        })
        baseConfig.name = res.data.cardInfo.name
        baseConfig.type = res.data.cardInfo.type
        baseConfig.datasourceId = res.data.cardInfo.datasourceId

        if (res.data.cardInfo?.webConfig?.title) {
            chartConfig.title = res.data.cardInfo.webConfig.title
        } else if (res.data.cardInfo.exampleData?.title?.text) {
            chartConfig.title = res.data.cardInfo.exampleData.title?.text
        }
        if (res.data.cardInfo?.webConfig?.time) {
            chartConfig.time = res.data.cardInfo.webConfig.time
        } else {
            chartConfig.time = 60
        }

        loading.value = false
    }).catch((error: any) => {
        loading.value = false
        console.error(error)
    })
}

// 保存数据
function saveConfigEvent() {
    saveLoading.value = true
    ConfigReportComponentData({
        id: route.query.id,
        webConfig: chartConfig,
        exampleData: echartOption.value,
        dataSql: {
            sqlList: baseConfig.sqls
        }
    }).then((res: any) => {
        saveLoading.value = false
        ElMessage.success(res.msg)
    }).catch(() => {
        saveLoading.value = false
    })
}

// 发布
function publishChartEvent() {
    saveLoading.value = true
    ConfigReportComponentData({
        id: route.query.id,
        webConfig: chartConfig,
        exampleData: echartOption.value,
        dataSql: {
            sqlList: baseConfig.sqls
        }
    }).then((res: any) => {
        PublishReportComponentData({
            id: route.query.id,
        }).then((res: any) => {
            saveLoading.value = false
            ElMessage.success(res.msg)
            router.push({
                name: 'report-components'
            })
        }).catch(() => {
            saveLoading.value = false
        })
    }).catch(() => {
        saveLoading.value = false
    })
}

// 基础配置事件
function fullScreenEvent(index: number) {
    fullScreenArr.value[index] = !fullScreenArr.value[index]
}

function addSqlEvent() {
    baseConfig.sqls.push('')
}

function removeSqlEvent(index: number) {
    baseConfig.sqls.splice(index, 1)
}

// 刷新数据
function refreshDataEvent() {
    RefreshReportComponentData({
        id: route.query.id,
        dataSql: {
            sqlList: baseConfig.sqls
        }
    }).then((res: any) => {
        echartOption.value = res.data.viewData
        myChart.setOption(echartOption.value)
    }).catch(() => {
    })
}

// 图表配置事件
function updateChartEvent() {
    echartOption.value.title.text = chartConfig.title
    myChart.setOption({ ...echartOption.value })
}

function resizeChart() {
    myChart.resize()
}
// 查询数据源
function getDataSourceList(e: boolean) {
  if (e) {
    GetDatasourceList({
      page: 0,
      pageSize: 10000,
      searchKeyWord: ''
    }).then((res: any) => {
      dataSourceList.value = res.data.content.map((item: any) => {
        return {
          label: item.name,
          value: item.id
        }
      })
    }).catch(() => {
        dataSourceList.value = []
    })
  }
}

// 预览
function previewChatEvent(sql: string) {
    previewReportRef.value.showModal({
        datasourceId: baseConfig.datasourceId,
        sql: sql
    })
}

onMounted(() => {
    getDataSourceList(true)
    initData()
})

onUnmounted(() => {
    window.removeEventListener('resize', resizeChart)
})
</script>

<style lang="scss">
.report-item {
    position: relative;
    .report-button-container {
        position: absolute;
        top: -56px;
        right: 0;
        display: flex;
        height: 56px;
        align-items: center;
        padding-right: 20px;
    }
    .zqy-loading {
        height: calc(100vh - 56px);
        display: flex;
        align-items: center;
        .charts-container {
            height: 50%;
            width: 100%;
        }
        .report-item-config {
            width: 360px;
            height: 100%;
            border-left: 1px solid var(--el-border-color);
            background-color: #ffffff;
            padding-top: 12px;
            box-sizing: border-box;
            position: relative;

            .el-tabs {
                position: absolute;
                left: -41px;
                width: calc(100% + 40px);
                top: 0;
                .el-tabs__header {
                    margin-right: 0;
                    border-left: 1px solid var(--el-border-color);
                    .el-tabs__item {
                        width: 40px;
                        padding: 0;
                        font-size: 12px;
                        display: flex;
                        justify-content: center;
                        white-space: pre-wrap;
                        padding: 0 14px;
                        height: 68px;
                        border-right: 1px solid var(--el-border-color);
                        border-bottom: 1px solid var(--el-border-color);
                        background-color: #ffffff;
                    }
                }
                .el-tabs__content {
                    padding-top: 12px;
                }
            }

            .el-scrollbar {
                .el-scrollbar__view {
                    height: calc(100vh - 80px);
                    padding-right: 0px;
                }
            }

            .item-title {
                font-size: 12px;
                padding-bottom: 12px;
                padding-left: 12px;
                box-sizing: border-box;
                border-bottom: 1px solid #ebeef5;
            }
            .el-form {
                padding: 12px 12px;
                box-sizing: border-box;
                .el-form-item {
                    &.sqls-container {
                        .el-form-item__label {
                            margin-bottom: 0;
                        }
                        .el-form-item__content {
                            position: relative;
                            flex-direction: column;
                            .sqls-container-add {
                                position: absolute;
                                top: -18px;
                                right: 0;
                                cursor: pointer;
                                color: getCssVar('color', 'primary');
                            }
                            .sqls-item {
                                width: 100%;
                                margin-bottom: 4px;
                                
                                &.show-screen__full {
                                    position: fixed;
                                    width: 100%;
                                    height: 100%;
                                    top: 0;
                                    left: 0;
                                    background-color: #ffffff;
                                    padding: 0 12px;
                                    box-sizing: border-box;
                                    transition: all 0.15s linear;
                                    z-index: 10;
                                    .vue-codemirror {
                                        height: calc(100% - 42px);
                                    }
                                }

                                .sql-option-container {
                                    display: flex;
                                    justify-content: flex-end;
                                    align-items: center;
                                    .sql-option-preview {
                                        margin-right: 8px;
                                        font-size: 12px;
                                        color: #666;
                                        cursor: pointer;
                                        color: getCssVar('color', 'primary', 'light-3');
                                        &:hover {
                                            color: getCssVar('color', 'primary');
                                        }
                                    }
                                    .sql-option-icon {
                                        padding-top: 3px;
                                        box-sizing: border-box;
                                        .remove-sql {
                                            color: getCssVar('color', 'danger');
                                            cursor: pointer;
                                            margin-right: 8px;
                                        }
                                        .modal-full-screen {
                                            cursor: pointer;
                                            color: getCssVar('color', 'primary', 'light-3');
                                            &:hover {
                                                color: getCssVar('color', 'primary');
                                            }
                                        }
                                    }
                                }
                            }
                            .vue-codemirror {
                                height: 130px;
                                width: 100%;
    
                                .cm-editor {
                                    height: 100%;
                                    outline: none;
                                    border: 1px solid #dcdfe6;
                                }
    
                                .cm-gutters {
                                    font-size: 12px;
                                    font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                                }
    
                                .cm-content {
                                    font-size: 12px;
                                    font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                                }
    
                                .cm-tooltip-autocomplete {
                                    ul {
                                        li {
                                        height: 40px;
                                        display: flex;
                                        align-items: center;
                                        font-size: 12px;
                                        background-color: #ffffff;
                                        font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                                        }
    
                                        li[aria-selected] {
                                        background: #409EFF;
                                        }
    
                                        .cm-completionIcon {
                                        margin-right: -4px;
                                        opacity: 0;
                                        }
                                    }
                                }
                            }
                            .shadow-input {
                                position: fixed;
                                top: -99999px;
                                opacity: 0;
                            }
                        }
                    }
                }
            }
        }
    }
}
</style>
