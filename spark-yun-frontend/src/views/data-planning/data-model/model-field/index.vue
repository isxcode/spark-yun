<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="zqy-seach-table model-field">
        <div class="zqy-table-top">
            <div class="btn-container">
                <el-button type="primary" @click="addData">
                    新建字段
                </el-button>
                <el-button @click="backDataModel">返回数据模型</el-button>
            </div>
            <div class="zqy-seach model-field-search">
                <div class="search-actions">
                    <el-button @click="configData">高级配置</el-button>
                    <el-button type="primary" @click="buildData">构建</el-button>
                </div>
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
                <div class="model-field-table">
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
    UpdateModelFieldList,
    GetDataModelList,
    UpdateDataModelData
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
const currentModelInfo = ref<any>(null)

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

function buildData() {
    ElMessageBox.confirm('是否确定构建？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        BuildDataModel({
            modelId: route.query.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
            loadCurrentModelInfo()
        }).catch(() => { })
    })
}

async function loadCurrentModelInfo() {
    const modelId = String(route.query.id || '')
    if (!modelId) {
        return null
    }

    let page = 0
    const pageSize = 200
    let total = 0

    do {
        const res = await GetDataModelList({
            page,
            pageSize,
            searchKeyWord: ''
        })
        const content = res?.data?.content || []
        const target = content.find((item: any) => item.id === modelId)
        if (target) {
            currentModelInfo.value = target
            return target
        }
        total = res?.data?.totalElements || 0
        page += 1
    } while (page * pageSize < total)

    return null
}

async function getCurrentModelInfo() {
    const modelId = String(route.query.id || '')
    if (currentModelInfo.value?.id === modelId) {
        return currentModelInfo.value
    }
    return loadCurrentModelInfo()
}

async function configData() {
    const modelInfo = await getCurrentModelInfo()
    if (!modelInfo) {
        ElMessage.warning('未找到当前数据模型信息')
        return
    }

    ElMessageBox.prompt('', '属性配置', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea',
        customClass: 'model-config-prompt',
        inputAttributes: {
            rows: '18'
        },
        inputValue: modelInfo.tableConfig || '',
        inputPlaceholder: '请输入'
    }).then(({ value }) => {
        UpdateDataModelData({
            id: modelInfo.id,
            name: modelInfo.name,
            layerId: modelInfo.layerId,
            dbType: modelInfo.dbType,
            datasourceId: modelInfo.datasourceId,
            tableName: modelInfo.tableName,
            tableConfig: value,
            remark: modelInfo.remark || ''
        }).then((res: any) => {
            currentModelInfo.value = {
                ...modelInfo,
                tableConfig: value
            }
            ElMessage.success(res.msg)
        }).catch(() => { })
    }).catch(() => { })
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

function backDataModel() {
    if (window.history.length > 1) {
        router.back()
        return
    }
    router.push({
        name: 'data-model'
    })
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
    --top-action-gap: 8px;

    .btn-container {
        height: 100%;
        display: flex;
        align-items: center;
        gap: var(--top-action-gap);
    }
    .model-field-search {
        display: flex;
        align-items: center;
        gap: var(--top-action-gap);
    }
    .search-actions {
        display: flex;
        align-items: center;
        gap: var(--top-action-gap);
        flex-shrink: 0;
    }

    .btn-container .el-button + .el-button,
    .search-actions .el-button + .el-button {
        margin-left: 0;
    }
    &.zqy-seach-table {
        .zqy-table {
            height: calc(100% - 16px);
            max-height: calc(100% - 12px);

            .model-field-table {
                height: 100%;
                overflow: hidden;

                :deep(.block-table) {
                    height: 100%;
                }
            }

            .btn-group-msg {
                justify-content: space-around;
            }
            .el-checkbox {
                &.is-disabled {
                    .el-checkbox__inner {
                        background-color: #F5F7FA;
                        border-color: #DCDFE6;
                    }
                    &.is-checked {
                        .el-checkbox__inner {
                            background-color: #E5E7EB;
                            border-color: #C0C4CC;
                            &::after {
                                border-color: #909399;
                            }
                        }
                    }
                }
            }
        }
    }
}

.model-config-prompt {
    width: 760px;

    .el-message-box__content {
        padding-bottom: 8px;
    }

    .el-message-box__input {
        .el-textarea__inner {
            min-height: 420px !important;
            height: 420px !important;
        }
    }
}
</style>
