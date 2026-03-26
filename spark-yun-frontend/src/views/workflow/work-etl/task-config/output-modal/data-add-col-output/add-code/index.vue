<template>
    <BlockModal :model-config="modelConfig">
        <el-form ref="form" class="add-computer-group" label-position="top" :model="formData" :rules="rules">
            <el-form-item label="来源表" prop="fromAliaCode">
                <el-select
                    v-model="formData.fromAliaCode"
                    filterable
                    clearable
                    placeholder="请选择"
                    @change="changeEvent"
                >
                    <el-option
                        v-for="item in sourceTablesList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="关联字段名" prop="fromColName">
                <el-select
                    v-model="formData.fromColName"
                    filterable
                    clearable
                    placeholder="请选择"
                    @visible-change="getTableFields($event)"
                    @change="onFromColNameChange"
                >
                    <el-option
                        v-for="item in tableFields"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="字段名" prop="colName">
                <el-input v-model="formData.colName" maxlength="20" placeholder="请输入"/>
            </el-form-item>
            <el-form-item label="类型" prop="colType">
                <el-input v-model="formData.colType" maxlength="20" placeholder="请输入"/>
            </el-form-item>
            <el-form-item label="备注">
                <el-input v-model="formData.remark" maxlength="20" placeholder="请输入"/>
            </el-form-item>
        </el-form>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'

interface codeParam {
    colName: string
    colType: string
    fromAliaCode: string
    fromColName: string
    remark: string
}

const form = ref<FormInstance>()
const callback = ref<any>()
const renderSence = ref('new')
const sourceTablesList = ref([])
const tableFields = ref([])
const pageInfo = ref({})

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
    zIndex: 1400,
    closeOnClickModal: false
})
const formData = reactive({
    colName: '',
    fromAliaCode: '',
    fromColName: '',
    colType: '',
    remark: ''
})
const rules = reactive<FormRules>({
    colName: [
        {
            required: true,
            message: '请输入字段名',
            trigger: ['blur', 'change']
        }
    ],
    colType: [
        {
            required: true,
            message: '请输入类型',
            trigger: ['blur', 'change']
        }
    ],
    fromAliaCode: [
        {
            required: true,
            message: '请选择来源表',
            trigger: ['blur', 'change']
        }
    ],
    fromColName: [
        {
            required: true,
            message: '请选择来源字段名',
            trigger: ['blur', 'change']
        }
    ]
})

function showModal(cb: () => void, data: codeParam, info: any): void {
    callback.value = cb
    modelConfig.visible = true
    if (data) {
        formData.colName = data.colName
        formData.colType = data.colType
        formData.fromColName = data.fromColName
        formData.fromAliaCode = data.fromAliaCode
        formData.remark = data.remark
        modelConfig.title = '编辑'
        renderSence.value = 'edit'
    } else {
        formData.colName = ''
        formData.colType = ''
        formData.fromColName = ''
        formData.fromAliaCode = ''
        formData.remark = ''
        modelConfig.title = '添加'
        renderSence.value = 'new'
    }
    pageInfo.value = info
    getTableList()
    nextTick(() => {
        form.value?.resetFields()
    })
}

function okEvent() {
    form.value?.validate((valid) => {
        if (valid) {
            callback.value({
                ...formData
            })
            closeEvent()
        } else {
            ElMessage.warning('请将表单输入完整')
        }
    })
}

function getTableList() {
    sourceTablesList.value = pageInfo.value.map((node: any) => {
        const currentNodeData = node.data.nodeConfigData
        return {
            label: currentNodeData.name,
            value: currentNodeData.aliaCode,
            data: currentNodeData
        }
    })
}

function getTableFields(e: boolean) {
    const currentItem = sourceTablesList.value.find(dd => dd.value === formData.fromAliaCode)
    if (e && currentItem && currentItem.data.outColumnList) {
        tableFields.value = (currentItem.data.outColumnList || []).filter((item: any) => item.checked !== false).map((column: any) => {
            return {
                label: column.colName,
                value: column.colName,
                colType: column.colType
            }
        })
    }
}

function onFromColNameChange(val: string) {
    if (val) {
        const field = tableFields.value.find((f: any) => f.value === val)
        if (field) {
            formData.colName = val
            formData.colType = field.colType || ''
        }
    }
}

function changeEvent() {
    formData.fromColName = ''
    formData.colName = ''
    formData.colType = ''
}

function closeEvent() {
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.add-computer-group {
    padding: 12px 20px 0 20px;
    box-sizing: border-box;
}
</style>
