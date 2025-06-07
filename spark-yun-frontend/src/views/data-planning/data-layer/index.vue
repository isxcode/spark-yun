<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="zqy-seach-table data-layer">
        <div class="zqy-table-top">
            <div class="btn-container">
                <el-button type="primary" @click="addData">
                    新建分层
                </el-button>
                <el-button type="primary" @click="showParentDetail" v-if="parentLayerId">
                    返回上一分层
                </el-button>
            </div>
            <div class="zqy-seach">
                <el-radio-group v-model="tableType" @change="changeTypeEvent">
                    <el-radio-button label="layer">分层搜索</el-radio-button>
                    <el-radio-button label="all">全局搜索</el-radio-button>
                </el-radio-group>
                <el-input
                    v-model="keyword"
                    placeholder="请输入搜索条件 回车进行搜索"
                    :maxlength="200"
                    clearable
                    @input="inputEvent"
                    @keyup.enter="handleCurrentChange(1)"
                />
            </div>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
            <div class="zqy-table">
                <BlockTable
                    :table-config="tableConfig"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                >
                    <template #nameSlot="scopeSlot">
                        <span
                            v-if="tableType === 'layer'"
                            class="name-click"
                            @click="showDetail(scopeSlot.row)"
                        >{{ scopeSlot.row.name }}</span>
                        <span v-else>{{ scopeSlot.row.name }}</span>
                    </template>
                    <template #parentNameSlot="scopeSlot">
                        <span
                            v-if="tableType === 'layer'"
                            class="name-click"
                            @click="showParentDetail(scopeSlot.row)"
                        >{{ scopeSlot.row.parentNameList }}</span>
                        <span v-else>{{ scopeSlot.row.parentNameList }}</span>
                    </template>
                    <template #options="scopeSlot">
                        <div class="btn-group btn-group-msg">
                            <span @click="editData(scopeSlot.row)">编辑</span>
                            <el-dropdown trigger="click">
                                <span class="click-show-more">更多</span>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item @click="dataModelPage(scopeSlot.row)">数据模型</el-dropdown-item>
                                        <el-dropdown-item @click="deleteData(scopeSlot.row)">删除</el-dropdown-item>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </div>
                    </template>
                </BlockTable>
            </div>
        </LoadingPage>
        <AddModal ref="addModalRef" />
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import AddModal from './add-modal/index.vue'

import { BreadCrumbList, TableConfig } from './list.config'
import { DeleteDataLayerData, GetDataLayerTreeData, GetDataLayerList, SaveDataLayerData, UpdateDataLayerData, GetParentLayerNode } from '@/services/data-layer.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref<any>(null)
const tableType = ref<string>('layer')
const parentLayerId = ref<string>('')

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    if (tableType.value !== 'all') {
        GetDataLayerTreeData({
            page: tableConfig.pagination.currentPage - 1,
            pageSize: tableConfig.pagination.pageSize,
            searchKeyWord: keyword.value,
            parentLayerId: parentLayerId.value
        }).then((res: any) => {
            tableConfig.tableData = res.data.content
            tableConfig.pagination.total = res.data.totalElements
            loading.value = false
            tableConfig.loading = false
            networkError.value = false
        }).catch(() => {
            tableConfig.tableData = []
            tableConfig.pagination.total = 0
            loading.value = false
            tableConfig.loading = false
            networkError.value = true
        })
    } else {
        GetDataLayerList({
            page: tableConfig.pagination.currentPage - 1,
            pageSize: tableConfig.pagination.pageSize,
            searchKeyWord: keyword.value
        }).then((res: any) => {
            tableConfig.tableData = res.data.content
            tableConfig.pagination.total = res.data.totalElements
            loading.value = false
            tableConfig.loading = false
            networkError.value = false
        }).catch(() => {
            tableConfig.tableData = []
            tableConfig.pagination.total = 0
            loading.value = false
            tableConfig.loading = false
            networkError.value = true
        })
    }
}

function changeTypeEvent(e: string) {
    parentLayerId.value = null
    tableConfig.pagination.currentPage = 1
    tableConfig.pagination.pageSize = 10

    tableConfig.tableData = []
    tableConfig.pagination.total = 0
    initData()
}

function addData() {
    addModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            const params = {
                ...data,
                parentLayerId: !data.parentLayerId ? null : data.parentLayerId
            }
            SaveDataLayerData(params).then((res: any) => {
                ElMessage.success(res.msg)
                initData()
                resolve()
            }).catch((error: any) => {
                reject(error)
            })
        })
    }, null, parentLayerId.value)
}
function editData(data: any) {
    addModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            UpdateDataLayerData(data).then((res: any) => {
                ElMessage.success(res.msg)
                initData()
                resolve()
            }).catch((error: any) => {
                reject(error)
            })
        })
    }, data)
}

// 删除
function deleteData(data: any) {
    ElMessageBox.confirm('确定删除该分层吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        DeleteDataLayerData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => { })
    })
}

// 跳转分层数据模型
function dataModelPage(data: any) {
    router.push({
        name: 'layer-model',
        query: {
            id: data.id
        }
    })
}

function inputEvent(e: string) {
    if (e === '') {
        handleCurrentChange(1)
    }
}

// 跳转子集分层
function showDetail(data: any) {
    parentLayerId.value = data.id
    tableConfig.pagination.currentPage = 1
    tableConfig.pagination.pageSize = 10
    initData()
}

// 跳转父级分层
function showParentDetail() {
    GetParentLayerNode({
        id: parentLayerId.value
    }).then((res: any) => {
        parentLayerId.value = res.data.parentLayerId
        tableConfig.pagination.currentPage = 1
        tableConfig.pagination.pageSize = 10
        initData()
    }).catch((error: any) => {
        console.error('获取父级节点失败', error)
    })
}

function handleSizeChange(e: number) {
    tableConfig.pagination.pageSize = e
    initData()
}

function handleCurrentChange(e: number) {
    tableConfig.pagination.currentPage = e
    initData()
}

onMounted(() => {
    tableConfig.pagination.currentPage = 1
    tableConfig.pagination.pageSize = 10
    initData()
})
</script>

<style lang="scss">
.data-layer {
    &.zqy-seach-table {
        .zqy-table-top {
            .btn-container {
                height: 100%;
                display: flex;
                align-items: center;
            }
            .zqy-seach {
                display: flex;
                align-items: center;
                .el-radio-group {
                    margin-right: 8px;
                    .el-radio-button__inner {
                        font-size: getCssVar('font-size', 'extra-small');
                    }
                }
            }
        }
        .zqy-table {
            .btn-group-msg {
                justify-content: space-around;
            }
        }
    }
}
</style>