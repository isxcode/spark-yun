<template>
    <BlockModal :model-config="modelConfig" top="10vh">
        <div class="zqy-table-top">
            <span></span>
            <div class="zqy-seach">
                <el-input
                    v-model="keyword"
                    placeholder="请输入搜索条件 回车进行搜索"
                    :maxlength="200"
                    clearable
                    @input="inputEvent"
                    @keyup.enter="handleCurrentChange(1)"
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
                        <span
                            class="name-click"
                            @click="showDetail(scopeSlot.row)"
                        >{{ scopeSlot.row.name }}</span>
                    </template>
                    <template #statusTag="scopeSlot">
                        <ZStatusTag :status="scopeSlot.row.status"></ZStatusTag>
                    </template>
                </BlockTable>
            </div>
        </LoadingPage>
        <DataFieldDetail ref="dataFieldDetailRef"></DataFieldDetail>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, defineEmits } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { GetDataModelTreeData } from '@/services/data-model.service'
import LoadingPage from '@/components/loading/index.vue'
import DataFieldDetail from '../data-field-detail/index.vue'

interface colConfig {
    prop?: string;
    title: string;
    align?: string;
    showOverflowTooltip?: boolean;
    customSlot?: string;
    width?: number;
    minWidth?: number;
    formatter?: any
    fixed?: string;
}

interface Pagination {
    currentPage: number;
    pageSize: number;
    total: number;
}

interface TableConfig {
    tableData: Array<any>
    colConfigs: Array<colConfig>
    seqType: string
    pagination?: Pagination
    loading?: boolean
}

const colConfigs: colConfig[] = [
    {
        prop: 'name',
        title: '名称',
        minWidth: 125,
        customSlot: 'nameSlot',
        showOverflowTooltip: true
    },
    {
        prop: 'modelType',
        title: '模型类型',
        minWidth: 125,
        formatter: (data: any) => {
            const obj: any = {
                ORIGIN_MODEL: '原始模型',
                LINK_MODEL: '关联模型'
            }
            return data.cellValue && obj[data.cellValue] ? obj[data.cellValue] : '-'
        },
        showOverflowTooltip: true
    },
    {
        prop: 'layerName',
        title: '数据分层',
        minWidth: 140
    },
    {
        prop: 'datasourceName',
        title: '数据源',
        minWidth: 140
    },
    {
        prop: 'tableName',
        title: '表名',
        minWidth: 140
    },
    {
        prop: 'status',
        title: '状态',
        customSlot: 'statusTag',
        minWidth: 100,
        showOverflowTooltip: true
    },
    {
        prop: 'remark',
        title: '备注',
        minWidth: 120
    },
    {
        prop: 'createUsername',
        title: '创建人',
        minWidth: 120
    },
    {
        prop: 'createDateTime',
        title: '创建时间',
        minWidth: 140
    }
]

const modelConfig = reactive<any>({
    title: '数据模型',
    visible: false,
    width: '720px',
    customClass: 'data-layer-model',
    needScale: false,
    zIndex: 1100,
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
    },
    closeOnClickModal: false
})

const loading = ref(false)
const networkError = ref(false)
const info = ref(null)
const dataFieldDetailRef = ref<any>()
const keyword = ref<string>('')
const tableConfig: TableConfig = reactive({
    tableData: [],
    colConfigs: colConfigs,
    pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
    },
    seqType: 'seq',
    loading: false
})

function showModal(data: any): void {
    console.log('数据', data)
    info.value = data
    handleCurrentChange(1)
    modelConfig.visible = true
}

function showDetail(data: any) {
    dataFieldDetailRef.value.showModal(data)
}

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    GetDataModelTreeData({
        page: tableConfig.pagination.currentPage - 1,
        pageSize: tableConfig.pagination.pageSize,
        searchKeyWord: keyword.value,
        layerId: info.value.id
    }).then((res: any) => {
        tableConfig.tableData = res.data.content
        tableConfig.pagination.total = res.data.totalElements
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

function handleSizeChange(e: number) {
    tableConfig.pagination.pageSize = e
    initData()
}

function handleCurrentChange(e: number) {
    tableConfig.pagination.currentPage = e
    initData()
}

function closeEvent() {
    modelConfig.visible = false
}

function editEvent(data: any) {
    emit('editEvent', data)
}

function inputEvent(e: string) {
    if (e === '') {
        handleCurrentChange(1)
    }
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.data-layer-model {
    .modal-content {
        padding: 12px 20px 0;
        padding-top: 0;
        box-sizing: border-box;
        .zqy-table-top {
            height: 50px;
            display: flex;
            box-sizing: border-box;
            align-items: center;
            justify-content: space-between;
            .zqy-seach {
                .el-input {
                    width: 330px;
                }
            }
        }
        .zqy-table {
            height: calc(56vh - 70px);
        }
    }
}
</style>