<template>
    <BlockModal :model-config="modelConfig">
        <el-form
            ref="form"
            class="add-computer-group model-field-container"
            label-position="top"
            :model="formData"
            :rules="rules"
        >
            <el-form-item label="名称" prop="name">
                <el-input v-model="formData.name" maxlength="500" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="字段标准" prop="columnFormatId">
                <el-button
                    class="add-format-data"
                    link
                    type="primary"
                    @click="addFormatDataEvent"
                >新建字段标准</el-button>
                <el-select
                    v-model="formData.columnFormatId"
                    filterable
                    clearable
                    placeholder="请选择"
                    @change="columnFormatChangeEvent"
                    @visible-change="getFieldFormatList"
                >
                    <el-option
                        v-for="item in fieldTypeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="字段名" prop="columnName" :show-message="false">
                <el-input
                    v-if="showPrefixRuleInput"
                    v-model="formData.columnNameInput"
                    maxlength="500"
                    :placeholder="columnNamePlaceholder"
                >
                    <template #prepend>{{ currentColumnRule.input }}</template>
                </el-input>
                <el-input
                    v-else-if="showSuffixRuleInput"
                    v-model="formData.columnNameInput"
                    maxlength="500"
                    :placeholder="columnNamePlaceholder"
                >
                    <template #append>{{ currentColumnRule.input }}</template>
                </el-input>
                <el-input
                    v-else
                    v-model="formData.columnName"
                    :disabled="showExactRuleInput"
                    maxlength="500"
                    :placeholder="columnNamePlaceholder"
                />
            </el-form-item>
            <el-form-item label="备注">
                <el-input v-model="formData.remark" type="textarea" maxlength="200"
                    :autosize="{ minRows: 4, maxRows: 4 }" placeholder="请输入" />
            </el-form-item>
        </el-form>
        <AddModal ref="addModalRef" />
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, computed, watch } from 'vue'
import { GetFieldFormatList } from '@/services/field-format.service'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import AddModal from '../../../field-format/add-modal/index.vue'
import { SaveFieldFormatData } from '@/services/field-format.service'

interface Option {
    label: string
    value: string
    columnRule?: string
}

type ColumnRuleMode = 'none' | 'prefix' | 'suffix' | 'contains' | 'exact' | 'regex'

const form = ref<FormInstance>()
const callback = ref<any>()
const fieldTypeList = ref<Option[]>([])
const addModalRef = ref<any>(null)
const currentColumnRule = reactive<{ mode: ColumnRuleMode; input: string; raw: string }>({
    mode: 'none',
    input: '',
    raw: ''
})

const modelConfig = reactive({
    title: '添加字段',
    visible: false,
    width: '520px',
    okConfig: {
        title: '确定',
        ok: okEvent,
        disabled: false,
        loading: false
    },
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
        disabled: false
    },
    needScale: false,
    zIndex: 1100,
    closeOnClickModal: false
})
const formData = reactive<any>({
    name: '',
    columnName: '',
    columnNameInput: '',
    columnFormatId: '',
    remark: '',
    id: ''
})
const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入字段', trigger: ['blur', 'change'] }],
    columnName: [
        { required: true, message: '请输入字段名', trigger: ['blur', 'change'] },
        {
            validator: (_rule, value, callback) => {
                if ((currentColumnRule.mode === 'prefix' || currentColumnRule.mode === 'suffix') && !formData.columnNameInput?.trim()) {
                    callback(new Error('请输入字段名可变部分'))
                    return
                }
                if (!value?.trim()) {
                    callback()
                    return
                }
                if (currentColumnRule.mode === 'contains' && currentColumnRule.input && !value.includes(currentColumnRule.input)) {
                    callback(new Error(`字段名需包含：${currentColumnRule.input}`))
                    return
                }
                if (currentColumnRule.mode === 'exact' && currentColumnRule.input && value !== currentColumnRule.input) {
                    callback(new Error(`字段名需为：${currentColumnRule.input}`))
                    return
                }
                if (currentColumnRule.mode === 'regex' && currentColumnRule.raw) {
                    try {
                        const regex = new RegExp(currentColumnRule.raw)
                        if (!regex.test(value)) {
                            callback(new Error('字段名不符合字段规范'))
                            return
                        }
                    } catch (error) {
                        // 字段标准规则非法时不阻塞提交流程
                    }
                }
                callback()
            },
            trigger: ['blur', 'change']
        }
    ],
    columnFormatId: [{ required: true, message: '请选择字段标准', trigger: ['blur', 'change'] }]
})

