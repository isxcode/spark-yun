<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="workflow-page">
            <div class="work-list">
                <div class="option-container">
                    <div class="option-title">
                        <span class="option-title__href" @click="backToFlow">
                            <EllipsisTooltip class="title-tooltip" :label="workFlowData.name" />
                        </span>
                    </div>
                    <el-dropdown trigger="click">
                        <el-icon class="change-workflow">
                            <Switch />
                        </el-icon>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item @click="changeWorkFlow(workFlow)" v-for="workFlow in workFlowList">{{ workFlow.name }}</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
                <div class="search-box">
                    <el-input v-model="searchParam" placeholder="回车搜索作业名称" @input="inputEvent"
                        @keyup.enter="initData"></el-input>
                    <el-button type="primary" circle @click="addData"><el-icon><Plus /></el-icon></el-button>
                </div>
                <el-scrollbar>
                    <div class="list-box">
                        <template v-if="workListItem.length" v-for="work in workListItem" :key="work.id">
                            <div class="list-item" :class="{ 'choose-item': workConfig && workConfig.id === work.id }" :draggable="true" @click="showWorkConfig(work)" @dragstart="handleDragEnd($event, work)">
                                <!-- {{ work.name }} -->
                                <!-- <div class="item-left">
                                    <el-icon v-if="work.workType === 'QUERY_JDBC'"><Search /></el-icon>
                                    <el-icon v-if="work.workType === 'DATA_SYNC_JDBC'"><Van /></el-icon>
                                </div> -->
                                <div class="item-right">
                                    <span class="label-type"><EllipsisTooltip class="label-name-text" :label="work.name" /></span>
                                    <!-- <span class="label-name">{{ work.name + work.name + work.name || '-' }}</span> -->
                                    <span class="label-name">{{ workTypeName(work.workType) }}</span>
                                </div>
                                <el-dropdown trigger="click">
                                    <el-icon class="option-more" @click.stop>
                                        <MoreFilled />
                                    </el-icon>
                                    <template #dropdown>
                                        <el-dropdown-menu>
                                            <el-dropdown-item @click="editData(work)">编辑</el-dropdown-item>
                                            <el-dropdown-item v-if="containerType === 'flow'" @click="changeContianer(work, 'config')">作业配置</el-dropdown-item>
                                            <el-dropdown-item @click="deleteData(work)">删除</el-dropdown-item>
                                            <el-dropdown-item @click="copyData(work)">复制</el-dropdown-item>
                                            <!-- <el-dropdown-item>导出</el-dropdown-item>
                                            <el-dropdown-item>置顶</el-dropdown-item> -->
                                        </el-dropdown-menu>
                                    </template>
                                </el-dropdown>
                            </div>
                        </template>
                        <empty-page v-else></empty-page>
                    </div>
                </el-scrollbar>
            </div>
            <div class="flow-container">
                <template v-if="containerType === 'flow'">
                    <div class="option-btns">
                        <!-- 非运行状态 -->
                        <div class="btn-box" @click="runWorkFlowDataEvent">
                            <el-icon v-if="!btnLoadingConfig.runningLoading">
                                <VideoPlay />
                            </el-icon>
                            <el-icon v-else class="is-loading">
                                <Loading />
                            </el-icon>
                            <span class="btn-text">运行</span>
                        </div>
                        <div class="btn-box" @click="stopWorkFlow">
                            <el-icon v-if="!btnLoadingConfig.stopWorkFlowLoading">
                                <Close />
                            </el-icon>
                            <el-icon v-else class="is-loading">
                                <Loading />
                            </el-icon>
                            <span class="btn-text">中止</span>
                        </div>
                        <div class="btn-box" @click="reRunWorkFlowDataEvent">
                            <el-icon v-if="!btnLoadingConfig.reRunLoading">
                                <RefreshLeft />
                            </el-icon>
                            <el-icon v-else class="is-loading">
                                <Loading />
                            </el-icon>
                            <span class="btn-text">重跑</span>
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
                        <div class="btn-box" @click="showConfigDetail">
                            <el-icon>
                                <Setting />
                            </el-icon>
                            <span class="btn-text">配置</span>
                        </div>
                        <div class="btn-box" @click="publishWorkFlow">
                            <el-icon v-if="!btnLoadingConfig.publishLoading">
                                <Promotion />
                            </el-icon>
                            <el-icon v-else class="is-loading">
                                <Loading />
                            </el-icon>
                            <span class="btn-text">发布</span>
                        </div>
                        <div class="btn-box" @click="underlineWorkFlow">
                            <el-icon v-if="!btnLoadingConfig.underlineLoading">
                                <Failed />
                            </el-icon>
                            <el-icon v-else class="is-loading">
                                <Loading />
                            </el-icon>
                            <span class="btn-text">下线</span>
                        </div>
                        <div class="btn-box" @click="queryRunWorkInstancesEvent" v-if="workflowInstanceId">
                            <el-icon>
                                <Refresh />
                            </el-icon>
                            <span class="btn-text">刷新</span>
                        </div>

                        <!-- <span v-if="!btnLoadingConfig.exportLoading" @click="exportWorkFlow">导出</span>
                        <el-icon v-else class="is-loading"><Loading /></el-icon>
                        <span v-if="!btnLoadingConfig.importLoading" @click="importWorkFlow">导入</span>
                        <el-icon v-else class="is-loading"><Loading /></el-icon> -->
                    </div>
                    <ZqyFlow ref="zqyFlowRef"></ZqyFlow>
                </template>
                <template v-else>
                    <spark-jar
                        v-if="showWorkItem && workConfig.workType === 'SPARK_JAR'"
                        :workItemConfig="workConfig"
                        :workFlowData="workFlowData"
                        @back="backToFlow"
                        @locationNode="locationNode"
                    ></spark-jar>
                    <WorkApi
                        v-if="showWorkItem && workConfig.workType === 'API'"
                        :workItemConfig="workConfig"
                        :workFlowData="workFlowData"
                        @back="backToFlow"
                        @locationNode="locationNode"
                    ></WorkApi>
                    <WorkItem
                        v-if="showWorkItem && workConfig.workType !== 'DATA_SYNC_JDBC' && workConfig.workType !== 'SPARK_JAR'"
                        :workItemConfig="workConfig"
                        :workFlowData="workFlowData"
                        @back="backToFlow"
                        @locationNode="locationNode"
                    ></WorkItem>
                    <data-sync
                        v-if="showWorkItem && workConfig.workType === 'DATA_SYNC_JDBC'"
                        :workItemConfig="workConfig"
                        @back="backToFlow"
                        @locationNode="locationNode"
                    ></data-sync>
                </template>
            </div>
        <AddModal ref="addModalRef" />
        <CopyModal ref="copyModalRef"></CopyModal>
        <workflow-config ref="workflowConfigRef"></workflow-config>
        <zqyLog ref="zqyLogRef"></zqyLog>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { useRoute } from 'vue-router'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import ZqyFlow from '@/lib/packages/zqy-flow/flow.vue'
