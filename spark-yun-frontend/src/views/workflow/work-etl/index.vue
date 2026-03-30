<template>
    <div class="work-etl">
        <LoadingPage :visible="loading">
            <OptionsContainer
                ref="optionsContainerRef"
                @optionsEvent="optionsEvent"
            ></OptionsContainer>
            <div class="work-etl-main">
                <TaskList @handleDragEnd="handleDragEnd"></TaskList>
                <ZEtlFlow
                    ref="zEtlFlowRef"
                    @refresh="initFlowData"
                    @nodeDropped="handleNodeDropped"
                ></ZEtlFlow>
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
        <AddTaskModal ref="addTaskModalRef"></AddTaskModal>
        <TaskConfig ref="taskConfigRef"></TaskConfig>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, defineEmits, defineProps, onMounted, nextTick, onUnmounted, markRaw } from 'vue'
import OptionsContainer from './options-container/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import TaskList from './task-list/index.vue'
import ZEtlFlow from './z-etl-flow/flow.vue'
import AddTaskModal from './add-task-modal/index.vue'
import TaskConfig from './task-config/index.vue'
import eventBus from '@/utils/eventBus'
import { GetWorkItemConfig, PublishWorkData, RunWorkItemConfig, SaveWorkItemConfig, TerWorkItemConfig } from '@/services/workflow.service'
import { TaskParams } from './task-params.ts'
import { cloneDeep } from 'lodash-es'
import PublishLog from '../work-item/publish-log.vue'
import RunningLog from '../work-item/running-log.vue'

const guid = function() {
    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }
    return (S4() + S4() + '-' + S4() + '-' + S4() + '-' + S4() + '-' +S4() + S4() +S4());
}

const emit = defineEmits(['back', 'locationNode'])
const props = defineProps<{
    workItemConfig: any
}>()

const changeStatus = ref<boolean>(false)
const optionsContainerRef = ref<any>()
const zEtlFlowRef = ref<any>()
const addTaskModalRef = ref<any>()
const taskConfigRef = ref<any>()
const loading = ref<boolean>(false)
const runningStatus = ref<boolean>(false)

const instanceId = ref('')
const containerInstanceRef = ref(null)
const logCollapseRef = ref()
const currentTab = ref()
const activeName = ref()
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

