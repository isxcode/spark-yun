<template>
    <div class="etl-flow-node" :class="status" @mousedown="onMouseDown" @click="onClick">
        <div class="flow-node-container" ref="content">
            <el-tooltip
                placement="top"
                :show-after="300"
            >
                <template #content>
                    <div>名称：{{ nodeConfigData?.name || '-' }}</div>
                    <div>编码：{{ nodeConfigData?.aliaCode || '-' }}</div>
                    <div>备注：{{ nodeConfigData?.remark || '暂无备注' }}</div>
                </template>
                <div class="info-container">
                    <el-icon class="node-icon"><component :is="nodeIcon" /></el-icon>
                    <span class="node-name">{{ nodeConfigData?.name || '-' }}</span>
                </div>
            </el-tooltip>
            <el-icon class="node-edit-icon" @click.stop="handleCommand('task_edit')">
                <component :is="EditIcon" />
            </el-icon>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { inject, onMounted, ref, computed, markRaw, type Component } from 'vue'
import { ElIcon } from 'element-plus'
import { Download, Upload, Link, CopyDocument, Filter, Switch, CirclePlus, SetUp, Document, Edit } from '@element-plus/icons-vue'

const EditIcon = markRaw(Edit)
import { RunAfterFlowData } from '@/services/workflow.service';
import eventBus from '@/utils/eventBus'

const getGraph = inject('getGraph')
const getNode = inject('getNode')

const name = ref('')
const node = ref()
const status = ref('')
const isRunning = ref(false)
const showMenu = ref(false)
const nodeConfigData = ref({})

const nodeIconMap: Record<string, Component> = {
    DATA_INPUT: markRaw(Download),
    DATA_OUTPUT: markRaw(Upload),
    DATA_JOIN: markRaw(Link),
    DATA_UNION: markRaw(CopyDocument),
    DATA_FILTER: markRaw(Filter),
    DATA_TRANSFORM: markRaw(Switch),
    DATA_ADD_COL: markRaw(CirclePlus),
    DATA_CUSTOM: markRaw(SetUp),
}

const nodeIcon = computed(() => {
    return nodeIconMap[nodeConfigData.value?.type] || markRaw(Document)
})

let Node
const mouseDownPos = ref({ x: 0, y: 0 })

function onMouseDown(e: MouseEvent) {
    mouseDownPos.value = { x: e.clientX, y: e.clientY }
}

function onClick(e: MouseEvent) {
    const dx = Math.abs(e.clientX - mouseDownPos.value.x)
    const dy = Math.abs(e.clientY - mouseDownPos.value.y)
    // 移动距离小于5px视为点击，否则视为拖拽
    if (dx < 5 && dy < 5) {
        handleCommand('task_config')
    }
}

function handleCommand(command: string) {
    eventBus.emit('taskFlowEvent', {
        data: node.value.data,
        type: command
    })
}

onMounted(() => {
    node.value = Node = getNode()
    name.value = node.value.data.name
    nodeConfigData.value = node.value.data.nodeConfigData
    node.value.on('change:data', ({ current }) => {
        nodeConfigData.value = current.nodeConfigData
    })
})

</script>

<style lang="scss">
.etl-flow-node {
    position: relative;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    width: 100%;
    height: 100%;
    background-color: #fff;
    border: 1px solid #c2c8d5;
    border-left: 4px solid getCssVar('color', 'primary');
    border-radius: 4px;
    box-shadow: 0 2px 5px 1px rgba(0, 0, 0, 0.06);
    cursor: pointer;

    .flow-node-container {
        display: flex;
        justify-content: space-between;
        width: 100%;
        padding-right: 8px;
        height: 100%;
        align-items: center;

        .info-container {
            display: flex;
            align-items: center;
            gap: 6px;
            height: 100%;
            padding: 0 8px;
            box-sizing: border-box;
            overflow: hidden;

            .node-icon {
                font-size: 16px;
                flex-shrink: 0;
                color: getCssVar('color', 'primary');
            }

            .node-name {
                font-size: 12px;
                max-width: 110px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
            }
        }

        .node-edit-icon {
            font-size: 12px;
            flex-shrink: 0;
            color: getCssVar('color', 'info');
            cursor: pointer;

            &:hover {
                color: getCssVar('color', 'primary');
            }
        }
    }
}
p {
    margin: 0;
    font-size: 12px;
}

.text {
    width: 100%;
    word-break: break-all;
    white-space: pre-wrap;
    padding-left: 8px;
}

.el-icon-add {
    position: absolute;
    bottom: -10px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 100;
    display: inline-flex;
    justify-content: center;
    align-items: center;
    font-size: 20px;
    width: 20px;
    height: 20px;
    background-color: #5F95FF;
    border-radius: 50%;
    cursor: pointer;
    color: #ffffff;
}

.zqy-flow-node img {
    width: 20px;
    height: 20px;
    flex-shrink: 0;
    margin-left: 8px;
}

.zqy-flow-node .label {
    display: inline-block;
    flex-shrink: 0;
    width: 104px;
    margin-left: 8px;
    color: #666;
    font-size: 12px;
}

.zqy-flow-node .status {
    flex-shrink: 0;
}

.zqy-flow-node.SUCCESS {
    border-left: 4px solid #52c41a;
}
.zqy-flow-node.PENDING {
    border-left: 4px solid #F5B041;
}
.zqy-flow-node.BREAK {
    border-left: 4px solid #3f3a24;
}

.zqy-flow-node.FAIL {
    border-left: 4px solid #ff4d4f;
}

.zqy-flow-node.ABORT {
    border-left: 4px solid #9f26e1;
}

.zqy-flow-node.ABORTING {
    border-left: 4px solid #b2b2b2;
}

.zqy-flow-node.RUNNING .status img {
    animation: spin 1s linear infinite;
}

.x6-node-selected .zqy-flow-node {
    border-color: #1890ff;
    border-radius: 2px;
    box-shadow: 0 0 0 4px #d4e8fe;
}

.x6-node-selected .zqy-flow-node.SUCCESS {
    border-color: #52c41a;
    border-radius: 2px;
    box-shadow: 0 0 0 4px #ccecc0;
}

.x6-node-selected .zqy-flow-node.ABORT {
    border-color: #9f26e1;
    border-radius: 2px;
    box-shadow: 0 0 0 4px #edcefe;
}

.x6-node-selected .zqy-flow-node.PENDING {
    border-color: #F5B041;
    border-radius: 2px;
    box-shadow: 0 0 0 4px #ffe1b4;
}

.x6-node-selected .zqy-flow-node.ABORTING {
    border-color: #b2b2b2;
    border-radius: 2px;
    box-shadow: 0 0 0 4px #f0e9e9;
}
.x6-node-selected .zqy-flow-node.BREAK {
    border-color: #3f3a24;
    border-radius: 2px;
    box-shadow: 0 0 0 4px #abaaa4;
}

.x6-node-selected .zqy-flow-node.FAIL {
    border-color: #ff4d4f;
    border-radius: 2px;
    box-shadow: 0 0 0 4px #fedcdc;
}

.x6-edge:hover path:nth-child(2) {
    stroke: #1890ff;
    stroke-width: 1px;
}

.x6-edge-selected path:nth-child(2) {
    stroke: #1890ff;
    stroke-width: 1.5px !important;
}

@keyframes running-line {
    to {
        stroke-dashoffset: -1000;
    }
}

@keyframes spin {
    from {
        transform: rotate(0deg);
    }

    to {
        transform: rotate(360deg);
    }
}
</style>