import AddModal from './add-modal/index.vue'
import CopyModal from './copy-modal/index.vue'
import WorkflowConfig from './workflow-config/index.vue'
import eventBus from '@/utils/eventBus'
import zqyLog from '@/components/zqy-log/index.vue'
import WorkItem from '../work-item/index.vue'
import DataSync from '../data-sync/index.vue'
import WorkApi from '../work-api/index.vue'
import sparkJar from '../spark-jar/index.vue';
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import EllipsisTooltip from '@/components/ellipsis-tooltip/ellipsis-tooltip.vue'
import { AddWorkflowDetailList, BreakFlowData, DeleteWorkflowDetailList, ExportWorkflowData, GetWorkflowData, GetWorkflowDetailList, GetWorkflowList, ImportWorkflowData, PublishWorkflowData, QueryRunWorkInstances, ReRunWorkflow, RerunCurrentNodeFlowData, RunAfterFlowData, RunWorkflowData, SaveWorkflowConfigData, SaveWorkflowData, StopWorkflowData, UnderlineWorkflowData, UpdateWorkflowDetailList, CopyWorkflowDetailList } from '@/services/workflow.service'

const route = useRoute()

const searchParam = ref('')
const workListItem = ref([])
const zqyFlowRef = ref(null)
const addModalRef = ref(null)
const workflowConfigRef = ref(null)
const zqyLogRef = ref(null)
const containerType = ref('flow')
const workConfig = ref()
const showWorkItem = ref(true)
const workFlowList = ref()
const workFlowData = ref({
    name: '',
    id: ''
})
const cronConfig = ref()
const workflowInstanceId = ref('')
const timer = ref()
const runningStatus = ref(false)
const copyModalRef = ref()

