<template>
    <div class="zqy-flow">
        <section class="section-cot">
            <div id="container">
                <div id="draw-cot" />
            </div>
            <div class="btn-container">
                <el-icon @click="zoomOut"><ZoomOut /></el-icon>
                <el-icon @click="zoomIn"><ZoomIn /></el-icon>
                <el-icon @click="locationCenter"><MapLocation /></el-icon>
            </div>
        </section>
    </div>
</template>
  
<script setup lang='ts'>
import { nextTick, onMounted, createVNode, ref } from 'vue'
import { Graph, Path, Addon } from '@antv/x6'
import CustomNode from './custom-node.vue'

let _Graph: any
let dnd: any
let container: HTMLElement | undefined;

const runningStatus = ref(false)

function initGraph() {
    Graph.registerNode(
        'dag-node',
        {
            inherit: 'vue-shape',
            width: 180,
            height: 36,
            component: {
                render: ()=>{
                   return createVNode(CustomNode);
                }
            },
            ports: {
                groups: {
                    top: {
                        position: 'top',
                        attrs:
                        {
                            circle: {
                                r: 4,
                                magnet: true,
                                stroke:
                                    '#C2C8D5',
                                strokeWidth: 1,
                                fill: '#fff'
                            }
                        }
                    },
                    bottom: {
                        position: 'bottom',
                        attrs: {
                            circle: {
                                r: 4,
                                magnet:
                                    true,
                                stroke: '#C2C8D5',
                                strokeWidth: 1,
                                fill: '#fff'
                            }
                        }
                    }
                }
            }
        },
        true
    )
    Graph.registerEdge(
        'dag-edge',
        {
            inherit: 'edge',
            attrs: {
                line: {
                    stroke: '#c2c8d5',
                    strokeWidth: 1,
                    targetMarker: {
                        name: 'block',
                        width: 6,
                        height: 4
                    }
                }
            }
        },
        true
    )
    Graph.registerConnector(
        'algo-connector',
        (s, e) => {
            const offset = 4
            const deltaY = Math.abs(e.y - s.y)
            const control = Math.floor((deltaY / 3) * 2)

            const v1 = { x: s.x, y: s.y + offset + control }
            const v2 = { x: e.x, y: e.y - offset - control }

            return Path.normalize(
                `M ${s.x} ${s.y}
             L ${s.x} ${s.y + offset}
             C ${v1.x} ${v1.y} ${v2.x} ${v2.y} ${e.x} ${e.y - offset}
             L ${e.x} ${e.y}
            `
            )
        },
        true
    )
    _Graph = new Graph({
        grid: {
            size: 10,
            visible: true,
            type: 'dot', // 'dot' | 'fixedDot' | 'mesh'
            args: {
                color: '#a05410', // 网格线/点颜色
                thickness: 1 // 网格线宽度/网格点大小
            }
        },
        background: {
            color: '#fff' // 设置画布背景颜色
        },
        container: container,
        panning: {
            enabled: true,
            eventTypes: ['leftMouseDown', 'mouseWheel']
        },
        mousewheel: {
            enabled: true,
            modifiers: 'ctrl',
            factor: 1.1,
            maxScale: 1.5,
            minScale: 0.5
        },
        highlighting: {
            magnetAdsorbed: {
                name: 'stroke',
                args: {
                    attrs: {
                        fill: '#fff',
                        stroke: '#31d0c6',
                        strokeWidth: 4
                    }
                }
            }
        },
        connecting: {
            snap: true,
            allowBlank: false,
            allowLoop: false,
            highlight: true,
            allowMulti: true,
            connector: 'algo-connector',
            connectionPoint: 'anchor',
            sourceAnchor: 'bottom',
            targetAnchor: 'top',
            allowNode: false,
            allowEdge: false,
            validateMagnet({ magnet }) {
                return magnet.getAttribute('port-group') !== 'top'

                // 限制连线配置
                return true
            },
            createEdge() {
                return _Graph.createEdge({
                    shape: 'dag-edge',
                    attrs: {
                        line: {
                            // strokeDasharray: '5 5'
                        }
                    },
                    zIndex: -1
                })
            }
        },
        selecting: {
            enabled: true,
            multiple: true,
            rubberEdge: true,
            rubberNode: true,
            modifiers: 'shift',
            rubberband: true
        },
        keyboard: true,
        clipboard: true,
        history: true
    })
    dnd = new Addon.Dnd({
        target: _Graph,
        getDragNode: (node: any) => node.clone({ keepId: true }),
        getDropNode: (node: any) => node.clone({ keepId: true }),
        validateNode() {
            return true
        }
    })
    _Graph.on('node:mouseenter', ({ node }) => {
        if (!runningStatus.value) {
            node.addTools({
                name: 'button-remove',
                args: {
                    x: 144,  // 160最右边
                    y: 13,
                    // x: 170,
                    // y: 0,
                    // offset: { x: 15, y: 5 },
                    offset: { x: 10, y: 10 },
                }
            })
        }
    })
    _Graph.on('node:mouseleave', ({ node }) => {
        node.removeTools()
    })
    _Graph.on('edge:mouseenter', ({ edge }) => {
        if (!runningStatus.value) {
            edge.addTools({
                name: 'button-remove',
                args: { distance: '50%' }
            })
        }
    })
    _Graph.on('edge:mouseleave', ({ edge }) => {
        edge.removeTools()
    })
    _Graph.bindKey('backspace', () => {
        if (!runningStatus.value) {
            const cells = _Graph.getSelectedCells()
            if (cells.length) {
                _Graph.removeCells(cells)
            }
        }
    })
}

