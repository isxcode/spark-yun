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
                    @keyup.enter="initData()"
                />
            </div>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
            <div class="zqy-table">
                <BlockTable
                    :table-config="tableConfig"
                >
                    <template #booleanTag="scopeSlot">
                        <el-checkbox disabled v-model="scopeSlot.row[scopeSlot.column.property]" true-label="ENABLE" false-label="DISABLE" />
                    </template>
                </BlockTable>
            </div>
        </LoadingPage>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, defineEmits } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { GetModelFieldList } from '@/services/data-model.service'
import LoadingPage from '@/components/loading/index.vue'

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
    // pagination?: Pagination
    loading?: boolean
}

const colConfigs: colConfig[] = [
    {
        prop: 'name',
        title: '字段',
        minWidth: 175,
        showOverflowTooltip: true
    },
    {
        prop: 'columnName',
        title: '字段名',
        minWidth: 170,
        showOverflowTooltip: true
    },
    {
        prop: 'columnFormatName',
        title: '字段标准',
        minWidth: 100,
        showOverflowTooltip: true
    },
    {
        prop: 'columnType',
        title: '字段精度',
        minWidth: 100,
        showOverflowTooltip: true
    },
    {
        prop: 'isNull',
        title: '可为空',
        minWidth: 100,
        customSlot: 'booleanTag',
        showOverflowTooltip: true,
        align: 'center'
    },
    {
        prop: 'isDuplicate',
        title: '可重复',
        minWidth: 100,
        customSlot: 'booleanTag',
        showOverflowTooltip: true,
        align: 'center'
    },
    {
        prop: 'isPrimary',
        title: '是否主键',
        minWidth: 100,
        customSlot: 'booleanTag',
        showOverflowTooltip: true,
        align: 'center'
    },
    {
        prop: 'isPartition',
        title: '是否分区键',
        minWidth: 100,
        customSlot: 'booleanTag',
        showOverflowTooltip: true,
        align: 'center'
    },
    {
        prop: 'defaultValue',
        title: '默认值',
        minWidth: 120
    },
    {
        prop: 'remark',
        title: '备注',
        minWidth: 170
    }
]

const modelConfig = reactive<any>({
    title: '模型字段',
    visible: false,
    width: '80%',
    customClass: 'data-field-detail',
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
const keyword = ref<string>('')
const tableConfig: TableConfig = reactive({
    tableData: [],
    colConfigs: colConfigs,
    // pagination: {
    //     currentPage: 1,
    //     pageSize: 10,
    //     total: 0
    // },
    seqType: 'seq',
    loading: false
})

function showModal(data: any): void {
    console.log('数据', data)
    info.value = data
    initData(1)
    modelConfig.visible = true
}

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    GetModelFieldList({
        page: 0,
        pageSize: 10000,
        searchKeyWord: keyword.value,
        modelId: info.value.id
    }).then((res: any) => {
        tableConfig.tableData = res.data.content
        loading.value = false
        tableConfig.loading = false
        networkError.value = false
    }).catch(() => {
        tableConfig.tableData = []
        loading.value = false
        tableConfig.loading = false
        networkError.value = true
    })
}

function closeEvent() {
    modelConfig.visible = false
}

function editEvent(data: any) {
    emit('editEvent', data)
}

function inputEvent(e: string) {
    if (e === '') {
        initData()
    }
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.data-field-detail {
    .modal-content {
        padding: 12px 20px 12px;
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
        .el-checkbox {
            &.is-disabled {
                .el-checkbox__inner {
                    background-color: #FFFFFF;
                }
                &.is-checked {
                    .el-checkbox__inner {
                        background-color: getCssVar('color', 'primary');
                        border-color: getCssVar('color', 'primary');
                        &::after {
                            border-color: #FFFFFF;
                        }
                    }
                }
            }
        }
    }
}
</style>