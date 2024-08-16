<template>
    <BlockModal :model-config="modelConfig">
        <el-form ref="form" class="add-computer-group" label-position="top" :model="formData" :rules="rules">
            <!-- <el-form-item label="字段名" prop="code">
                <el-input v-model="formData.code" maxlength="20" placeholder="请输入"/>
            </el-form-item>
            <el-form-item label="类型" prop="type">
                <el-input v-model="formData.type" maxlength="20" placeholder="请输入"/>
            </el-form-item> -->
            <el-form-item label="转换">
                <code-mirror v-model="formData.sql" basic :lang="lang"/>
            </el-form-item>
        </el-form>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
// import CodeMirror from 'vue-codemirror6'
import {sql} from '@codemirror/lang-sql'

interface codeParam {
    // code: string
    // type: string
    sql: string
}
const lang = ref<any>(sql())
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
    zIndex: 1100,
    closeOnClickModal: false
})
const formData = reactive({
    // code: '',
    // type: '',
    sql: ''
})
const rules = reactive<FormRules>({
    // code: [
    //     {
    //         required: true,
    //         message: '请输入字段名',
    //         trigger: ['blur', 'change']
    //     }
    // ],
    // type: [
    //     {
    //         required: true,
    //         message: '请输入类型',
    //         trigger: ['blur', 'change']
    //     }
    // ]
})

function showModal(cb: () => void, data: codeParam): void {
    callback.value = cb
    modelConfig.visible = true
    if (data) {
        // formData.code = data.code
        // formData.type = data.type
        formData.sql = data.sql
        modelConfig.title = '编辑'
        renderSence.value = 'edit'
    } else {
        // formData.code = ''
        // formData.type = ''
        formData.sql = ''
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
