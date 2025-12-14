<!--
 * @Author: fancinate 1585546519@qq.com
 * @Date: 2025-12-09 21:26:39
 * @LastEditors: fancinate 1585546519@qq.com
 * @LastEditTime: 2025-12-14 21:32:16
 * @FilePath: /spark-yun/spark-yun-frontend/src/views/metadata-page/metadata-management/data-lineage/index.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
    <BlockModal :model-config="modelConfig">
        <div id="containerDataLineage" class="containerDataLineage"></div>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, createVNode, onMounted, nextTick, defineEmits } from 'vue'
import { VueNode } from 'g6-extension-vue';
import {
    Graph,
    register,
    ExtensionCategory,
    treeToGraphData,
    GraphEvent,
    TreeData
} from '@antv/g6';
import eventBus from '@/utils/eventBus'
import CustomNode from './custom-node.vue';

let graph: any

const guid = function () {
    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
    }
    return S4() + S4() + '-' + S4() + '-' + S4() + '-' + S4() + '-' + S4() + S4() + S4()
}

const emit = defineEmits(['showDetail'])

const callback = ref<any>()
const lineageData = ref<any[]>([])

const modelConfig = reactive({
    title: '数据血缘',
    visible: false,
    width: '80%',
    customClass: 'data-lineage',
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
        disabled: false
    },
    needScale: false,
    zIndex: 1100,
    closeOnClickModal: false
})

function showModal(data: any, cb: any): void {
    modelConfig.visible = true

    eventBus.on('lineageEvent', (e: any) => {
        if (e.type === 'checkUp') {
            getParentNode(e.data)
        } else if (e.type === 'checkDown') {
            getChildNode(e.data)
        } else if (e.type === 'showDetail') {
            emit('showDetail', e.data)
        }
    })
    callback.value = cb
    nextTick(() => {
        initData().then((res: any) => {
            initGraph(res)
        })
    })
}

function initData(params?: any) {
    if (callback.value && callback.value instanceof Function) {
        return new Promise((resolve: any, reject: any) => {
            callback.value(params).then((res: any) => {
                resolve(res)
            }).catch((error: any) => {
                console.error('请求失败', error)
                reject(error)
            })
        })
    }
}

function initGraph(data: any) {
    graph = new Graph({
        container: document.getElementById('containerDataLineage'),
        autoFit: {
            type: 'center', // 自适应类型：'view' 或 'center'
            animation: {
                // 自适应动画效果
                duration: 300, // 动画持续时间(毫秒)
                easing: 'ease-in-out', // 动画缓动函数
            }
        },
        node: {
            type: 'vue-node',
            style: {
                component: (data: any) => {
                    return createVNode(CustomNode, {
                        config: data
                    })
                },
                ports: [
                    {
                        placement: 'left'
                    },
                    {
                        placement: 'right'
                    }
                ],
                size: [150, 40]
            }
        },
        edge: {
            type: 'cubic-horizontal',
            style: {
                endArrow: true,
                labelText: (d) => {
                    return d.data.workName
                },
                labelBackground: true
            }
        },
        layout: {
            type: 'compact-box',
            direction: 'LR',
            getHeight: () => 10,
            getWidth: () => 120,
            getVGap: () => 30,
            getHGap: () => 70
        },
        data: treeToGraphData(data, {
            getNodeData: (nodeData) => {
                return {
                    id: nodeData.id,
                    name: nodeData.name,
                    data: {
                        ...nodeData
                    }
                }
            },
            getEdgeData: (source: TreeData, target: TreeData) => {
                return {
                    id: `${guid()}-${target.workId}`,
                    source: source.id,
                    target: target.id,
                    data: target
                }
            }
        }),
        behaviors: ['drag-canvas', 'zoom-canvas', 'drag-element'],
    });
    renderGraph()
}

function renderGraph() {
    // graph.once(GraphEvent.AFTER_RENDER, () => {
    //     graph.fitView();
    // });
    graph.render();
}

// 查看上游
function getParentNode(data: any) {
    data.lineageType = 'UP'
    initData(data).then((res: any) => {
        graph.setData(treeToGraphData(data, {
            getEdgeData: (source: TreeData, target: TreeData) => {
                return {
                    id: `${source.id}-${target.id}-${target.data?.name}`,
                    source: source.id,
                    target: target.id,
                    data: target.data,
                    linkId: target.data?.name
                }
            }
        }))
    }).catch((error) => {
        console.error('请求失败', error)
    })
}
// 查看下游
function getChildNode(data: any) {
    data.lineageType = 'DOWN'
    initData(data).then((res: any) => {
        if (res.sonDbLineageList && res.sonDbLineageList.length) {
            graph.addChildrenData(data.id, res.sonDbLineageList.map((node: any) => {
                return {
                    id: node.dbId,
                    name: node.dbName,
                    data: node
                }
            }))
        }
        renderGraph()
    }).catch((error) => {
        console.error('请求失败', error)
    })
}

function closeEvent() {
    eventBus.off('lineageEvent')
    modelConfig.visible = false
}

onMounted(() => {
    register(ExtensionCategory.NODE, 'vue-node', VueNode);
})

defineExpose({
    showModal
})
</script>

<style lang="scss">
.data-lineage {
    .modal-content {
        height: 440px;
        .containerDataLineage {
            height: 100%;
        }
    }
}
</style>
