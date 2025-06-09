<template>
    <BlockModal :model-config="modelConfig">
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
            <div class="zqy-table">
                <BlockTable
                    :table-config="tableConfig"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                >
                    <template #statusTag="scopeSlot">
                        <ZStatusTag :status="scopeSlot.row.status"></ZStatusTag>
                    </template>
                </BlockTable>
            </div>
        </LoadingPage>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, defineEmits } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { GetDataModelTreeData } from '@/services/data-model.service'

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

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    GetDataModelTreeData({
        page: tableConfig.pagination.currentPage - 1,
        pageSize: tableConfig.pagination.pageSize,
        searchKeyWord: '',
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

defineExpose({
    showModal
})
</script>

<style lang="scss">
.data-layer-model {
    .modal-content {
        padding: 12px 20px 0;
        box-sizing: border-box;

    }
}
</style>