<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="workflow-page">
            <div class="work-list">
                <div class="option-container">
                    <div class="option-title">
                        <span class="option-title__href" @click="backToFlow">{{ workFlowData.name }}</span>
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
                    <el-button type="primary" @click="addData"><el-icon><Plus /></el-icon></el-button>
                </div>
                <div class="list-box">
                    <template v-for="work in workListItem" :key="work.id">
                        <div class="list-item" :class="{ 'choose-item': workConfig && workConfig.id === work.id }" :draggable="true" @click="showWorkConfig(work)" @dragstart="handleDragEnd($event, work)">{{ work.name }}
                            <el-dropdown trigger="click">
                                <el-icon class="option-more" @click.stop>
                                    <MoreFilled />
                                </el-icon>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item @click="editData(work)">编辑</el-dropdown-item>
                                        <el-dropdown-item v-if="containerType === 'flow'" @click="changeContianer(work, 'config')">作业配置</el-dropdown-item>
                                        <el-dropdown-item @click="deleteData(work)">删除</el-dropdown-item>
                                        <!-- <el-dropdown-item>复制</el-dropdown-item>
                                        <el-dropdown-item>导出</el-dropdown-item>
                                        <el-dropdown-item>置顶</el-dropdown-item> -->
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </div>
                    </template>
                </div>
            </div>
            <div class="flow-container">
                <template v-if="containerType === 'flow'">
                    <div class="option-btns">
                        <!-- 非运行状态 -->
                        <template v-if="!runningStatus || true">
                            <span v-if="!btnLoadingConfig.runningLoading" @click="runWorkFlowDataEvent">运行</span>
                            <el-icon v-else class="is-loading"><Loading /></el-icon>

                            <template v-if="workflowInstanceId || true">
                                <span v-if="!btnLoadingConfig.reRunLoading" @click="reRunWorkFlowDataEvent">重跑</span>
                                <el-icon v-else class="is-loading"><Loading /></el-icon>
                            </template>

                            <span v-if="!btnLoadingConfig.saveLoading || true" @click="saveData">保存</span>
                            <el-icon v-else class="is-loading"><Loading /></el-icon>

                            <!-- <span @click="showConfigDetail">配置</span> -->

                            <span v-if="!btnLoadingConfig.publishLoading || true" @click="publishWorkFlow">发布</span>
                            <el-icon v-else class="is-loading"><Loading /></el-icon>

                            <!-- <span>下线</span> -->
                        </template>
                        <!-- 运行状态 -->
                        <!-- <template v-else> -->
                            <span v-if="!btnLoadingConfig.stopWorkFlowLoading" @click="stopWorkFlow">中止</span>
                            <el-icon v-else class="is-loading"><Loading /></el-icon>
                        <!-- </template> -->
                        <!-- <span v-if="!btnLoadingConfig.exportLoading" @click="exportWorkFlow">导出</span>
                        <el-icon v-else class="is-loading"><Loading /></el-icon>
                        <span v-if="!btnLoadingConfig.importLoading" @click="importWorkFlow">导入</span>
                        <el-icon v-else class="is-loading"><Loading /></el-icon> -->
                        <!-- <span>收藏</span> -->
                        <span @click="queryRunWorkInstancesEvent">刷新</span>
                    </div>
                    <ZqyFlow ref="zqyFlowRef"></ZqyFlow>
                </template>
                <template v-else>
                    <WorkItem v-if="showWorkItem && workConfig.workType !== 'DATA_SYNC_JDBC'" @back="backToFlow" @locationNode="locationNode" :workItemConfig="workConfig" :workFlowData="workFlowData"></WorkItem>
                    <data-sync v-if="showWorkItem && workConfig.workType === 'DATA_SYNC_JDBC'"></data-sync>
                </template>
            </div>
        <AddModal ref="addModalRef" />
        <ConfigDetail ref="configDetailRef"></ConfigDetail>
        <zqyLog ref="zqyLogRef"></zqyLog>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import ZqyFlow from '@/lib/packages/zqy-flow/flow.vue'
