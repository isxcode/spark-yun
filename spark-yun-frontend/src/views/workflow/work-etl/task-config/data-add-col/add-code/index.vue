<template>
    <BlockModal :model-config="modelConfig">
        <el-form ref="form" class="add-computer-group" label-position="top" :model="formData" :rules="rules">
            <el-form-item label="字段名" prop="colName">
                <el-input v-model="formData.colName" maxlength="20" placeholder="请输入"/>
            </el-form-item>
            <el-form-item label="类型" prop="colType">
                <el-select v-model="formData.colType" clearable filterable placeholder="请选择">
                    <el-option v-for="item in sparkTypeList" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
            <el-form-item label="备注">
                <el-input v-model="formData.remark" maxlength="20" placeholder="请输入"/>
            </el-form-item>
            <!-- <el-form-item label="转换">
                <code-mirror v-model="formData.sql" basic :lang="lang"/>
            </el-form-item> -->
        </el-form>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'

const sparkTypeList = [
    { label: 'string(字符串)', value: 'string' },
    { label: 'int(整数)', value: 'int' },
    { label: 'long(长整数)', value: 'long' },
    { label: 'double(双精度浮点)', value: 'double' },
    { label: 'boolean(布尔)', value: 'boolean' },
    { label: 'date(日期)', value: 'date' },
    { label: 'timestamp(时间戳)', value: 'timestamp' }
]
// import CodeMirror from 'vue-codemirror6'
// import {sql} from '@codemirror/lang-sql'

interface codeParam {
    colName: string
    colType: string
    remark: string
}
// const lang = ref<any>(sql())
const form = ref<FormInstance>()
const callback = ref<any>()
const renderSence = ref('new')
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
    zIndex: 3100,
    closeOnClickModal: false
})
const formData = reactive({
    colName: '',
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
    ]
})

function showModal(cb: () => void, data: codeParam): void {
    callback.value = cb
    modelConfig.visible = true
    if (data) {
        formData.colName = data.colName
        formData.colType = data.colType
        formData.remark = data.remark
        modelConfig.title = '编辑'
        renderSence.value = 'edit'
    } else {
        formData.colName = ''
        formData.colType = ''
        formData.remark = ''
        modelConfig.title = '添加'
        renderSence.value = 'new'
    }
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

    .vue-codemirror {
        height: 100px;
        width: 100%;
        .cm-editor {
          height: 100%;
          outline: none;
          border: 1px solid #dcdfe6;
        }

        .cm-gutters {
          font-size: 12px;
          font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
        }

        .cm-content {
          font-size: 12px;
          font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
        }
        .cm-tooltip-autocomplete {

          // display: none !important;
            ul {
                li {
                    height: 40px;
                    display: flex;
                    align-items: center;
                    font-size: 12px;
                    background-color: #ffffff;
                    font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                }

                li[aria-selected] {
                    background: #409EFF;
                }

                .cm-completionIcon {
                    margin-right: -4px;
                    opacity: 0;
                }
            }
        }
    }
}
</style>
