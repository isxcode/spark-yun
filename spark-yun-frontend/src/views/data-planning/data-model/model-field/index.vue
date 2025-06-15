<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="zqy-seach-table model-field">
        <div class="zqy-table-top">
            <div class="btn-container">
                <el-button type="primary" @click="addData">
                    新建字段
                </el-button>
                <el-button type="primary" @click="buildData">
                    构建
                </el-button>
            </div>
            <div class="zqy-seach">
                <el-input
                    v-model="keyword"
                    placeholder="请输入搜索条件 回车进行搜索"
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
                    @rowDragendEvent="rowDragendEvent"
                >
                    <template #statusTag="scopeSlot">
                        <ZStatusTag :status="scopeSlot.row.status"></ZStatusTag>
                    </template>
                    <template #booleanTag="scopeSlot">
                        <el-checkbox disabled v-model="scopeSlot.row[scopeSlot.column.property]" true-label="ENABLE" false-label="DISABLE" />
                    </template>
                    <template #options="scopeSlot">
                        <div class="btn-group btn-group-msg">
                            <template v-if="route.query && route.query.modelType === 'ORIGIN_MODEL'">
                                <span @click="editData(scopeSlot.row)">编辑</span>
                                <span @click="deleteData(scopeSlot.row)">删除</span>
                            </template>
                            <template v-else>
                                <span> - </span>
                            </template>
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
import LoadingPage from '@/components/loading/index.vue'
import AddModal from './add-modal/index.vue'
import { BreadCrumbList, TableConfig } from './list.config'
import {
    GetModelFieldList,
    AddModelFieldData,
    UpdateModelFieldData,
    DeleteModelField,
    BuildDataModel,
    UpdateModelFieldList
} from '@/services/data-model.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref<any>(null)

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    GetModelFieldList({
        page: 0,
        pageSize: 10000,
        searchKeyWord: keyword.value,
        modelId: route.query.id
    }).then((res: any) => {
        tableConfig.tableData = res.data.content
        loading.value = false
        tableConfig.loading = false
        networkError.value = false
    })
    .catch(() => {
        tableConfig.tableData = []
        loading.value = false
        tableConfig.loading = false
        networkError.value = true
    })
}

function addData() {
    addModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            AddModelFieldData({
                ...data,
                modelId: route.query.id
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

function buildData(data: any) {
    ElMessageBox.confirm('是否确定构建？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        BuildDataModel({
            modelId: route.query.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
        }).catch(() => { })
    })
}

function editData(data: any) {
    addModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            UpdateModelFieldData({
                ...data,
                modelId: route.query.id
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
    ElMessageBox.confirm('确定删除该字段标准吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        DeleteModelField({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => { })
    })
}

function rowDragendEvent(e) {
    UpdateModelFieldList({ 
        dataModelColumnIdList: e.tableData.map(d => d.id),
        modelId: route.query.id
    }).then((res: any) => {
        ElMessage.success(res.msg)
        initData()
    }).catch(() => {
        initData()
    })
}

function inputEvent(e: string) {
    if (e === '') {
        initData()
    }
}

onMounted(() => {
    if (!route.query.id) {
        ElMessage.error('暂无模型信息')
        router.push({
            name: 'data-model',
        })
    } else {
        initData()
    }
})
</script>

<style lang="scss">
.model-field {
    .btn-container {
        height: 100%;
        display: flex;
        align-items: center;
    }
    &.zqy-seach-table {
        .zqy-table {
            height: calc(100% - 16px);
            max-height: calc(100% - 12px);
            .btn-group-msg {
                justify-content: space-around;
            }
            .el-checkbox {
                &.is-disabled {
                    .el-checkbox__inner {
                        background-color: #FFFFFF;
                    }
                    &.is-checked {
                        .el-checkbox__inner {
                            background-color: getCssVar('color', 'primary');
                            border-color: getCssVar('color', 'primary');
                            &::after {
                                border-color: #FFFFFF;
                            }
                        }
                    }
                }
            }
        }
    }
}
</style>