import AddModal from './add-modal/index.vue'
import ConfigDetail from './config-detail/index.vue'
import eventBus from '@/utils/eventBus'
import zqyLog from '@/components/zqy-log/index.vue'
import WorkItem from '../work-item/index.vue'
import DataSync from '../data-sync/index.vue'

import { AddWorkflowDetailList, BreakFlowData, DeleteWorkflowDetailList, ExportWorkflowData, GetWorkflowData, GetWorkflowDetailList, GetWorkflowList, ImportWorkflowData, PublishWorkflowData, QueryRunWorkInstances, ReRunWorkflow, RerunCurrentNodeFlowData, RunAfterFlowData, RunWorkflowData, SaveWorkflowData, StopWorkflowData, UpdateWorkflowDetailList } from '@/services/workflow.service'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()

const searchParam = ref('')
const workListItem = ref([])
const zqyFlowRef = ref(null)
const workflowName = ref('')
const addModalRef = ref(null)
const configDetailRef = ref(null)
const zqyLogRef = ref(null)
const containerType = ref('flow')
const workConfig = ref()
const showWorkItem = ref(true)
const workFlowList = ref()
const workFlowData = ref({
    name: '',
    id: ''
})

const workflowInstanceId = ref('')
const timer = ref()
const runningStatus = ref(false)

const btnLoadingConfig = reactive({
    runningLoading: false,
    reRunLoading: false,
    saveLoading: false,
    publishLoading: false,
    stopWorkFlowLoading: false,
    importLoading: false,
    exportLoading: false
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
    tableConfig.tableData = []
    tableConfig.pagination.total = 0
    loading.value = false
    tableConfig.loading = false
    networkError.value = true
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
        ElMessage.success(res.msg)
        initData()
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
    addModalRef.value.showModal((formData: FormData) => {
        return new Promise((resolve: any, reject: any) => {
            AddWorkflowDetailList({
                ...formData,
                workflowId: workFlowData.value.id
            })
                .then((res: any) => {
                    ElMessage.success(res.msg)
                    initData()
                    resolve()
                })
                .catch((error: any) => {
                    reject(error)
                })
        })
    })
}

// 编辑作业
function editData(data: any) {
    addModalRef.value.showModal((formData: FormData) => {
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

function initFlowData() {
    return new Promise((resolve, reject) => {
        GetWorkflowData({
            workflowId: workFlowData.value.id
        }).then((res: any) => {
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
    configDetailRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            console.log(data)
            // AddWorkflowDetailList({
            //     ...formData,
            //     workflowId: workFlowData.value.id
            // })
            //     .then((res: any) => {
            //         ElMessage.success(res.msg)
            //         initData()
                    resolve()
            //     })
            //     .catch((error: any) => {
            //         reject(error)
            //     })
        })
    })
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
    // workflowName.value = route.query.name

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
                    color: getCssVar('color', 'primary');;
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
                margin-right: 8px;
                height: 32px;
                width: 36px;
            }
        }

        .list-box {
            // padding: 0 4px;
            box-sizing: border-box;
            overflow: auto;
            max-height: calc(100vh - 206px);

            .list-item {
                height: getCssVar('menu', 'item-height');
                line-height: getCssVar('menu', 'item-height');
                padding-left: 12px;
                padding-right: 12px;
                box-sizing: border-box;
                border-bottom: 1px solid getCssVar('border-color');
                cursor: pointer;
                font-size: getCssVar('font-size', 'extra-small');
                position: relative;

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
                    top: 15px;
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
            height: 50px;
            background-color: getCssVar('color', 'white');
            border-bottom: 1px solid getCssVar('border-color');
            display: flex;
            align-items: center;
            padding-left: 12px;
            box-sizing: border-box;
            font-size: getCssVar('font-size', 'extra-small');

            .el-icon {
                margin-right: 8px;
            }

            span {
                margin-right: 8px;
                color: getCssVar('color', 'primary', 'light-5');
                cursor: pointer;

                &:hover {
                    color: getCssVar('color', 'primary');;
                    text-decoration: underline;
                }
            }
        }
    }
}
</style>