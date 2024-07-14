<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="data-sync-page">
        <div class="data-sync__option-container">
            <div class="btn-box" @click="goBack">
                <el-icon>
                    <RefreshLeft />
                </el-icon>
                <span class="btn-text">返回</span>
            </div>
            <div class="btn-box" @click="saveData">
                <el-icon v-if="!btnLoadingConfig.saveLoading">
                    <Finished />
                </el-icon>
                <el-icon v-else class="is-loading">
                    <Loading />
                </el-icon>
                <span class="btn-text">保存</span>
            </div>
            <div class="btn-box" @click="startComputing">
                <el-icon v-if="!btnLoadingConfig.runningLoading">
                    <VideoPlay />
                </el-icon>
                <el-icon v-else class="is-loading">
                    <Loading />
                </el-icon>
                <span class="btn-text">运行</span>
            </div>
            <div class="btn-box" @click="stopData">
                <el-icon v-if="!btnLoadingConfig.stopLoading">
                    <Close />
                </el-icon>
                <el-icon v-else class="is-loading">
                    <Loading />
                </el-icon>
                <span class="btn-text">停止</span>
            </div>
            <div class="btn-box" @click="setConfigData">
                <el-icon>
                    <Setting />
                </el-icon>
                <span class="btn-text">配置</span>
            </div>
        </div>
        <div class="data-sync" :class="{ 'data-sync__log': !!instanceId }" id="data-sync">
            <div class="data-sync-top">
                <el-card class="box-card">
                    <template #header>
                        <div class="card-header">
                            <span>数据来源</span>
                        </div>
                    </template>
                    <el-form ref="form" label-position="left" label-width="88px" :model="formData" :rules="rules">
                        <el-form-item prop="sourceDBType" label="类型">
                            <el-select v-model="formData.sourceDBType" placeholder="请选择" @change="pageChangeEvent">
                                <el-option v-for="item in [{ label: 'Kafka', value: 'KAFKA', }]" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <el-form-item prop="sourceDBId" label="数据源">
                            <el-select v-model="formData.sourceDBId" clearable filterable placeholder="请选择"
                                @visible-change="getDataSource($event, formData.sourceDBType, 'source')"
                                @change="dbIdChange('source')">
                                <el-option v-for="item in sourceList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <el-form-item prop="sourceTable" label="topic">
                            <el-select v-model="formData.sourceTable" clearable filterable placeholder="请选择" @change="pageChangeEvent"
                                @visible-change="getTopicList($event, formData.sourceDBId)"
                            >
                                <el-option v-for="item in sourceTablesList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <!-- <el-form-item prop="kafkaDataType" label="kafka数据类型">
                            <el-select v-model="formData.kafkaDataType" disabled clearable filterable placeholder="请选择">
                                <el-option label="JSON" value="JSON" />
                                <el-option label="CSV" value="CSV" />
                            </el-select>
                        </el-form-item> -->
                        <el-form-item prop="jsonTemplate" label="Json模板">
                            <code-mirror v-model="formData.jsonTemplate" basic :lang="jsonLang" @change="pageChangeEvent" />
                        </el-form-item>
                        <el-form-item prop="jsonDataType" label="解析类型">
                            <el-select v-model="formData.jsonDataType" clearable filterable placeholder="请选择" @change="getCurrentTableColumn">
                                <el-option label="数组节点" value="LIST" />
                                <el-option label="对象节点" value="OBJECT" />
                            </el-select>
                        </el-form-item>
                        <el-form-item prop="rootJsonPath" label="节点" v-if="formData.jsonDataType === 'LIST'">
                            <el-select v-model="formData.rootJsonPath" clearable filterable placeholder="请选择" @change="rootJsonPathBlur" @visible-change="getJsonNodeArray">
                                <el-option v-for="item in nodeArrayList" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <el-form-item prop="queryCondition" label="过滤条件">
                            <code-mirror v-model="formData.queryCondition" basic :lang="lang" @change="pageChangeEvent" />
                        </el-form-item>
                    </el-form>
                </el-card>
                <el-card class="box-card">
                    <template #header>
                        <div class="card-header">
                            <span>数据去向</span>
                        </div>
                    </template>
                    <el-form ref="form" label-position="left" label-width="70px" :model="formData" :rules="rules">
                        <el-form-item prop="targetDBType" label="类型">
                            <el-select v-model="formData.targetDBType" clearable filterable placeholder="请选择"
                                @change="dbTypeChange">
                                <el-option v-for="item in typeList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <el-form-item prop="targetDBId" label="数据源">
                            <el-select v-model="formData.targetDBId" clearable filterable placeholder="请选择"
                                @visible-change="getDataSource($event, formData.targetDBType, 'target')"
                                @change="dbIdChange('target')">
                                <el-option v-for="item in targetList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <el-form-item prop="targetTable" label="表">
                            <el-select v-model="formData.targetTable" clearable filterable placeholder="请选择"
                                @visible-change="getDataSourceTable($event, formData.targetDBId)"
                                @change="tableChangeEvent($event, formData.targetDBId, 'target')">
                                <el-option v-for="item in targetTablesList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <el-form-item prop="overMode" label="写入模式">
                            <el-select v-model="formData.overMode" clearable filterable placeholder="请选择" @change="pageChangeEvent">
                                <el-option v-for="item in overModeList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                    </el-form>
                </el-card>
            </div>
            <data-sync-table ref="dataSyncTableRef" :formData="formData"></data-sync-table>
        </div>
        <!-- 数据同步日志部分  v-if="instanceId" -->
        <el-collapse v-if="!!instanceId" v-model="collapseActive" class="data-sync-log__collapse" ref="logCollapseRef">
            <el-collapse-item title="查看日志" :disabled="true" name="1">
                <template #title>
                    <el-tabs v-model="activeName" @tab-change="tabChangeEvent">
                        <template v-for="tab in tabList" :key="tab.code">
                        <el-tab-pane v-if="!tab.hide" :label="tab.name" :name="tab.code" />
                        </template>
                    </el-tabs>
                    <span class="log__collapse">
                        <el-icon v-if="isCollapse" @click="changeCollapseDown"><ArrowDown /></el-icon>
                        <el-icon v-else @click="changeCollapseUp"><ArrowUp /></el-icon>
                    </span>
                </template>
                <div class="log-show log-show-download">
                    <component :is="currentTab" ref="containerInstanceRef" class="show-container" />
                </div>
            </el-collapse-item>
        </el-collapse>
        <!-- 数据预览 -->
        <table-detail ref="tableDetailRef"></table-detail>
        <!-- 配置 -->
        <config-detail ref="configDetailRef"></config-detail>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, defineProps, nextTick, markRaw } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import CodeMirror from 'vue-codemirror6'
