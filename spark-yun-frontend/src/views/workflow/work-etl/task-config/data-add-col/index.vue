<template>
    <div class="config-components">
        <el-form-item>
            <el-button type="primary" @click="addNewCode">添加</el-button>
        </el-form-item>
        <div style="max-height: 444px;">
            <BlockTable
              :table-config="tableConfig"
            >
                <template #options="scopeSlot">
                    <div class="btn-group">
                        <el-dropdown trigger="click">
                            <el-icon class="option-more" @click.stop>
                                <MoreFilled />
                            </el-icon>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item @click="editCode(scopeSlot.row)">编辑</el-dropdown-item>
                                    <el-dropdown-item @click="removeCode(scopeSlot.row)">删除</el-dropdown-item>
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
import { ref, defineProps, defineEmits, computed, onMounted, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { TypeList, ConfigRules, TableConfig } from './config.ts'
import AddCode from './add-code/index.vue'

interface codeParam {
    colName: string
    colType: string
    remark: string
}

const props = defineProps<{
    modelValue: any,
    incomeNodes: any
}>()
const emit = defineEmits(['update:modelValue'])

const addCodeRef = ref()
const tableConfig = reactive(TableConfig)

const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})


// 删除来源编码
function removeCode(row: codeParam) {
    ElMessageBox.confirm('确定删除该字段吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        formData.value.outColumnList = formData.value.outColumnList.filter(item => item.colName !== row.colName)
        tableConfig.tableData = formData.value.outColumnList
    })
}

function addNewCode() {
    addCodeRef.value.showModal((params: any) => {
        formData.value.outColumnList.push({ ...params })
        tableConfig.tableData = formData.value.outColumnList
    })
}
function editCode(row: codeParam) {
    addCodeRef.value.showModal((formData: codeParam) => {
        row.colName = formData.colName
        row.colType = formData.colType
        row.remark = formData.remark
    }, row)
}

onMounted(() => {
    // tableConfig.tableData = formData.value.outColumnList
    tableConfig.tableData = []
    if (props.incomeNodes && props.incomeNodes[0]) {
        formData.value.outColumnList = props.incomeNodes[0].data.nodeConfigData.outColumnList
        tableConfig.tableData = formData.value.outColumnList
        formData.value.inputEtl = props.incomeNodes[0].data.nodeConfigData.inputEtl
    }
})
</script>

<style lang="scss">
.config-components {
    padding:  12px 20px;
    box-sizing: border-box;
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
            // position: absolute;
            // right: 4px;
            // top: 13px;

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
