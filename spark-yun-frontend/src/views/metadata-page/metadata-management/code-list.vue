<template>
    <BlockTable :table-config="tableConfig" @size-change="handleSizeChange"
        @current-change="handleCurrentChange">
    </BlockTable>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import { GetMetadataCodesList } from '@/services/metadata-page.service'

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
            showOverflowTooltip: true
        },
        {
            prop: 'columnType',
            title: '字段类型',
            minWidth: 125,
            showOverflowTooltip: true
        },
        {
            prop: 'dbName',
            title: '数据库',
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
            prop: 'columnComment',
            title: '备注',
            minWidth: 140
        },
        {
            prop: 'lastModifiedDateTime',
            title: '更新时间',
            minWidth: 140
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
})

defineExpose({
    initData
})
</script>
