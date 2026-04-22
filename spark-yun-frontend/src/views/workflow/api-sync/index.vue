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
            <div class="btn-box" @click="emit('sortWorkList')">
                <el-icon>
                    <Sort v-if="!props.orderType" />
                    <SortDown v-else-if="props.orderType === 'desc'" />
                    <SortUp v-else />
                </el-icon>
                <span class="btn-text">{{ props.orderType === 'desc' ? '降序' : props.orderType === 'acs' ? '升序' : '排序' }}</span>
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
                            <el-form-item prop="requestUrl">
                                <template #label>
                                    <span class="request-body-label">
                                        <span>接口</span>
                                        <el-tooltip placement="right" effect="dark">
                                            <template #content>
                                                <div class="request-body-tip__content">
                                                    <div>POST分页请求案例：</div>
                                                    <div>{</div>
                                                    <div>&nbsp;&nbsp;"custom_page": "${page}",</div>
                                                    <div>&nbsp;&nbsp;"custom_pageSize": "${pageSize}"</div>
                                                    <div>}</div>
                                                    <div>GET分页请求案例：</div>
                                                    <div>?custom_page=${page}&custom_pageSize=${pageSize}</div>
                                                </div>
                                            </template>
                                            <el-icon class="tooltip-msg"><QuestionFilled /></el-icon>
                                        </el-tooltip>
                                    </span>
                                </template>
                                <div class="api-request-line">
                                    <el-select v-model="formData.requestType" class="api-request-line__method" placeholder="请求方式" @change="sourceRequestTypeChangeEvent">
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
                                        :min="0"
                                        :max="100000"
                                        controls-position="right"
                                        :disabled="formData.pageAll"
                                        @change="pageChangeEvent"
                                    />
                                    <span class="paging-line__text">到</span>
                                    <el-input-number
                                        v-model="formData.pageEnd"
                                        :min="0"
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
                            <el-select v-model="formData.targetDBType" placeholder="请选择" @change="dbTypeChange">
                                <el-option v-for="item in typeList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <template v-if="formData.targetDBType === 'API'">
                            <el-form-item label="接口">
                                <div class="api-request-line">
                                    <el-select v-model="formData.targetRequestType" class="api-request-line__method" placeholder="请求方式" @change="targetRequestTypeChangeEvent">
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
                        <el-form-item prop="overMode">
                            <template #label>
                                <span class="request-body-label">
                                    <span>写入模式</span>
                                    <el-tooltip placement="right" effect="dark" content="覆写模式会修改目标表的字段结构">
                                        <el-icon class="tooltip-msg"><QuestionFilled /></el-icon>
                                    </el-tooltip>
                                </span>
                            </template>
                            <el-select v-model="formData.overMode" clearable filterable placeholder="请选择" @change="pageChangeEvent">
                                <el-option v-for="item in filteredOverModeList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                    </el-form>
                </el-card>
            </div>
            <data-sync-table ref="dataSyncTableRef" :formData="formData" :disabled="props.disabled"></data-sync-table>
        </div>
        <!-- 数据同步日志部分 -->
        <el-collapse v-if="showLogPanel" v-model="collapseActive" class="data-sync-log__collapse" ref="logCollapseRef">
            <div class="log-resize-handle" @mousedown="startResizeLogPanel"></div>
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
                <div class="log-show log-show-download" :style="{ height: `${logPanelHeight}px` }">
                    <component :is="currentTab" ref="containerInstanceRef" class="show-container" :style="{ height: `${logPanelHeight}px` }" />
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
import { ref, reactive, onMounted, onUnmounted, defineProps, nextTick, markRaw, computed } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
// import CodeMirror from 'vue-codemirror6'
import { sql } from '@codemirror/lang-sql'
import {json} from '@codemirror/lang-json'
import { OverModeList } from './data.config.ts'
import { GetDatasourceList } from '@/services/datasource.service'
import { GetDataSourceTables, GetTableColumnsByTableId } from '@/services/data-sync.service'
import TableDetail from './table-detail/index.vue'
import DataSyncTable from './data-sync-table/index.vue'
import ConfigDetail from '../workflow-page/config-detail/index.vue'
import { GetJsonArrayNodeList, GetTopicDataList } from '@/services/realtime-computing.service.ts'
import { GetApiDataPreview, GetLineageWorkItemConfig, GetWorkItemConfig, RunWorkItemConfig, SaveWorkItemConfig, TerWorkItemConfig } from '@/services/workflow.service'
import PublishLog from '../work-item/publish-log.vue'
import RunningLog from '../work-item/running-log.vue'
import { Delete, Loading, QuestionFilled } from '@element-plus/icons-vue'
import { jsonFormatter } from '@/utils/formatter'

