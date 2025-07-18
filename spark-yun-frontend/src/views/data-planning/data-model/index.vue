<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="zqy-seach-table message-notification">
        <div class="zqy-table-top">
            <el-button type="primary" @click="addData">
                新建模型
            </el-button>
            <div class="zqy-seach">
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
                    <template #statusTag="scopeSlot">
                        <ZStatusTag :status="scopeSlot.row.status"></ZStatusTag>
                    </template>
                    <template #nameSlot="scopeSlot">
                        <span
                            class="name-click"
                            @click="showDetail(scopeSlot.row)"
                        >{{ scopeSlot.row.name }}</span>
                    </template>
                    <template #layerNameSlot="scopeSlot">
                        <span
                            class="name-click"
                            @click="showDataLayer(scopeSlot.row)"
                        >{{ scopeSlot.row.layerName }}</span>
                    </template>
                    <template #options="scopeSlot">
                        <div class="btn-group btn-group-msg">
                            <span @click="showLog(scopeSlot.row)">日志</span>
                            <el-dropdown trigger="click">
                                <span class="click-show-more">更多</span>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item @click="editData(scopeSlot.row)">编辑</el-dropdown-item>
                                        <el-dropdown-item @click="deleteData(scopeSlot.row)">删除</el-dropdown-item>
                                        <el-dropdown-item @click="resetData(scopeSlot.row)">重置</el-dropdown-item>
                                        <el-dropdown-item @click="buildData(scopeSlot.row)">构建</el-dropdown-item>
                                        <el-dropdown-item @click="copyData(scopeSlot.row)">复制</el-dropdown-item>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </div>
                    </template>
                </BlockTable>
            </div>
        </LoadingPage>
        <AddModal ref="addModalRef" />
        <CopyModal ref="copyModalRef" />
        <ShowLog ref="showLogRef" />
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import AddModal from './add-modal/index.vue'
import CopyModal from './copy-modal/index.vue'

import { BreadCrumbList, TableConfig } from './list.config'
import { GetParentLayerNode } from '@/services/data-layer.service'
import {
    GetDataModelList,
    GetDataModelTreeData,
    SaveDataModelData,
    UpdateDataModelData,
    DeleteDataModelData,
    ResetDataModel,
    BuildDataModel,
    CopyDataModelData
 } from '@/services/data-model.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import ShowLog from '../../computer-group/computer-pointer/show-log/index.vue'

const router = useRouter()
const route = useRoute()

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref<any>(null)
const copyModalRef = ref<any>(null)
const showLogRef = ref<any>(null)

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    if (route.query.id) {
        GetDataModelTreeData({
            page: tableConfig.pagination.currentPage - 1,
            pageSize: tableConfig.pagination.pageSize,
            searchKeyWord: keyword.value,
            layerId: route.query.id
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
        GetDataModelList({
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

function addData() {
    addModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            SaveDataModelData(data).then((res: any) => {
                ElMessage.success(res.msg)
                initData()
                resolve()
            }).catch((error: any) => {
                reject(error)
            })
        })
    })
}
function editData(data: any) {
    addModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            UpdateDataModelData(data).then((res: any) => {
                ElMessage.success(res.msg)
                initData()
                resolve()
            }).catch((error: any) => {
                reject(error)
            })
        })
    }, data)
}
function copyData(data: any) {
    copyModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            CopyDataModelData({
                modelId: data.id,
                name: data.name,
                layerId: data.layerId,
                tableName: data.tableName,
                remark: data.remark
            }).then((res: any) => {
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
        DeleteDataModelData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => { })
    })
}

function resetData(data: any) {
    ElMessageBox.confirm('是否确定重置？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        ResetDataModel({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => { })
    })
}

function buildData(data: any) {
    ElMessageBox.confirm('是否确定构建？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        BuildDataModel({
            modelId: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => { })
    })
}

function inputEvent(e: string) {
    if (e === '') {
        handleCurrentChange(1)
    }
}

function showDataLayer(data: any) {
    GetParentLayerNode({
        id: data.layerId
    }).then((res: any) => {
        router.push({
            name: 'data-layer',
            query: {
                parentLayerId: res.data.parentLayerId ?? null
            }
        })
    }).catch((error: any) => {
        console.error('获取父级节点失败', error)
    })
}

function showDetail(data: any) {
    router.push({
        name: 'model-field',
        query: {
            id: data.id,
            modelType: data.modelType
        }
    })
}

// 查看日志
function showLog(e: any) {
    showLogRef.value.showModal(e.buildLog)
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
.message-notification {
    &.zqy-seach-table {
        .zqy-table {
            .btn-group-msg {
                justify-content: space-around;
            }
        }
    }
}
</style>