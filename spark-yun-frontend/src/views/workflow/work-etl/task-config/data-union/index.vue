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
        <div class="form-options__list" v-for="(unionItem, i) in formData.unionEtl" :style="getGroupStyle(i)">
            <el-icon v-if="formData.unionEtl.length > 1" class="remove-block-btn" @click="removeItem(i)">
                <CircleClose />
            </el-icon>
            <div class="list-item-row">
                <el-form-item :prop="`unionEtl[${i}].unionWay`" :rules="rules.unionWay" class="form-item-top" label="连接方式">
                    <el-select v-model="formData.unionEtl[i].unionWay" placeholder="请选择" :filterable="true">
                        <el-option label="去重合并" value="UNION"/>
                        <el-option label="并集合并" value="UNION_ALL"/>
                    </el-select>
                </el-form-item>
                <el-form-item :prop="`unionEtl[${i}].aliaCode`" :rules="rules.aliaCode" class="form-item-top" label="表名">
                    <el-select v-model="formData.unionEtl[i].aliaCode" :filterable="true" placeholder="请选择">
                        <el-option
                            v-for="item in tableNameList"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        />
                    </el-select>
                </el-form-item>
            </div>
        </div>
        <!-- <div v-show="false" class="table-container" style="height: 314px;">
            <BlockTable
              :table-config="tableConfig"
            >
                <template #options="scopeSlot">
                    <div class="btn-group">
                        <span>备注</span>
                    </div>
                </template>
            </BlockTable>
        </div> -->
    </div>
</template>

<script lang="ts" setup>
import { ref, defineEmits, computed, onMounted, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { TypeList, ConfigRules, TableConfig } from './config.ts'
import { groupColorPalette } from '../data-filter/config.ts'

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
                label: currentNodeData.name,
                value: currentNodeData.aliaCode,
                data: currentNodeData
            }
        })
    } else {
        return []
    }
})

function getGroupStyle(index: number) {
    const palette = groupColorPalette[index % groupColorPalette.length]
    return {
        borderColor: palette.borderOuter,
        borderLeftColor: palette.border,
        borderLeftWidth: '3px',
        borderLeftStyle: 'solid',
        backgroundColor: palette.background
    }
}

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
    updateOutColumnList()
}

function updateOutColumnList() {
    const mainAliaCode = formData.value.mainAliaCode
    if (!mainAliaCode || !props.incomeNodes || !props.incomeNodes.length) {
        tableConfig.tableData = []
        formData.value.outColumnList = []
        return
    }
    // 如果已有保存的outColumnList，直接使用
    if (formData.value.outColumnList && formData.value.outColumnList.length) {
        tableConfig.tableData = formData.value.outColumnList
        return
    }
    const mainItem = tableNameList.value.find((item: any) => item.value === mainAliaCode)
    if (mainItem && mainItem.data.outColumnList) {
        const fields = (mainItem.data.outColumnList || []).filter((item: any) => item.checked !== false).map((column: any) => {
            return {
                colName: column.colName,
                colType: column.colType,
                remark: column.remark,
                checked: true
            }
        })
        tableConfig.tableData = fields
        formData.value.outColumnList = [...fields]
        formData.value.inputEtl = mainItem.data.inputEtl
    } else {
        tableConfig.tableData = []
        formData.value.outColumnList = []
    }
}

onMounted(() => {
    updateOutColumnList()
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
                    margin-right: 16px;
                    &:first-child {
                        flex: 0 0 30%;
                    }
                    &:last-child {
                        flex: 1;
                        margin-right: 0;
                    }
                }
            }

            .remove-block-btn {
                position: absolute;
                right: -8px;
                top: -8px;
                font-size: 16px;
                color: #c0c4cc;
                cursor: pointer;
                z-index: 1;
                background: #fff;
                border-radius: 50%;
                transition: color 0.2s;

                &:hover {
                    color: #f56c6c;
                }
            }
            .form-options-ul__list {
                width: 100%;
            }

            .form-options__item {
                display: flex;

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
        &.form-item-top {
            display: flex;
            flex-direction: column;
            .el-form-item__label {
                margin-bottom: 1px;
            }
        }
    }
}
</style>
