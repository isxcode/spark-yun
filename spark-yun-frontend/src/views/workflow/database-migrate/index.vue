<template>
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
        <LoadingPage
            :visible="loading"
            :network-error="networkError"
            @loading-refresh="getDate"
        >
        <div class="data-sync" :class="{ 'data-sync__log': !!instanceId }" id="data-sync">
                <div class="data-sync-top">
                    <el-card class="box-card">
                        <template #header>
                            <div class="card-header">
                                <span>数据来源</span>
                            </div>
                        </template>
                        <el-form ref="form" label-position="left" label-width="70px" :model="formData" :rules="rules">
                            <el-form-item prop="sourceDBType" label="类型">
                                <el-select
                                    v-model="formData.sourceDBType"
                                    clearable
                                    filterable
                                    placeholder="请选择"
                                    @change="dbTypeChange('source')"
                                >
                                    <el-option
                                        v-for="item in typeList"
                                        :key="item.value"
                                        :label="item.label"
                                        :value="item.value"
                                    />
                                </el-select>
                            </el-form-item>
                            <el-form-item prop="sourceDBId" label="数据源">
                                <el-tooltip content="数据源网速直接影响同步速度,推荐使用内网ip" placement="top">
                                    <el-icon style="left: -30px" class="tooltip-msg"><QuestionFilled /></el-icon>
                                </el-tooltip>
                                <el-select
                                    v-model="formData.sourceDBId"
                                    clearable
                                    filterable
                                    placeholder="请选择"
                                    @visible-change="getDataSource($event, formData.sourceDBType, 'source')"
                                    @change="dbIdChange('source')"
                                >
                                    <el-option v-for="item in sourceList" :key="item.value" :label="item.label"
                                        :value="item.value" />
                                </el-select>
                            </el-form-item>
                            <el-form-item prop="excludeSourceRule" label="补充规则">
                                <el-tooltip content="正则匹配：匹配不需要同步哪些表" placement="top">
                                    <el-icon style="left: -20px" class="tooltip-msg"><QuestionFilled /></el-icon>
                                </el-tooltip>
                                <el-input
                                    v-model="formData.excludeSourceRule"
                                    placeholder="请输入正则"
                                    clearable
                                    @blur="dbIdChange('source')"
                                ></el-input>
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
                                <el-select
                                    v-model="formData.targetDBType"
                                    clearable
                                    filterable
                                    placeholder="请选择"
                                    @change="dbTypeChange('target')"
                                >
                                    <el-option v-for="item in typeList" :key="item.value" :label="item.label"
                                        :value="item.value" />
                                </el-select>
                            </el-form-item>
                            <el-form-item prop="targetDBId" label="数据源">
                                <el-select
                                    v-model="formData.targetDBId"
                                    clearable
                                    filterable
                                    placeholder="请选择"
                                    @visible-change="getDataSource($event, formData.targetDBType, 'target')"
                                    @change="dbIdChange('target')"
                                >
                                    <el-option v-for="item in targetList" :key="item.value" :label="item.label"
                                        :value="item.value" />
                                </el-select>
                            </el-form-item>
                            <el-form-item prop="includeTargetRule" label="补充规则">
                                <el-tooltip content="正则匹配：匹配受保护的表" placement="top">
                                    <el-icon style="left: -20px" class="tooltip-msg"><QuestionFilled /></el-icon>
                                </el-tooltip>
                                <el-input
                                    v-model="formData.includeTargetRule"
                                    placeholder="请输入正则"
                                    clearable
                                    @blur="dbIdChange('target')"
                                ></el-input>
                            </el-form-item>
                        </el-form>
                    </el-card>
                </div>
                <table-list ref="tableListRef" :formData="formData"></table-list>
            </div>
        </LoadingPage>
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
        <!-- 配置 -->
        <config-detail ref="configDetailRef"></config-detail>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, defineProps, nextTick, markRaw } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import ConfigDetail from '../workflow-page/config-detail/index.vue'
import { GetWorkItemConfig, RunWorkItemConfig, SaveWorkItemConfig, TerWorkItemConfig } from '@/services/workflow.service'
import { DataSourceType } from './data.config.ts'
import { GetDatasourceList } from '@/services/datasource.service'
import TableList from './table-list/index.vue'

