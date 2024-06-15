<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="zqy-seach-table">
        <div class="zqy-table-top">
            <el-button type="primary" @click="addData">添加视图</el-button>
            <div class="zqy-seach">
                <el-input
                    v-model="keyword"
                    placeholder="请输入名称 回车进行搜索"
                    :maxlength="200"
                    clearable
                    @input="inputEvent"
                    @keyup.enter="initData(false)"
                />
            </div>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(true)">
            <div class="zqy-table">
                <BlockTable
                    :table-config="tableConfig"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                >
                    <template #nameSlot="scopeSlot">
                        <span class="name-click" @click="showDetail(scopeSlot.row)">{{ scopeSlot.row.name }}</span>
                    </template>
                    <template #options="scopeSlot">
                        <div class="btn-group">
                            <span @click="editData(scopeSlot.row)">编辑</span>
                            <el-dropdown trigger="click">
                                <span class="click-show-more">更多</span>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item @click="deleteData(scopeSlot.row)">删除</el-dropdown-item>
                                        <el-dropdown-item @click="underlineData(scopeSlot.row)">下线</el-dropdown-item>
                                        <el-dropdown-item @click="publishApi(scopeSlot.row)">发布</el-dropdown-item>
                                        <el-dropdown-item>预览</el-dropdown-item>
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

import { BreadCrumbList, TableConfig } from './report-views.config'
import {
    GetTenantList,
    AddTenantData,
    DeleteTenantData,
    CheckTenantData,
    DisableTenantData,
    EnableTenantData,
    UpdateTenantData
} from '@/services/tenant-list.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { useAuthStore } from '@/store/useAuth'
import { useRouter } from 'vue-router'

const router = useRouter()

const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref(null)
const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)

function initData(tableLoading?: boolean) {
    tableConfig.tableData = [
        {
            name: '123'
        }
    ]
    tableConfig.pagination.total = 1
}

// 添加
function addData() {
    addModalRef.value.showModal((formData: any) => {
        return new Promise((resolve: any, reject: any) => {
            AddTenantData(formData).then((res: any) => {
                ElMessage.success(res.msg)
                initData()
                resolve()
            }).catch((error: any) => {
                reject(error)
            })
        })
    })
}
// 删除
function deleteData(data: any) {
    ElMessageBox.confirm('确定删除该视图吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {})
}
// 下线
function underlineData(data: any) {
    ElMessageBox.confirm('确定下线该视图吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {})
}
// 发布接口
function publishData(data: any) {
    ElMessageBox.confirm('确定发布该视图吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {})
}
function showDetail(data: any) {
    router.push({
        name: 'report-views-detail'
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
    initData()
})
</script>
  