function optionsEvent(e: string) {
    if (e === 'goBack') {
        goBack()
    } else if (e === 'locationNode') {
        locationNode()
    } else if (e === 'saveData') {
        saveData()
    } else if (e === 'runWorkData') {
        runWorkData()
    } else if (e === 'terWorkData') {
        terWorkData()
    }
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
function pageChangeEvent() {
    changeStatus.value = true
}

// 配置流程图
// 拖动后松开鼠标触发事件
function handleDragEnd(e: any, item: any) {
    if (!runningStatus.value) {
        nextTick(() => {
            item.id = guid()
            const nodeId = String(Math.floor(Math.random() * 1000000)).padStart(6, '0')
            item.aliaCode = 'node_' + nodeId
            const newItem = {
                name: item.typeName + '_' + nodeId,
                inputEtl: null,
                ...item,
                ...cloneDeep(TaskParams[item.type]),
            }
            // 执行拖拽动画，节点落到画布上后由 nodeDropped 事件触发弹窗
            zEtlFlowRef.value.addNodeFn(newItem, e)
        })
    }
}

// 节点真正放到画布后弹出编辑弹窗
function handleNodeDropped(nodeData: any) {
    addTaskModalRef.value?.showModal((formResult: any) => {
        return new Promise((resolve: any) => {
            zEtlFlowRef.value.updateNodeFn({
                ...nodeData,
                name: formResult.name,
                aliaCode: formResult.aliaCode || nodeData.aliaCode,
                remark: formResult.remark || '',
            })
            resolve()
        })
    }, {
        name: nodeData.name,
        aliaCode: nodeData.aliaCode,
        remark: nodeData.remark || ''
    }, () => {
        // 取消时删除已放置的节点
        zEtlFlowRef.value.removeNodeById(nodeData.id)
    })
}

function initFlowData() {
    loading.value = true
    GetWorkItemConfig({
        workId: props.workItemConfig.id
    }).then((res: any) => {
        loading.value = false
        if (res.data && res.data.sparkEtlConfig && res.data.sparkEtlConfig.webConfig) {
            zEtlFlowRef.value.initCellList(res.data.sparkEtlConfig.webConfig)
        }
    }).catch(err => {
        loading.value = false
        networkError.value = false
        console.error(err)
    })
}

function saveData() {
    const data = zEtlFlowRef.value.getAllCellData()
    const allCellData = data.map((node: any) => {
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
    const requestParasm = {
        workId: props.workItemConfig.id,
        sparkEtlConfig: {
            webConfig: allCellData,
            nodeList: allCellData.filter((node: any) => node.shape === 'dag-node').map((node: any) => {
                const nodeConfigData = node.data.nodeConfigData
                const noInputEtlTypes = ['DATA_UNION', 'DATA_FILTER', 'DATA_TRANSFORM', 'DATA_CUSTOM', 'DATA_ADD_COL']
                if (noInputEtlTypes.includes(nodeConfigData.type)) {
                    const { inputEtl, ...rest } = nodeConfigData
                    return rest
                }
                return nodeConfigData
            }),
            nodeMapping: allCellData.filter((node: any) => node.shape === 'dag-edge').map((edge: any) => {
                return [edge.source.cell, edge.target.cell]
            })
        }
    }
    optionsContainerRef.value.btnLoadingConfig.saveLoading = true
    SaveWorkItemConfig(requestParasm).then((res: any) => {
        changeStatus.value = false
        ElMessage.success(res.msg)
        optionsContainerRef.value.closeLoading()
    }).catch(() => {
        optionsContainerRef.value.closeLoading()
    })
}

// 运行
function runWorkData() {
    optionsContainerRef.value.btnLoadingConfig.runningLoading = true
    RunWorkItemConfig({
      workId: props.workItemConfig.id
    }).then((res: any) => {
        optionsContainerRef.value.closeLoading()
        instanceId.value = res.data.instanceId
        ElMessage.success(res.msg)
        // 获取到 instanceId 后重新初始化日志组件
        // initFlowData()
        tabChangeEvent('PublishLog')
        nextTick(() => {
            changeCollapseUp()
            // 立即初始化日志组件为加载状态
            containerInstanceRef.value?.initData(instanceId.value)
        })
    }).catch(() => {
        optionsContainerRef.value.closeLoading()
    })
}

// 终止
function terWorkData() {
    if (!instanceId.value) {
        ElMessage.warning('暂无可中止的作业')
        return
    }
    terLoading.value = true
    TerWorkItemConfig({
        workId: props.workItemConfig.id,
        instanceId: instanceId.value
    }).then((res: any) => {
        optionsContainerRef.value.closeLoading()
        ElMessage.success(res.msg)
        initFlowData()
    }).catch(() => {
        optionsContainerRef.value.closeLoading()
    })
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
        containerInstanceRef.value?.initData(instanceId.value)
    })
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

onMounted(() => {
    activeName.value = 'PublishLog'
    currentTab.value = markRaw(PublishLog)
    initFlowData()
    eventBus.on('taskFlowEvent', (e: any) => {
        if (e.type === 'task_config') {
            nextTick(() => {
                // 点开编辑的时候，直接获取上级流转的参数
                const incomeNodes = zEtlFlowRef.value?.getIncomeNodes(e.data.nodeConfigData)
                if (incomeNodes) {
                    if (incomeNodes && incomeNodes.length || ['DATA_INPUT'].includes(e.data.nodeConfigData.type)) {
                        taskConfigRef.value?.showModal(incomeNodes, (formData: any) => {
                            return new Promise((resolve: any) => {
                                zEtlFlowRef.value?.updateNodeFn({
                                    ...e.data.nodeConfigData,
                                    ...formData
                                })
                                resolve()
                            })
                        }, e.data.nodeConfigData)
                    } else {
                        ElMessage.error('无前置任务，请先设置上游')
                    }
                }
            })
        } else if (e.type === 'task_edit') {
            nextTick(() => {
                const nodeData = e.data.nodeConfigData
                addTaskModalRef.value?.showModal((formResult: any) => {
                    return new Promise((resolve: any) => {
                        zEtlFlowRef.value?.updateNodeFn({
                            ...nodeData,
                            name: formResult.name,
                            aliaCode: formResult.aliaCode || nodeData.aliaCode,
                            remark: formResult.remark || '',
                        })
                        resolve()
                    })
                }, {
                    name: nodeData.name,
                    aliaCode: nodeData.aliaCode,
                    remark: nodeData.remark || ''
                })
            })
        }
    })
})
onUnmounted(() => {
    eventBus.off('taskFlowEvent')
})
</script>

<style lang="scss">
.work-etl {
    position: relative;
    .work-etl-main {
        // position: absolute;
        // top: 0;
        // left: 0;
        position: relative;
        height: calc(100vh - 56px);
        .z-etl-flow {
            position: absolute;
            left: 102px;
            top: 50px;
            right: 0;
        }
    }
    .data-sync-log__collapse {
        position: absolute;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: 100;

        .el-collapse-item__header {
            // padding-left: 20px;
            cursor: default;
        }
        .el-collapse-item__arrow {
            display: none;
        }
        .el-collapse-item__content {
            padding-bottom: 14px;
        }

        .log__collapse {
            position: absolute;
            right: 20px;
            cursor: pointer;
        }

        .el-tabs {
            width: 100%;
            // padding: 0 20px;
            height: 40px;
            box-sizing: border-box;
            .el-tabs__item {
                font-size: getCssVar('font-size', 'extra-small');
            }

            .el-tabs__nav-scroll {
                padding-left: 20px;
                box-sizing: border-box;
            }

            .el-tabs__content {
                height: 0;
            }

            .el-tabs__nav-scroll {
                border-bottom: 1px solid getCssVar('border-color');
            }
        }
        .log-show {
            padding: 0 20px;
            box-sizing: border-box;
            &.log-show-datasync {
                .zqy-download-log {
                    right: 40px;
                    top: 12px;
                }
            }

            pre {
                width: 100px;
            }

            .show-container {
                height: calc(100vh - 368px);
                overflow: auto;
            }

            .empty-page {
                height: 80%;
            }
        }
    }
}
</style>
