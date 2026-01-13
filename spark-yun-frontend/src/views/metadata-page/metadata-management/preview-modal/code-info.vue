<template>
    <BlockTable :table-config="tableConfig">
        <template #options="scopeSlot">
            <div class="btn-group">
                <span @click="dataLineageEvent(scopeSlot.row)">血缘</span>
                <span @click="editEvent(scopeSlot.row)">备注</span>
            </div>
        </template>
    </BlockTable>
    <DataLineage :isCode="true" ref="dataLineageRef"></DataLineage>
</template>

<script lang="ts" setup>
import { ref, onMounted, defineEmits, defineProps, reactive } from 'vue'
import { GetTableCodeInfo, GetDataLineageByCode  } from '@/services/metadata-page.service';
import DataLineage from '../data-lineage/index.vue'

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

const guid = function () {
    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
    }
    return S4() + S4() + '-' + S4() + '-' + S4() + '-' + S4() + '-' + S4() + S4() + S4()
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
        width: 80,
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
        width: 80,
        fixed: 'right'
    }
]

const dataLineageRef = ref<any>(null)
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

function dataLineageEvent(data: any) {
    dataLineageRef.value.showModal(data, (params?: any) => {
        return new Promise((resolve: any, reject: any) => {
            let requestParams = {
                dbId: data.datasourceId,
                tableName: data.tableName,
                columnName: data.columnName,
                lineageType: 'SON'
            }
            if (params) {
                requestParams.dbId = params.data.dbId
                requestParams.tableName = params.data.tableName,
                requestParams.columnName = params.data.columnName,
                requestParams.lineageType = params.lineageType
            }
            GetDataLineageByCode(requestParams).then((res: any) => {
                resolve(getFinalData(res.data, 'code'))
            }).catch((error: any) => {
                reject(error)
            })
        })
    }, 'code')
}

// 递归格式化树节点数据
function getFinalData(node: any, pageType: string) {
    node.pageType = pageType

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
    return node
}

onMounted(() => {
    tableConfig.colConfigs = hiveCol
    initData()
})
</script>
