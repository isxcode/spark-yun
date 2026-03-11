<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="zqy-seach-table">
        <div class="zqy-table-top">
            <el-button type="primary" @click="addData">添加规则</el-button>
            <div class="zqy-seach">
                <el-input
                    v-model="keyword"
                    placeholder="请输入名称/IP地址/备注 回车进行搜索"
                    :maxlength="200"
                    clearable
                    @input="inputEvent"
                    @keyup.enter="initData(false)"
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
                    <template #ruleTypeTag="scopeSlot">
                        <el-tag :type="scopeSlot.row.ruleType === 'WHITELIST' ? 'success' : 'danger'">
                            {{ scopeSlot.row.ruleType === 'WHITELIST' ? '白名单' : '黑名单' }}
                        </el-tag>
                    </template>
                    <template #options="scopeSlot">
                        <div class="btn-group">
                            <span @click="editData(scopeSlot.row)">编辑</span>
                            <span @click="deleteData(scopeSlot.row)">删除</span>
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
import AddModal from './add-modal/index.vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'

import { BreadCrumbList, TableConfig } from './access-rule.config'
import { QueryAccessRuleList, CreateAccessRule, UpdateAccessRule, DeleteAccessRule } from '@/services/access-rule.service'
import { ElMessage, ElMessageBox } from 'element-plus'

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref()

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    QueryAccessRuleList({
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
        tableConfig.tableData = [{}]
        tableConfig.pagination.total = 0
        loading.value = false
        tableConfig.loading = false
        networkError.value = true
    })
}

function addData() {
    addModalRef.value.showModal((data: any) => {
        return CreateAccessRule(data).then((res: any) => {
            initData()
            return res
        })
    }, null)
}

function editData(row: any) {
    addModalRef.value.showModal((data: any) => {
        return UpdateAccessRule(data).then((res: any) => {
            initData()
            return res
        })
    }, row)
}

function deleteData(data: any) {
    ElMessageBox.confirm('确定删除该规则吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        DeleteAccessRule({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => {})
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
    .name-click {
        cursor: pointer;
        color: getCssVar('color', 'primary', 'light-5');
        &:hover {
            color: getCssVar('color', 'primary');
        }
    }
}
</style>

