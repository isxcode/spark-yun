<template>
    <div class="data-sync-page computing-detail" :class="{ 'work-detail__disabled': props.disabled }">
        <div class="data-sync__option-container" v-if="!props.disabled">
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
                <span class="btn-text">中止</span>
            </div>
            <div class="btn-box" @click="setConfigData">
                <el-icon>
                    <Setting />
                </el-icon>
                <span class="btn-text">配置</span>
            </div>
            <div class="btn-box" @click="locationNode">
                <el-icon>
                    <Position />
                </el-icon>
                <span class="btn-text">定位</span>
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
                    <el-form ref="form" label-position="left" label-width="88px" :model="formData" :rules="rules" :disabled="props.disabled">
                        <el-form-item prop="sourceDBType" label="类型">
                            <el-select v-model="formData.sourceDBType" placeholder="请选择" @change="sourceDBTypeChangeEvent">
                                <el-option v-for="item in sourceTypeList" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <template v-if="formData.sourceDBType === 'API'">
                            <el-form-item prop="requestUrl" label="接口">
                                <div class="api-request-line">
                                    <el-select v-model="formData.requestType" class="api-request-line__method" placeholder="请求方式">
                                        <el-option label="GET" value="GET" />
                                        <el-option label="POST" value="POST" />
                                    </el-select>
                                    <el-input
                                        v-model="formData.requestUrl"
                                        class="api-request-line__url"
                                        clearable
                                        placeholder="请输入接口"
                                        maxlength="1000"
                                    ></el-input>
                                </div>
                            </el-form-item>
                            <el-form-item label="配置" class="api-config-row">
                                <div class="api-request-line__actions">
                                    <el-button type="primary" link size="small" @click="openRequestHeaderConfig('source')">请求头</el-button>
                                    <el-button type="primary" link size="small" @click="openRequestBodyTemplateConfig('source')">请求体模版</el-button>
                                    <el-button type="primary" link size="small" @click="openResponseBodyTemplateConfig('source')">响应体模版</el-button>
                                </div>
                            </el-form-item>
                            <el-form-item label="请求体" v-if="formData.requestType === 'POST'">
                                <code-mirror v-model="formData.requestBody" basic :lang="jsonLang" @change="pageChangeEvent" />
                            </el-form-item>
                            <!-- <el-form-item prop="kafkaConfig.topic" label="topic">
                                <el-select v-model="formData.kafkaConfig.topic" clearable filterable placeholder="请选择" @change="pageChangeEvent"
                                    @visible-change="getTopicList($event, formData.sourceDBId)"
                                >
                                    <el-option v-for="item in topList" :key="item.value" :label="item.label"
                                        :value="item.value" />
                                </el-select>
                            </el-form-item> -->
                            <el-form-item prop="jsonDataType" label="解析类型">
                                <div class="parse-type-line">
                                    <el-select v-model="formData.jsonDataType" clearable filterable placeholder="请选择" @change="getCurrentTableColumn">
                                        <el-option label="数组节点" value="LIST" />
                                        <el-option label="对象节点" value="OBJECT" />
                                    </el-select>
                                    <el-button type="primary" link size="small" @click="previewDataEvent">数据预览</el-button>
                                </div>
                            </el-form-item>
                            <el-form-item prop="rootJsonPath" label="节点" v-if="formData.jsonDataType === 'LIST'">
                                <el-select v-model="formData.rootJsonPath" clearable filterable placeholder="请选择" @change="rootJsonPathBlur" @visible-change="getJsonNodeArray">
                                    <el-option v-for="item in nodeArrayList" :key="item.value" :label="item.label" :value="item.value" />
                                </el-select>
                            </el-form-item>
                            <el-form-item label="每页">
                                <div class="paging-line">
                                    <el-input-number
                                        v-model="formData.pageSize"
                                        :min="1"
                                        :max="100000"
                                        controls-position="right"
                                        @change="pageChangeEvent"
                                    />
                                    <span class="paging-line__text">第</span>
                                    <el-input-number
                                        v-model="formData.pageStart"
                                        :min="1"
                                        :max="100000"
                                        controls-position="right"
                                        :disabled="formData.pageAll"
                                        @change="pageChangeEvent"
                                    />
                                    <span class="paging-line__text">到</span>
                                    <el-input-number
                                        v-model="formData.pageEnd"
                                        :min="1"
                                        :max="100000"
                                        controls-position="right"
                                        :disabled="formData.pageAll"
                                        @change="pageChangeEvent"
                                    />
                                    <span class="paging-line__text">页</span>
                                    <el-checkbox v-model="formData.pageAll" @change="pageChangeEvent">
                                        <span class="paging-line__checkbox-text">全部</span>
                                    </el-checkbox>
                                </div>
                            </el-form-item>
                            <el-form-item prop="queryCondition" label="过滤条件">
                                <code-mirror v-model="formData.queryCondition" basic :lang="lang" @change="pageChangeEvent" />
                            </el-form-item>
                        </template>
                        <template v-else>
                            <el-form-item prop="sourceDBId" label="数据源">
                                <el-select v-model="formData.sourceDBId" clearable filterable placeholder="请选择"
                                    @visible-change="getDataSource($event, formData.sourceDBType, 'source')"
                                    @change="dbIdChange('source')">
                                    <el-option v-for="item in sourceList" :key="item.value" :label="item.label"
                                        :value="item.value" />
                                </el-select>
                            </el-form-item>
                            <el-form-item prop="sourceTable" label="表">
                                <el-select
                                    v-model="formData.sourceTable"
                                    clearable
                                    filterable
                                    allow-create
                                    default-first-option
                                    placeholder="请选择或输入表名"
                                    @visible-change="getDataSourceTable($event, formData.sourceDBId, 'source')"
                                    @change="tableChangeEvent($event, formData.sourceDBId, 'source')"
                                >
                                    <el-option v-for="item in sourceTablesList" :key="item.value" :label="item.label"
                                        :value="item.value" />
                                </el-select>
                                <el-button v-if="!props.disabled" type="primary" link @click="showSourceTableDetail">数据预览</el-button>
                            </el-form-item>
                            <el-form-item label="分区键">
                                <el-select
                                    v-model="formData.partitionColumn"
                                    clearable
                                    filterable
                                    placeholder="请选择"
                                    @visible-change="getTableColumnData($event, formData.sourceDBId, formData.sourceTable)"
                                    @change="pageChangeEvent"
                                >
                                    <el-option v-for="item in partKeyList" :key="item.value" :label="item.label" :value="item.value" />
                                </el-select>
                            </el-form-item>
                            <el-form-item prop="queryCondition" label="过滤条件">
                                <code-mirror v-model="formData.queryCondition" basic :lang="lang" @change="pageChangeEvent" />
                            </el-form-item>
                        </template>
                    </el-form>
                </el-card>
                <el-card class="box-card">
                    <template #header>
                        <div class="card-header">
                            <span>数据去向</span>
                        </div>
                    </template>
                    <el-form ref="form" label-position="left" label-width="70px" :model="formData" :rules="rules" :disabled="props.disabled">
                        <el-form-item prop="targetDBType" label="类型">
                            <el-select v-model="formData.targetDBType" clearable filterable placeholder="请选择"
                                @change="dbTypeChange">
                                <el-option v-for="item in typeList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <template v-if="formData.targetDBType === 'API'">
                            <el-form-item label="接口">
                                <div class="api-request-line">
                                    <el-select v-model="formData.targetRequestType" class="api-request-line__method" placeholder="请求方式">
                                        <el-option label="GET" value="GET" />
                                        <el-option label="POST" value="POST" />
                                    </el-select>
                                    <el-input
                                        v-model="formData.targetRequestUrl"
                                        class="api-request-line__url"
                                        clearable
                                        placeholder="请输入接口"
                                        maxlength="1000"
                                    ></el-input>
                                </div>
                            </el-form-item>
                            <el-form-item label="配置" class="api-config-row">
                                <div class="api-request-line__actions">
                                    <el-button type="primary" link size="small" @click="openRequestHeaderConfig('target')">请求头</el-button>
                                    <el-button type="primary" link size="small" @click="openRequestBodyTemplateConfig('target')">请求体模版</el-button>
                                </div>
                            </el-form-item>
                            <el-form-item label="请求体" v-if="formData.targetRequestType === 'POST'">
                                <code-mirror v-model="formData.targetRequestBody" basic :lang="jsonLang" @change="pageChangeEvent" />
                            </el-form-item>
                        </template>
                        <template v-else>
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
                                    @visible-change="getDataSourceTable($event, formData.targetDBId, 'target')"
                                    @change="tableChangeEvent($event, formData.targetDBId, 'target')">
                                    <el-option v-for="item in targetTablesList" :key="item.value" :label="item.label"
                                        :value="item.value" />
                                </el-select>
                            </el-form-item>
                        </template>
                        <!-- <el-form-item prop="overMode" label="写入模式">
                            <el-select v-model="formData.overMode" clearable filterable placeholder="请选择" @change="pageChangeEvent">
                                <el-option v-for="item in overModeList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item> -->
                    </el-form>
                </el-card>
            </div>
            <data-sync-table ref="dataSyncTableRef" :formData="formData" :disabled="props.disabled"></data-sync-table>
        </div>
        <!-- 数据同步日志部分  v-if="instanceId" -->
        <el-collapse v-if="!!instanceId" v-model="collapseActive" class="data-sync-log__collapse" ref="logCollapseRef">
            <el-collapse-item title="查看日志" :disabled="true" name="1">
                <template #title>
                    <el-tabs v-model="activeName" @tab-click="changeCollapseUp" @tab-change="tabChangeEvent">
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
        <el-dialog
            v-model="requestHeaderVisible"
            title="请求头配置"
            width="520px"
            :close-on-click-modal="false"
            append-to-body
            class="advanced-config-dialog"
        >
            <div class="advanced-config-content">
                <div
                    v-for="(item, index) in requestHeaderConfigList"
                    :key="index"
                    class="advanced-config-row"
                >
                    <el-input v-model="item.label" placeholder="Key" style="flex: 1;" />
                    <el-input v-model="item.value" placeholder="Value" style="flex: 1; margin-left: 8px;" />
                    <el-button type="danger" link @click="removeRequestHeader(index)" style="margin-left: 8px;">
                        <el-icon><Delete /></el-icon>
                    </el-button>
                </div>
            </div>
            <template #footer>
                <div class="advanced-config-footer">
                    <el-button type="primary" link @click="addRequestHeader">添加请求头</el-button>
                    <div>
                        <el-button @click="requestHeaderVisible = false">取消</el-button>
                        <el-button type="primary" @click="saveRequestHeaderConfig">确定</el-button>
                    </div>
                </div>
            </template>
        </el-dialog>
        <el-dialog
            v-model="requestBodyTemplateVisible"
            title="请求体模版"
            width="720px"
            :close-on-click-modal="false"
            append-to-body
        >
            <div class="template-json-body">
                <code-mirror v-model="requestBodyTemplateText" class="template-json-editor" basic :lang="jsonLang" />
            </div>
            <template #footer>
                <div class="template-config-footer template-config-footer--between">
                    <el-button type="primary" @click="formatTemplateJson('request')">格式化JSON</el-button>
                    <div class="template-config-footer__right">
                        <el-button @click="requestBodyTemplateVisible = false">取消</el-button>
                        <el-button type="primary" @click="saveRequestBodyTemplateConfig">确定</el-button>
                    </div>
                </div>
            </template>
        </el-dialog>
        <el-dialog
            v-model="responseBodyTemplateVisible"
            title="响应体模版"
            width="720px"
            :close-on-click-modal="false"
            append-to-body
        >
            <code-mirror v-model="responseBodyTemplateText" basic :lang="jsonLang" />
            <template #footer>
                <div class="template-config-footer template-config-footer--between">
                    <el-button type="primary" @click="formatTemplateJson('response')">格式化JSON</el-button>
                    <div class="template-config-footer__right">
                        <el-button @click="responseBodyTemplateVisible = false">取消</el-button>
                        <el-button type="primary" @click="saveResponseBodyTemplateConfig">确定</el-button>
                    </div>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, defineProps, nextTick, markRaw } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
