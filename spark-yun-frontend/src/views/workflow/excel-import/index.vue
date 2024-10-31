<template>
    <div class="data-sync-page excel-import">
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
            <div class="btn-box" @click="runWorkData">
                <el-icon v-if="!btnLoadingConfig.runningLoading">
                    <VideoPlay />
                </el-icon>
                <el-icon v-else class="is-loading">
                    <Loading />
                </el-icon>
                <span class="btn-text">运行</span>
            </div>
            <div class="btn-box" @click="terWorkData">
                <el-icon v-if="!btnLoadingConfig.stopWorkFlowLoading">
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
                    <el-form ref="form" label-position="left" label-width="70px" :model="formData" :rules="rules">
                        <el-form-item prop="sourceFileId" label="Excel文件">
                            <el-select v-model="formData.sourceFileId" clearable filterable placeholder="请选择" @change="getTableColummList" @visible-change="getFileCenterList">
                                <el-option v-for="item in fileList" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="存在表头" label-width="80px">
                            <el-switch v-model="formData.hasHeader" @change="getTableColummList(formData.sourceFileId)"></el-switch>
                            <el-button style="margin-left: 24px" type="primary" link @click="showTableDetail">数据预览</el-button>
                        </el-form-item>
                        <el-form-item prop="sourceTable" label="开启文件替换" label-width="140px">
                            <el-switch v-model="formData.fileReplace" @change="pageChangeEvent"></el-switch>
                            <el-button style="margin-left: 12px" type="primary" link :disabled="!formData.fileReplace" :loading="fileNameLoading" @click="showReplaceEvent">文件名预览</el-button>
                        </el-form-item>
                        <el-form-item prop="filePattern" label="替换规则" v-if="formData.fileReplace">
                            <el-input v-model="formData.filePattern" clearable placeholder="请输入" @change="pageChangeEvent"></el-input>
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
                                @change="dbTypeChange('target')">
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
                                @visible-change="getDataSourceTable($event, formData.targetDBId, 'target')"
                                @change="tableChangeEvent($event, formData.targetDBId, 'target')">
                                <el-option v-for="item in targetTablesList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                            <!-- <el-button type="primary" link @click="createTableWork">生成建表作业</el-button> -->
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
                <div class="log-show log-show-datasync">
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
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
// import CodeMirror from 'vue-codemirror6'
import { sql } from '@codemirror/lang-sql'
import { DataSourceType, OverModeList } from './data.config.ts'
import { GetDatasourceList } from '@/services/datasource.service'
import { GetFileCenterList } from '@/services/file-center.service'
import { GetDataSourceTables, GetExcelReplaceName } from '@/services/data-sync.service'
import TableDetail from './table-detail/index.vue'
import DataSyncTable from './data-sync-table/index.vue'
import ConfigDetail from '../workflow-page/config-detail/index.vue'
import { GetWorkItemConfig, RunWorkItemConfig, SaveWorkItemConfig, TerWorkItemConfig } from '@/services/workflow.service'
import PublishLog from '../work-item/publish-log.vue'
import RunningLog from '../work-item/running-log.vue'
import { Loading } from '@element-plus/icons-vue'

interface Option {
    label: string
    value: string
}

const props = defineProps<{
    workItemConfig: any
}>()

const emit = defineEmits(['back', 'locationNode'])

const changeStatus = ref(false)
const configDetailRef = ref()
const form = ref<FormInstance>()
const tableDetailRef = ref()
const dataSyncTableRef = ref()
const lang = ref<any>(sql())
const sourceList = ref<Option[]>([])
const targetList = ref([])
const sourceTablesList = ref<Option[]>([])
const targetTablesList = ref<Option[]>([])
const overModeList = ref<Option[]>(OverModeList)
const typeList = ref(DataSourceType);
const fileList = ref<Option[]>([])
const fileNameLoading = ref<boolean>(false)   // 文件名预览loading

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
    sourceFileId: '',     // Excel文件
    hasHeader: true,      // 是否有表头
    fileReplace: false,   // 是否开启文件替换
    filePattern: '',      // 替换规则
    queryCondition: '',   // 过滤条件

    targetDBType: '',     // 目标数据库类型
    targetDBId: '',       // 目标数据源
    targetTable: '',      // 目标数据库表名
    overMode: '',         // 写入模式
})
const rules = reactive<FormRules>({
})
const btnLoadingConfig = reactive({
    runningLoading: false,
    reRunLoading: false,
    saveLoading: false,
    publishLoading: false,
    stopWorkFlowLoading: false,
    importLoading: false,
    exportLoading: false,
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
    containerInstanceRef.value.initData(instanceId.value)
  })
}