interface Option {
    label: string
    value: string
}
const props = defineProps<{
    workItemConfig: any,
    disabled?: boolean,
    orderType?: 'acs' | 'desc' | ''
}>()
const emit = defineEmits(['back', 'locationNode', 'sortWorkList'])

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
const overModeList = ref<Option[]>(OverModeList)
const sourceTypeList = ref<Option[]>([
    {
        label: '接口',
        value: 'API'
    }
])
const typeList = ref<Option[]>([
    {
        label: '数据源',
        value: 'DATASOURCE'
    }
]);
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
const suppressTabLogInitOnce = ref(false)
const logCollapseRef = ref()
const collapseActive = ref('0')
const isCollapse = ref(false)
const logPanelHeight = ref(320)
const resizeState = reactive({
    resizing: false,
    startY: 0,
    startHeight: 320
})
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
    overMode: 'INTO',     // 写入模式
})
const rules = reactive<FormRules>({})
const btnLoadingConfig = reactive({
    saveLoading: false,
    publishLoading: false,
    runningLoading: false,
    stopLoading: false
})
const showLogPanel = computed(() => !!instanceId.value || btnLoadingConfig.runningLoading)

function toNumberOrDefault(value: any, defaultValue: number) {
    const numberValue = Number(value)
    return Number.isNaN(numberValue) ? defaultValue : numberValue
}

// 日志tab切换
function startResizeLogPanel(event: MouseEvent) {
    resizeState.resizing = true
    resizeState.startY = event.clientY
    resizeState.startHeight = logPanelHeight.value
    document.addEventListener('mousemove', onResizeLogPanel)
    document.addEventListener('mouseup', stopResizeLogPanel)
    event.preventDefault()
}

function onResizeLogPanel(event: MouseEvent) {
    if (!resizeState.resizing) {
        return
    }
    const maxHeight = Math.max(220, window.innerHeight - 220)
    const nextHeight = resizeState.startHeight + (resizeState.startY - event.clientY)
    logPanelHeight.value = Math.min(maxHeight, Math.max(180, nextHeight))
}

function stopResizeLogPanel() {
    resizeState.resizing = false
    document.removeEventListener('mousemove', onResizeLogPanel)
    document.removeEventListener('mouseup', stopResizeLogPanel)
}

function tabChangeEvent(e: string) {
  const lookup = {
    PublishLog: PublishLog,
    RunningLog: RunningLog
  }
  activeName.value = e
  currentTab.value = markRaw(lookup[e])
  if (suppressTabLogInitOnce.value) {
    suppressTabLogInitOnce.value = false
    nextTick(() => {
      changeCollapseUp()
    })
    return
  }
  nextTick(() => {
    changeCollapseUp()
    containerInstanceRef.value?.initData(instanceId.value)
  })
}

function convertHeaderListToMap(headerList: Array<{ label: string; value: string }> = []) {
    const headerMap: Record<string, string> = {}
    headerList.forEach((item: any) => {
        const key = item?.label?.trim?.()
        if (key) {
            headerMap[key] = item?.value ?? ''
        }
    })
    return headerMap
}

function normalizeHeaderToList(headerData: any) {
    if (Array.isArray(headerData)) {
        const validData = headerData
            .filter((item: any) => item?.label?.trim?.())
            .map((item: any) => ({
                label: item.label.trim(),
                value: item?.value ?? ''
            }))
        return validData.length ? validData : [{ label: '', value: '' }]
    }

    if (headerData && typeof headerData === 'object') {
        const data = Object.keys(headerData).map((key: string) => ({
            label: key,
            value: headerData[key] ?? ''
        }))
        return data.length ? data : [{ label: '', value: '' }]
    }

    return [{ label: '', value: '' }]
}

