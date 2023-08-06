<template>
    <div class="zqy-flow-node " :class="status">
        <div class="flow-node-container" ref="content">
            <p class="text">{{ name }}</p>
            <template v-if="isRunning">
                <el-icon v-if="status === 'RUNNING'" class="custom-icon is-loading"><Loading /></el-icon>
                <el-icon v-if="status === 'PENDING'" class="custom-icon"><Clock /></el-icon>
                <el-icon v-if="status === 'ABORT'" class="custom-icon"><VideoPause /></el-icon>
            </template>
            <el-dropdown v-if="showMenu" trigger="click" @command="handleCommand">
                <el-icon class="node-option-more">
                    <MoreFilled />
                </el-icon>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="node_log">日志</el-dropdown-item>
                        <el-dropdown-item command="node_runAfter">重跑下游</el-dropdown-item>
                        <el-dropdown-item command="node_break">中断</el-dropdown-item>
                        <el-dropdown-item command="node_reRun">重跑当前</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { inject, onMounted, ref } from 'vue'
import { ElIcon, ElDropdown, ElDropdownMenu, ElDropdownItem } from 'element-plus'
import { MoreFilled, Loading, Clock, VideoPause } from '@element-plus/icons-vue'
import { RunAfterFlowData } from '@/services/workflow.service';
import eventBus from '@/utils/eventBus'

const getGraph = inject('getGraph')
const getNode = inject('getNode')

const name = ref('')
const node = ref()
const status = ref('')
const isRunning = ref(false)
const showMenu = ref(false)

let Node

function handleCommand(command: string) {
    eventBus.emit('nodeMenuEvent', {
        data: node.value.data,
        type: command
    })
}

onMounted(() => {
    node.value = Node = getNode()
    name.value = node.value.data.name
    node.value.on('change:data', ({ current }) => {
        status.value = current.status
        isRunning.value = current.isRunning
        showMenu.value = !!current.workInstanceId
    })
})

</script>

<style lang="scss">
.zqy-flow-node {
    position: relative;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    width: 100%;
    height: 100%;
    background-color: #fff;
    border: 1px solid #c2c8d5;
    border-left: 4px solid #5F95FF;
    border-radius: 4px;
    box-shadow: 0 2px 5px 1px rgba(0, 0, 0, 0.06);

    .flow-node-container {
        display: flex;
        justify-content: space-between;
        width: 100%;
        padding-right: 8px;

        .node-option-more {
            font-size: 14px;
            transform: rotate(90deg);
            cursor: pointer;
            color: $--app-info-color;
        }
        .custom-icon {
            color: #9599a2;
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
    border-left: 4px solid #F1C40F;
}

.zqy-flow-node.FAIL {
    border-left: 4px solid #ff4d4f;
}

.zqy-flow-node.ABORT {
    border-left: 4px solid #839192;
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
    border-color: #839192;
    border-radius: 2px;
    box-shadow: 0 0 0 4px #cfdcdd;
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
    border-color: #F1C40F;
    border-radius: 2px;
    box-shadow: 0 0 0 4px #faf1cf;
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
