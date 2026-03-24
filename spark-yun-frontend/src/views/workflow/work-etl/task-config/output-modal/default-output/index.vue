<template>
    <div class="default-output">
        <div style="max-height: 444px;">
            <BlockTable
              :table-config="tableConfig"
            >
                <template #checkboxHeaderSlot>
                    <el-checkbox :model-value="isAllChecked" @change="toggleSelectAll" />
                </template>
                <template #checkboxSlot="scopeSlot">
                    <el-checkbox v-model="scopeSlot.row.checked" @change="updateAllChecked" />
                </template>
                <template #fromSource="scopeSlot">
                    <span>【{{ scopeSlot.row.fromAliaCode }}】{{ getNodeName(scopeSlot.row.fromAliaCode) }}【{{ scopeSlot.row.fromColName }}】</span>
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
            showOverflowTooltip: true
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
function removeCode(scopeSlot: any) {
    ElMessageBox.confirm('确定删除该字段吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        tableConfig.tableData.splice(scopeSlot.index, 1)
    })
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
