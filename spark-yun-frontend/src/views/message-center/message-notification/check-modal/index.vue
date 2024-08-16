<template>
    <BlockModal :model-config="modelConfig">
        <el-form ref="form" class="add-computer-group message-check-form" label-position="top" :model="formData" :rules="rules">
            <el-form-item label="名称">
                <el-input :disabled="true" v-model="formData.name" maxlength="200" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="类型">
                <el-select :disabled="true" v-model="formData.msgType" placeholder="请选择">
                    <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
            <el-form-item label="通知内容模板">
                <el-input :disabled="true" v-model="formData.contentTemplate" maxlength="200" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="通知对象" prop="receiver">
                <el-select v-model="formData.receiver" placeholder="请选择">
                    <el-option v-for="item in userList" :key="item.userId" :label="item.username" :value="item.userId" />
                </el-select>
            </el-form-item>
            <el-form-item label="通知内容" prop="content" :class="{ 'show-screen__full': fullStatus }">
                <span v-if="formData.msgType === 'ALI_SMS'" class="format-json" @click="formatterJsonEvent(formData, 'content')">格式化JSON</span>
                <el-icon class="modal-full-screen" @click="fullScreenEvent()">
                    <FullScreen v-if="!fullStatus" />
                    <Close v-else />
                </el-icon>
                <code-mirror ref="responseBodyRef" v-model="formData.content" basic :lang="jsonLang"/>
            </el-form-item>
        </el-form>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetUserList } from '@/services/tenant-user.service'
// import CodeMirror from 'vue-codemirror6'
import {json} from '@codemirror/lang-json'
import { jsonFormatter } from '@/utils/formatter'

const form = ref<FormInstance>()
const jsonLang = ref<any>(json())
const callback = ref<any>()
const fullStatus = ref(false)
const typeList = ref([
    {
        label: '阿里短信',
        value: 'ALI_SMS'
    },
    {
        label: '邮箱',
        value: 'EMAIL'
    }
])
const userList = ref([])
const modelConfig = reactive({
    title: '测试',
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
    name: '',
    msgType: '',
    receiver: '',
    content: '',
    contentTemplate: '',
    id: ''
})
const rules = reactive<FormRules>({
    receiver: [{ required: true, message: '请选择通知对象', trigger: ['blur', 'change'] }],
    content: [{ required: true, message: '请输入通知内容', trigger: ['blur', 'change'] }],
})

function showModal(cb: () => void, data: any): void {
    getUserList()
    formData.name = data.name
    formData.msgType = data.msgType
    if (data.messageConfig) {
        formData.contentTemplate = data.messageConfig.contentTemplate
    }
    formData.id = data.id
    formData.content = ''
    formData.receiver = ''
    modelConfig.title = '测试'

    callback.value = cb
    modelConfig.visible = true
}

function getUserList() {
    GetUserList({
        page: 0,
        pageSize: 10000,
        searchKeyWord: '',
        tenantId: ''
    }).then((res: any) => {
        userList.value = res.data.content
    }).catch(() => {
        userList.value = []
    })
}

function okEvent() {
    form.value?.validate((valid) => {
        if (valid) {
            modelConfig.okConfig.loading = true
            let formDataParams = {
                receiver: formData.receiver,
                content: formData.content,
                id: formData.id
            }
            callback.value(formDataParams).then((res: any) => {
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
function fullScreenEvent() {
    fullStatus.value = !fullStatus.value
}
function formatterJsonEvent(formData: any, key: string) {
  try {
    formData[key] = jsonFormatter(formData[key])
  } catch (error) {
    console.error('请检查输入的JSON格式是否正确', error)
    ElMessage.error('请检查输入的JSON格式是否正确')
  }
}

function closeEvent() {
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.message-check-form {
    .el-form-item {
        .format-json {
        position: absolute;
        top: -34px;
        right: 20px;
        font-size: 12px;
        color: getCssVar('color', 'primary');
        cursor: pointer;
        &:hover {
            text-decoration: underline;
        }
        }
        &.show-screen__full {
        position: fixed;
        width: 100%;
        height: 100%;
        top: 0;
        left: 0;
        background-color: #ffffff;
        padding: 12px 20px;
        box-sizing: border-box;
        transition: all 0.15s linear;
        z-index: 10;
        .el-form-item__content {
            align-items: flex-start;
            height: 100%;
            .vue-codemirror {
            height: calc(100% - 36px);
            }
        }
        }
        .el-form-item__content {
        position: relative;
        flex-wrap: nowrap;
        justify-content: space-between;

        .copy-url {
            min-width: 24px;
            font-size: 12px;
            margin-left: 10px;
            color: getCssVar('color', 'primary');
            cursor: pointer;
            &:hover {
                text-decoration: underline;
            }

        }
        .modal-full-screen {
            position: absolute;
            top: -26px;
            right: 0;
            cursor: pointer;
            &:hover {
            color: getCssVar('color', 'primary');;
            }
        }
        .vue-codemirror {
            height: 130px;
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
    }
}
</style>