const btnLoadingConfig = reactive({
    runningLoading: false,
    reRunLoading: false,
    saveLoading: false,
    publishLoading: false,
    stopWorkFlowLoading: false,
    importLoading: false,
    exportLoading: false,
    underlineLoading: false
})
const breadCrumbList = reactive([
    {
        name: '作业流',
        code: 'workflow'
    },
    {
        name: '作业',
        code: 'workflow-page'
    }
])
const typeList = reactive([
  {
    label: 'Jdbc执行作业',
    value: 'EXE_JDBC'
  },
  {
    label: 'Jdbc查询作业',
    value: 'QUERY_JDBC'
  },
  {
    label: 'Prql查询作业',
    value: 'PRQL'
  },
  {
    label: 'SparkSql查询作业',
    value: 'SPARK_SQL'
  },
  {
    label: 'SparkSql容器作业',
    value: 'SPARK_CONTAINER_SQL'
  },
  {
    label: '数据同步作业',
    value: 'DATA_SYNC_JDBC'
  },
  {
    label: 'bash作业',
    value: 'BASH'
  },
  {
    label: 'python作业',
    value: 'PYTHON'
  },
  {
    label: '自定义作业',
    value: 'SPARK_JAR'
  },
  {
    label: '接口调用作业',
    value: 'API'
  }
])

const workTypeName = computed(() => {
    return (code: string) => {
        return typeList.find(t => t.value === code)?.label
    }
})

function initData() {
    GetWorkflowDetailList({
        page: 0,
        pageSize: 99999,
        searchKeyWord: searchParam.value,
        workflowId: workFlowData.value.id
    }).then((res: any) => {
        workListItem.value = res.data.content
    }).catch(() => {
        workListItem.value = []
    })
}

function getWorkFlows() {
    GetWorkflowList({
        page: 0,
        pageSize: 100000,
        searchKeyWord: ''
    }).then((res: any) => {
        workFlowList.value = res.data.content
    }).catch(() => {
        workFlowList.value = []
    })
}

// 拖动后松开鼠标触发事件
function handleDragEnd(e: any, item: any) {
    if (!runningStatus.value) {
        if (containerType.value === 'config') {
            containerType.value = 'flow'
            workConfig.value = null
            initFlowData()
        }
        nextTick(() => {
            zqyFlowRef.value.addNodeFn(item, e)
        })
    }
}

// 保存数据
function saveData() {
    const data = zqyFlowRef.value.getAllCellData()
    btnLoadingConfig.saveLoading = true
    SaveWorkflowData({
        workflowId: workFlowData.value.id,
        webConfig: data.map((node: any) => {
            const item = node.store.data
            if (item.shape === 'dag-node') {
                return {
                    position: item.position,
                    shape: item.shape,
                    ports: item.ports,
                    id: item.id,
                    data: item.data,
                    zIndex: item.zIndex
                }
            } else {
                return item
            }
        })
    }).then((res: any) => {
        btnLoadingConfig.saveLoading = false
        ElMessage.success('保存成功')
    }).catch(() => {
        btnLoadingConfig.saveLoading = false
    })
}

// 删除
function deleteData(data: any) {
  ElMessageBox.confirm('确定删除该作业吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    DeleteWorkflowDetailList({
      workId: data.id
    })
      .then((res: any) => {
        initData()
        if (data.id === workConfig.value.id) {
            backToFlow()
        }
        ElMessage.success(res.msg)
      })
      .catch((error: any) => {
        console.error(error)
      })
  })
}

// 运行作业流
function runWorkFlowDataEvent() {
    btnLoadingConfig.runningLoading = true
    RunWorkflowData({
        workflowId: workFlowData.value.id
    }).then((res: any) => {
        workflowInstanceId.value = res.data
        ElMessage.success(res.msg)
        zqyFlowRef.value.hideGrid(true)
        // 判断是否开始运行
        runningStatus.value = true
        queryRunWorkInstancesEvent()
        if (!timer.value) {
            timer.value = setInterval(() => {
                queryRunWorkInstancesEvent()
            }, 2000)
        }
        btnLoadingConfig.runningLoading = false
    }).catch(() => {
        btnLoadingConfig.runningLoading = false
    })
}
// 重跑工作流
function reRunWorkFlowDataEvent() {
    btnLoadingConfig.reRunLoading = true
    ReRunWorkflow({
        workflowInstanceId: workflowInstanceId.value
    }).then((res: any) => {
        ElMessage.success(res.msg)
        zqyFlowRef.value.hideGrid(true)
        // 判断是否开始运行
        runningStatus.value = true
        queryRunWorkInstancesEvent()
        if (!timer.value) {
            timer.value = setInterval(() => {
                queryRunWorkInstancesEvent()
            }, 1000)
        }
        btnLoadingConfig.reRunLoading = false
    }).catch(() => {
        btnLoadingConfig.reRunLoading = false
    })
}

