<template>
    <BlockModal :model-config="modelConfig">
        <el-form
            ref="form"
            class="add-computer-group model-field-container"
            label-position="top"
            :model="formData"
            :rules="rules"
        >
            <el-form-item label="字段" prop="name">
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
            <el-form-item label="字段名" prop="columnName">
                <el-input v-model="formData.columnName" maxlength="500" placeholder="请输入" />
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
import { reactive, defineExpose, ref } from 'vue'
import { GetDataLayerList } from '@/services/data-layer.service'
import { GetFieldFormatList } from '@/services/field-format.service'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import AddModal from '../../../field-format/add-modal/index.vue'
import { SaveFieldFormatData } from '@/services/field-format.service'

interface Option {
    label: string
    value: string
}

const form = ref<FormInstance>()
const callback = ref<any>()
const fieldTypeList = ref<Option[]>([])
const addModalRef = ref<any>(null)

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
    columnName: '',
    columnFormatId: '',
    remark: '',
    id: ''
})
const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入字段', trigger: ['blur', 'change'] }],
    columnName: [{ required: true, message: '请输入字段名', trigger: ['blur', 'change'] }],
    columnFormatId: [{ required: true, message: '请选择字段标准', trigger: ['blur', 'change'] }]
})

function showModal(cb: () => void, data: any): void {
    if (data) {
        Object.keys(formData).forEach((key: string) => {
            formData[key] = data[key]
        })
        modelConfig.title = '编辑'
    } else {
        Object.keys(formData).forEach((key: string) => {
            formData[key] = ''
        })
        modelConfig.title = '添加'
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
                    value: item.id
                }
            })
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