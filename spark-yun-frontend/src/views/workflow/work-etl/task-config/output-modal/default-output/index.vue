<template>
    <div class="default-output">
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
import { ref, defineEmits, computed, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AddCode from '../data-join-output/add-code/index.vue'
import { GetTableColumnsByTableId } from '@/services/data-sync.service'

const props = defineProps<{
    modelValue: any,
    preNodes: any,
    nodeFormData?: any
}>()
const emit = defineEmits(['update:modelValue'])

const addCodeRef = ref()
const hideOperations = computed(() => ['DATA_INPUT', 'DATA_UNION'].includes(props.nodeFormData?.type))

const baseColConfigs = [
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
]

const tableConfig = reactive({
    tableData: [],
    colConfigs: hideOperations.value ? baseColConfigs.slice(0, -1) : baseColConfigs,
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

// 当preNodes为空时（如数据输入节点），使用节点自身信息构造虚拟节点
const effectivePreNodes = computed(() => {
    if (props.preNodes && props.preNodes.length) {
        return props.preNodes
    }
    if (props.nodeFormData && props.nodeFormData.aliaCode) {
        return [{
            data: {
                nodeConfigData: props.nodeFormData
            }
        }]
    }
    return []
})

function getNodeName(aliaCode: string): string {
    if (!effectivePreNodes.value.length) return ''
    const node = effectivePreNodes.value.find((n: any) => n.data.nodeConfigData.aliaCode === aliaCode)
    return node ? node.data.nodeConfigData.name : ''
}

function addNewCode() {
    addCodeRef.value.showModal((params: any) => {
        tableConfig.tableData.push({ ...params, checked: true })
    }, null, effectivePreNodes.value)
}

function editCode(row: any, index: number) {
    addCodeRef.value.showModal((params: any) => {
        row.colName = params.colName
        row.fromAliaCode = params.fromAliaCode
        row.fromColName = params.fromColName
        row.colType = params.colType
        row.remark = params.remark
    }, row, effectivePreNodes.value)
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

// 刷新字段列表
function refreshFields() {
    const nodeType = props.nodeFormData?.type
    const inputEtl = props.nodeFormData?.inputEtl

    // 数据输入节点：通过接口获取字段
    if (nodeType === 'DATA_INPUT') {
        if (!inputEtl || !inputEtl.datasourceId || !inputEtl.tableName) {
            ElMessage.warning('请先配置数据源和表')
            return
        }
        tableConfig.loading = true
        GetTableColumnsByTableId({
            dataSourceId: inputEtl.datasourceId,
            tableName: inputEtl.tableName
        }).then((res: any) => {
            const defaultFromAliaCode = getDefaultFromAliaCode()
            tableConfig.tableData = (res.data.columns || []).map((column: any) => ({
                colName: column.name,
                colType: column.type,
                remark: column.columnComment,
                fromAliaCode: defaultFromAliaCode,
                fromColName: column.name,
                checked: true
            }))
            isAllChecked.value = true
            tableConfig.loading = false
        }).catch((err: any) => {
            console.error(err)
            tableConfig.loading = false
        })
        return
    }

    // 数据合并节点：以选中的主表为主
    if (nodeType === 'DATA_UNION') {
        const mainAliaCode = props.nodeFormData?.mainAliaCode
        if (!mainAliaCode || !props.preNodes || !props.preNodes.length) {
            ElMessage.warning('请先选择主表')
            return
        }
        const mainNode = props.preNodes.find((n: any) => n.data.nodeConfigData.aliaCode === mainAliaCode)
        if (!mainNode) {
            ElMessage.warning('未找到主表节点')
            return
        }
        const nodeData = mainNode.data.nodeConfigData
        const newFields = (nodeData.outColumnList || [])
            .filter((item: any) => item.checked !== false)
            .map((col: any) => ({
                colName: col.colName,
                colType: col.colType,
                fromAliaCode: nodeData.aliaCode,
                fromColName: col.colName,
                remark: col.remark || '',
                checked: true
            }))
        tableConfig.tableData = newFields
        isAllChecked.value = newFields.length > 0
        return
    }

    // 其他节点（数据过滤、数据转换等）：从上游节点获取字段
    if (props.preNodes && props.preNodes.length) {
        const sourceNode = props.preNodes[0]
        const nodeData = sourceNode.data.nodeConfigData
        const newFields = (nodeData.outColumnList || [])
            .filter((item: any) => item.checked !== false)
            .map((col: any) => ({
                colName: col.colName,
                colType: col.colType,
                fromAliaCode: nodeData.aliaCode,
                fromColName: col.colName,
                remark: col.remark || '',
                checked: true
            }))
        tableConfig.tableData = newFields
        isAllChecked.value = newFields.length > 0
    } else {
        ElMessage.warning('无上游节点')
    }
}

// 获取默认来源aliaCode
function getDefaultFromAliaCode(): string {
    // 数据合并节点：来源使用主表节点的aliaCode
    if (props.nodeFormData?.mainAliaCode && props.preNodes && props.preNodes.length) {
        const mainNode = props.preNodes.find((n: any) =>
            n.data.nodeConfigData.inputEtl?.tableName === props.nodeFormData.mainAliaCode
                || n.data.nodeConfigData.aliaCode === props.nodeFormData.mainAliaCode
        )
        if (mainNode) {
            return mainNode.data.nodeConfigData.aliaCode
        }
    }
    // 数据输入等节点：来源使用自身aliaCode
    return props.nodeFormData?.aliaCode || ''
}

onMounted(() => {
    const defaultFromAliaCode = getDefaultFromAliaCode()
    tableConfig.tableData = formData.value.map((item: any) => ({
        ...item,
        fromAliaCode: item.fromAliaCode || defaultFromAliaCode,
        fromColName: item.fromColName || item.colName,
        checked: item.checked !== false
    }))
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
.default-output {
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