// 保存数据
function saveData() {
    btnLoadingConfig.saveLoading = true
    SaveWorkItemConfig({
        workId: props.workItemConfig.id,
        apiSyncConfig: {
            workId: props.workItemConfig.id,
            sourceType: formData.sourceDBType,
            targetType: formData.targetDBType,
            sourceDBId: formData.sourceDBId,
            sourceTable: formData.sourceTable,
            jsonDataType: formData.jsonDataType,
            nodeRootJsonPath: formData.rootJsonPath,
            partitionColumn: formData.partitionColumn,
            queryCondition: formData.queryCondition,
            sourceRequestType: formData.requestType,
            sourceRequestHttp: formData.requestUrl,
            sourceRequestHeader: convertHeaderListToMap(formData.requestHeader),
            sourceRequestBody: formData.requestBody,
            sourceResponseBody: formData.jsonTemplate,
            sourcePageSize: Number(formData.pageSize) || 10,
            sourceStartPage: toNumberOrDefault(formData.pageStart, 0),
            sourceEndPage: toNumberOrDefault(formData.pageEnd, 1),
            syncAll: !!formData.pageAll,
            targetDBId: formData.targetDBId,
            targetTable: formData.targetTable,
            targetRequestType: formData.targetRequestType,
            targetRequestHttp: formData.targetRequestUrl,
            targetRequestHeader: convertHeaderListToMap(formData.targetRequestHeader),
            targetRequestBody: formData.targetRequestBody,
            overMode: formData.overMode,
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
        if (res.data.apiSyncConfig) {
            const apiSyncConfig = res.data.apiSyncConfig
            const tableInitData = {
                ...apiSyncConfig,
                sourceTableColumn: apiSyncConfig.sourceTableColumn || [],
                targetTableColumn: apiSyncConfig.targetTableColumn || [],
                columnMap: apiSyncConfig.columnMap || []
            }
            Object.keys(formData).forEach((key: string) => {
                formData[key] = apiSyncConfig[key] ?? formData[key]
            })
            formData.sourceDBType = 'API'
            formData.targetDBType = 'DATASOURCE'
            formData.requestType = apiSyncConfig.sourceRequestType || formData.requestType || 'GET'
            formData.requestUrl = apiSyncConfig.sourceRequestHttp || formData.requestUrl || ''
            formData.requestBody = apiSyncConfig.sourceRequestBody || formData.requestBody || ''
            formData.requestHeader = normalizeHeaderToList(apiSyncConfig.sourceRequestHeader)
            formData.jsonTemplate = apiSyncConfig.sourceResponseBody || formData.jsonTemplate || ''
            formData.jsonDataType = apiSyncConfig.jsonDataType || formData.jsonDataType || ''
            formData.rootJsonPath = apiSyncConfig.nodeRootJsonPath || formData.rootJsonPath || ''
            formData.pageSize = Number(apiSyncConfig.sourcePageSize) || 10
            formData.pageStart = toNumberOrDefault(apiSyncConfig.sourceStartPage, 0)
            formData.pageEnd = toNumberOrDefault(apiSyncConfig.sourceEndPage, 1)
            formData.pageAll = !!apiSyncConfig.syncAll
            formData.targetRequestType = apiSyncConfig.targetRequestType || formData.targetRequestType || 'POST'
            formData.targetRequestUrl = apiSyncConfig.targetRequestHttp || formData.targetRequestUrl || ''
            formData.targetRequestBody = apiSyncConfig.targetRequestBody || formData.targetRequestBody || ''
            formData.targetRequestHeader = normalizeHeaderToList(apiSyncConfig.targetRequestHeader)

            formData.sourceDBId = apiSyncConfig.sourceDBId || formData.sourceDBId || ''
            formData.sourceTable = apiSyncConfig.sourceTable || formData.sourceTable || ''
            formData.partitionColumn = apiSyncConfig.partitionColumn || formData.partitionColumn || ''
            formData.queryCondition = apiSyncConfig.queryCondition || formData.queryCondition || ''
            formData.targetDBId = apiSyncConfig.targetDBId || formData.targetDBId || ''
            formData.targetTable = apiSyncConfig.targetTable || formData.targetTable || ''
            formData.overMode = apiSyncConfig.overMode || formData.overMode || 'INTO'
            sourceTablesList.value = []
            targetTablesList.value = []

            nextTick(() => {
                getDataSource(true, formData.sourceDBType, 'source')
                getDataSource(true, formData.targetDBType, 'target')

                dataSyncTableRef.value.initPageData(tableInitData)
                changeStatus.value = false
            })
            return
        }

        // 兼容历史配置字段，避免旧数据无法回显
        if (res.data.syncWorkConfig) {
            Object.keys(formData).forEach((key: string) => {
                formData[key] = res.data.syncWorkConfig[key]
            })
            formData.sourceDBType = 'API'
            formData.targetDBType = 'DATASOURCE'
            formData.pageSize = Number(formData.pageSize) || 10
            formData.pageStart = toNumberOrDefault(formData.pageStart, 0)
            formData.pageEnd = toNumberOrDefault(formData.pageEnd, 1)
            formData.pageAll = !!formData.pageAll
            formData.overMode = formData.overMode || 'INTO'
            sourceTablesList.value = []
            targetTablesList.value = []

            nextTick(() => {
                getDataSource(true, formData.sourceDBType, 'source')
                getDataSource(true, formData.targetDBType, 'target')

                dataSyncTableRef.value.initPageData(res.data.syncWorkConfig)
                changeStatus.value = false
            })
        }
        if (res.data.apiWorkConfig) {
            formData.requestUrl = res.data.apiWorkConfig.requestUrl || ''
            formData.requestType = res.data.apiWorkConfig.requestType || ''
            formData.requestBody = res.data.apiWorkConfig.requestBody || ''
            formData.requestHeader = normalizeHeaderToList(res.data.apiWorkConfig.requestHeader)
        }
    }).catch(err => {
        console.error(err)
    })
}

const filteredOverModeList = computed(() => {
    return overModeList.value
})

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
            runTimeFunc()
        })
    } else {
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
        suppressTabLogInitOnce.value = true
        activeName.value = 'PublishLog'
        currentTab.value = markRaw(PublishLog)
        nextTick(() => {
            suppressTabLogInitOnce.value = false
            changeCollapseUp()
            containerInstanceRef.value?.initData(instanceId.value)
        })
        btnLoadingConfig.runningLoading = false
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
function getJsonNodeArray(e: boolean, jsonTemplate?: string) {
    if (!e) {
        return
    }
    const currentJsonTemplate = jsonTemplate ?? formData.jsonTemplate
    if (currentJsonTemplate) {
        GetJsonArrayNodeList({
            jsonStr: currentJsonTemplate
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

function parseSourceTemplateByJsonType(jsonDataType?: string, showEmptyTemplateError?: boolean) {
    const parseType = jsonDataType || formData.jsonDataType
    if (!parseType) {
        return
    }
    const currentJsonTemplate = (formData.jsonTemplate || '').trim()
    if (!currentJsonTemplate) {
        nodeArrayList.value = []
        dataSyncTableRef.value.getTableColumnData({
            dataSourceId: '',
            tableName: ''
        }, 'source')
        if (showEmptyTemplateError) {
            ElMessage.error('响应体模版为空')
        }
        return
    }
    if (parseType === 'OBJECT') {
        dataSyncTableRef.value.getCurrentTableColumn({
            jsonStr: currentJsonTemplate
        })
        return
    }
    if (parseType === 'LIST') {
        getJsonNodeArray(true, currentJsonTemplate)
        if (formData.rootJsonPath) {
            dataSyncTableRef.value.getCurrentTableColumn({
                jsonStr: currentJsonTemplate,
                rootPath: formData.rootJsonPath
            })
        } else {
            dataSyncTableRef.value.getTableColumnData({
                dataSourceId: '',
                tableName: ''
            }, 'source')
        }
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
    parseSourceTemplateByJsonType(e, true)
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
    if (formData.sourceDBType !== 'API') {
        ElMessage.warning('当前数据来源不是接口类型')
        return
    }
    if (!formData.requestType || !formData.requestUrl) {
        ElMessage.warning('请先填写接口和请求方式')
        return
    }
    if (!formData.jsonDataType) {
        ElMessage.warning('请先选择解析类型')
        return
    }
    const tableColumn = dataSyncTableRef.value?.getSourceTableColumn?.() || []
    if (!tableColumn.length) {
        ElMessage.warning('请先解析来源字段')
        return
    }

    GetApiDataPreview({
        requestType: formData.requestType,
        requestHttp: formData.requestUrl,
        requestHeader: convertHeaderListToMap(formData.requestHeader),
        requestBody: formData.requestBody,
        responseBody: formData.jsonTemplate,
        pageSize: Number(formData.pageSize) || 10,
        startPage: toNumberOrDefault(formData.pageStart, 0),
        endPage: toNumberOrDefault(formData.pageEnd, 1),
        syncAll: !!formData.pageAll,
        jsonDataType: formData.jsonDataType,
        nodeRootJsonPath: formData.rootJsonPath,
        tableColumn: tableColumn
    }).then((res: any) => {
        tableDetailRef.value.showApiModal({
            columns: res.data?.columns || [],
            rows: res.data?.rows || []
        })
    }).catch((err: any) => {
        console.error(err)
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
    if (!logCollapseRef.value) {
        return
    }
    logCollapseRef.value.setActiveNames('0')
    isCollapse.value = false
}
function changeCollapseUp(e: any) {
    if (!logCollapseRef.value) {
        return
    }
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

function sourceRequestTypeChangeEvent() {
    if (formData.requestType === 'GET') {
        formData.requestBody = ''
    }
    pageChangeEvent()
}

function targetRequestTypeChangeEvent() {
    if (formData.targetRequestType === 'GET') {
        formData.targetRequestBody = ''
    }
    pageChangeEvent()
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
        parseSourceTemplateByJsonType()
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

onUnmounted(() => {
    stopResizeLogPanel()
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
                                width: 80px;
                                min-width: 80px;
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

                            .tooltip-msg {
                                font-size: 16px;
                                color: #c0c4cc;
                            }

                            .request-body-label {
                                display: inline-flex;
                                align-items: center;
                                gap: 4px;
                            }

                            .request-body-tip__content {
                                line-height: 1.6;
                                white-space: nowrap;
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

    .data-sync-log__collapse {
        .log-resize-handle {
            height: 6px;
            cursor: ns-resize;
            position: absolute;
            left: 0;
            right: 0;
            top: -3px;
            z-index: 2;
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
