<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="zqy-seach-table metadata-management">
        <div class="zqy-table-top">
            <el-radio-group v-model="tableType" @change="changeTypeEvent">
                <el-radio-button label="db">数据源</el-radio-button>
                <el-radio-button label="table">表</el-radio-button>
                <el-radio-button label="code">字段</el-radio-button>
            </el-radio-group>
            <div class="zqy-tenant__select" v-if="tableType === 'table'">
                <el-select
                    v-model="datasourceId"
                    placeholder="请选择数据源"
                    filterable
                    clearable
                    @change="datasourceIdChangeEvent"
                >
                    <el-option
                        v-for="item in dataSourceList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    ></el-option>
                </el-select>
            </div>
            <div class="zqy-seach meta-list-search">
                <el-button v-if="tableType === 'db'" type="primary" :loading="refreshLoading" @click="refreshDataEvent">
                    刷新数据
                </el-button>
                <el-button v-if="tableType === 'table'" type="primary" :loading="refreshLoading" @click="acquisetionTriggerEvent">
                    立即采集
                </el-button>
                <el-input v-model="keyword" placeholder="请输入搜索条件 回车进行搜索" :maxlength="200" clearable @input="inputEvent"
                    @keyup.enter="initData(false)" />
            </div>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
            <div class="zqy-table">
                <component
                    :is="tabComponent"
                    :keyword="keyword"
                    ref="currentTabRef"
                    @redirectToTable="redirectToTable"
                    @editEvent="editEvent"
                    @dataLineageEvent="dataLineageEvent"
                ></component>
            </div>
        </LoadingPage>
        <AddModal ref="addModalRef" />
        <RemarkModal ref="remarkModalRef"></RemarkModal>
        <DataLineage @showDetail="showDetail" ref="dataLineageRef"></DataLineage>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, markRaw, nextTick } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { BreadCrumbList } from './list.config'
import datasourceList from './datasource-list.vue'
import tableList from './table-list.vue'
import codeList from './code-list.vue'
import {
    AddMetadataTaskData,
    CodeRemarkEdit,
    DatasourceRemarkEdit,
    FastTriggerMetadataTaskData,
    GetMetadataManagementList,
    RefreshMetadataManagementList,
    TableRemarkEdit,
    GetDataLineageByDatasource,
    GetDataLineageByTable,
    GetDataLineageByCode
} from '@/services/metadata-page.service'
import { ElMessage } from 'element-plus'
import AddModal from './add-modal/index.vue'
import RemarkModal from './remark-modal/index.vue'
import DataLineage from './data-lineage/index.vue'

const guid = function () {
    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
    }
    return S4() + S4() + '-' + S4() + '-' + S4() + '-' + S4() + '-' + S4() + S4() + S4()
}

const breadCrumbList = reactive(BreadCrumbList)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const tableType = ref('db')
const tabComponent = ref()
const currentTabRef = ref()
const refreshLoading = ref<boolean>(false)
const addModalRef = ref<any>(null)
const remarkModalRef = ref<any>(null)
const dataLineageRef = ref<any>(null)

const datasourceId = ref('')
const dbType = ref('')
const dataSourceList = ref<any[]>([])

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    currentTabRef.value?.initPage()
    currentTabRef.value?.initData(keyword.value, datasourceId.value).then(() => {
        loading.value = false
        networkError.value = false
    }).catch(() => {
        loading.value = false
        networkError.value = true
    })
}

function inputEvent(e: string) {
    if (e === '') {
        initData()
    }
}

function changeTypeEvent(e: string, id?: string) {
    const lookup: any = {
        db: datasourceList,
        table: tableList,
        code: codeList
    }
    tabComponent.value = markRaw(lookup[e])
    if (!id) {
        datasourceId.value = ''
        dbType.value = ''
    }
    keyword.value = ''
    nextTick(() => {
        initData()
        if (e === 'table') {
            getDataSourceList()
        }
    })
}

// 立即采集
function acquisetionTriggerEvent() {
    addModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            AddMetadataTaskData(data).then((resp: any) => {
                FastTriggerMetadataTaskData({
                    id: resp.data.id
                }).then((res: any) => {
                    ElMessage.success(res.msg)
                    initData()
                    resolve()
                }).catch((error: any) => {
                    reject(error)
                })
            }).catch((error: any) => {
                reject(error)
            })
        })
    }, {
        datasourceId: datasourceId.value,
        dbType: dbType.value
    })
}

