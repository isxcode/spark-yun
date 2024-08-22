<template>
    <BlockTable :table-config="tableConfig" />
</template>

<script lang="ts" setup>
import { onMounted, ref, defineProps, reactive } from 'vue'
import { GetTableCodeInfo } from '@/services/metadata-page.service';

const props = defineProps<{
    datasourceId: string,
    tableName: string
}>()

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
            prop: 'columnType',
            title: '字段类型',
            minWidth: 125,
            showOverflowTooltip: true
        },
        // {
        //     prop: 'columnType',
        //     title: '主键',
        //     minWidth: 125,
        //     showOverflowTooltip: true
        // },
        // {
        //     prop: 'columnType',
        //     title: '分区键',
        //     minWidth: 125,
        //     showOverflowTooltip: true
        // },
        {
            prop: 'columnComment',
            title: '备注',
            minWidth: 140
        },
    ],
    loading: false
})

function initData() {
    tableConfig.loading = true
    GetTableCodeInfo({
        datasourceId: props.datasourceId,
        tableName: props.tableName
    }).then((res: any) => {
        tableConfig.tableData = res.data
        tableConfig.loading = false
    }).catch(() => {
        tableConfig.tableData = []
        tableConfig.loading = false
    })
}

onMounted(() => {
    initData()
})
</script>
