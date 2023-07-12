<template>
    <div class="zqy-flow-node " :class="status">
        <div ref="content">
            <p class="text">{{ name }}</p>
            <!-- <span class="el-icon-add" @click="iconClick">+</span> -->
        </div>
    </div>
</template>
  
<script>

let Node = ''

import { $Bus } from '@/plugins/global.js'

export default {
    name: 'database',

    inject: ['getGraph', 'getNode'],
    data() {
        return {
            status: 'logo',
            name: '',
            node: '',
            text: 11
        }
    },
    methods: {
        iconClick() {
            // $Bus.$Bus.emit('nodeOpenConfig', this.node)
        }
    },
    mounted() {
        const self = this
        this.node = Node = this.getNode()

        this.name = Node.data.name
        // 监听数据改变事件
        this.node.on('change:data', ({ current }) => {
            self.name = current.name
            self.status = current.status
        })


    }
}
</script>
<style>

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
}
p {
    margin: 0;
    font-size: 12px;
}

.text {
    width: 100%;
    word-break: break-all;
    white-space: pre-wrap;
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

.node img {
    width: 20px;
    height: 20px;
    flex-shrink: 0;
    margin-left: 8px;
}

.node .label {
    display: inline-block;
    flex-shrink: 0;
    width: 104px;
    margin-left: 8px;
    color: #666;
    font-size: 12px;
}

.node .status {
    flex-shrink: 0;
}

.node.success {
    border-left: 4px solid #52c41a;
}

.node.failed {
    border-left: 4px solid #ff4d4f;
}

.node.running .status img {
    animation: spin 1s linear infinite;
}

.x6-node-selected .node {
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
  