const showPrefixRuleInput = computed(() => currentColumnRule.mode === 'prefix')
const showSuffixRuleInput = computed(() => currentColumnRule.mode === 'suffix')
const showExactRuleInput = computed(() => currentColumnRule.mode === 'exact')

const columnNamePlaceholder = computed(() => {
    if (showPrefixRuleInput.value || showSuffixRuleInput.value) {
        return '请输入字段名可变部分'
    }
    if (currentColumnRule.mode === 'contains' && currentColumnRule.input) {
        return `请输入完整字段名（需包含 ${currentColumnRule.input}）`
    }
    if (showExactRuleInput.value) {
        return '字段名已按规范自动填写'
    }
    if (currentColumnRule.mode === 'regex' && currentColumnRule.input) {
        return `请按表达式填写：${currentColumnRule.input}`
    }
    return '请输入'
})

watch(() => formData.columnNameInput, () => {
    syncColumnNameByRule()
})

function showModal(cb: () => void, data: any): void {
    if (data) {
        Object.keys(formData).forEach((key: string) => {
            formData[key] = data[key]
        })
        fillDefaultColumnNameByRule(false)
        modelConfig.title = '编辑字段'
    } else {
        Object.keys(formData).forEach((key: string) => {
            formData[key] = ''
        })
        currentColumnRule.mode = 'none'
        currentColumnRule.input = ''
        currentColumnRule.raw = ''
        modelConfig.title = '添加字段'
    }
    getFieldFormatList(true)

    callback.value = cb
    modelConfig.visible = true
}

function okEvent() {
    form.value?.validate((valid: boolean) => {
        if (valid) {
            modelConfig.okConfig.loading = true
            callback.value(formData).then((res: any) => {
                modelConfig.okConfig.loading = false
                if (res === undefined) {
                    modelConfig.visible = false
                } else {
                    modelConfig.visible = true
                }
            }).catch((err: any) => {
                modelConfig.okConfig.loading = false
            })
        } else {
            ElMessage.warning('请将表单输入完整')
        }
    })
}

function getFieldFormatList(e: boolean, searchType?: string) {
    if (e) {
        GetFieldFormatList({
            page: 0,
            pageSize: 10000,
            searchKeyWord: searchType || '',
        }).then((res: any) => {
            fieldTypeList.value = res.data.content.map((item: any) => {
                return {
                    label: item.name,
                    value: item.id,
                    columnRule: item.columnRule
                }
            })
            fillDefaultColumnNameByRule(false)
        }).catch(() => {
            fieldTypeList.value = []
        })
    } else {
        fieldTypeList.value = []
    }
}

// 添加字段标准
function addFormatDataEvent() {
    addModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            SaveFieldFormatData(data).then((res: any) => {
                formData.columnFormatId = res.data.id
                getFieldFormatList(true)
                fillDefaultColumnNameByRule(true, true)
                ElMessage.success(res.msg)
                resolve()
            }).catch((error: any) => {
                reject(error)
            })
        })
    })
}

function closeEvent() {
    modelConfig.visible = false
}

function columnFormatChangeEvent() {
    fillDefaultColumnNameByRule(true, true)
}

function fillDefaultColumnNameByRule(force: boolean, resetInput?: boolean) {
    const currentFormat = fieldTypeList.value.find((item: Option) => item.value === formData.columnFormatId)
    const columnRule = currentFormat?.columnRule?.trim()
    const columnRuleInfo = parseColumnRule(columnRule || '')
    currentColumnRule.mode = columnRuleInfo.mode
    currentColumnRule.input = columnRuleInfo.input
    currentColumnRule.raw = columnRule || ''

    if (!columnRule) {
        if (resetInput) {
            formData.columnNameInput = ''
            formData.columnName = ''
        }
        return
    }

    if (resetInput) {
        formData.columnNameInput = ''
    } else if (force || !formData.columnNameInput) {
        formData.columnNameInput = parseColumnNameInputByRule(formData.columnName, columnRuleInfo)
    }

    if (force || !formData.columnName) {
        formData.columnName = buildColumnNameByRule(formData.columnNameInput, columnRuleInfo)
    }
    if (columnRuleInfo.mode === 'prefix' || columnRuleInfo.mode === 'suffix') {
        formData.columnName = buildColumnNameByRule(formData.columnNameInput, columnRuleInfo)
    }
    if (columnRuleInfo.mode === 'exact') {
        formData.columnName = columnRuleInfo.input
    }
    form.value?.validateField('columnName')
}

