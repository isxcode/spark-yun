<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="zqy-seach-table metadata-management">
        <div class="zqy-table-top">
            <el-radio-group v-model="tableType" @change="changeTypeEvent">
                <el-radio-button label="db">数据源</el-radio-button>
                <el-radio-button label="table">表</el-radio-button>
            </el-radio-group>
            <div class="zqy-seach">
                <el-input v-model="keyword" placeholder="请输入搜索条件 回车进行搜索" :maxlength="200" clearable @input="inputEvent"
                    @keyup.enter="initData(false)" />
            </div>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
            <div class="zqy-table">
                <BlockTable :table-config="tableConfig" @size-change="handleSizeChange"
                    @current-change="handleCurrentChange">
                    <template #statusTag="scopeSlot">
                        <div class="btn-group">
                            <el-tag v-if="scopeSlot.row.status === 'DISABLE'" type="warning" class="ml-2">禁用</el-tag>
                            <el-tag v-if="scopeSlot.row.status === 'ENABLE'" type="success" class="ml-2">启用</el-tag>
                            <el-tag v-if="scopeSlot.row.status === 'NEW'" class="ml-2">新建</el-tag>
                        </div>
                    </template>
                    <template #options="scopeSlot">
                        <div class="btn-group btn-group-msg">
                            <!-- <span v-if="['DISABLE'].includes(scopeSlot.row.status)" @click="enableData(scopeSlot.row)">启用</span>
                            <span v-if="['ENABLE'].includes(scopeSlot.row.status)" @click="disableData(scopeSlot.row)">禁用</span> -->
                        </div>
                    </template>
                </BlockTable>
            </div>
        </LoadingPage>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'

import { BreadCrumbList, TableConfig } from './list.config'
import { GetMetadataManagementList, RefreshMetadataManagementList } from '@/services/metadata-page.service'
import { ElMessage, ElMessageBox } from 'element-plus'

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const tableType = ref('db')

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    GetMetadataManagementList({
        page: tableConfig.pagination.currentPage - 1,
        pageSize: tableConfig.pagination.pageSize,
        searchKeyWord: keyword.value
    }).then((res: any) => {
        tableConfig.tableData = res.data.content
        tableConfig.pagination.total = res.data.totalElements
        loading.value = false
        tableConfig.loading = false
        networkError.value = false
    })
    .catch(() => {
        tableConfig.tableData = []
        tableConfig.pagination.total = 0
        loading.value = false
        tableConfig.loading = false
        networkError.value = true
    })
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

<style lang="scss">
.zqy-seach-table {
    &.metadata-management {
        .el-radio-group {
            .el-radio-button__inner {
            font-size: getCssVar('font-size', 'extra-small');
            }
        }
    }
}
</style>