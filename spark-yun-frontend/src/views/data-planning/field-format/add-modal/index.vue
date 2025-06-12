<template>
    <BlockModal :model-config="modelConfig">
        <el-form
            ref="form"
            class="add-computer-group acquisition-task-add"
            label-position="top"
            :model="formData"
            :rules="rules"
            :disabled="readonly"
        >
            <el-form-item label="名称" prop="name">
                <el-input v-model="formData.name" maxlength="500" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="字段类型" prop="columnTypeCode">
                <el-select
                    v-model="formData.columnTypeCode"
                    filterable
                    placeholder="请选择"
                >
                    <el-option
                        v-for="item in fieldTypeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="字段规范" prop="columnRule">
                <el-input v-model="formData.columnRule" maxlength="500" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="字段精度" >
                <el-input v-model="formData.columnType" maxlength="200" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="主键" prop="isPrimary" class="inline-show">
                <el-checkbox v-model="formData.isPrimary" true-label="ENABLE" false-label="DISABLE" />
            </el-form-item>
            <el-form-item label="非空" prop="isNull" class="inline-show">
                <el-checkbox v-model="formData.isNull" true-label="ENABLE" false-label="DISABLE" />
            </el-form-item>
            <el-form-item label="唯一" prop="isDuplicate" class="inline-show">
                <el-checkbox v-model="formData.isDuplicate" true-label="ENABLE" false-label="DISABLE" />
            </el-form-item>
            <el-form-item label="分区键" prop="isPartition" class="inline-show">
                <el-checkbox v-model="formData.isPartition" true-label="ENABLE" false-label="DISABLE" />
            </el-form-item>
            <el-form-item label="默认值" prop="defaultValue">
                <el-input v-model="formData.defaultValue" maxlength="500" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="字段规范" prop="columnRule">
                <el-input v-model="formData.columnRule" maxlength="500" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="备注">
                <el-input v-model="formData.remark" type="textarea" maxlength="200"
                    :autosize="{ minRows: 4, maxRows: 4 }" placeholder="请输入" />
            </el-form-item>
        </el-form>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref } from 'vue'
import { GetDataLayerList } from '@/services/data-layer.service'
import { ElMessage, FormInstance, FormRules } from 'element-plus'

interface Option {
    label: string
    value: string
}

const form = ref<FormInstance>()
const callback = ref<any>()
const readonly = ref<boolean>(false)
const fieldTypeList = ref<Option[]>([
    {
        label: '大文本',
        value: 'TEXT'
    },
    {
        label: '字符串',
        value: 'STRING'
    },
    {
        label: '日期',
        value: 'DATE'
    },
    {
        label: '日期时间',
        value: 'DATETIME'
    },
    {
        label: '整数',
        value: 'INT'
    },
    {
        label: '小数',
        value: 'DOUBLE'
    },
    {
        label: '自定义',
        value: 'CUSTOM'
    }
])

const modelConfig = reactive({
    title: '添加',
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
    columnTypeCode: '',
    columnType: '',
    isNull: 'DISABLE',
    isPrimary: 'DISABLE',
    isDuplicate: 'DISABLE',
    isPartition: 'DISABLE',
    defaultValue: '',
    columnRule: '',
    remark: '',
    id: ''
})
const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入名称', trigger: ['blur', 'change'] }],
    columnTypeCode: [{ required: true, message: '请选择字段类型', trigger: ['blur', 'change'] }],
    columnType: [{ required: true, message: '请输入字段精度', trigger: ['blur', 'change'] }]
})

function showModal(cb: () => void, data: any, type?: string): void {
    modelConfig.okConfig = {
        title: '确定',
        ok: okEvent,
        disabled: false,
        loading: false
    }
    if (data) {
        readonly.value = false
        Object.keys(formData).forEach((key: string) => {
            formData[key] = data[key]
        })
        modelConfig.title = '编辑'
    } else {
        readonly.value = false
        const keys = ['isNull', 'isPrimary', 'isDuplicate', 'isPartition']
        Object.keys(formData).forEach((key: string) => {
            formData[key] = ''
            if (keys.includes(key)) {
                formData[key] = 'DISABLE'
            }
        })
        modelConfig.title = '添加'
    }

    if (type === 'readonly') {
        readonly.value = true
        modelConfig.title = '查看'
        modelConfig.okConfig = null
    }

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

function closeEvent() {
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.acquisition-task-add {
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
        }

        &.inline-show {
            display: flex;
            align-items: center;
            .el-form-item__label {
                margin: 0;
            }
            .el-form-item__content {
                justify-content: flex-start !important;
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