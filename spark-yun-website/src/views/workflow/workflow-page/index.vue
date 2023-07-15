<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="option-container">
        <div class="option-title">
            {{ workflowName }}
        </div>
        <div class="option-btns">
            <span>运行</span>
            <span @click="saveData">保存</span>
            <span>中止</span>
            <span>配置</span>
            <span>发布</span>
            <span>下线</span>
            <span>导出</span>
            <span>导入</span>
            <span>收藏</span>
        </div>
    </div>
    <div class="workflow-page">
        <div class="work-list">
            <div class="search-box">
                <el-input v-model="searchParam" placeholder="回车搜索作业名称" @input="inputEvent"
                    @keyup.enter="initData"></el-input>
                <el-button @click="addData">新建作业</el-button>
            </div>
            <div class="list-box">
                <template v-for="work in workListItem" :key="work.id">
                    <div :draggable="true" class="list-item" @dragend="handleDragEnd($event, work)">{{ work.name }}
                        <el-dropdown trigger="click">
                            <el-icon class="option-more">
                                <MoreFilled />
                            </el-icon>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item @click="editData(work)">编辑</el-dropdown-item>
                                    <el-dropdown-item>删除</el-dropdown-item>
                                    <el-dropdown-item>复制</el-dropdown-item>
                                    <el-dropdown-item>导出</el-dropdown-item>
                                    <el-dropdown-item>置顶</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                </template>
            </div>
        </div>
        <!-- <div class="workflow-btn-container">
            <el-button type="primary" @click="initFlowData">初始化</el-button>
            <el-button type="primary" @click="saveData">保存</el-button>
        </div> -->
        <div class="flow-container">
            <ZqyFlow ref="zqyFlowRef"></ZqyFlow>
        </div>
        <AddModal ref="addModalRef" />
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import ZqyFlow from '@/lib/packages/zqy-flow/flow.vue'
import AddModal from './add-modal/index.vue'

import { AddWorkflowDetailList, GetWorkflowDetailList, SaveWorkflowData, UpdateWorkflowDetailList } from '@/services/workflow.service'
import { ElMessage } from 'element-plus'

const route = useRoute()

const searchParam = ref('')
const workListItem = ref([])
const zqyFlowRef = ref(null)
const workflowName = ref('')
const addModalRef = ref(null)

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
        webConfig: JSON.stringify(data.map((item: any) => {
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
        }))
    }).then((res: any) => {
        ElMessage.success('保存成功')
    }).catch(() => {

    })
    console.log(JSON.stringify(data))
}

// 添加作业
function addData() {
    addModalRef.value.showModal((formData: FormData) => {
        return new Promise((resolve: any, reject: any) => {
            AddWorkflowDetailList({
                ...formData,
                workflowId: route.query.id
            })
                .then((res: any) => {
                    ElMessage.success(res.msg)
                    initData()
                    resolve()
                })
                .catch((error: any) => {
                    reject(error)
                })
        })
    })
}

// 编辑作业
function editData(data: any) {
  addModalRef.value.showModal((formData: FormData) => {
    return new Promise((resolve: any, reject: any) => {
      UpdateWorkflowDetailList(formData)
        .then((res: any) => {
          ElMessage.success(res.msg)
          initData()
          resolve()
        })
        .catch((error: any) => {
          reject(error)
        })
    })
  }, data)
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
    workflowName.value = route.query.name
})
</script>

<style lang="scss">
.option-container {
    height: 50px;
    width: 100%;
    background-color: $--app-light-color;
    border-bottom: 1px solid $--app-border-color;
    display: flex;

    .option-title {
        height: 100%;
        width: 201px;
        display: flex;
        align-items: center;
        font-size: $--app-normal-font-size;
        color: $--app-base-font-color;
        padding-left: 12px;
        border-right: 1px solid $--app-border-color;
        box-sizing: border-box;
    }

    .option-btns {
        display: flex;
        align-items: center;
        padding-left: 12px;
        box-sizing: border-box;
        font-size: $--app-small-font-size;

        span {
            margin-right: 8px;
            cursor: pointer;

            &:hover {
                color: $--app-primary-color;
                text-decoration: underline;
            }
        }
    }
}

.workflow-page {
    height: calc(100% - 106px);
    display: flex;
    position: relative;

    .work-list {
        min-width: 200px;
        width: 200px;
        max-width: 200px;
        height: 100%;
        border-right: 1px solid $--app-border-color;

        .search-box {
            height: 72px;
            padding: 4px 0;
            box-sizing: border-box;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            align-items: center;
            width: 100%;
            border-bottom: 1px solid $--app-border-color;

            .el-input {
                width: 180px;
            }

            .el-button {
                width: 90%;
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
                position: relative;

                &:hover {
                    background-color: $--app-click-color;

                    .el-dropdown {
                        display: block;
                    }
                }

                .el-dropdown {
                    position: absolute;
                    right: 8px;
                    top: 15px;
                    // display: none;

                    .option-more {
                        font-size: 14px;
                        transform: rotate(90deg);
                        cursor: pointer;
                        color: $--app-info-color;
                    }
                }
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