function syncColumnNameByRule() {
    if (currentColumnRule.mode === 'prefix' || currentColumnRule.mode === 'suffix') {
        formData.columnName = buildColumnNameByRule(formData.columnNameInput, currentColumnRule)
    } else if (currentColumnRule.mode === 'exact') {
        formData.columnName = currentColumnRule.input
    } else {
        formData.columnName = formData.columnNameInput
    }
    form.value?.validateField('columnName')
}

function parseColumnRule(value: string): { mode: ColumnRuleMode; input: string } {
    if (!value) {
        return { mode: 'none', input: '' }
    }

    const prefixMatch = value.match(/^\^(.+)\.\*$/)
    if (prefixMatch?.[1]) {
        return { mode: 'prefix', input: unEscapeRegexChar(prefixMatch[1]) }
    }

    const suffixMatch = value.match(/^\.\*(.+)\$$/)
    if (suffixMatch?.[1]) {
        return { mode: 'suffix', input: unEscapeRegexChar(suffixMatch[1]) }
    }

    const containsMatch = value.match(/^\.\*(.+)\.\*$/)
    if (containsMatch?.[1]) {
        return { mode: 'contains', input: unEscapeRegexChar(containsMatch[1]) }
    }

    const exactMatch = value.match(/^\^(.+)\$$/)
    if (exactMatch?.[1]) {
        return { mode: 'exact', input: unEscapeRegexChar(exactMatch[1]) }
    }

    return { mode: 'regex', input: value }
}

function unEscapeRegexChar(value: string) {
    return value.replace(/\\([.*+?^${}()|[\]\\])/g, '$1')
}

function buildColumnNameByRule(inputValue: string, ruleInfo: { mode: ColumnRuleMode; input: string }) {
    const safeInput = (inputValue || '').trim()
    if (ruleInfo.mode === 'prefix') {
        return `${ruleInfo.input}${safeInput}`
    }
    if (ruleInfo.mode === 'suffix') {
        return `${safeInput}${ruleInfo.input}`
    }
    if (ruleInfo.mode === 'exact') {
        return ruleInfo.input
    }
    return safeInput
}

function parseColumnNameInputByRule(columnName: string, ruleInfo: { mode: ColumnRuleMode; input: string }) {
    const currentColumnName = (columnName || '').trim()
    if (!currentColumnName) {
        return ''
    }
    if (ruleInfo.mode === 'prefix' && currentColumnName.startsWith(ruleInfo.input)) {
        return currentColumnName.slice(ruleInfo.input.length)
    }
    if (ruleInfo.mode === 'suffix' && currentColumnName.endsWith(ruleInfo.input)) {
        return currentColumnName.slice(0, currentColumnName.length - ruleInfo.input.length)
    }
    return currentColumnName
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.model-field-container {
    .el-form-item {
        .el-form-item__content {
            position: relative;
            flex-wrap: nowrap;
            justify-content: space-between;

            .time-num-input {
                height: 36px;

                .el-input-number__decrease {
                    top: 16px
                }
            }

            .add-format-data {
                position: absolute;
                top: -28px;
                right: 0;
            }
        }

        &.inline-show {
            display: flex;
            align-items: center;
            .el-form-item__label {
                margin: 0;
            }
            .el-form-item__content {
                justify-content: flex-end;
                padding-right: 12px;
                box-sizing: border-box;
            }
        }
    }

    .cron-config {
        border: 1px solid getCssVar('border-color');
        padding: 8px 12px;
        margin-bottom: 12px;
        border-radius: 5px;
    }
}
</style>
