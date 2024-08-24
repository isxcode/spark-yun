<template>
    <BlockTable :table-config="tableConfig" />
</template>

<script lang="ts" setup>
import { onMounted, ref, defineProps, reactive } from 'vue'
import { GetTableCodeInfo } from '@/services/metadata-page.service';

interface colConfig {
    prop?: string;
    title: string;
    align?: string;
    showOverflowTooltip?: boolean;
    customSlot?: string;
    width?: number;
    minWidth?: number;
    formatter?: any
}

interface TableConfig {
    tableData: Array<any>;
    colConfigs: Array<colConfig>;
    seqType: string;
    loading?: boolean; // 表格loading
}

const props = defineProps<{
    datasourceId: string,
    tableName: string
}>()

const normalCol = [
    {
        prop: 'columnName',
        title: '字段名',
        minWidth: 100,
        showOverflowTooltip: true
    },
    {
        prop: 'columnType',
        title: '字段类型',
        minWidth: 100,
        showOverflowTooltip: true
    },
    {
        prop: 'columnComment',
        title: '备注',
        minWidth: 100
    }
]

const hiveCol = [
    {
        prop: 'columnName',
        title: '字段名',
        minWidth: 100,
        showOverflowTooltip: true
    },
    {
        prop: 'columnType',
        title: '字段类型',
        minWidth: 100,
        showOverflowTooltip: true
    },
    {
        prop: 'isPartitionColumn',
        title: '分区键',
        minWidth: 100,
        align: 'center',
        showOverflowTooltip: true,
        formatter: (data: any): string => {
            return data.row.isPartitionColumn ? '是' : '否'
        }
    },
    {
        prop: 'columnComment',
        title: '备注',
        minWidth: 100
    }
]

const tableConfig = reactive<TableConfig>({
    tableData: [],
    colConfigs: [],
    seqType: 'seq',
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
    tableConfig.colConfigs = hiveCol
    initData()
})
</script>
