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
                    <!-- <template #nameSlot="scopeSlot">
                        <span class="name-click" @click="showDetail(scopeSlot.row)">{{ scopeSlot.row.name }}</span>
                    </template> -->
                    <template #statusTag="scopeSlot">
                        <div class="btn-group">
                            <el-tag v-if="scopeSlot.row.status === 'NEW'" class="ml-2" type="info">新建</el-tag>
                            <el-tag v-if="scopeSlot.row.status === 'OFFLINE'" class="ml-2" type="danger">已下线</el-tag>
                            <el-tag v-if="scopeSlot.row.status === 'PUBLISHED'" class="ml-2" type="success">已发布</el-tag>
                        </div>
                    </template>
                    <template #options="scopeSlot">
                        <div class="btn-group">
                            <span v-if="['NEW', 'OFFLINE'].includes(scopeSlot.row.status)" @click="publishReport(scopeSlot.row)">发布</span>
                            <span v-else @click="underlineReport(scopeSlot.row)">下线</span>
                            <span v-if="['NEW', 'OFFLINE'].includes(scopeSlot.row.status)" @click="showDetail(scopeSlot.row)">配置</span>
                            <span v-if="['NEW', 'OFFLINE'].includes(scopeSlot.row.status)" @click="deleteData(scopeSlot.row)">删除</span>
                            <span v-if="['PUBLISHED'].includes(scopeSlot.row.status)" @click="previewReport(scopeSlot.row)">预览</span>
                            <span v-if="['PUBLISHED'].includes(scopeSlot.row.status)" @click="shareReport(scopeSlot.row)">分享</span>
                        </div>
                    </template>
                </BlockTable>
            </div>
        </LoadingPage>
        <AddModal ref="addModalRef" />
        <ShareReportModal ref="shareReportModalRef"></ShareReportModal>
    </div>
</template>
  
  <script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import AddModal from './add-modal/index.vue'
import {
    QueryReportView,
    AddReportView,
    UnderlineReportViewData,
    PublishReportViewData,
    DeleteReportViewData
} from '@/services/report-echarts.service'
import ShareReportModal from './share-report-modal/index.vue'

import { BreadCrumbList, TableConfig } from './report-views.config'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref(null)
const shareReportModalRef = ref<any>()
const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    QueryReportView({
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

// 添加
function addData() {
    addModalRef.value.showModal((formData: any) => {
        return new Promise((resolve: any, reject: any) => {
            AddReportView(formData).then((res: any) => {
                ElMessage.success(res.msg)
                showDetail(res.data)
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
    }).then(() => {
        DeleteReportViewData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => {})
    })
}
// 下线
function underlineReport(data: any) {
    ElMessageBox.confirm('确定下线该报表视图吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        UnderlineReportViewData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => {})
    })
}
// 发布
function publishReport(data: any) {
    ElMessageBox.confirm('确定发布该报表视图吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        PublishReportViewData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => {})
    })
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
        name: 'report-views-detail',
        query: {
            id: data.id,
            type: data.status !== 'PUBLISHED' ? 'edit' : 'readonly'
        }
    })
}
function previewReport(data: any) {
    router.push({
        name: 'report-views-detail',
        query: {
            id: data.id,
            type: 'readonly'
        }
    })
}
// 分享
function shareReport(card: any) {
    shareReportModalRef.value.showModal(card)
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
