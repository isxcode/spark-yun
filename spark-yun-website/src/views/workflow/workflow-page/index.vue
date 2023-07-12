<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="workflow-page">
        <div class="work-list">
            <div class="search-box">
                <el-input v-model="searchParam" placeholder="回车搜索作业名称" @input="inputEvent"
                    @keyup.enter="initData"></el-input>
            </div>
            <div class="list-box">
                <template v-for="work in workListItem" :key="work.id">
                    <div :draggable="true" class="list-item" @dragend="handleDragEnd($event, work)">{{ work.name }}</div>
                </template>
            </div>
        </div>
        <div class="workflow-btn-container">
            <el-button type="primary" @click="initFlowData">初始化</el-button>
            <el-button type="primary" @click="saveData">保存</el-button>
        </div>
        <div class="flow-container">
            <ZqyFlow ref="zqyFlowRef"></ZqyFlow>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import ZqyFlow from '@/lib/packages/zqy-flow/flow.vue'

import { GetWorkflowDetailList, SaveWorkflowData } from '@/services/workflow.service'
import { ElMessage } from 'element-plus'

const route = useRoute()

const searchParam = ref('')
const workListItem = ref([])
const zqyFlowRef = ref(null)

const breadCrumbList = reactive([
    {
        name: '作业流',
        code: 'workflow'
    },
    {
        name: '作业',
        code: 'workflow-page'
    }
])

function initData() {
    GetWorkflowDetailList({
        page: 0,
        pageSize: 99999,
        searchKeyWord: searchParam.value,
        workflowId: route.query.id
    }).then((res: any) => {
        workListItem.value = res.data.content
    }).catch(() => {
        workListItem.value = []
    })
}

// 拖动后松开鼠标触发事件
function handleDragEnd(e, item) {
    //   addHandleNode(e.pageX - 240, e.pageY - 40, new Date().getTime(), item.name, item.type)
    zqyFlowRef.value.addNodeFn(item)
}

// 保存数据
function saveData() {
    const data = zqyFlowRef.value.getAllCellData()
    SaveWorkflowData({
        workflowId: route.query.id,
        webConfig: data.map((item: any) => {
            debugger
            if (item.shape === 'dag-node') {
                return {
                    position: item.position,
                    shape: item.shape,
                    // ports: item.ports,
                    id: item.id,
                    data: item.data,
                    zIndex: item.zIndex
                }
            } else {
                return item
            }
        })
    }).then((res: any) => {
        ElMessage.success('保存成功')
    }).catch(() => {

    })
    console.log(JSON.stringify(data))
}

function initFlowData() {
    const data = []
    zqyFlowRef.value.initCellList(data)
}

function inputEvent(e: string) {
    if (e === '') {
        initData()
    }
}

onMounted(() => {
    initData()
})
</script>

<style lang="scss">
.workflow-page {
    height: calc(100% - 56px);
    display: flex;
    position: relative;

    .work-list {
        min-width: 200px;
        width: 200px;
        max-width: 200px;
        height: 100%;
        border-right: 1px solid $--app-border-color;

        .search-box {
            height: 50px;
            display: flex;
            justify-content: center;
            align-items: center;
            width: 100%;
            border-bottom: 1px solid $--app-border-color;

            .el-input {
                width: 180px;
            }
        }

        .list-box {
            padding: 0 4px;
            box-sizing: border-box;

            .list-item {
                height: $--app-item-height;
                line-height: $--app-item-height;
                padding-left: 8px;
                padding-right: 8px;
                box-sizing: border-box;
                border-bottom: 1px solid $--app-border-color;
                cursor: grab;
                font-size: $--app-small-font-size;
            }
        }

    }

    .workflow-btn-container {
        position: absolute;
        right: 20px;
        top: -55px;
        height: 55px;
        display: flex;
        align-items: center;
        z-index: 10;
    }

    .flow-container {
        width: 100%;
    }
}
</style>