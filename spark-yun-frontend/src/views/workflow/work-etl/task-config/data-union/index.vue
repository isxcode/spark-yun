<template>
    <div class="config-components data-join">
        <el-form-item prop="mainAliaCode" class="form-item-top" label="主表" :rules="rules.mainAliaCode">
            <el-select
                v-model="formData.mainAliaCode"
                filterable
                clearable
                placeholder="请选择"
                @change="tableChangeEvent($event, formData.unionEtl)"
            >
                <el-option
                    v-for="item in tableNameList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
            </el-select>
        </el-form-item>
        <div class="config-label">
            <span>关联配置</span>
            <span class="add-btn">
                <el-icon @click="addNewOption">
                    <CirclePlus />
                </el-icon>
            </span>
        </div>
        <div class="form-options__list" v-for="(item, i) in formData.unionEtl">
            <el-icon v-if="formData.unionEtl.length > 1" class="remove-btn" @click="removeItem(index)">
                <CircleClose />
            </el-icon>
            <div class="list-item-row">
                <el-form-item :prop="`unionEtl[${i}].unionWay`" :rules="rules.unionWay" class="form-item-top" label="连接方式">
                    <el-select v-model="formData.unionEtl[i].unionWay" placeholder="请选择">
                        <el-option label="去重合并" value="UNION"/>
                        <el-option label="并集合并" value="UNION_ALL"/>
                    </el-select>
                </el-form-item>
                <el-form-item :prop="`unionEtl[${i}].aliaCode`" :rules="rules.aliaCode" class="form-item-top" label="表名">
                    <el-select v-model="formData.unionEtl[i].aliaCode" placeholder="请选择">
                        <el-option
                            v-for="item in tableNameList"
                            :disabled="formData.mainAliaCode === item.value"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        />
                    </el-select>
                </el-form-item>
            </div>
        </div>
        <div class="table-container" style="height: 314px;">
            <BlockTable
              :table-config="tableConfig"
            >
                <template #options="scopeSlot">
                    <div class="btn-group">
                        <span>备注</span>
                    </div>
                </template>
            </BlockTable>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineEmits, computed, onMounted, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { TypeList, ConfigRules, TableConfig } from './config.ts'

interface Option {
    label: string
    value: string
    inputSize?: number
}

const props = defineProps<{
    modelValue: any,
    incomeNodes: any
}>()
const emit = defineEmits(['update:modelValue'])

const rules = reactive<FormRules>({
    mainAliaCode: [{ required: true, message: '请选择主表', trigger: ['blur', 'change'] }],
    aliaCode: [{ required: true, message: '请选择表', trigger: ['blur', 'change'] }],
    unionWay: [{ required: true, message: '请选择连接方式', trigger: ['blur', 'change'] }]
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

const tableNameList = computed(() => {
    if (props.incomeNodes && props.incomeNodes.length) {
        return props.incomeNodes.map((node: any) => {
            const currentNodeData = node.data.nodeConfigData
            return {
                label: `${currentNodeData.name}-${currentNodeData.inputEtl.tableName}`,
                value: currentNodeData.inputEtl.tableName
            }
        })
    } else {
        return []
    }
})

function addNewOption() {
    formData.value.unionEtl.push({
        unionWay: 'UNION',
        aliaCode: ''
    })
}
function removeItem(index: number) {
    formData.value.unionEtl.splice(index, 1)
}

function tableChangeEvent(e: string, list: any[]) {
    list.forEach((data: any) => {
        data.aliaCode = ''
    })
}

onMounted(() => {
    tableConfig.tableData = []
    if (props.incomeNodes && props.incomeNodes[0]) {
        tableConfig.tableData = props.incomeNodes[0].data.nodeConfigData.outColumnList.map((column) => {
            return {
                colName: column.colName,
                colType: column.colType,
                remark: column.remark
            }
        })
        formData.value.outColumnList = props.incomeNodes[0].data.nodeConfigData.outColumnList.map((column) => {
            return {
                colName: column.colName,
                colType: column.colType,
                remark: column.remark
            }
        })
        formData.value.inputEtl = props.incomeNodes[0].data.nodeConfigData.inputEtl
    }
})
</script>

<style lang="scss">
.config-components {
    padding:  12px 20px;
    box-sizing: border-box;
    &.data-join {
        .config-label {
            font-size: 13px;
            display: flex;
            align-items: center;
            color: #606266;
            .add-btn {
                color: getCssVar('color', 'primary');
                cursor: pointer;
                margin-left: 4px;
                margin-top: 1px;
                font-size: 16px;
            }
        }
        .form-options__list {
            width: 100%;
            margin-top: 12px;
            border: 1px solid #e9eaec;
            border-radius: 4px;
            padding: 0px 12px;
            box-sizing: border-box;
            position: relative;

            .list-item-row {
                display: flex;
                .el-form-item {
                    width: 100%;
                    margin-right: 16px;
                }
            }

            .remove-btn {
                position: absolute;
                right: 8px;
                top: 32px;
                color: getCssVar('color', 'primary');
                cursor: pointer;
            }
            .form-options-ul__list {
                width: 100%;
            }

            .form-options__item {
                display: flex;
                margin-bottom: 8px;

                .el-form-item {
                    width: 100%;
                    margin-bottom: 12px;
                }
                .el-form-item+.el-form-item {
                    margin-left: 4px;
                }

                .option-btn {
                    display: flex;
                    height: 32px;
                    align-items: center;
                    justify-content: flex-end;
                    min-width: 20px;

                    .remove {
                        color: red;
                        cursor: pointer;

                        &:hover {
                            color: getCssVar('color', 'primary');
                        }
                    }

                    .move {
                        color: getCssVar('color', 'primary');

                        &:active {
                            cursor: move;
                        }
                    }
                }
            }
        }
        .table-container {
            margin-top: 20px;
        }
    }
    .el-form-item {
        .el-form-item__content {
            justify-content: flex-end;
            .el-select {
                width: 100%;
            }
        }
    }
}
</style>