// 保存数据
function saveData() {
    btnLoadingConfig.saveLoading = true
    SaveWorkItemConfig({
        workId: formData.workId,
        excelSyncConfig: {
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

function getDate() {
    GetWorkItemConfig({
        workId: props.workItemConfig.id
    }).then((res: any) => {
        if (res.data.excelSyncConfig) {
            formData.sourceFileId = res.data.excelSyncConfig.sourceFileId
            formData.hasHeader = res.data.excelSyncConfig.hasHeader
            formData.fileReplace = res.data.excelSyncConfig.fileReplace
            formData.filePattern = res.data.excelSyncConfig.filePattern
            formData.queryCondition = res.data.excelSyncConfig.queryCondition
            formData.targetDBType = res.data.excelSyncConfig.targetDBType
            formData.targetDBId = res.data.excelSyncConfig.targetDBId
            formData.targetTable = res.data.excelSyncConfig.targetTable
            formData.overMode = res.data.excelSyncConfig.overMode

            nextTick(() => {
                getFileCenterList(true)
                getDataSource(true, formData.targetDBType, 'target')
                getDataSourceTable(true, formData.targetDBId, 'target')
                dataSyncTableRef.value.initPageData(res.data.excelSyncConfig)
                changeStatus.value = false
            })
        }
    }).catch(err => {
        console.error(err)
    })
}
// 运行
function runWorkData() {
    if (changeStatus.value) {
        ElMessageBox.confirm('作业尚未保存，是否确定要运行作业？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
        }).then(() => {
            btnLoadingConfig.runningLoading = true
            // 运行自动切换到提交日志
            tabChangeEvent('PublishLog')

            RunWorkItemConfig({
                workId: props.workItemConfig.id
            }).then((res: any) => {
                instanceId.value = res.data.instanceId
                ElMessage.success(res.msg)
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
        })
    } else {
        btnLoadingConfig.runningLoading = true
        // 运行自动切换到提交日志
        tabChangeEvent('PublishLog')

        RunWorkItemConfig({
            workId: props.workItemConfig.id
        }).then((res: any) => {
            instanceId.value = res.data.instanceId
            ElMessage.success(res.msg)
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
}
// 终止
function terWorkData() {
  if (!instanceId.value) {
    ElMessage.warning('暂无可中止的作业')
    return
  }
  btnLoadingConfig.stopWorkFlowLoading = true
  TerWorkItemConfig({
    workId: props.workItemConfig.id,
    instanceId: instanceId.value
  }).then((res: any) => {
    btnLoadingConfig.stopWorkFlowLoading = false
    ElMessage.success(res.msg)
  }).catch(() => {
    btnLoadingConfig.stopWorkFlowLoading = false
  })
}

// 获取资源-excel文件
function getFileCenterList(e: boolean) {
    if (e) {
        GetFileCenterList({
            page: 0,
            pageSize: 10000,
            searchKeyWord: '',
            type: 'EXCEL'
        }).then((res: any) => {
            fileList.value = res.data.content.map(item => {
            return {
                label: item.fileName,
                value: item.id
            }
            })
        }).catch(() => {
            fileList.value = []
        })
    }
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

// 数据预览
function showTableDetail(): void {
    if (formData.sourceFileId) {
        tableDetailRef.value.showModal({
            fileId: formData.sourceFileId,
            hasHeader: formData.hasHeader
        })
    } else {
        ElMessage.warning('请选择Excel文件')
    }
}

// 文件名预览
function showReplaceEvent() {
    if (!formData.filePattern) {
        ElMessage.error('请输入替换规则')
        return
    }
    fileNameLoading.value = true
    GetExcelReplaceName({
        filePattern: formData.filePattern
    }).then((res: any) => {
        ElMessageBox.alert(res.data.fileName, '文件名预览', {
            confirmButtonText: '关闭'
        })
        fileNameLoading.value = false
    }).catch(() => {
        fileNameLoading.value = false
    })
}

function getTableColummList(e: string) {
    changeStatus.value = true
    if (e) {
        dataSyncTableRef.value.getTableColummListByExcel({
            fileId: e,
            hasHeader: formData.hasHeader
        })
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
function dbTypeChange(type: string) {
    changeStatus.value = true
    formData.targetDBId = ''
    formData.targetTable = ''
}
// 级联控制
function dbIdChange(type: string) {
    changeStatus.value = true
    formData.targetTable = ''
}

// 返回
function goBack() {
    if (changeStatus.value) {
        ElMessageBox.confirm('作业尚未保存，是否确定要返回吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
        }).then(() => {
        emit('back', props.workItemConfig.id)
        })
    } else {
        emit('back', props.workItemConfig.id)
    }
}
function locationNode() {
    if (changeStatus.value) {
        ElMessageBox.confirm('作业尚未保存，是否确定要返回吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
        }).then(() => {
        emit('locationNode', props.workItemConfig.id)
        })
    } else {
        emit('locationNode', props.workItemConfig.id)
    }
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

onMounted(() => {
    formData.workId = props.workItemConfig.id
    getDate()
    activeName.value = 'PublishLog'
    currentTab.value = markRaw(PublishLog)
})
</script>

<style lang="scss">
.data-sync-page {
    &.excel-import {

    }
}
</style>
