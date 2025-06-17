<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="layer-area">
        <div class="layer-btn-container">
            <el-button @click="backPage">返回</el-button>
        </div>
        <div id="container" class="container-layout"></div>
        <DataModelDetail ref="dataModelDetailRef"></DataModelDetail>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, createApp, h } from 'vue'
import { Graph, treeToGraphData } from '@antv/g6';
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { GetDataLayerTreeNodeAll } from '@/services/data-layer.service'
import TreeNode from './tree-node/index.vue'
import DataModelDetail from './data-model-detail/index.vue'

const route = useRoute()
const router = useRouter()

let graph: any

const treeData = ref<any>({})
const dataModelDetailRef = ref<any>(null)

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

function getTreeData() {
    return new Promise((resolve: any, reject) => {
        GetDataLayerTreeNodeAll({
            id: route.query.id
        }).then((res: any) => {
            resolve(res.data)
        }).catch(() => {
            data.loading = false
        })
    })
}

function initGraph() {
    const data = treeToGraphData(treeData.value)

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

    graph.on('node:click', (evt: any) => {
        const nodeId = evt.target.id;
        const nodeData = graph.getNodeData(nodeId)
        dataModelDetailRef.value.showModal(nodeData)
    });

    graph.render()
}

function getComponentHTMLString(data: any) {
    const app = createApp(TreeNode, { name: 'TreeNode', params: data }); // 提供必要的 props。
    const container = document.createElement('div');
    document.body.appendChild(container); // 将容器添加到 DOM 中。
    app.mount(container); // 挂载应用。
    const htmlString = container.innerHTML; // 获取内部的 HTML。
    app.unmount(); // 卸载应用。
    document.body.removeChild(container); // 从 DOM 中移除容器。
    return htmlString; // 返回 HTML 字符串。
}

function backPage() {
    console.log('ddd', route.query)
    router.push({
        name: 'data-layer',
        query: {
            parentLayerId: route.query.parentLayerId ?? undefined,
            tableType: route.query.tableType
        }
    })
}

onMounted(() => {
    if (!route.query.id) {
        ElMessage.error('暂无分层信息')
        router.push({
            name: 'data-layer',
        })
    }
    getTreeData().then((res: any) => {
        treeData.value = res
        initGraph()
    }).catch((error) => {

    })
})
</script>

<style lang="scss">
.layer-area {
    height: calc(100vh - 56px);
    position: relative;
    .layer-btn-container {
        position: absolute;
        top: -56px;
        right: 20px;
        height: 56px;
        display: flex;
        align-items: center
    }
    .container-layout {
        height: 100%;
    }
}
</style>