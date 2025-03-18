<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="zqy-seach-table report-component">
        <div class="zqy-table-top">
            <el-button type="primary" @click="addData">添加卡片</el-button>
            <div class="zqy-seach">
                <el-input
                    v-model="keyword"
                    placeholder="请输入名称/备注 回车进行搜索"
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
                    <template #nameSlot="scopeSlot">
                        <span class="name-click" @click="showDetail(scopeSlot.row)">{{ scopeSlot.row.name }}</span>
                    </template>
                    <template #typeSlot="scopeSlot">
                        {{ typeName(scopeSlot.row.type) }}
                    </template>
                    <template #statusTag="scopeSlot">
                        <ZStatusTag :status="scopeSlot.row.status"></ZStatusTag>
                    </template>
                    <template #options="scopeSlot">
                        <div class="btn-group">
                            <span v-if="['NEW', 'OFFLINE'].includes(scopeSlot.row.status)" @click="publishReport(scopeSlot.row)">发布</span>
                            <span v-else @click="underlineReport(scopeSlot.row)">下线</span>
                            <el-dropdown trigger="click">
                              <span class="click-show-more">更多</span>
                              <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item @click="editReport(scopeSlot.row)">
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
import { reactive, ref, onMounted, computed } from 'vue'
import AddModal from './add-modal/index.vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'

import { BreadCrumbList, TableConfig } from './report-components.config'
import { QueryReportComponent, CreateReportComponentData, PublishReportComponentData, OfflineReportComponentData, DeleteReportComponentData, EditReportComponent } from '@/services/report-echarts.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { ChartTypeList } from './report-item/report-item.config'

interface Option {
    label: string
    value: string
}

const router = useRouter()

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref()

const typeName = computed(() => {
    return (type: string) => {
        const nameObj = ChartTypeList.find((item: Option) => item.value === type)
        return nameObj?.label ? nameObj.label : ''
    }
})

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    QueryReportComponent({
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
        return new Promise((resolve: any, reject: any) => {
            CreateReportComponentData(data).then((res: any) => {
                showDetail(res.data.cardInfo)
                ElMessage.success(res.msg)
                resolve()
            }).catch(err => {
                reject(err)
            })
        })
    })
}

// 编辑
function editReport(data: any) {
    addModalRef.value.showModal((formData: any) => {
        return new Promise((resolve: any, reject: any) => {
            EditReportComponent({
                id: formData.id,
                name: formData.name,
                remark: formData.remark
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
    ElMessageBox.confirm('确定删除该数据卡片吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        DeleteReportComponentData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => {})
    })
}
// 下线
function underlineReport(data: any) {
    ElMessageBox.confirm('确定下线该数据卡片吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        OfflineReportComponentData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => {})
    })
}
// 发布
function publishReport(data: any) {
    ElMessageBox.confirm('确定发布该数据卡片吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        PublishReportComponentData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => {})
    })
}

function showDetail(data: any) {
  router.push({
    name: 'report-item',
    query: {
        id: data.id
    }
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
    &.report-component {
        .zqy-table {
            .btn-group {
                // justify-content: center;
            }
        }
    }
}
</style>
  ./report-components.config