import PublishLog from '../work-item/publish-log.vue'
import RunningLog from '../work-item/running-log.vue'
import { Loading } from '@element-plus/icons-vue'
import LoadingPage from '@/components/loading/index.vue'

interface Option {
    label: string
    value: string
}

const props = defineProps<{
    workItemConfig: any
}>()

const emit = defineEmits(['back', 'locationNode'])

const changeStatus = ref<boolean>(false)
const configDetailRef = ref<any>(null)
const tableListRef = ref<any>(null)
const form = ref<FormInstance>()
const loading = ref<boolean>(false)
const networkError = ref<boolean>(false)
const typeList = ref(DataSourceType);

// 日志展示相关
const containerInstanceRef = ref<any>(null)
const activeName = ref<any>()
const currentTab = ref<any>()
const instanceId = ref<string>('')
const logCollapseRef = ref<any>()
const collapseActive = ref<string>('0')
const isCollapse = ref<boolean>(false)

const sourceList = ref<Option[]>([])
const targetList = ref<Option[]>([])
const rules = reactive<FormRules>({})
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
    workId: '',     // 作业id
    sourceDBType: '',     // 来源数据源类型
    sourceDBId: '',       // 来源数据源
    excludeSourceRule: '',

    targetDBType: '',     // 目标数据库类型
    targetDBId: '',       // 目标数据源
    includeTargetRule: ''
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


// 保存数据
function saveData() {
    btnLoadingConfig.saveLoading = true
    SaveWorkItemConfig({
        workId: formData.workId,
        dbMigrateConfig: {
            ...formData
        }
    }).then((res: any) => {
        changeStatus.value = false
        btnLoadingConfig.saveLoading = false
        getDate()
        ElMessage.success('保存成功')
    }).catch(err => {
        btnLoadingConfig.saveLoading = false
        console.error(err)
    })
}

function getDate() {
    loading.value = true
    networkError.value = networkError.value || false
    GetWorkItemConfig({
        workId: props.workItemConfig.id
    }).then((res: any) => {
        networkError.value = false

        if (res.data.dbMigrateConfig) {
            loading.value = false
            changeStatus.value = false
            formData.sourceDBType = res.data.dbMigrateConfig.sourceDBType
            formData.sourceDBId = res.data.dbMigrateConfig.sourceDBId
            formData.excludeSourceRule = res.data.dbMigrateConfig.excludeSourceRule
            formData.targetDBType = res.data.dbMigrateConfig.targetDBType
            formData.targetDBId = res.data.dbMigrateConfig.targetDBId
            formData.includeTargetRule = res.data.dbMigrateConfig.includeTargetRule

            dbIdChange('source')
            dbIdChange('target')
        } else {
            loading.value = false
        }
    }).catch(err => {
        loading.value = false
        networkError.value = false
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
            // initData(res.data.instanceId)
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

// 级联控制
function dbTypeChange(type: string) {
    changeStatus.value = true
    if (type === 'source') {
        formData.sourceDBId = ''
    } else {
        formData.targetDBId = ''
    }
}

// 级联控制
function dbIdChange(type: string) {
    changeStatus.value = true
    if (type === 'source') {
        // 数据来源获取表
        if (formData.sourceDBId) {
            tableListRef.value.setSourceTableList({
                dataSourceId: formData.sourceDBId,
                tablePattern: formData.excludeSourceRule
            })
        }
    } else {
        if (formData.targetDBId) {
            // 数据去向获取表
            tableListRef.value.setTargetTableList({
                dataSourceId: formData.targetDBId,
                tablePattern: formData.includeTargetRule
            })
        }
    }
}


// 获取数据源
function getDataSource(e: boolean, sourceType: string, type: string) {
    return new Promise((resolve, reject) => {
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
                resolve()
            }).catch(err => {
                console.error(err)
                type === 'source' ? sourceList.value = [] : targetList.value = []
                reject(err)
            })
        } else {
            type === 'source' ? sourceList.value = [] : targetList.value = []
            resolve()
        }
    })
}

onMounted(() => {
    formData.workId = props.workItemConfig.id
    getDate()
    activeName.value = 'PublishLog'
    currentTab.value = markRaw(PublishLog)
})
</script>