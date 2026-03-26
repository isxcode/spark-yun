<template>
    <div class="data-add-col-output">
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
                <template #fromSource="scopeSlot">
                    <span v-if="scopeSlot.row.fromAliaCode">【{{ getNodeName(scopeSlot.row.fromAliaCode) }}】{{ scopeSlot.row.fromColName }}</span>
                    <span v-else>【手动添加】{{ scopeSlot.row.colName }}</span>
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
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import AddCode from './add-code/index.vue'

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
            title: '来源',
            minWidth: 200,
            showOverflowTooltip: true,
            customSlot: 'fromSource'
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

onMounted(() => {
    // 上游节点的输出字段
    const preFields: any[] = []
    if (props.preNodes && props.preNodes[0]) {
        const fromAliaCode = props.preNodes[0].data.nodeConfigData.aliaCode || ''
        ;(props.preNodes[0].data.nodeConfigData.outColumnList || [])
            .filter((item: any) => item.checked !== false)
            .forEach((column: any) => {
                preFields.push({
                    colName: column.colName,
                    colType: column.colType,
                    fromAliaCode: fromAliaCode,
                    fromColName: column.colName,
                    remark: column.remark,
                    checked: true
                })
            })
    }

    // addColEtl 中新增的字段
    const addColFields: any[] = []
    const addColEtl = props.nodeFormData?.addColEtl || []
    addColEtl.forEach((item: any) => {
        if (item.colName) {
            addColFields.push({
                colName: item.colName,
                colType: item.colType || '',
                fromAliaCode: item.fromAliaCode || '',
                fromColName: item.fromColName || item.colName,
                remark: item.remark || '',
                checked: true
            })
        }
    })

    if (formData.value && formData.value.length) {
        // 已有保存数据，合并新增的 addColEtl 字段（去重）
        const existingNames = new Set(formData.value.map((item: any) => item.colName))
        const merged = formData.value.map((item: any) => ({
            ...item,
            checked: item.checked !== false
        }))
        addColFields.forEach((field: any) => {
            if (!existingNames.has(field.colName)) {
                merged.push(field)
            }
        })
        tableConfig.tableData = merged
    } else {
        // 无保存数据，使用上游字段 + 新增字段
        tableConfig.tableData = [...preFields, ...addColFields]
    }
    updateAllChecked()
})

function refreshFields() {
    // 上游节点的输出字段
    const preFields: any[] = []
    if (props.preNodes && props.preNodes[0]) {
        const fromAliaCode = props.preNodes[0].data.nodeConfigData.aliaCode || ''
        ;(props.preNodes[0].data.nodeConfigData.outColumnList || [])
            .filter((item: any) => item.checked !== false)
            .forEach((column: any) => {
                preFields.push({
                    colName: column.colName,
                    colType: column.colType,
                    fromAliaCode: fromAliaCode,
                    fromColName: column.colName,
                    remark: column.remark,
                    checked: true
                })
            })
    }

    // addColEtl 中新增的字段
    const addColFields: any[] = []
    const addColEtl = props.nodeFormData?.addColEtl || []
    addColEtl.forEach((item: any) => {
        if (item.colName) {
            addColFields.push({
                colName: item.colName,
                colType: item.colType || '',
                fromAliaCode: item.fromAliaCode || '',
                fromColName: item.fromColName || item.colName,
                remark: item.remark || '',
                checked: true
            })
        }
    })

    tableConfig.tableData = [...preFields, ...addColFields]
    updateAllChecked()
}

function getTableData() {
    return tableConfig.tableData
}

defineExpose({
    refreshFields,
    getTableData
})
</script>

<style lang="scss">
.data-add-col-output {
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