import { sql } from '@codemirror/lang-sql'
import {json} from '@codemirror/lang-json'
import { DataSourceType, OverModeList, BreadCrumbList } from './data.config.ts'
import { GetDatasourceList } from '@/services/datasource.service'
import { GetDataSourceTables, GetTableColumnsByTableId } from '@/services/data-sync.service'
import TableDetail from './table-detail/index.vue'
import DataSyncTable from './data-sync-table/index.vue'
import ConfigDetail from '../config-detail/index.vue'
import { ConifgTimeComputingData, GetJsonArrayNodeList, GetTimeComputingDetail, GetTopicDataList, RunTimeComputingData, StopTimeComputingData } from '@/services/realtime-computing.service.ts'
import PublishLog from './publish-log.vue'
import RunningLog from './running-log.vue'
import { Loading } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'

interface Option {
    label: string
    value: string
}
const route = useRoute()
const router = useRouter()
const emit = defineEmits(['back'])

const changeStatus = ref(false)
const configDetailRef = ref()
const form = ref<FormInstance>()
const tableDetailRef = ref()
const dataSyncTableRef = ref()
const lang = ref<any>(sql())
const jsonLang = ref<any>(json())
const sourceList = ref<Option[]>([])
const targetList = ref<Option[]>([])
const sourceTablesList = ref<Option[]>([])
const targetTablesList = ref<Option[]>([])
const overModeList = ref<Option[]>(OverModeList)
const typeList = ref(DataSourceType);
const nodeArrayList = ref([])

