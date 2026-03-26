<template>
    <div class="config-components data-add-col">
        <el-form-item class="form-item-top">
            <div class="form-options__list" v-for="(element, index) in formData.addColEtl" :key="index" :style="getGroupStyle(index)">
                <el-icon class="remove-block-btn" @click="removeItem(index)">
                    <CircleClose />
                </el-icon>
                <el-form-item :prop="`addColEtl[${index}].addType`" :rules="rules.addType" label="字段类型">
                    <el-select v-model="element.addType" placeholder="请选择" @change="onAddTypeChange(element)">
                        <el-option label="来源表" value="SOURCE_TABLE" />
                        <el-option label="手动添加" value="MANUAL" />
                    </el-select>
                </el-form-item>
                <template v-if="element.addType === 'SOURCE_TABLE'">
                    <el-form-item :prop="`addColEtl[${index}].fromAliaCode`" :rules="rules.fromAliaCode" label="来源表">
                        <el-select
                            v-model="element.fromAliaCode"
                            filterable
                            clearable
                            placeholder="请选择"
                            @change="onSourceChange(element)"
                        >
                            <el-option
                                v-for="opt in tableNameList"
                                :key="opt.value"
                                :label="opt.label"
                                :value="opt.value"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item :prop="`addColEtl[${index}].fromColName`" :rules="rules.fromColName" label="关联字段名">
                        <el-select
                            v-model="element.fromColName"
                            filterable
                            clearable
                            placeholder="请选择"
                            @visible-change="getTableFields($event, element)"
                            @change="onFieldChange($event, element)"
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
                <el-form-item :prop="`addColEtl[${index}].colName`" :rules="rules.colName" label="字段名">
                    <el-input v-model="element.colName" clearable placeholder="请输入字段名"></el-input>
                </el-form-item>
                <el-form-item :prop="`addColEtl[${index}].colType`" :rules="rules.colType" label="类型">
                    <el-select v-model="element.colType" clearable filterable placeholder="请选择类型">
                        <el-option v-for="item in sparkTypeList" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item v-if="element.addType === 'MANUAL'" label="返回值">
                    <el-input v-model="element.defaultValue" clearable placeholder="返回值"></el-input>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="element.remark" clearable placeholder="请输入备注"></el-input>
                </el-form-item>
            </div>
            <div class="transform-condition-actions">
                <el-button link type="primary" size="small" @click="addNewField">
                    <el-icon><Plus /></el-icon>
                    字段
                </el-button>
            </div>
        </el-form-item>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineEmits, computed, onMounted, reactive } from 'vue'
import { FormRules } from 'element-plus'
import { groupColorPalette } from '../data-filter/config.ts'

const sparkTypeList = [
    { label: 'string(字符串)', value: 'string' },
    { label: 'int(整数)', value: 'int' },
    { label: 'long(长整数)', value: 'long' },
    { label: 'double(双精度浮点)', value: 'double' },
    { label: 'boolean(布尔)', value: 'boolean' },
    { label: 'date(日期)', value: 'date' },
    { label: 'timestamp(时间戳)', value: 'timestamp' }
]

const props = defineProps<{
    modelValue: any,
    incomeNodes: any
}>()
const emit = defineEmits(['update:modelValue'])

const tableFields = ref<any[]>([])

const rules = reactive<FormRules>({
    addType: [{ required: true, message: '请选择字段类型', trigger: ['blur', 'change'] }],
    colName: [{ required: true, message: '字段名不能为空', trigger: ['blur', 'change'] }],
    colType: [{ required: true, message: '类型不能为空', trigger: ['blur', 'change'] }],
    fromAliaCode: [{ required: true, message: '请选择来源表', trigger: ['blur', 'change'] }],
    fromColName: [{ required: true, message: '请选择关联字段名', trigger: ['blur', 'change'] }]
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
    }
    return []
})

const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
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

function onAddTypeChange(element: any) {
    element.fromAliaCode = ''
    element.fromColName = ''
    element.colName = ''
    element.colType = ''
    element.defaultValue = ''
}

function onSourceChange(element: any) {
    element.fromColName = ''
    element.colName = ''
    element.colType = ''
}

function getTableFields(e: boolean, element: any) {
    if (!e) return
    const currentItem = tableNameList.value.find((dd: any) => dd.value === element.fromAliaCode)
    if (currentItem && currentItem.data.outColumnList) {
        tableFields.value = (currentItem.data.outColumnList || []).filter((item: any) => item.checked !== false).map((column: any) => ({
            label: column.colName,
            value: column.colName,
            colType: column.colType
        }))
    }
}

function onFieldChange(val: string, element: any) {
    if (val) {
        const field = tableFields.value.find((f: any) => f.value === val)
        if (field) {
            element.colName = val
            element.colType = field.colType || ''
        }
    }
}

function addNewField() {
    if (!formData.value.addColEtl) {
        formData.value.addColEtl = []
    }
    formData.value.addColEtl.push({
        addType: 'SOURCE_TABLE',
        colName: '',
        colType: '',
        fromAliaCode: '',
        fromColName: '',
        defaultValue: '',
        remark: ''
    })
}

function removeItem(index: number) {
    formData.value.addColEtl.splice(index, 1)
}

onMounted(() => {
    if (props.incomeNodes && props.incomeNodes[0]) {
        if (!formData.value.outColumnList || !formData.value.outColumnList.length) {
            const fromAliaCode = props.incomeNodes[0].data.nodeConfigData.aliaCode || ''
            formData.value.outColumnList = props.incomeNodes[0].data.nodeConfigData.outColumnList.filter((item: any) => item.checked !== false).map((column: any) => {
                return {
                    colName: column.colName,
                    colType: column.colType,
                    fromAliaCode: fromAliaCode,
                    fromColName: column.colName,
                    remark: column.remark,
                    checked: true
                }
            })
        }
        formData.value.inputEtl = props.incomeNodes[0].data.nodeConfigData.inputEtl
    }
    if (!formData.value.addColEtl || !formData.value.addColEtl.length) {
        formData.value.addColEtl = [{ addType: 'SOURCE_TABLE', colName: '', colType: '', fromAliaCode: '', fromColName: '', defaultValue: '', remark: '' }]
    }
    // 兼容旧数据，补充 addType 字段
    formData.value.addColEtl.forEach((item: any) => {
        if (!item.addType) {
            item.addType = item.fromAliaCode ? 'SOURCE_TABLE' : 'MANUAL'
        }
    })
})
</script>

<style lang="scss">
.data-add-col {
    .el-form-item {
        &.form-item-top {
            display: flex;
            flex-direction: column;
            margin-bottom: 0;
            > .el-form-item__label {
                display: none;
            }
            > .el-form-item__content {
                justify-content: flex-start !important;
                flex-direction: column;
                align-items: stretch;
                margin-left: 0 !important;
            }
            .transform-condition-actions {
                width: 100%;
                padding: 10px 0 8px;
            }
        }
    }
    .form-options__list {
        width: 100%;
        margin-top: 12px;
        border: 1px solid #e9eaec;
        border-radius: 4px;
        padding: 12px 12px 0;
        box-sizing: border-box;
        position: relative;

        .el-form-item {
            margin-bottom: 16px;
            display: flex !important;
            flex-direction: column !important;
            align-items: stretch !important;
            .el-form-item__label {
                justify-content: flex-start !important;
                width: auto !important;
                max-width: none !important;
                flex: none !important;
                padding-right: 0 !important;
            }
            .el-form-item__content {
                justify-content: flex-start !important;
                margin-left: 0 !important;
                flex: 1;
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
    }
}
</style>
