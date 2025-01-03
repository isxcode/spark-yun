<template>
    <BlockTable :table-config="tableConfig" @size-change="handleSizeChange"
        @current-change="handleCurrentChange">
        <template #nameSlot="scopeSlot">
            <span
              class="name-click"
              @click="redirectToTable(scopeSlot.row)"
            >{{ scopeSlot.row.name }}</span>
        </template>
        <template #statusTag="scopeSlot">
            <ZStatusTag :status="scopeSlot.row.status"></ZStatusTag>
          </template>
    </BlockTable>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, defineEmits} from 'vue'
import { GetMetadataManagementList } from '@/services/metadata-page.service'

const emit = defineEmits(['redirectToTable'])

const tableConfig = reactive({
    tableData: [],
    colConfigs: [
        {
          prop: 'name',
          title: '数据源',
          minWidth: 125,
          showOverflowTooltip: true,
          customSlot: 'nameSlot'
        },
        {
          prop: 'dbName',
          title: '库名',
          minWidth: 125,
          showOverflowTooltip: true
        },
        {
            prop: 'dbType',
            title: '类型',
            minWidth: 125,
            showOverflowTooltip: true
        },
        {
            prop: 'status',
            title: '状态',
            minWidth: 100,
            customSlot: 'statusTag'
        },
        {
            prop: 'dbComment',
            title: '备注',
            minWidth: 140
        },
        {
            prop: 'lastModifiedDateTime',
            title: '更新时间',
            minWidth: 140
        },
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
        GetMetadataManagementList({
            page: tableConfig.pagination.currentPage - 1,
            pageSize: tableConfig.pagination.pageSize,
            searchKeyWord: searchKeyWord
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

function handleSizeChange(e: number) {
    tableConfig.pagination.pageSize = e
    initData()
}

function handleCurrentChange(e: number) {
    tableConfig.pagination.currentPage = e
    initData()
}

function redirectToTable(data: any) {
    emit('redirectToTable', data)
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