// 日志展示相关
const containerInstanceRef = ref(null)
const activeName = ref()
const currentTab = ref()
const instanceId = ref('')
const logCollapseRef = ref()
const collapseActive = ref('0')
const isCollapse = ref(false)
const breadCrumbList = reactive(BreadCrumbList)
const tabList = reactive([
  {
    name: '提交日志',
    code: 'PublishLog',
    hide: false
  },
  {
    name: '运行日志',
    code: 'RunningLog',
    hide: false
  }
])

const formData = reactive({
    workId: '',           // 作业id
    sourceDBType: 'KAFKA',     // 来源数据源类型
    sourceDBId: '',       // 来源数据源
    sourceTable: '',      // 来源数据库表名
    kafkaDataType: 'JSON',     // kafka数据类型
    jsonTemplate: '',     // json模板
    jsonDataType: '',     // 数据解析类型
    rootJsonPath: '',     // 节点
    queryCondition: '',   // 来源数据库查询条件

    targetDBType: '',     // 目标数据库类型
    targetDBId: '',       // 目标数据源
    targetTable: '',      // 目标数据库表名
    overMode: '',         // 写入模式
})
const rules = reactive<FormRules>({
})
const btnLoadingConfig = reactive({
    saveLoading: false,
    publishLoading: false,
    runningLoading: false,
    stopLoading: false
})

// 日志tab切换
function tabChangeEvent(e: string) {
  const lookup = {
    PublishLog: PublishLog,
    RunningLog: RunningLog
  }
  activeName.value = e
  currentTab.value = markRaw(lookup[e])
  nextTick(() => {
    changeCollapseUp()
    containerInstanceRef.value.initData(instanceId.value, true)
  })
}

// 保存数据
function saveData() {
    btnLoadingConfig.saveLoading = true
    ConifgTimeComputingData({
        realId: formData.workId,
        syncConfig: {
            ...formData,
            sourceTableColumn: dataSyncTableRef.value.getSourceTableColumn(),
            targetTableColumn: dataSyncTableRef.value.getTargetTableColumn(),
            columnMap: dataSyncTableRef.value.getConnect()
        }
    }).then((res: any) => {
        changeStatus.value = false
        btnLoadingConfig.saveLoading = false
        ElMessage.success('保存成功')
    }).catch(err => {
        btnLoadingConfig.saveLoading = false
        console.error(err)
    })
}

function getData() {
    GetTimeComputingDetail({id: route.query.id}).then((res: any) => {
        if (res.data.syncConfig) {
            Object.keys(formData).forEach((key: string) => {
                formData[key] = res.data.syncConfig[key]
            })

            nextTick(() => {
                getTopicList(true, formData.sourceDBId)
                getDataSource(true, formData.sourceDBType, 'source')
                getDataSource(true, formData.targetDBType, 'target')
                getDataSourceTable(true, formData.targetDBId)

                dataSyncTableRef.value.initPageData(res.data.syncConfig)
                changeStatus.value = false
            })
        }
    }).catch(err => {
        console.error(err)
    })
}

// 停止实时计算
function stopData() {
    btnLoadingConfig.stopLoading = true
    StopTimeComputingData({
        id: route.query.id
    }).then((res: any) => {
        ElMessage.success(res.msg)
        btnLoadingConfig.stopLoading = false
    })
    .catch((error: any) => {
        btnLoadingConfig.stopLoading = false
    })
}

// 运行实时计算
function startComputing() {
    if (changeStatus.value) {
        ElMessageBox.confirm('作业尚未保存，是否确定要运行？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
        }).then(() => {
            runTimeFunc()
        })
    } else {
        runTimeFunc()
    }
}
function runTimeFunc() {
    btnLoadingConfig.runningLoading = true
    RunTimeComputingData({
        id: route.query.id
    }).then((res: any) => {
        ElMessage.success(res.msg)
        instanceId.value = route.query.id
        nextTick(() => {
            containerInstanceRef.value.initData(instanceId.value, true)
        })
        btnLoadingConfig.runningLoading = false
        nextTick(() => {
            changeCollapseUp()
        })
    }).catch(() => {
        btnLoadingConfig.runningLoading = false
    })
}

