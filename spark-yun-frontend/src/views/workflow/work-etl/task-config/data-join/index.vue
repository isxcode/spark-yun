<template>
    <div class="config-components data-join">
        <el-form-item prop="mainAliaCode" class="form-item-top" label="主表" :rules="rules.mainAliaCode">
            <el-select
                v-model="formData.mainAliaCode"
                filterable
                clearable
                placeholder="请选择"
                @change="tableChangeEvent($event, formData.joinEtl, tableNameList)"
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
        <div class="form-options__list" v-for="(item, i) in formData.joinEtl">
            <el-icon v-if="formData.joinEtl.length > 1" class="remove-btn" @click="removeItem(index)">
                <CircleClose />
            </el-icon>
            <el-form-item :prop="`joinEtl[${i}].joinWay`" :rules="rules.joinWay" class="form-item-top" label="关联">
                <el-select v-model="formData.joinEtl[i].joinWay" placeholder="请选择">
                    <el-option label="左连接" value="LEFT_JOIN"/>
                    <el-option label="右连接" value="RIGHT_JOIN"/>
                    <el-option label="内连接" value="INNER_JOIN"/>
                    <el-option label="外连接" value="OUTER_JOIN"/>
                </el-select>
            </el-form-item>
            <el-form-item class="form-item-top" label="条件">
                <span class="add-btn">
                    <el-icon @click="addNewCondition(formData.joinEtl[i].joinConditions)">
                        <CirclePlus />
                    </el-icon>
                </span>
                <div class="form-options-ul__list" v-if="formData.joinEtl[i].joinConditions && formData.joinEtl[i].joinConditions.length">
                    <div class="form-options__item" v-for="(element, index) in formData.joinEtl[i].joinConditions">
                        <el-form-item :prop="`joinEtl[${i}].joinConditions[${index}].joinAliaCode`" :rules="rules.joinAliaCode">
                            <el-select
                                v-model="element.joinAliaCode"
                                filterable
                                clearable
                                placeholder="输入表"
                            >
                                <el-option
                                    v-for="item in tableNameList"
                                    :disabled="formData.mainAliaCode === item.value"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value"
                                />
                            </el-select>
                        </el-form-item>
                        <el-form-item :prop="`joinEtl[${i}].joinConditions[${index}].joinType`" :rules="rules.joinType">
                            <el-select v-model="element.joinType" @change="transformChangeEvent($event, element)">
                                <el-option label="字段关联" value="COLUMN_JOIN"/>
                                <el-option label="过滤关联" value="CONDITION_JOIN"/>
                                <el-option label="自定义关联" value="CUSTOM_JOIN"/>
                            </el-select>
                        </el-form-item>
                        <template v-if="element.joinType === 'COLUMN_JOIN'">
                            <el-form-item :prop="`joinEtl[${i}].joinConditions[${index}].joinLeftColumn`" :rules="rules.joinLeftColumn">
                                <el-select
                                    v-model="element.joinLeftColumn"
                                    filterable
                                    clearable
                                    placeholder="主表字段"
                                    @visible-change="getMainTableFields($event)"
                                >
                                    <el-option
                                        v-for="item in mainTableFields"
                                        :key="item.value"
                                        :label="item.label"
                                        :value="item.value"
                                    />
                                </el-select>
                            </el-form-item>
                            <el-form-item :prop="`joinEtl[${i}].joinConditions[${index}].joinCondition`" :rules="rules.joinCondition">
                                <el-select
                                    v-model="element.joinCondition"
                                    filterable
                                    clearable
                                    placeholder="条件"
                                >
                                    <el-option label="=" value="=" />
                                    <el-option label="<" value="<" />
                                    <el-option label=">" value=">" />
                                </el-select>
                            </el-form-item>
                            <el-form-item :prop="`joinEtl[${i}].joinConditions[${index}].joinRightColumn`" :rules="rules.joinRightColumn">
                                <el-select
                                    v-model="element.joinRightColumn"
                                    filterable
                                    clearable
                                    placeholder="字段"
                                    @visible-change="getTableFields($event, element)"
                                >
                                    <el-option
                                        v-for="item in tableFields"
                                        :key="item.value"
                                        :label="item.label"
                                        :value="item.value"
                                    />
                                </el-select>
                            </el-form-item>
                        </template>
                        <template v-if="element.joinType === 'CONDITION_JOIN'">
                            <el-form-item :prop="`joinEtl[${i}].joinConditions[${index}].joinColumn`" :rules="rules.joinColumn">
                                <el-select
                                    v-model="element.joinColumn"
                                    filterable
                                    clearable
                                    placeholder="请选择字段"
                                    @visible-change="getTableFields($event, element)"
                                >
                                    <el-option
                                        v-for="item in tableFields"
                                        :key="item.value"
                                        :label="item.label"
                                        :value="item.value"
                                    />
                                </el-select>
                            </el-form-item>
                            <el-form-item :prop="`joinEtl[${i}].joinConditions[${index}].joinCondition`" :rules="rules.joinCondition">
                                <el-select
                                    v-model="element.joinCondition"
                                    filterable
                                    clearable
                                    placeholder="条件"
                                >
                                    <el-option label="=" value="=" />
                                    <el-option label="<" value="<" />
                                    <el-option label=">" value=">" />
                                </el-select>
                            </el-form-item>
                            <el-form-item :prop="`joinEtl[${i}].joinConditions[${index}].joinValue`" :rules="rules.joinValue">
                                <el-input
                                    v-model="element.joinValue"
                                    clearable
                                    placeholder="请输入"
                                ></el-input>
                            </el-form-item>
                        </template>
                        <el-form-item v-if="element.joinType === 'CUSTOM_JOIN'" :prop="`joinEtl[${i}].joinConditions[${index}].joinSql`" :rules="rules.joinSql">
                            <el-input
                                v-model="element.joinSql"
                                clearable
                                placeholder="请输入"
                            ></el-input>
                        </el-form-item>
                        <!-- --------------- -->
                        <div class="option-btn">
                            <el-icon v-if="formData.joinEtl[i].joinConditions.length > 1" class="remove"
                                @click="removeCondition(formData.joinEtl[i].joinConditions, index)">
                                <CircleClose />
                            </el-icon>
                        </div>
                    </div>
                </div>
            </el-form-item>
        </div>
        <div class="table-container" style="height: 314px;">
            <el-form-item>
                <el-button type="primary" @click="addNewCode">添加</el-button>
            </el-form-item>
            <BlockTable
              :table-config="tableConfig"
            >
                <template #options="scopeSlot">
                    <div class="btn-group">
                        <span @click="removeCode(scopeSlot.row)">删除</span>
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
import { GetTableColumnsByTableId } from '@/services/data-sync.service'
import AddCode from './add-code/index.vue'

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

