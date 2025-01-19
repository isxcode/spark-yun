<template>
    <BlockTable :table-config="tableConfig">
        <template #options="scopeSlot">
            <div class="btn-group btn-group__center">
                <span @click="editEvent(scopeSlot.row)">备注</span>
            </div>
        </template>
    </BlockTable>
</template>

<script lang="ts" setup>
import { onMounted, defineEmits, defineProps, reactive } from 'vue'
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

const emit = defineEmits(['editEvent'])

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
    },
    {
        title: '操作',
        align: 'center',
        customSlot: 'options',
        width: 60,
        fixed: 'right'
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
    },
    {
        title: '操作',
        align: 'center',
        customSlot: 'options',
        width: 60,
        fixed: 'right'
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

function editEvent(data: any) {
    data.pageType = 'code_pre'
    emit('editEvent', {
        ...data,
        callback: () => {
            initData()
        }
    })
}

onMounted(() => {
    tableConfig.colConfigs = hiveCol
    initData()
})
</script>