// 运行作业流后获取节点运行状态
function queryRunWorkInstancesEvent() {
    if (workflowInstanceId.value) {
        QueryRunWorkInstances({
            workflowInstanceId: workflowInstanceId.value
        }).then((res: any) => {
            const statusList = ['SUCCESS', 'FAIL', 'ABORT']
            if (statusList.includes(res.data.flowStatus)) {
                clearInterval(timer.value)
                timer.value = null
                zqyFlowRef.value.hideGrid(false)
                // 这里关闭运行状态
                runningStatus.value = false
            }
            zqyFlowRef.value.updateFlowStatus(res.data.workInstances, runningStatus.value)
        }).catch(() => {

        })
    } else {
        // ElMessage.warning('请先运行作业流')
    }
}


// 添加作业
function addData() {
    addModalRef.value.showModal((formData: any) => {
        return new Promise((resolve: any, reject: any) => {
            AddWorkflowDetailList({
                ...formData,
                workflowId: workFlowData.value.id
            })
                .then((res: any) => {
                    ElMessage.success(res.msg)
                    initData()
                    resolve()

                    showWorkConfig({
                        id: res.data.workId,
                        name: res.data.name,
                        workType: formData.workType
                    })
                })
                .catch((error: any) => {
                    reject(error)
                })
        })
    })
}

// 编辑作业
function editData(data: any) {
    addModalRef.value.showModal((formData: any) => {
        return new Promise((resolve: any, reject: any) => {
            UpdateWorkflowDetailList(formData)
                .then((res: any) => {
                    ElMessage.success(res.msg)
                    initData()
                    resolve()
                })
                .catch((error: any) => {
                    reject(error)
                })
        })
    }, data)
}

function copyData(data: any) {
    copyModalRef.value.showModal((formData: any) => {
        return new Promise((resolve: any, reject: any) => {
            CopyWorkflowDetailList(formData).then((res: any) => {
                ElMessage.success(res.msg)
                initData()
                resolve()
            })
            .catch((error: any) => {
                reject(error)
            })
        })
    }, data)
}

function initFlowData() {
    return new Promise((resolve, reject) => {
        GetWorkflowData({
            workflowId: workFlowData.value.id
        }).then((res: any) => {
            cronConfig.value = res.data?.cronConfig
            if (res.data?.webConfig) {
                zqyFlowRef.value.initCellList(res.data.webConfig)
            }
            resolve()
        }).catch(() => {
            reject()
        })
    })
}

// 发布作业流
function publishWorkFlow() {
    btnLoadingConfig.publishLoading = true
    PublishWorkflowData({
        workflowId: workFlowData.value.id
    }).then((res: any) => {
        btnLoadingConfig.publishLoading = false
        ElMessage.success(res.msg)
    }).catch(() => {
        btnLoadingConfig.publishLoading = false
    })
}
// 下线工作流
function underlineWorkFlow() {
    btnLoadingConfig.underlineLoading = true
    UnderlineWorkflowData({
        workflowId: workFlowData.value.id
    }).then((res: any) => {
        btnLoadingConfig.underlineLoading = false
        ElMessage.success(res.msg)
        queryRunWorkInstancesEvent()
    }).catch(() => {
        btnLoadingConfig.underlineLoading = false
    })
}

// 中止工作流
function stopWorkFlow() {
    btnLoadingConfig.stopWorkFlowLoading = true
    StopWorkflowData({
        workflowInstanceId: workflowInstanceId.value
    }).then((res: any) => {
        btnLoadingConfig.stopWorkFlowLoading = false
        ElMessage.success(res.msg)
        queryRunWorkInstancesEvent()
    }).catch(() => {
        btnLoadingConfig.stopWorkFlowLoading = false
    })
}

// 导入工作流
function importWorkFlow() {
    btnLoadingConfig.importLoading = true
    ImportWorkflowData({
        workflowId: workFlowData.value.id
    }).then((res: any) => {
        btnLoadingConfig.importLoading = false
        ElMessage.success(res.msg)
    }).catch(() => {
        btnLoadingConfig.importLoading = false
    })
}

// 导出工作流
function exportWorkFlow() {
    btnLoadingConfig.exportLoading = true
    ExportWorkflowData({
        workflowId: workFlowData.value.id
    }).then((res: any) => {
        btnLoadingConfig.exportLoading = false
        ElMessage.success(res.msg)
    }).catch(() => {
        btnLoadingConfig.exportLoading = false
    })
}

