<template>
    <div class="zqy-seach-table custom-form-query">
        <div class="zqy-table-top">
            <div class="btn-container">
                <el-button type="primary" @click="addData">添加</el-button>
                <el-button v-if="status !== 'PUBLISHED'" type="default" @click="editFormConfigEvent">配置</el-button>
            </div>
            <div class="zqy-seach">
                <el-input
                    v-model="keyword"
                    placeholder="请输入 回车进行搜索"
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
import LoadingPage from '@/components/loading/index.vue'
import AddModal from './add-modal/index.vue'
import { useRouter, useRoute } from 'vue-router'
import { BreadCrumbList, TableConfig } from './form-query.config'
import { AddFormData, DeleteFormData, QueryFormConfigById, QueryFormDataList, UpdateFormData } from '@/services/custom-form.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cloneDeep, clone } from 'lodash-es'

const route = useRoute()
const router = useRouter()

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref(null)

const formConfigList = ref([])
const status = ref('')

function getFormConfigById(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    QueryFormConfigById({
        formId: route.query.id
    }).then((res: any) => {
        formConfigList.value = res.data?.components
        status.value = res.data?.status
        router.replace({
            query: {
                id: res.data?.formId,
                formVersion: res.data?.formVersion
            }
        })
        if (res.data?.components && res.data?.components.length) {
            tableConfig.colConfigs = [...(res.data?.components || []).filter(item => item.type !== 'static').map(item => {
                return {
                    prop: item.uuid,
                    title: item.label,
                    minWidth: 100,
                    showHeaderOverflow: true,
                    showOverflowTooltip: true
                }
            }), {
                title: '操作',
                align: 'center',
                customSlot: 'options',
                fixed: 'right',
                width: 80
            }]
        } else {
            tableConfig.colConfigs = []
        }
        tableConfig.pagination.currentPage = 1
        tableConfig.pagination.pageSize = 10
        initData()
    }).catch(() => {
        loading.value = false
        networkError.value = true
    })
}

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    QueryFormDataList({
        page: tableConfig.pagination.currentPage - 1,
        pageSize: tableConfig.pagination.pageSize,
        searchKeyWord: keyword.value,
        formId: route.query.id,
        formVersion: route.query.formVersion
    }).then((res: any) => {
        tableConfig.tableData = (res.data.data || []).map(item => {
            let columnData = {}
            let formDetailData = {}
            Object.keys(item).forEach((k: string) => {
                if (item[k] && item[k] instanceof Array && item[k].length > 0) {
                    columnData[k] = item[k].map(d => d.label).join('，')
                    formDetailData[k] = item[k].map(d => d.value)
                } else if (item[k] && item[k] instanceof Object && item[k].value) {
                    columnData[k] = item[k].label
                    formDetailData[k] = item[k].value
                } else {
                    columnData[k] = item[k]
                    formDetailData[k] = item[k]
                }
            })
            columnData.formDetailData = formDetailData
            return columnData
        })
        tableConfig.pagination.total = res.data.count
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
    addModalRef.value.showModal((formData: any) => {
        return new Promise((resolve: any, reject: any) => {
            AddFormData({
                formId: route.query.id,
                formVersion: route.query.formVersion,
                data: formData
            }).then((res: any) => {
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
    const oldData = cloneDeep(data.formDetailData)
    addModalRef.value.showModal((formData: any) => {
        return new Promise((resolve: any, reject: any) => {
            UpdateFormData({
                formId: route.query.id,
                formVersion: route.query.formVersion,
                oldData: oldData,
                newData: formData
            }).then((res: any) => {
                ElMessage.success(res.msg)
                initData()
                resolve()
            })
            .catch((error: any) => {
                reject(error)
            })
        })
    }, data.formDetailData)
}

// 删除
function deleteData(data: any) {
    ElMessageBox.confirm('确定删除该数据吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        const oldData = cloneDeep(data)
        delete oldData._X_ROW_KEY
        DeleteFormData({
            formId: route.query.id,
            formVersion: route.query.formVersion,
            data: oldData
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch((error: any) => {
        })
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

function editFormConfigEvent() {
    router.push({
        name: 'form-setting',
        query: {
            id: route.query.id,
            formVersion: route.query.formVersion
        }
    })
}

onMounted(() => {
    if (!route.query.id) {
        ElMessage.error('暂无表单信息')
    }
    getFormConfigById()
})
</script>

<style lang="scss">
.custom-form-query {
    .zqy-table {
        padding: 0 20px;
        .vxe-table--body-wrapper {
            max-height: calc(100vh - 232px);
        }
    }
    .zqy-table-top {
        .btn-container {
            display: flex;
        }
    }
}
</style>