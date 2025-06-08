<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="layer-area">
        <div id="container"></div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, createApp, h } from 'vue'
import { Graph, treeToGraphData } from '@antv/g6';
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { GetDataLayerTreeData } from '@/services/data-layer.service'
import TreeNode from './tree-node/index.vue'

const router = useRouter()

let graph: any

const breadCrumbList = reactive([
    {
        name: '数据分层',
        code: 'data-layer'
    },
    {
        name: '分层领域',
        code: 'layer-area'
    }
])

function getTreeData(data: any) {
    return new Promise((resolve: any, reject) => {
        if (data.loading) {
            reject('子节点正在加载中，请稍后！')
            data.loading = false
        } else {
            data.loading = true
            GetDataLayerTreeData({
                page: 0,
                pageSize: 10000,
                searchKeyWord: '',
                parentLayerId: data.id === 'root' ? null : data.id
            }).then((res: any) => {
                data.loading = false
                data.done = true
                resolve(res.data.content.map((node: any) => {
                    return {
                        id: node.id,
                        data: {
                            ...node,
                        },
                        loading: false,
                        children: []
                    }
                }))
            }).catch(() => {
                data.loading = false
            })
        }
    })
}

const treeData = {
    id: 'root',
    data: {
        id: 'root',
        name: '分层领域',
        remark: '分层领域',
    },
    loading: false,
    done: false,
    children: []
}

function initGraph() {
    const data = treeToGraphData(treeData)

    graph = new Graph({
        container: document.getElementById('container'),
        data: data,
        edge: {
            type: 'cubic-horizontal',
            style: {
                endArrow: true,
            },
        },
        autoFit: {
            type: 'center', // 自适应类型：'view' 或 'center'
            animation: {
                // 自适应动画效果
                duration: 300, // 动画持续时间(毫秒)
                easing: 'ease-in-out', // 动画缓动函数
            }
        },
        node: {
            type: 'html',
            style: {
                size: [140, 72],
                dx: -100,
                dy: -40,
                innerHTML: (e: any) => {
                    const htmlString = getComponentHTMLString(e)
                    return htmlString
                },
                port: true,
                ports: [{ placement: 'right' }, { placement: 'left' }]
            },
            state: {
                selected: {
                    fill: '#ffa940',
                    stroke: '#ff7a00',
                    haloStroke: '#ff7a00',
                },
                highlight: {
                    stroke: '#1890ff',
                    lineWidth: 3,
                },
            },
            animation: false
        },
        layout: {
            type: 'antv-dagre',
            rankdir: 'LR',
            nodesep: 20,
            ranksep: 60,
        },
        behaviors: ['drag-canvas', 'scroll-canvas']
    })

    // graph.once(GraphEvent.AFTER_RENDER, () => {
    //     graph.fitCenter();
    // });

    graph.on('node:click', (evt: any) => {
        const nodeId = evt.target.id;
        const nodeData = graph.getNodeData(nodeId)
        if (nodeData.done) {
            console.log('数据已经加载完毕', nodeData.done)
            return
        }
        getTreeData(nodeData).then((res: any) => {
            graph.addChildrenData(nodeId, res)
            graph.render()
        }).catch((error: any) => {
            console.error('节点展开失败', error)
        })
    });

    graph.render()
}

function getComponentHTMLString(data: any) {
    const app = createApp(TreeNode, { name: 'TreeNode', params: data.data, loading: data.loading }); // 提供必要的 props。
    const container = document.createElement('div');
    document.body.appendChild(container); // 将容器添加到 DOM 中。
    app.mount(container); // 挂载应用。
    const htmlString = container.innerHTML; // 获取内部的 HTML。
    app.unmount(); // 卸载应用。
    document.body.removeChild(container); // 从 DOM 中移除容器。
    return htmlString; // 返回 HTML 字符串。
}

onMounted(() => {
    initGraph()
})
</script>

<style lang="scss">
.layer-area {
    height: calc(100vh - 56px);
}
</style>