function inputEvent(e: string) {
    if (e === '') {
        initData()
    }
}

// 配置设置
function showConfigDetail() {
    workflowConfigRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            SaveWorkflowConfigData({
                workflowId: workFlowData.value.id,
                cronConfig: data
            }).then((res: any) => {
                initFlowData()
                ElMessage.success(res.msg)
                resolve()
            }).catch((err) => {
                reject(err)
            })
        })
    }, cronConfig.value)
}

// 节点运行日志
function nodeRunningLog(e: any, type: string) {
    zqyLogRef.value.showModal(() => {
      console.log('关闭')
    }, { id: e.data.workInstanceId, type: type }
  )
}

// 节点重跑下游
function nodeRunAfterFlow(e: any) {
    RunAfterFlowData({
        workInstanceId: e.data.workInstanceId
    }).then((res: any) => {
        ElMessage.success(res.msg)
        zqyFlowRef.value.hideGrid(true)
        // 判断是否开始运行
        runningStatus.value = true
        queryRunWorkInstancesEvent()
        if (!timer.value) {
            timer.value = setInterval(() => {
                queryRunWorkInstancesEvent()
            }, 1000)
        }
    }).catch(() => {
    })
}

// 节点中断
function nodeBreakFlow(e: any) {
    BreakFlowData({
        workInstanceId: e.data.workInstanceId
    }).then((res: any) => {
        ElMessage.success(res.msg)
        queryRunWorkInstancesEvent()
    }).catch(() => {})
}

// 重跑当前节点
function reRunCurrentNodeFlow(e: any) {
    RerunCurrentNodeFlowData({
        workInstanceId: e.data.workInstanceId
    }).then((res: any) => {
        ElMessage.success(res.msg)
        zqyFlowRef.value.hideGrid(true)
        // 判断是否开始运行
        runningStatus.value = true
        queryRunWorkInstancesEvent()
        if (!timer.value) {
            timer.value = setInterval(() => {
                queryRunWorkInstancesEvent()
            }, 1000)
        }
    }).catch(() => {})
}

// 切换拖拽以及作业配置页面
function changeContianer(data: any, type: string) {
    workConfig.value = data
    containerType.value = type
}

function showWorkConfig(data: any) {
    workConfig.value = data
    containerType.value = 'config'
    showWorkItem.value = false
    nextTick(() => {
        showWorkItem.value = true
    })
}

function backToFlow() {
    containerType.value = 'flow'
    initFlowData()
}

// 定位选中节点
function locationNode(nodeId: string) {
    containerType.value = 'flow'
    initFlowData().then(() => {
        queryRunWorkInstancesEvent()
        zqyFlowRef.value.selectNodeEvent(nodeId)
    })
}

function changeWorkFlow(workFlow: any) {
    containerType.value = 'flow'
    workConfig.value = null
    workFlowData.value = {
        name: workFlow.name,
        id: workFlow.id
    }
    initData()
    initFlowData()
}

onMounted(() => {
    workFlowData.value = {
        name: route.query.name,
        id: route.query.id
    }
    initData()
    initFlowData()
    getWorkFlows()

    eventBus.on('nodeMenuEvent', (e: any) => {
        console.log('eeee', e)
        if (e.type === 'node_log') {
            // 日志
            nodeRunningLog(e, 'log')
        } else if (e.type === 'node_runAfter') {
            // 重跑下游
            nodeRunAfterFlow(e)
        } else if (e.type === 'node_result') {
            // 查看结果
            nodeRunningLog(e, 'result')
        } else if (e.type === 'node_yarnLog') {
            // 运行日志
            nodeRunningLog(e, 'yarnLog')
        } else if (e.type === 'node_break') {
            // 中断
            nodeBreakFlow(e)
        } else if (e.type === 'node_reRun') {
            // 重跑当前
            reRunCurrentNodeFlow(e)
        } else if (e.type === 'dbclick') {
            // 双击节点跳转到作业配置
            showWorkConfig(e.data.nodeConfigData)
        }
    })
})

onUnmounted(() => {
    clearInterval(timer.value)
    timer.value = null
    eventBus.off('nodeMenuEvent')
})
</script>