// 添加节点
function addNodeFn(item: any, e: any) {
    const node = _Graph.createNode({
        id: item.id,
        shape: 'dag-node',
        data: {
            name: item.name,
            nodeConfigData: item
        },
        ports: [
            {
                id: '1-2',
                group: 'top'
            },
            {
                id: '1-1',
                group: 'bottom'
            }
        ]
    })
    dnd.start(node, e)
}

// 获取所有节点以及连线的数据
function getAllCellData() {
    return _Graph.getCells()
}

// 根据给定的数据结构渲染流程图
function initCellList(data: any) {
    if (data) {
        _Graph.fromJSON(data)
        _Graph.centerContent()
    }
}

// 更新节点状态
function updateFlowStatus(statusList: Array<any>, isRunning: boolean) {
    runningStatus.value = isRunning
    statusList.forEach((item: any) => {
        const node = _Graph.getCellById(item.workId)
        const data = node.getData()
        node.setData({
            ...data,
            workInstanceId: item.workInstanceId,
            status: item.runStatus,
            isRunning: isRunning
        })
    })
}

// 设置是否隐藏网格以及工具---运行中
function hideGrid(status: boolean) {
    if (status) {
        _Graph.hideGrid()
        _Graph.hideTools()
    } else {
        _Graph.showGrid()
        _Graph.showTools()
    }
}

function zoomIn() {
  _Graph.zoom(0.2)
}
function zoomOut() {
  _Graph.zoom(-0.2)
}
function locationCenter() {
  _Graph.centerContent()
}

onMounted(() => {
    container = document.getElementById('container') as HTMLElement | undefined
    initGraph()
})

defineExpose({
    addNodeFn,
    getAllCellData,
    initCellList,
    updateFlowStatus,
    hideGrid
})
</script>

<style lang="scss" scoped>
.zqy-flow {
    height: calc(100vh - 162px);

    .my-selecting {
        border: 1px solid red;
        display: block;
        z-index: 0;
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

    .section-cot {
        width: 100%;
        height: 100%;
        display: flex;
        position: relative;
    }

    .section-cot .btn-container {
        position: absolute;
        // top: 8px;
        // right: 8px;
        bottom: 8px;
        left: 8px;
        height: 28px;
        padding-left: 8px;
        background-color: #f3f3f3;
        display: flex;
        align-items: center;
    }
    .section-cot .btn-container .el-icon {
        font-size: 18px;
        color: #b2b2b2;
        margin-right: 8px;
        cursor: pointer;
        &:hover {
            color: $--app-primary-color;
        }
    }

    .section-cot #container {
        height: 100%;
        position: relative;
        flex: 1;
    }

    .section-cot #container #draw-cot {
        width: 100%;
        height: 100%;
    }

    ::-webkit-scrollbar {
        width: 0;
    }
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
  