function editEvent(e: any) {
    const remark = e.dbComment || e.tableComment || e.columnComment
    remarkModalRef.value.showModal((data: any) => {
        return new Promise((resolve, reject) => {
            if (e.pageType === 'datasource') {
                DatasourceRemarkEdit({
                    datasourceId: e.datasourceId,
                    comment: data.remark
                }).then((res: any) => {
                    ElMessage.success(res.msg)
                    initData()
                    resolve()
                }).catch((error: any) => {
                    reject(error)
                })
            } else if (e.pageType === 'table') {
                TableRemarkEdit({
                    datasourceId: e.datasourceId,
                    tableName: e.tableName,
                    comment: data.remark
                }).then((res: any) => {
                    ElMessage.success(res.msg)
                    initData()
                    resolve()
                }).catch((error: any) => {
                    reject(error)
                })
            } else if (e.pageType === 'code') {
                CodeRemarkEdit({
                    datasourceId: e.datasourceId,
                    tableName: e.tableName,
                    columnName: e.columnName,
                    comment: data.remark
                }).then((res: any) => {
                    ElMessage.success(res.msg)
                    initData()
                    resolve()
                }).catch((error: any) => {
                    reject(error)
                })
            } else if (e.pageType === 'code_pre') {
                CodeRemarkEdit({
                    datasourceId: e.datasourceId,
                    tableName: e.tableName,
                    columnName: e.columnName,
                    comment: data.remark
                }).then((res: any) => {
                    ElMessage.success(res.msg)
                    if (e.callback && e.callback instanceof Function) {
                        e.callback()
                    }
                    resolve()
                }).catch((error: any) => {
                    reject(error)
                })
            }
        })
    }, { remark: remark })
}

function refreshDataEvent() {
    getDataSourceList()
    refreshLoading.value = true
    RefreshMetadataManagementList().then((res: any) => {
        ElMessage.success(res.msg)
        initData()
        refreshLoading.value = false
    }).catch(() => {
        refreshLoading.value = false
    })
}

function getDataSourceList() {
    GetMetadataManagementList({
        page: 0,
        pageSize: 10000,
        searchKeyWord: ''
    }).then((res: any) => {
        dataSourceList.value = res.data.content.filter((item: any) => item.dbType !== 'KAFKA').map((item: any) => {
            return {
                label: item.name,
                value: item.datasourceId
            }
        })
    }).catch(() => {
        dataSourceList.value = []
    })
}

function datasourceIdChangeEvent() {
    initData()
}

function redirectToTable(data: any) {
    keyword.value = ''
    datasourceId.value = data.datasourceId
    dbType.value = data.dbType
    changeTypeEvent('table', datasourceId.value)
    tableType.value = 'table'
}

function dataLineageEvent(data: any) {
    dataLineageRef.value.showModal(data, (params?: any) => {
        return new Promise((resolve: any, reject: any) => {
            let requestParams = {
                dbId: data.datasourceId,
                tableName: data.tableName,
                columnName: data.columnName,
                lineageType: 'SON'
            }
            if (data.pageType === 'datasource') {
                if (params) {
                    requestParams.dbId = params.data.dbId
                    requestParams.lineageType = params.lineageType
                }
                GetDataLineageByDatasource(requestParams).then((res: any) => {
                    resolve(getFinalData(res.data, data.pageType))
                }).catch((error: any) => {
                    reject(error)
                })
            } else if (data.pageType === 'table') {
                if (params) {
                    requestParams.dbId = params.data.dbId
                    requestParams.tableName = params.data.tableName,
                    requestParams.lineageType = params.lineageType
                }
                GetDataLineageByTable(requestParams).then((res: any) => {
                    resolve(getFinalData(res.data, data.pageType))
                }).catch((error: any) => {
                    reject(error)
                })
            } else if (data.pageType === 'code') {
                if (params) {
                    requestParams.dbId = params.data.dbId
                    requestParams.tableName = params.data.tableName,
                    requestParams.columnName = params.data.columnName,
                    requestParams.lineageType = params.lineageType
                }
                GetDataLineageByCode(requestParams).then((res: any) => {
                    resolve(getFinalData(res.data, data.pageType))
                }).catch((error: any) => {
                    reject(error)
                })
            }
        })
    })
}

// 递归格式化树节点数据
function getFinalData(node: any, pageType: string) {
    node.pageType = pageType
    if (node && pageType === 'datasource') {
        node.id = guid()
        node.name = node.dbName
        node.children = (node.children || []).map((item: any) => {
            item.id = guid()
            item.name = item.dbName
            item.pageType = pageType
            return item
        })
        node.parent = (node.parent || []).map((item: any) => {
            item.id = guid()
            item.name = item.dbName
            item.pageType = pageType
            return item
        })
    }
    if (node && pageType === 'table') {
        node.id = guid()
        node.name = node.tableName
        node.children = (node.children || []).map((item: any) => {
            item.id = guid()
            item.name = item.tableName
            item.pageType = pageType
            return item
        })
        node.parent = (node.parent || []).map((item: any) => {
            item.id = guid()
            item.name = item.tableName
            item.pageType = pageType
            return item
        })
    }
    if (node && node.children && pageType === 'code') {
        node.id = guid()
        node.name = node.columnName
        node.children = (node.children || []).map((item: any) => {
            item.id = guid()
            item.name = item.columnName
            item.pageType = pageType
            return item
        })
        node.parent = (node.parent || []).map((item: any) => {
            item.id = guid()
            item.name = item.columnName
            item.pageType = pageType
            return item
        })
    }
    return node
}

function showDetail(e: any) {
    currentTabRef.value?.showPreviewModal({
        datasourceId: e.data.dbId,
        dbName: e.data.dbName,
        tableName: e.data.tableName,
    })
}

onMounted(() => {
    getDataSourceList()
    changeTypeEvent('db')
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
        .meta-list-search {
            display: flex;
            align-items: center;
            .el-button {
                margin-right: 12px;
            }
        }
    }
}
</style>