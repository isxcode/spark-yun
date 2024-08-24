<template>
    <BlockTable :table-config="tableConfig" />
</template>

<script lang="ts" setup>
import { onMounted, defineProps, reactive, nextTick } from 'vue'
import { GetTableDetailData } from '@/services/metadata-page.service';

const props = defineProps<{
    datasourceId: string,
    tableName: string
}>()

const tableConfig = reactive({
    tableData: [],
    colConfigs: [],
    loading: false
})

function initData() {
    tableConfig.loading = true
    GetTableDetailData({
        datasourceId: props.datasourceId,
        tableName: props.tableName
    }).then((res: any) => {
        const col = res.data.columns
        tableConfig.colConfigs = col.map((colunm: any) => {
            return {
                prop: colunm,
                title: colunm,
                minWidth: 160,
                showHeaderOverflow: true,
                showOverflowTooltip: true
            }
        })
        nextTick(() => {
            tableConfig.tableData = []
            res.data.rows.forEach(rowData => {
                const columnData = {}
                col.forEach((cl, index) => {
                    columnData[cl] = rowData[index]
                })
                tableConfig.tableData.push(columnData)
            })
            tableConfig.loading = false
        })
    }).catch(() => {
        tableConfig.colConfigs = []
        tableConfig.tableData = []
        tableConfig.loading = false
    })
}

onMounted(() => {
    initData()
})
</script>
