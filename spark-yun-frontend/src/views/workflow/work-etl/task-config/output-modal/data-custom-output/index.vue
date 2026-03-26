<template>
    <div class="data-custom-output">
        <div style="max-height: 444px;">
            <BlockTable
                :table-config="tableConfig"
                @rowDragendEvent="onRowDragend"
            >
                <template #checkboxHeaderSlot>
                    <el-checkbox :model-value="isAllChecked" @change="toggleSelectAll" />
                </template>
                <template #checkboxSlot="scopeSlot">
                    <el-checkbox v-model="scopeSlot.row.checked" @change="updateAllChecked" />
                </template>

                <template #options="scopeSlot">
                    <div class="btn-group">
                        <el-dropdown trigger="click">
                            <el-icon class="option-more" @click.stop>
                                <MoreFilled />
                            </el-icon>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item @click="addNewCode">添加</el-dropdown-item>
                                    <el-dropdown-item @click="editCode(scopeSlot.row, scopeSlot.index)">编辑</el-dropdown-item>
                                    <el-dropdown-item @click="removeCode(scopeSlot)">删除</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                </template>
            </BlockTable>
        </div>
        <add-code ref="addCodeRef"></add-code>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineEmits, computed, onMounted, reactive } from 'vue'
import AddCode from './add-code/index.vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { ParseCustomSqlFunction } from '@/services/etl-config.service'

const props = defineProps<{
    modelValue: any,
    preNodes: any,
    nodeFormData?: any
}>()
const emit = defineEmits(['update:modelValue'])

const addCodeRef = ref()
const tableConfig = reactive({
    tableData: [],
    colConfigs: [
        {
            title: '',
            customSlot: 'checkboxSlot',
            customHeaderSlot: 'checkboxHeaderSlot',
            width: 44,
            align: 'center',
        },
        {
            prop: 'colName',
            title: '字段名',
            minWidth: 120,
            showOverflowTooltip: true,
            dragSort: true
        },
        {
            prop: 'colType',
            title: '类型',
            minWidth: 80,
            showOverflowTooltip: true
        },

        {
            prop: 'remark',
            title: '备注',
            minWidth: 100,
            showOverflowTooltip: true
        },
        {
            title: '操作',
            align: 'center',
            customSlot: 'options',
            width: 80,
            fixed: 'right'
        }
    ],
    seqType: '',
    loading: false
})

const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

const isAllChecked = ref(true)

function updateAllChecked() {
    isAllChecked.value = tableConfig.tableData.length > 0 && tableConfig.tableData.every((item: any) => item.checked)
}

function toggleSelectAll(val: boolean) {
    tableConfig.tableData.forEach((item: any) => {
        item.checked = val
    })
    isAllChecked.value = val
}

function getNodeName(aliaCode: string): string {
    if (!props.preNodes) return ''
    const node = props.preNodes.find((n: any) => n.data.nodeConfigData.aliaCode === aliaCode)
    return node ? node.data.nodeConfigData.name : ''
}

function addNewCode() {
    addCodeRef.value.showModal((params: any) => {
        tableConfig.tableData.push({ ...params, checked: true })
    }, null, props.preNodes)
}

function editCode(row: any, index: number) {
    addCodeRef.value.showModal((params: any) => {
        row.colName = params.colName
        row.fromAliaCode = params.fromAliaCode
        row.fromColName = params.fromColName
        row.colType = params.colType
        row.remark = params.remark
    }, row, props.preNodes)
}

// 删除来源编码
function onRowDragend(e: any) {
    tableConfig.tableData = e.tableData
    updateAllChecked()
}

function removeCode(scopeSlot: any) {
    ElMessageBox.confirm('确定删除该字段吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        tableConfig.tableData.splice(scopeSlot.index, 1)
    })
}

onMounted(() => {
    tableConfig.tableData = formData.value.map((item: any) => ({
        ...item,
        checked: item.checked !== false
    }))
})

const refreshLoading = ref(false)

function refreshFields() {
    const sql = props.nodeFormData?.customSqlEtl?.sql
    if (!sql) {
        ElMessage.error('请先输入SQL')
        return
    }
    refreshLoading.value = true
    ParseCustomSqlFunction({ sql }).then((res: any) => {
        const newColumns = (res.data.columns || []).map((column: any) => ({
            colName: column.name,
            colType: column.type,
            remark: column.columnComment,
            checked: true
        }))
        tableConfig.tableData = newColumns
        refreshLoading.value = false
    }).catch((err: any) => {
        console.error(err)
        ElMessage.error(err || '解析失败')
        refreshLoading.value = false
    })
}

function getTableData() {
    return tableConfig.tableData
}

defineExpose({
    getTableData,
    refreshFields
})
</script>

<style lang="scss">
.data-custom-output {
    padding:  12px 20px;
    box-sizing: border-box;
    .el-form-item {
        .el-form-item__content {
            justify-content: flex-end;
            .el-select {
                width: 100%;
            }
        }
    }
    .btn-group {
        display: flex;
        justify-content: space-around;
        align-items: center;
        span {
            cursor: pointer;
            color: getCssVar('color', 'primary', 'light-5');
            &:hover {
                color: getCssVar('color', 'primary');;
            }
        }
        .el-dropdown {
            .option-more {
                font-size: 14px;
                transform: rotate(90deg);
                cursor: pointer;
                color: getCssVar('color', 'info');
            }
        }
    }
}
</style>
