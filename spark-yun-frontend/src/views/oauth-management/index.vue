<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="zqy-seach-table">
        <div class="zqy-table-top">
            <el-button type="primary" @click="addData">
                新增配置
            </el-button>
            <div class="zqy-seach">
                <el-input v-model="keyword" placeholder="请输入备注 回车进行搜索" :maxlength="200" clearable @input="inputEvent"
                    @keyup.enter="initData(false)" />
            </div>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
            <div class="zqy-table">
                <BlockTable :table-config="tableConfig" @size-change="handleSizeChange"
                    @current-change="handleCurrentChange">
                    <template #statusTag="scopeSlot">
                        <div class="btn-group">
                            <el-tag v-if="scopeSlot.row.status === 'ENABLE'" class="ml-2" type="success">
                                启用
                            </el-tag>
                            <el-tag v-if="scopeSlot.row.status === 'DISABLE'" class="ml-2" type="danger">
                                禁用
                            </el-tag>
                        </div>
                    </template>
                    <template #options="scopeSlot">
                        <div class="btn-group">
                            <template v-if="scopeSlot.row.status === 'ENABLE'">
                                <span v-if="!scopeSlot.row.statusLoading"
                                    @click="changeStatus(scopeSlot.row, false)">禁用</span>
                                <el-icon v-else class="is-loading">
                                    <Loading />
                                </el-icon>
                            </template>
                            <template v-else>
                                <span v-if="!scopeSlot.row.statusLoading"
                                    @click="changeStatus(scopeSlot.row, true)">启用</span>
                                <el-icon v-else class="is-loading">
                                    <Loading />
                                </el-icon>
                            </template>
                            <el-dropdown trigger="click">
                                <span class="click-show-more">更多</span>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item @click="getUrlEvent(scopeSlot.row)">
                                            复制链接
                                        </el-dropdown-item>
                                        <el-dropdown-item @click="editData(scopeSlot.row)">
                                            编辑
                                        </el-dropdown-item>
                                        <el-dropdown-item @click="deleteData(scopeSlot.row)">
                                            删除
                                        </el-dropdown-item>
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
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import AddModal from './add-modal/index.vue'
import { BreadCrumbList, TableConfig } from './list.config'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { OauthQueryList, OauthCreateData, OauthUpdateData, OauthDeleteData, OauthEnableData, OauthDisableData, OauthUrlCopy } from '@/services/login.service'

const keyword = ref<string>('')
const loading = ref<boolean>(false)
const networkError = ref<boolean>(false)
const addModalRef = ref<any>(null)

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    OauthQueryList({
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

function addData() {
    addModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            OauthCreateData(data).then((res: any) => {
                ElMessage.success(res.msg)
                initData()
                resolve()
            }).catch((error: any) => {
                reject(error)
            })
        })
    })
}
function editData(row: any) {
    addModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            OauthUpdateData(data).then((res: any) => {
                ElMessage.success(res.msg)
                initData()
                resolve()
            }).catch((error: any) => {
                reject(error)
            })
        })
    }, row)
}

// 启用 or 禁用
function changeStatus(data: any, status: boolean) {
    data.statusLoading = true
    if (status) {
        OauthEnableData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            data.statusLoading = false
            initData(true)
        }).catch(() => {
            data.statusLoading = false
        })
    } else {
        OauthDisableData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            data.statusLoading = false
            initData(true)
        }).catch(() => {
            data.statusLoading = false
        })
    }
}

// 删除
function deleteData(data: any) {
    ElMessageBox.confirm('确定删除该配置吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        OauthDeleteData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => { })
    })
}
// 复制链接
function getUrlEvent(data: any) {
    OauthUrlCopy({
        id: data.id
    }).then(async (res: any) => {
        await navigator.clipboard.writeText(res.data.invokeUrl);
        ElMessage.success('复制成功')
    }).catch(() => { })
}

function inputEvent(e: string) {
    if (e === '') {
        initData()
    }
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