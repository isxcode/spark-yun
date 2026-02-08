<template>
    <div class="work-etl">
        <OptionsContainer
            ref="optionsContainerRef"
            @optionsEvent="optionsEvent"
        ></OptionsContainer>
        <div class="work-etl-main">
            <TaskList @handleDragEnd="handleDragEnd"></TaskList>
            <ZEtlFlow
                ref="zEtlFlowRef"
                @refresh="initFlowData"
            ></ZEtlFlow>
        </div>
        <AddTaskModal ref="addTaskModalRef"></AddTaskModal>
        <TaskConfig ref="taskConfigRef"></TaskConfig>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineEmits, defineProps, onMounted, nextTick, onUnmounted } from 'vue'
import OptionsContainer from './options-container/index.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import TaskList from './task-list/index.vue'
import ZEtlFlow from './z-etl-flow/flow.vue'
import AddTaskModal from './add-task-modal/index.vue'
import TaskConfig from './task-config/index.vue'
import eventBus from '@/utils/eventBus'
import { GetWorkItemConfig, PublishWorkData, RunWorkItemConfig, SaveWorkItemConfig, TerWorkItemConfig } from '@/services/workflow.service'

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

const runningStatus = ref<boolean>(false)

function optionsEvent(e: string) {
    if (e === 'goBack') {
        goBack()
    } else if (e === 'locationNode') {
        locationNode()
    } else if (e === 'saveData') {
        saveData()
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
            // 数据输入
            if (item.type === 'DATA_INPUT') {
                item.inputEtl = {
                    datasourceId: '',
                    dbType: '',
                    tableName: '',
                    numPartitions: null,
                    partitionColumn: ''
                }
                item.outColumnList = []
            }
            // 数据输出
            if (item.type === 'DATA_OUTPUT') {
                item.outputEtl = {
                    datasourceId: '',
                    dbType: '',
                    tableName: '',
                    writeMode: '',
                    partitionColumn: ''
                }
                item.fromColumnList = []
                item.toColumnList = []
                item.colMapping = []
            }
            // 数据转换
            if (item.type === 'DATA_TRANSFORM') {
                item.transformEtl = [{
                    colName: '',
                    transformWay: 'FUNCTION_TRANSFORM',
                    transformFunc: '',
                    transformSql: '',
                    inputValue: []
                }]
                item.outColumnList = []
            }
            zEtlFlowRef.value.addNodeFn(item, e)
        })
    }
}

function initFlowData() {
    GetWorkItemConfig({
        workId: props.workItemConfig.id
    }).then((res: any) => {
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
                return node.data.nodeConfigData
            }),
            nodeMapping: allCellData.filter((node: any) => node.shape === 'dag-edge').map((edge: any) => {
                return [edge.source.cell, edge.target.cell]
            })
        }
    }
    optionsContainerRef.value.saveLoading = true
    SaveWorkItemConfig(requestParasm).then((res: any) => {
        changeStatus.value = false
        ElMessage.success(res.msg)
        optionsContainerRef.value.closeLoading()
    }).catch(() => {
        optionsContainerRef.value.closeLoading()
    })
}

onMounted(() => {
    initFlowData()
    eventBus.on('taskFlowEvent', (e: any) => {
        if (e.type === 'task_edit') {
            addTaskModalRef.value.showModal((formData) => {
                return new Promise((resolve: any, reject: any) => {
                    zEtlFlowRef.value?.updateNodeFn({
                        ...e.data.nodeConfigData,
                        ...formData
                    })
                    resolve()
                })
            }, {
                name: e.data.name,
                remark: e.data.nodeConfigData.remark,
                aliaCode: e.data.nodeConfigData.aliaCode,
                id: e.data.nodeConfigData.id
            })
        } else if (e.type === 'task_config') {
            nextTick(() => {
                const incomeNodes = zEtlFlowRef.value?.getIncomeNodes(e.data.nodeConfigData)
                taskConfigRef.value?.showModal(incomeNodes, (formData: any) => {
                    return new Promise((resolve: any) => {
                        zEtlFlowRef.value?.updateNodeFn({
                            ...e.data.nodeConfigData,
                            ...formData
                        })
                        resolve()
                    })
                }, e.data.nodeConfigData)
            })
        }
    })
})
onUnmounted(() => {
    eventBus.off('nodeMenuEvent')
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
}
</style>
