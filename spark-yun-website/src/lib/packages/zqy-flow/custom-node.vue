<template>
    <div class="zqy-flow-node " :class="status">
        <div class="flow-node-container" ref="content">
            <p class="text">{{ name }}</p>
            <el-dropdown trigger="click">
                <el-icon class="node-option-more">
                    <MoreFilled />
                </el-icon>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item>编辑</el-dropdown-item>
                        <el-dropdown-item>删除</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { inject, onMounted, ref } from 'vue'
import { ElIcon, ElDropdown, ElDropdownMenu, ElDropdownItem } from 'element-plus'
import { MoreFilled } from '@element-plus/icons-vue'

const getGraph = inject('getGraph')
const getNode = inject('getNode')

const name = ref('')
const node = ref()

let Node

onMounted(() => {
    node.value = Node = getNode()
    name.value = node.value.data.name
    // node.value.on('change:data', ({ current }) => {
    //     name.value = current.name
    // })
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

.zqy-flow-node.success {
    border-left: 4px solid #52c41a;
}

.zqy-flow-node.failed {
    border-left: 4px solid #ff4d4f;
}

.zqy-flow-node.running .status img {
    animation: spin 1s linear infinite;
}

.x6-node-selected .zqy-flow-node {
    border-color: #1890ff;
    border-radius: 2px;
    box-shadow: 0 0 0 4px #d4e8fe;
}

.x6-node-selected .node.success {
    border-color: #52c41a;
    border-radius: 2px;
    box-shadow: 0 0 0 4px #ccecc0;
}

.x6-node-selected .node.failed {
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
