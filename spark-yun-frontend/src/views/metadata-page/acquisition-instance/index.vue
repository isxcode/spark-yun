<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="zqy-seach-table message-notification">
        <div class="zqy-table-top">
            <span></span>
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
                        <ZStatusTag :status="scopeSlot.row.status"></ZStatusTag>
                    </template>
                    <template #options="scopeSlot">
                        <div class="btn-group btn-group-msg">
                          <span @click="showLog(scopeSlot.row)">日志</span>
                          <el-dropdown trigger="click">
                            <span class="click-show-more">更多</span>
                            <template #dropdown>
                              <el-dropdown-menu>
                                <el-dropdown-item v-if="['COLLECTING'].includes(scopeSlot.row.status)" @click="abortData(scopeSlot.row)">
                                  中止
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
        <ShowLog ref="showLogRef" />
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, onUnmounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { BreadCrumbList, TableConfig } from './list.config'
import { ElMessage, ElMessageBox } from 'element-plus'
import { AbortMetadataInstanceList, RefreshMetadataInstanceList, RemoveMetadataInstanceList } from '@/services/metadata-page.service'
import ShowLog from '../../computer-group/computer-pointer/show-log/index.vue'

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const showLogRef = ref(null)
const timer = ref()

function initData(tableLoading?: boolean, type?: string) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    RefreshMetadataInstanceList({
        page: tableConfig.pagination.currentPage - 1,
        pageSize: tableConfig.pagination.pageSize,
        searchKeyWord: keyword.value
    }).then((res: any) => {
        if (type) {
            res.data.content.forEach((item: any) => {
                tableConfig.tableData.forEach((col: any) => {
                    if (item.id === col.id) {
                        col.status = item.status
                    }
                })
            })
        } else {
            tableConfig.tableData = res.data.content
            tableConfig.pagination.total = res.data.totalElements
        }
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

function showLog(data: any) {
    showLogRef.value.showModal(data.collectLog)
}

// 删除
function deleteData(data: any) {
    ElMessageBox.confirm('确定删除该采集实例吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        RemoveMetadataInstanceList({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => { })
    })
}

// 中止
function abortData(data: any) {
    ElMessageBox.confirm('确定中止该采集实例吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        AbortMetadataInstanceList({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => { })
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
    timer.value = setInterval(() => {
        initData(true, 'interval')
    }, 10000)
})
onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
  timer.value = null
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