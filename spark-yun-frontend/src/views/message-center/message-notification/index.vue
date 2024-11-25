<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="zqy-seach-table message-notification">
        <div class="zqy-table-top">
            <el-button type="primary" @click="addData">
                新增消息体
            </el-button>
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
                            <ZStatusTag :status="scopeSlot.row.status == 'CHECK_FAIL' ? 'CHECK_ERROR' : scopeSlot.row.status == 'ACTIVE' ? 'ENABLE' : scopeSlot.row.status"></ZStatusTag>
                            <el-popover
                                placement="right"
                                title="响应信息"
                                :width="400"
                                trigger="hover"
                                popper-class="message-error-tooltip"
                                :content="scopeSlot.row.response"
                            >
                                <template #reference>
                                    <el-icon class="hover-tooltip" v-if="scopeSlot.row.response"><Warning /></el-icon>
                                </template>
                            </el-popover>
                        </div>
                    </template>
                    <template #options="scopeSlot">
                        <div class="btn-group btn-group-msg">
                          <span @click="checkData(scopeSlot.row)">检测</span>
                          <el-dropdown trigger="click">
                            <span class="click-show-more">更多</span>
                            <template #dropdown>
                              <el-dropdown-menu>
                                <el-dropdown-item @click="editData(scopeSlot.row)">
                                  编辑
                                </el-dropdown-item>
                                <el-dropdown-item v-if="['CHECK_SUCCESS', 'DISABLE'].includes(scopeSlot.row.status)" @click="enableData(scopeSlot.row)">
                                  启动
                                </el-dropdown-item>
                                <el-dropdown-item v-if="['ACTIVE'].includes(scopeSlot.row.status)"  @click="disableData(scopeSlot.row)">
                                  禁用
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
        <CheckModal ref="checkModalRef" />
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import AddModal from './add-modal/index.vue'
import CheckModal from './check-modal/index.vue'

import { BreadCrumbList, TableConfig } from './message-notification.config'
import { GetMessagePagesList, AddMessageData, UpdateMessageData, DeleteMessageData, EnableMessageData, DisabledMessageData, CheckMessageData } from '@/services/message-center.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import {Loading} from "@element-plus/icons-vue";

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref(null)
const checkModalRef = ref(null)

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    GetMessagePagesList({
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

function addData() {
    addModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            AddMessageData(data).then((res: any) => {
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
            UpdateMessageData(data).then((res: any) => {
                ElMessage.success(res.msg)
                initData()
                resolve()
            }).catch((error: any) => {
                reject(error)
            })
        })
    }, data)
}

// 检测
function checkData(data: any) {
    checkModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            CheckMessageData(data).then((res: any) => {
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
    ElMessageBox.confirm('确定删除该消息通知吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        DeleteMessageData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => { })
    })
}
// 启用
function enableData(data: any) {
    ElMessageBox.confirm('确定启用该消息通知吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        EnableMessageData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => { })
    })
}
// 禁用
function disableData(data: any) {
    ElMessageBox.confirm('确定禁用该消息通知吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        DisabledMessageData({
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
})
</script>

<style lang="scss">
.message-notification {
    &.zqy-seach-table {
        .zqy-table {
            .btn-group-msg {
                justify-content: space-around;
            }
            .hover-tooltip {
                font-size: 16px;
                color: getCssVar('color', 'danger', 'light-5');
            }
        }
    }
}
.message-error-tooltip {
    .el-popover__title {
        font-size: 14px;
    }
    font-size: 12px;
}
</style>