const tableNameList = computed(() => {
    if (props.incomeNodes && props.incomeNodes.length) {
        return props.incomeNodes.map((node: any) => {
            const currentNodeData = node.data.nodeConfigData
            return {
                label: `${currentNodeData.name}-${currentNodeData.inputEtl.tableName}`,
                value: currentNodeData.inputEtl.tableName,
                data: currentNodeData
            }
        })
    } else {
        return []
    }
})

function addNewOption() {
    formData.value.joinEtl.push({
        joinWay: 'LEFT_JOIN',
        joinAliaCode: '',
        joinConditions: [{
            joinType: 'COLUMN_JOIN',
            joinLeftColumn: '',
            joinCondition: '',
            joinRightColumn: '',
            joinAliaCode: '',
            joinColumn: '',
            joinValue: '',
            joinSql: ''
        }]
    })
}
function removeItem(index: number) {
    formData.value.joinEtl.splice(index, 1)
}

function addNewCondition(list: any[]) {
    list.push({
        joinType: 'COLUMN_JOIN',
        joinLeftColumn: '',
        joinCondition: '',
        joinRightColumn: '',
        joinAliaCode: '',
        joinColumn: '',
        joinValue: '',
        joinSql: ''
    })
}

function tableChangeEvent(e: string, list: any[], optionsList?: any[]) {
    list.forEach((data: any) => {
        if (data.joinConditions && data.joinConditions.length) {
            data.joinConditions.forEach(cc => {
                cc.joinAliaCode = ''
                cc.joinLeftColumn = ''
                cc.joinRightColumn = ''
            })
        }
    })

    if (optionsList && optionsList.length) {
        const currentItem = optionsList.find(dd => dd.value === e)
        if (currentItem && currentItem) {
            formData.value.outColumnList = currentItem.data.outColumnList.map(item => {
                return {
                    colName: item.colName,
                    fromAliaCode: currentItem.data.name,
                    fromColName: item.colName,
                    colType: item.colType,
                    remark: item.remark
                }
            })
            tableConfig.tableData = formData.value.outColumnList
        }
    }
}

