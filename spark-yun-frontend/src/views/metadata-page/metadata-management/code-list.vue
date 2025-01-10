<template>
    <BlockTable :table-config="tableConfig" @size-change="handleSizeChange"
        @current-change="handleCurrentChange">
        <template #nameSlot="scopeSlot">
            <span
              class="name-click"
              @click="showPreviewModal(scopeSlot.row)"
            >{{ scopeSlot.row.tableName }}</span>
        </template>
        <template #options="scopeSlot">
            <div class="btn-group btn-group__center">
                <span @click="editEvent(scopeSlot.row)">备注</span>
            </div>
        </template>
    </BlockTable>
    <PreviewModal ref="previewModalRef"></PreviewModal>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, defineEmits } from 'vue'
import { GetMetadataCodesList } from '@/services/metadata-page.service'
import PreviewModal from './preview-modal/index.vue'

const emit = defineEmits(['editEvent'])

const previewModalRef = ref<any>(null)
const tableConfig = reactive({
    tableData: [],
    colConfigs: [
        {
            prop: 'columnName',
            title: '字段名',
            minWidth: 125,
            showOverflowTooltip: true
        },
        {
            prop: 'tableName',
            title: '表名',
            minWidth: 125,
            showOverflowTooltip: true,
            customSlot: 'nameSlot'
        },
        {
            prop: 'columnType',
            title: '字段类型',
            minWidth: 125,
            showOverflowTooltip: true
        },
        {
            prop: 'datasourceName',
            title: '数据源',
            minWidth: 125,
            showOverflowTooltip: true
        },
        {
            prop: 'dbName',
            title: '库名',
            minWidth: 125,
            showOverflowTooltip: true
        },
        {
            prop: 'columnComment',
            title: '备注',
            minWidth: 140
        },
        {
            prop: 'lastModifiedDateTime',
            title: '更新时间',
            minWidth: 140
        },
        {
            title: '操作',
            align: 'center',
            customSlot: 'options',
            width: 60,
            fixed: 'right'
        }
    ],
    pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
    },
    seqType: 'seq',
    loading: false
})

function initData(searchKeyWord?: string) {
    return new Promise((resolve, reject) => {
        tableConfig.loading = true
        GetMetadataCodesList({
            page: tableConfig.pagination.currentPage - 1,
            pageSize: tableConfig.pagination.pageSize,
            searchKeyWord: searchKeyWord || ''
        }).then((res: any) => {
            tableConfig.tableData = res.data.content
            tableConfig.pagination.total = res.data.totalElements
            tableConfig.loading = false
            resolve(true)
        })
        .catch(() => {
            tableConfig.tableData = []
            tableConfig.pagination.total = 0
            tableConfig.loading = false
            reject()
        })
    })
}

function initPage() {
    tableConfig.pagination.currentPage = 1
    tableConfig.pagination.pageSize = 10
}

function showPreviewModal(data: any) {
    previewModalRef.value.showModal(data)
}

function handleSizeChange(e: number) {
    tableConfig.pagination.pageSize = e
    initData()
}

function handleCurrentChange(e: number) {
    tableConfig.pagination.currentPage = e
    initData()
}

function editEvent(data: any) {
    data.pageType = 'code'
    emit('editEvent', data)
}

onMounted(() => {
    tableConfig.pagination.currentPage = 1
    tableConfig.pagination.pageSize = 10
})

defineExpose({
    initData,
    initPage
})
</script>