// import CodeMirror from 'vue-codemirror6'
import { sql } from '@codemirror/lang-sql'
import {json} from '@codemirror/lang-json'
import { DataSourceType, CurrentSourceType } from './data.config.ts'
import { GetDatasourceList } from '@/services/datasource.service'
import { GetDataSourceTables, GetTableColumnsByTableId } from '@/services/data-sync.service'
import TableDetail from './table-detail/index.vue'
import DataSyncTable from './data-sync-table/index.vue'
import ConfigDetail from '../workflow-page/config-detail/index.vue'
import { GetJsonArrayNodeList, GetTopicDataList } from '@/services/realtime-computing.service.ts'
import { GetLineageWorkItemConfig, GetWorkItemConfig, RunWorkItemConfig, SaveWorkItemConfig, TerWorkItemConfig } from '@/services/workflow.service'
import PublishLog from '../work-item/publish-log.vue'
import RunningLog from '../work-item/running-log.vue'
import { Delete, Loading } from '@element-plus/icons-vue'
import { jsonFormatter } from '@/utils/formatter'

interface Option {
    label: string
    value: string
}
const props = defineProps<{
    workItemConfig: any,
    disabled?: boolean
}>()
const emit = defineEmits(['back', 'locationNode'])

const changeStatus = ref(false)
const configDetailRef = ref()
const form = ref<FormInstance>()
const tableDetailRef = ref()
const dataSyncTableRef = ref()
const lang = ref<any>(sql())
const jsonLang = ref<any>(json())
const sourceList = ref<Option[]>([])
const targetList = ref<Option[]>([])
const topList = ref<Option[]>([])
const sourceTablesList = ref<Option[]>([])
const targetTablesList = ref<Option[]>([])
const partKeyList = ref<Option[]>([])
const kafkaSourceList = ref<Option[]>([])
// const overModeList = ref<Option[]>(OverModeList)
const sourceTypeList = ref<Option[]>(CurrentSourceType)
const typeList = ref(DataSourceType);
const nodeArrayList = ref([])
const requestHeaderVisible = ref(false)
const requestHeaderConfigList = ref<Array<{ label: string; value: string }>>([])
const requestBodyTemplateVisible = ref(false)
const requestBodyTemplateText = ref('')
const responseBodyTemplateVisible = ref(false)
const responseBodyTemplateText = ref('')
const requestConfigScope = ref<'source' | 'target'>('source')