function removeCondition(list: any[], index: number) {
    list.splice(index, 1)
}

function transformChangeEvent(e: string, element: any) {
    element.inputValue = []
    element.transformSql = ''
}

function functionSelectEvent(e: string, element: any) {
    if (e) {
        const currentItem = funcOptions.value.find((item: Option) => item.value === e)
        element.inputValue = []
        if (currentItem.inputSize) {
            for (let i = 0; i < currentItem.inputSize; i++) {
                element.inputValue.push('')
            }
        }
    } else {
        element.inputValue = []
    }
}

function getMainTableFields(e: boolean) {
    const currentItem = tableNameList.value.find(dd => dd.value === formData.value.mainAliaCode)
    if (e && currentItem.data.inputEtl && currentItem.data.inputEtl.datasourceId && currentItem.data.inputEtl.tableName) {
        GetTableColumnsByTableId({
            dataSourceId: currentItem.data.inputEtl.datasourceId,
            tableName: currentItem.data.inputEtl.tableName
        }).then((res: any) => {
            mainTableFields.value = (res.data.columns || []).map((column: any) => {
                return {
                    label: column.name,
                    value: column.name
                }
            })
        }).catch(err => {
            console.error(err)
        })
    }
}
function getTableFields(e: boolean, config: any) {
    const currentItem = tableNameList.value.find(dd => dd.value === config.joinAliaCode)
    if (e && currentItem.data.inputEtl && currentItem.data.inputEtl.datasourceId && currentItem.data.inputEtl.tableName) {
        GetTableColumnsByTableId({
            dataSourceId: currentItem.data.inputEtl.datasourceId,
            tableName: currentItem.data.inputEtl.tableName
        }).then((res: any) => {
            tableFields.value = (res.data.columns || []).map((column: any) => {
                return {
                    label: column.name,
                    value: column.name
                }
            })
        }).catch(err => {
            console.error(err)
        })
    }
}

function addNewCode() {
    const currentItem = tableNameList.value.find(dd => dd.value === formData.value.mainAliaCode)
    addCodeRef.value.showModal((params: any) => {
        formData.value.outColumnList.push({ ...params })
        tableConfig.tableData = formData.value.outColumnList
    }, null, currentItem.data)
}
// 删除来源编码
function removeCode(row: any) {
    ElMessageBox.confirm('确定删除该字段吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        formData.value.outColumnList = formData.value.outColumnList.filter(item => item.colName !== row.colName)
        tableConfig.tableData = formData.value.outColumnList
    })
}

onMounted(() => {
    tableConfig.tableData = formData.value.outColumnList
    // if (props.incomeNodes && props.incomeNodes[0]) {
    //     tableConfig.tableData = props.incomeNodes[0].data.nodeConfigData.outColumnList.map((column) => {
    //         return {
    //             colName: column.colName,
    //             colType: column.colType,
    //             remark: column.remark
    //         }
    //     })
    //     formData.value.outColumnList = props.incomeNodes[0].data.nodeConfigData.outColumnList.map((column) => {
    //         return {
    //             colName: column.colName,
    //             colType: column.colType,
    //             remark: column.remark
    //         }
    //     })
    //     formData.value.inputEtl = props.incomeNodes[0].data.nodeConfigData.inputEtl
    // }
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

            .remove-btn {
                position: absolute;
                right: 12px;
                top: 4px;
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
