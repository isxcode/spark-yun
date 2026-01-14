<!--
 * @Author: fancinate 1585546519@qq.com
 * @Date: 2025-12-09 21:26:39
 * @LastEditors: fancinate 1585546519@qq.com
 * @LastEditTime: 2026-01-14 22:08:13
 * @FilePath: /spark-yun/spark-yun-frontend/src/views/metadata-page/metadata-management/data-lineage/index.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
    <BlockModal :model-config="modelConfig" top="4vh">
        <div id="containerDataLineage" class="containerDataLineage"></div>
        <WorkDetail ref="workDetailRef"></WorkDetail>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, defineProps, ref, createVNode, onMounted, nextTick, defineEmits } from 'vue'
import { VueNode } from 'g6-extension-vue';
import {
    Graph,
    register,
    ExtensionCategory,
    treeToGraphData,
    GraphEvent,
    TreeData,
    EdgeEvent
} from '@antv/g6';
import eventBus from '@/utils/eventBus'
import CustomNode from './custom-node.vue';
import WorkDetail from './work-detail.vue'

let graph: any

const guid = function () {
    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
    }
    return S4() + S4() + '-' + S4() + '-' + S4() + '-' + S4() + '-' + S4() + S4() + S4()
}

const emit = defineEmits(['showDetail'])
const props = defineProps<{
    isCode: boolean
}>()

const callback = ref<any>()
const lineageData = ref<any[]>([])
const workDetailRef = ref<any>()
const pageType = ref<string>('')

const modelConfig = reactive({
    title: '数据血缘',
    visible: false,
    width: '90%',
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

function showModal(data: any, cb: any, pt?: string): void {
    modelConfig.visible = true
    pageType.value = pt
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
            res.childrenStatus = true
            if (res.children && res.children.length) {
                res.children.forEach(dd => {
                    dd.parentStatus = true
                })
            }
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
    const obj = {
        datasource: 50,
        table: 74,
        code: 98
    }
    graph = new Graph({
        container: document.getElementById('containerDataLineage'),
        autoFit: {
            type: 'center', // 自适应类型：'view' 或 'center'
            animation: {
                // 自适应动画效果
                duration: 30, // 动画持续时间(毫秒)
                // easing: 'ease-in-out', // 动画缓动函数
            }
        },
        node: {
            type: 'vue-node',
            style: {
                component: (data: any) => {
                    return createVNode(CustomNode, {
                        config: data,
                        isCode: props.isCode
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
                size: [150, obj[pageType.value]]
            }
        },
        edge: {
            type: 'cubic-horizontal',
            style: {
                endArrow: true,
                labelText: (d) => {
                    return d.name
                },
                labelBackground: true
            }
        },
        layout: {
            type: 'compact-box',
            direction: 'LR',
            getHeight: () => 10,
            getWidth: () => 180,
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
                    id: `${guid()}&&sparkYun&&${target.workVersionId || ''}&&sparkYun&&${target.workName || ''}&&sparkYun&&${target.workType || ''}`,
                    source: source.id,
                    target: target.id,
                    name: `${target.workflowName}（${target.workName}）`,
                    data: target
                }
            }
        }),
        behaviors: ['drag-canvas', 'zoom-canvas', 'drag-element'],
    });
    renderGraph()
}

async function renderGraph() {
    graph.on(EdgeEvent.CLICK, (e, d) => {
        const argumentsParams = e.target.id.split('&&sparkYun&&')
        const workId = argumentsParams[1] ? argumentsParams[1] : ''
        const name = argumentsParams[2] ? argumentsParams[2] : ''
        const workType = argumentsParams[3] ? argumentsParams[3] : ''
        workDetailRef.value.showModal({
            workId: workId,
            name: name,
            workType: workType
        })
    });
    await graph.render();
}

// 查看上游
function getParentNode(data: any) {
    data.lineageType = 'PARENT'
    initData(data).then(async (res: any) => {
        const parentData = res.parent || []
        if (parentData && parentData.length) {
            const allData = graph.getData()
            const parentNodeConfig = {
                nodes: [],
                edges: []
            }
            parentNodeConfig.nodes = parentData.map((pNode: any, index: number) => {
                return {
                    id: pNode.id,
                    name: pNode.name,
                    data: {
                        ...pNode,
                        childrenStatus: true
                    },
                    style: {
                        x: data.style.x - 320,
                        y: (data.style.y / 2) * (index + 1),
                    }
                }
            })
            parentNodeConfig.nodes.forEach((node: any) => {
                parentNodeConfig.edges.push({
                    data: res,
                    id: `${guid()}&&sparkYun&&${node.data.workVersionId || ''}&&sparkYun&&${node.data.workName || ''}&&sparkYun&&${node.data.workType || ''}`,
                    source: node.id,
                    target: data.id,
                    name: `${node.data.workflowName}（${node.data.workName}）`
                })
            })
            graph.addNodeData(parentNodeConfig.nodes)
            graph.addEdgeData(parentNodeConfig.edges)
            await graph.draw();
        }
    }).catch((error) => {
        console.error('请求失败', error)
    })
}
// 查看下游
function getChildNode(data: any) {
    data.lineageType = 'SON'
    initData(data).then(async (res: any) => {
        if (res.children && res.children.length) {
            graph.addNodeData(res.children.map((node: any, index: number) => {
                return {
                    id: node.id,
                    data: {
                        ...node,
                        parentStatus: true
                    },
                    style: {
                        x: data.style.x + 320,
                        y: (data.style.y) * (index + 1),
                    }
                }
            }))
            graph.addEdgeData(res.children.map((node: any) => {
                return {
                    data: node,
                    id: `${guid()}&&sparkYun&&${node.workVersionId || ''}&&sparkYun&&${node.workName || ''}&&sparkYun&&${node.workType || ''}`,
                    source: data.id,
                    target: node.id,
                    name: `${node.workflowName}（${node.workName}）`
                }
            }))
        }
        // renderGraph()
        await graph.draw();
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
    height: calc(100vh - 80px);
    .el-dialog__body {
        max-height: calc(100vh - 182px);
    }
    .modal-content {
        // height: 440px;
        height: calc(97vh - 100px);
        .containerDataLineage {
            height: 100%;
        }
    }
}
</style>