// 日志展示相关
const containerInstanceRef = ref(null)
const activeName = ref()
const currentTab = ref()
const instanceId = ref('')
const logCollapseRef = ref()
const collapseActive = ref('0')
const isCollapse = ref(false)
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
    sourceDBType: 'API',  // 来源数据源类型
    sourceDBId: '',       // 来源数据源
    sourceTable: '',      // 来源数据库表名
    kafkaConfig: {        // topic
        topic: ''
    },
    kafkaSourceId: '',    // kafka数据源
    cat: [],              // cat
    kafkaDataType: 'JSON',// kafka数据类型
    jsonTemplate: '',     // json模板
    jsonDataType: '',     // 数据解析类型
    rootJsonPath: '',     // 节点
    queryCondition: '',   // 来源数据库查询条件
    partitionColumn: '',
    pageSize: 10,
    pageStart: 1,
    pageEnd: 2,
    pageAll: false,
    requestUrl: '',
    requestType: '',
    requestBody: '',
    requestHeader: [
        {
            label: '',
            value: ''
        }
    ],

    targetDBType: 'DATASOURCE', // 目标数据库类型
    targetDBId: '',       // 目标数据源
    targetTable: '',      // 目标数据库表名
    targetRequestUrl: '',
    targetRequestType: '',
    targetRequestBody: '',
    targetRequestHeader: [
        {
            label: '',
            value: ''
        }
    ],
    targetJsonTemplate: '',
    overMode: '',         // 写入模式
})
const rules = reactive<FormRules>({})
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
    containerInstanceRef.value.initData(instanceId.value)
  })
}

