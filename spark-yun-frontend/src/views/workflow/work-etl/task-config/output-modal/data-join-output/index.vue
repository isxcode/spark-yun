<template>
    <div class="data-join-output">
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
                    <span>【{{ getNodeName(scopeSlot.row.fromAliaCode) }}】{{ scopeSlot.row.fromColName }}</span>
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
        <!-- 添加字段 -->
        <add-code ref="addCodeRef"></add-code>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineEmits, computed, onMounted, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { TypeList, ConfigRules, TableConfig } from './config.ts'
import AddCode from './add-code/index.vue'

interface Option {
    label: string
    value: string
    inputSize?: number
}

const props = defineProps<{
    modelValue: any,
    preNodes: any,
    nodeFormData?: any
}>()
const emit = defineEmits(['update:modelValue'])

const mainTableFields = ref<Option[]>()
const tableFields = ref<Option[]>()
const addCodeRef = ref()

const rules = reactive<FormRules>({
    mainAliaCode: [{ required: true, message: '请选择主表', trigger: ['blur', 'change'] }],
    joinWay: [{ required: true, message: '请选择关联', trigger: ['blur', 'change'] }],
    joinAliaCode: [{ required: true, message: '请选择输入表', trigger: ['blur', 'change'] }],
    joinType: [{ required: true, message: '请选择关联关系', trigger: ['blur', 'change'] }],
    joinLeftColumn: [{ required: true, message: '请选择主表字段', trigger: ['blur', 'change'] }],
    joinCondition: [{ required: true, message: '请选择条件', trigger: ['blur', 'change'] }],
    joinRightColumn: [{ required: true, message: '请选择字段', trigger: ['blur', 'change'] }],
    joinColumn: [{ required: true, message: '请选择字段', trigger: ['blur', 'change'] }],
    joinValue: [{ required: true, message: '请输入字段值', trigger: ['blur', 'change'] }],
    joinSql: [{ required: true, message: '请输入sql', trigger: ['blur', 'change'] }],
})
const tableConfig = reactive(TableConfig)

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

function refreshFields() {
    const mainAliaCode = props.nodeFormData?.mainAliaCode
    if (!mainAliaCode || !props.preNodes || !props.preNodes.length) {
        // 没有选择主表，清空字段
        tableConfig.tableData = []
        isAllChecked.value = false
        return
    }
    const mainNode = props.preNodes.find((n: any) => n.data.nodeConfigData.aliaCode === mainAliaCode)
    if (!mainNode) {
        tableConfig.tableData = []
        isAllChecked.value = false
        return
    }
    const nodeData = mainNode.data.nodeConfigData
    const newFields: any[] = []
    if (nodeData.outColumnList) {
        nodeData.outColumnList.filter((item: any) => item.checked !== false).forEach((col: any) => {
            newFields.push({
                colName: col.colName,
                colType: col.colType,
                fromAliaCode: nodeData.aliaCode,
                fromColName: col.colName,
                remark: col.remark || '',
                checked: true
            })
        })
    }
    tableConfig.tableData = newFields
    isAllChecked.value = newFields.length > 0
}

onMounted(() => {
    if (formData.value && formData.value.length) {
        tableConfig.tableData = formData.value.map((item: any) => ({
            ...item,
            checked: item.checked !== false
        }))
        updateAllChecked()
    } else {
        refreshFields()
    }
})

function getTableData() {
    return tableConfig.tableData
}

defineExpose({
    refreshFields,
    getTableData
})
</script>

<style lang="scss">
.data-join-output {
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