<style lang="scss">
.workflow-page {
    height: calc(100% - 56px);
    display: flex;
    position: relative;
    overflow: hidden;

    .work-list {
        min-width: 200px;
        width: 200px;
        max-width: 200px;
        height: 100%;
        border-right: 1px solid getCssVar('border-color');
        border-left: 1px solid getCssVar('border-color');
        background-color: getCssVar('color', 'white');

        .option-container {
            height: 50px;
            width: 201px;
            background-color: getCssVar('color', 'white');
            border-bottom: 1px solid getCssVar('border-color');
            display: flex;
            position: relative;

            .option-title {
                height: 100%;
                width: 201px;
                display: flex;
                align-items: center;
                font-size: getCssVar('font-size', 'base');
                color: getCssVar('text-color', 'primary');
                padding-left: 12px;
                border-right: 1px solid getCssVar('border-color');
                box-sizing: border-box;
                .option-title__href {
                    cursor: pointer;
                    .title-tooltip {
                        max-width: 150px;
                    }
                    &:hover {
                        color: getCssVar('color', 'primary');;
                        text-decoration: underline;
                    }
                }
            }

            .change-workflow {
                position: absolute;
                right: 12px;
                top: 16px;
                cursor: pointer;
                &:hover {
                    color: getCssVar('color', 'primary');
                }
            }
        }

        .search-box {
            padding: 4px 0;
            box-sizing: border-box;
            display: flex;
            justify-content: space-between;
            align-items: center;
            width: 100%;
            border-bottom: 1px solid getCssVar('border-color');

            .el-input {
                margin-left: 8px;
                width: 180px;
            }

            .el-button {
                margin-left: 4px;
                margin-right: 4px;
                // height: 32px;
                width: 28px;
            }
        }
        .el-scrollbar {
            width: 200px;
            max-height: calc(100vh - 148px);
            .el-scrollbar__view {
                height: 100%;
                .list-box {
                    // padding: 0 4px;
                    box-sizing: border-box;
                    position: relative;
                    height: 100%;

                    .list-item {
                        height: 52px;
                        // height: getCssVar('menu', 'item-height');
                        // line-height: getCssVar('menu', 'item-height');
                        // padding-left: 12px;
                        padding-right: 12px;
                        box-sizing: border-box;
                        border-bottom: 1px solid getCssVar('border-color');
                        cursor: pointer;
                        font-size: getCssVar('font-size', 'extra-small');
                        position: relative;
                        display: flex;
                        align-items: center;

                        // .item-left {
                        //     font-size: 16px;
                        //     margin-left: 12px;
                        // }

                        .item-right {
                            margin-left: 8px;
                            display: flex;
                            flex-direction: column;
                            justify-content: space-between;
                            padding: 2px 0;
                            box-sizing: border-box;
                            font-size: 12px;
                            height: 80%;
                            .label-type {
                                color: getCssVar('color', 'primary');
                                .label-name-text {
                                    max-width: 170px;
                                }
                            }
                            .label-name {
                                color: getCssVar('color', 'info');
                                .label-name-text {
                                    max-width: 170px;
                                }
                            }
                        }

                        &.choose-item {
                            background-color: getCssVar('color', 'primary', 'light-8');
                        }

                        &:hover {
                            background-color: getCssVar('color', 'primary', 'light-8');

                            .el-dropdown {
                                display: block;
                            }
                        }

                        .el-dropdown {
                            position: absolute;
                            right: 8px;
                            top: 20px;
                            // display: none;

                            .option-more {
                                font-size: 14px;
                                transform: rotate(90deg);
                                cursor: pointer;
                                color: getCssVar('color', 'info');
                            }
                        }
                    }
                }
            }
        }


    }

    .workflow-btn-container {
        position: absolute;
        right: 20px;
        top: -55px;
        height: 55px;
        display: flex;
        align-items: center;
        z-index: 10;
    }

    .flow-container {
        width: 100%;

        .option-btns {
            height: 51px;
            background-color: getCssVar('color', 'white');
            border-bottom: 1px solid getCssVar('border-color');
            display: flex;
            align-items: center;
            padding-left: 20px;
            box-sizing: border-box;
            font-size: getCssVar('font-size', 'extra-small');
            color: getCssVar('color', 'primary', 'light-5');

            .btn-box {
                font-size: getCssVar('font-size', 'extra-small');
                display: flex;
                cursor: pointer;
                width: 48px;
                margin-right: 8px;

                &.btn-box__4 {
                    width: 70px;
                }

                .btn-text {
                    margin-left: 4px;
                }

                &:hover {
                    color: getCssVar('color', 'primary');;
                }
            }
        }
    }
}
</style>