// 保存数据
function saveData() {
    btnLoadingConfig.saveLoading = true
    SaveWorkItemConfig({
        workId: props.workItemConfig.id,
        apiWorkConfig: {
            requestUrl: formData.requestUrl,
            requestType: formData.requestType,
            requestBody: formData.requestBody,
            requestParam: [],
            requestHeader: formData.requestHeader
        },
        syncWorkConfig: {
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
    let requestMethod = GetWorkItemConfig
    let requestParams: any = { workId: props.workItemConfig.id }
    if (props.disabled) {
        requestMethod = GetLineageWorkItemConfig
        requestParams = { workVersionId: props.workItemConfig.id }
    }

    requestMethod(requestParams).then((res: any) => {
        if (res.data.syncWorkConfig) {
            Object.keys(formData).forEach((key: string) => {
                formData[key] = res.data.syncWorkConfig[key]
            })
            formData.pageSize = Number(formData.pageSize) || 10
            formData.pageStart = Number(formData.pageStart) || 1
            formData.pageEnd = Number(formData.pageEnd) || 2
            formData.pageAll = !!formData.pageAll

            nextTick(() => {
                getDataSource(true, formData.sourceDBType, 'source')
                getDataSource(true, formData.targetDBType, 'target')
                if (formData.sourceDBType === 'DATASOURCE') {
                    getDataSourceTable(true, formData.sourceDBId, 'source')
                }
                getDataSourceTable(true, formData.targetDBId, 'target')

                dataSyncTableRef.value.initPageData(res.data.syncWorkConfig)
                changeStatus.value = false
            })
        }
        if (res.data.apiWorkConfig) {
            formData.requestUrl = res.data.apiWorkConfig.requestUrl || ''
            formData.requestType = res.data.apiWorkConfig.requestType || ''
            formData.requestBody = res.data.apiWorkConfig.requestBody || ''
            formData.requestHeader = res.data.apiWorkConfig.requestHeader?.length
                ? res.data.apiWorkConfig.requestHeader
                : [{ label: '', value: '' }]
        }
    }).catch(err => {
        console.error(err)
    })
}

// 停止接口采集作业
function stopData() {
    if (!instanceId.value) {
        ElMessage.warning('暂无可停止的作业')
        return
    }
    btnLoadingConfig.stopLoading = true
    TerWorkItemConfig({
        workId: props.workItemConfig.id,
        instanceId: instanceId.value
    }).then((res: any) => {
        ElMessage.success(res.msg)
        btnLoadingConfig.stopLoading = false
    })
    .catch((error: any) => {
        btnLoadingConfig.stopLoading = false
    })
}

// 运行接口采集作业
function startComputing() {
    if (changeStatus.value) {
        ElMessageBox.confirm('作业尚未保存，是否确定要运行？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
        }).then(() => {
            tabChangeEvent('PublishLog')
            runTimeFunc()
        })
    } else {
        tabChangeEvent('PublishLog')
        runTimeFunc()
    }
}
function runTimeFunc() {
    btnLoadingConfig.runningLoading = true
    RunWorkItemConfig({
        workId: props.workItemConfig.id
    }).then((res: any) => {
        ElMessage.success(res.msg)
        instanceId.value = res.data.instanceId
        nextTick(() => {
            containerInstanceRef.value.initData(instanceId.value)
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
        let searchKeyWord = sourceType || ''
        if (['API', 'DATASOURCE'].includes(sourceType)) {
            searchKeyWord = ''
        }
        GetDatasourceList({
            page: 0,
            pageSize: 10000,
            searchKeyWord: searchKeyWord
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
            topList.value = res.data.map((item: any) => {
                return {
                    label: item,
                    value: item
                }
            })
        }).catch(err => {
            console.error(err)
            topList.value = []
        })
    } else {
        topList.value = []
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
function getDataSourceTable(e: boolean, dataSourceId: string, type: string) {
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
            type === 'source' ? sourceTablesList.value = options : targetTablesList.value = options
        }).catch(err => {
            console.error(err)
            type === 'source' ? sourceTablesList.value = [] : targetTablesList.value = []
        })
    } else {
        type === 'source' ? sourceTablesList.value = [] : targetTablesList.value = []
    }
}

// 获取kafka数据源表
function getKafkaSourceTable(e: boolean, sourceType: string) {
    if (e && sourceType) {
        GetDatasourceList({
            page: 0,
            pageSize: 10000,
            searchKeyWord: sourceType || ''
        }).then((res: any) => {
            kafkaSourceList.value = res.data.content.map((item: any) => {
                return {
                    label: item.name,
                    value: item.id
                }
            })
        }).catch(err => {
            console.error(err)
            kafkaSourceList.value = []
        })
    } else {
        kafkaSourceList.value = []
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
    if (type === 'source') {
        formData.partitionColumn = ''
    }
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
        formData.partitionColumn = ''
        partKeyList.value = []
    } else {
        formData.targetTable = ''
    }
}

// 返回
function goBack() {
    if (changeStatus.value) {
        ElMessageBox.confirm('接口采集作业尚未保存，是否确定要返回吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
        }).then(() => {
            emit('back')
        })
    } else {
        emit('back')
    }
}

function getTableColumnData(e: boolean, dataSourceId: string, tableName: string) {
    if (e && dataSourceId && tableName) {
        GetTableColumnsByTableId({
            dataSourceId: dataSourceId,
            tableName: tableName
        }).then((res: any) => {
            partKeyList.value = (res.data.columns || []).map((item: any) => {
                return {
                    label: item.name,
                    value: item.name
                }
            })
        }).catch(err => {
            console.error(err)
            partKeyList.value = []
        })
    } else {
        partKeyList.value = []
    }
}

function previewDataEvent() {
    if (!formData.targetDBId || !formData.targetTable) {
        ElMessage.warning('请先选择数据去向中的数据源和表')
        return
    }
    tableDetailRef.value.showModal({
        dataSourceId: formData.targetDBId,
        tableName: formData.targetTable
    })
}

function showSourceTableDetail() {
    if (formData.sourceDBId && formData.sourceTable) {
        tableDetailRef.value.showModal({
            dataSourceId: formData.sourceDBId,
            tableName: formData.sourceTable
        })
    } else {
        ElMessage.warning('请选择数据源和表')
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
    configDetailRef.value.showModal(props.workItemConfig)
}

function changeCollapseDown() {
    logCollapseRef.value.setActiveNames('0')
    isCollapse.value = false
}
function changeCollapseUp(e: any) {
    if (e && e.paneName === activeName.value && isCollapse.value) {
        changeCollapseDown()
    } else {
        logCollapseRef.value.setActiveNames('1')
        isCollapse.value = true
    }
}
function pageChangeEvent() {
    changeStatus.value = true
}

function openRequestHeaderConfig(scope: 'source' | 'target' = 'source') {
    requestConfigScope.value = scope
    const currentHeader = scope === 'source' ? formData.requestHeader : formData.targetRequestHeader
    requestHeaderConfigList.value = currentHeader?.length
        ? currentHeader.map((item: any) => ({ label: item.label || '', value: item.value || '' }))
        : [{ label: '', value: '' }]
    requestHeaderVisible.value = true
}

function addRequestHeader() {
    requestHeaderConfigList.value.push({
        label: '',
        value: ''
    })
}

function removeRequestHeader(index: number) {
    requestHeaderConfigList.value.splice(index, 1)
}

function saveRequestHeaderConfig() {
    const validItems = requestHeaderConfigList.value.filter((item: any) => item.label?.trim() !== '')
    const keys = validItems.map((item: any) => item.label.trim())
    const uniqueKeys = new Set(keys)
    if (keys.length !== uniqueKeys.size) {
        ElMessage.warning('配置项Key不能重复')
        return
    }

    const saveData = validItems.map((item: any) => ({
        label: item.label.trim(),
        value: item.value?.trim?.() || ''
    }))
    if (requestConfigScope.value === 'source') {
        formData.requestHeader = saveData
    } else {
        formData.targetRequestHeader = saveData
    }
    requestHeaderConfigList.value = saveData.length
        ? saveData.map((item: any) => ({ ...item }))
        : [{ label: '', value: '' }]
    requestHeaderVisible.value = false
    pageChangeEvent()
}

function openRequestBodyTemplateConfig(scope: 'source' | 'target' = 'source') {
    requestConfigScope.value = scope
    requestBodyTemplateText.value = scope === 'source' ? (formData.requestBody || '') : (formData.targetRequestBody || '')
    requestBodyTemplateVisible.value = true
}

function saveRequestBodyTemplateConfig() {
    if (requestConfigScope.value === 'source') {
        formData.requestBody = requestBodyTemplateText.value
    } else {
        formData.targetRequestBody = requestBodyTemplateText.value
    }
    requestBodyTemplateVisible.value = false
    pageChangeEvent()
}

function formatTemplateJson(type: 'request' | 'response') {
    try {
        if (type === 'request') {
            requestBodyTemplateText.value = jsonFormatter(requestBodyTemplateText.value || '')
        } else {
            responseBodyTemplateText.value = jsonFormatter(responseBodyTemplateText.value || '')
        }
    } catch (error) {
        console.error('请检查输入的JSON格式是否正确', error)
        ElMessage.error('请检查输入的JSON格式是否正确')
    }
}

function openResponseBodyTemplateConfig(scope: 'source' | 'target' = 'source') {
    requestConfigScope.value = scope
    responseBodyTemplateText.value = scope === 'source' ? (formData.jsonTemplate || '') : (formData.targetJsonTemplate || '')
    responseBodyTemplateVisible.value = true
}

function saveResponseBodyTemplateConfig() {
    if (requestConfigScope.value === 'source') {
        formData.jsonTemplate = responseBodyTemplateText.value
    } else {
        formData.targetJsonTemplate = responseBodyTemplateText.value
    }
    responseBodyTemplateVisible.value = false
    pageChangeEvent()
}

function sourceDBTypeChangeEvent() {
    changeStatus.value = true
    formData.sourceDBId = ''
    formData.sourceTable = ''
    formData.partitionColumn = ''
    partKeyList.value = []
    dataSyncTableRef.value.getTableColumnData({
        dataSourceId: '',
        tableName: ''
    }, 'source')
}

function locationNode() {
    if (changeStatus.value) {
        ElMessageBox.confirm('作业尚未保存，是否要保存并定位节点？', '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            saveData()
            emit('locationNode', props.workItemConfig.id)
        })
    } else {
        emit('locationNode', props.workItemConfig.id)
    }
}

onMounted(() => {
    formData.workId = props.workItemConfig.id

    if (props.workItemConfig.status !== 'NEW') {
        instanceId.value = props.workItemConfig.id
    }
    getData()
    activeName.value = 'PublishLog'
    currentTab.value = markRaw(PublishLog)
})
</script>

<style lang="scss">
.computing-detail {
    .data-sync {
        .data-sync-top {
            .box-card {
                .el-form {
                    .el-form-item {
                        .el-form-item__content {
                            .api-request-line {
                                display: flex;
                                gap: 10px;
                                width: 100%;
                            }

                            .api-request-line__method {
                                width: 140px;
                                min-width: 140px;
                            }

                            .api-request-line__url {
                                flex: 1;
                            }

                            .api-request-line__actions {
                                display: flex;
                                align-items: center;
                                justify-content: flex-start;
                                gap: 6px;
                                width: 100%;
                                min-height: 32px;
                                flex-shrink: 0;
                            }

                            .api-config-row :deep(.el-form-item__content) {
                                min-height: 32px;
                                display: flex;
                                align-items: center;
                            }

                            .api-config-row :deep(.el-button) {
                                line-height: 32px;
                            }

                            .parse-type-line {
                                width: 100%;
                                display: flex;
                                align-items: center;
                                justify-content: space-between;
                                gap: 8px;
                            }

                            .paging-line {
                                width: 100%;
                                display: flex;
                                align-items: center;
                                gap: 8px;
                                flex-wrap: nowrap;
                                font-size: 14px;
                                font-weight: 400;
                                font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
                                color: var(--el-text-color-regular);
                            }

                            .paging-line :deep(.el-input-number) {
                                width: 120px;
                            }

                            .paging-line__text {
                                font-size: inherit;
                                color: inherit;
                                line-height: 32px;
                            }

                            .paging-line :deep(.el-checkbox__label) {
                                font-size: inherit;
                                color: inherit;
                                line-height: 32px;
                                font-weight: inherit;
                                font-family: inherit;
                            }

                            .paging-line :deep(.el-checkbox.is-checked .el-checkbox__label) {
                                color: var(--el-text-color-regular);
                            }

                            .paging-line__checkbox-text {
                                font-size: inherit;
                                color: inherit;
                                line-height: 32px;
                                font-weight: inherit;
                                font-family: inherit;
                            }

                            .paging-line :deep(.el-input__inner),
                            .paging-line :deep(.el-input-number__decrease),
                            .paging-line :deep(.el-input-number__increase) {
                                font-size: 14px;
                                font-weight: 400;
                                font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
                            }

                            .el-checkbox-group {
                                .el-checkbox {
                                    .el-checkbox__label {
                                        font-size: 12px;
                                        line-height: normal;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    .log-show-download {
        .zqy-download-log {
            right: 40px;
            top: 12px;
        }
    }
}

.advanced-config-content {
    .advanced-config-row {
        display: flex;
        align-items: center;
        margin-bottom: 8px;
    }
}

.advanced-config-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;

    > div {
        display: flex;
        align-items: center;
    }
}

.template-config-footer {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 8px;
    width: 100%;
}

.template-config-footer--between {
    justify-content: space-between;
}

.template-config-footer__right {
    display: flex;
    align-items: center;
    gap: 8px;
}

.template-json-body {
    position: relative;
}

.template-json-editor {
    padding-top: 4px;
}

.template-json-editor :deep(.cm-editor) {
    min-height: 360px;
}
</style>