// 获取数据源
function getDataSource(e: boolean, sourceType: string, type: string) {
    if (e && sourceType) {
        let options = []
        GetDatasourceList({
            page: 0,
            pageSize: 10000,
            searchKeyWord: sourceType || ''
        }).then((res: any) => {
            options = res.data.content.map((item: any) => {
                return {
                    label: item.name,
                    value: item.id
                }
            })
            type === 'source' ? sourceList.value = options : targetList.value = options
        }).catch(err => {
            console.error(err)
            type === 'source' ? sourceList.value = [] : targetList.value = []
        })
    } else {
        type === 'source' ? sourceList.value = [] : targetList.value = []
    }
}

// 获取topic列表
function getTopicList(e: boolean, dataSourceId: string) {
    if (e && dataSourceId) {
        GetTopicDataList({
            datasourceId: dataSourceId
        }).then((res: any) => {
            sourceTablesList.value = res.data.map((item: any) => {
                return {
                    label: item,
                    value: item
                }
            })
        }).catch(err => {
            console.error(err)
            sourceTablesList.value = []
        })
    } else {
        sourceTablesList.value = []
    }
}
// 获取节点
function getJsonNodeArray(e: boolean) {
    if (!e) {
        return
    }
    if (formData.jsonTemplate) {
        GetJsonArrayNodeList({
            jsonStr: formData.jsonTemplate
        }).then((res: any) => {
            nodeArrayList.value = res.data.map((item: any) => {
                return {
                    label: item,
                    value: item
                }
            })
        }).catch(err => {
            console.error(err)
        })
    } else {
        nodeArrayList.value = []
    }
}

// 获取数据源表
function getDataSourceTable(e: boolean, dataSourceId: string) {
    if (e && dataSourceId) {
        let options = []
        GetDataSourceTables({
            dataSourceId: dataSourceId,
            tablePattern: ""
        }).then((res: any) => {
            options = res.data.tables.map((item: any) => {
                return {
                    label: item,
                    value: item
                }
            })
            targetTablesList.value = options
        }).catch(err => {
            console.error(err)
            targetTablesList.value = []
        })
    } else {
        targetTablesList.value = []
    }
}

function getCurrentTableColumn(e: string) {
    changeStatus.value = true
    if (e === 'OBJECT') {
        dataSyncTableRef.value.getCurrentTableColumn({
            jsonStr: formData.jsonTemplate
        })
    } else {
        getJsonNodeArray(true)
    }
}

function tableChangeEvent(e: string, dataSourceId: string, type: string) {
    changeStatus.value = true
    dataSyncTableRef.value.getTableColumnData({
        dataSourceId: dataSourceId,
        tableName: e
    }, type)
}

// 级联控制
function dbTypeChange() {
    changeStatus.value = true
    formData.targetDBId = ''
    formData.targetTable = ''
}

// 级联控制
function dbIdChange(type: string) {
    changeStatus.value = true
    if (type === 'source') {
        formData.sourceTable = ''
    } else {
        formData.targetTable = ''
    }
}

// 返回
function goBack() {
    if (changeStatus.value) {
        ElMessageBox.confirm('实时计算尚未保存，是否确定要返回吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
        }).then(() => {
            router.push({
                name: 'realtime-computing'
            })
        })
    } else {
        router.push({
            name: 'realtime-computing'
        })
    }
}

function rootJsonPathBlur() {
    dataSyncTableRef.value.getCurrentTableColumn({
        jsonStr: formData.jsonTemplate,
        rootPath: formData.rootJsonPath
    })
}

// 配置打开
function setConfigData() {
    configDetailRef.value.showModal(route.query)
}

function changeCollapseDown() {
    logCollapseRef.value.setActiveNames('0')
    isCollapse.value = false
}
function changeCollapseUp() {
    logCollapseRef.value.setActiveNames('1')
    isCollapse.value = true
}
function pageChangeEvent() {
    changeStatus.value = true
}

onMounted(() => {
    formData.workId = route.query.id

    if (route.query.status !== 'NEW') {
        instanceId.value = route.query.id
    }
    getData()
    activeName.value = 'PublishLog'
    currentTab.value = markRaw(PublishLog)
})
</script>

<style lang="scss">
.log-show-download {
    .zqy-download-log {
        right: 40px;
        top: 12px;
    }
}
</style>
