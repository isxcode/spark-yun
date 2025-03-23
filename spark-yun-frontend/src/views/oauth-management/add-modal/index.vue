<template>
    <BlockModal :model-config="modelConfig">
        <el-form ref="form" class="add-computer-group" label-position="top" :model="formData" :rules="rules">
            <el-form-item label="名称" prop="name">
                <el-input v-model="formData.name" maxlength="200" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="类型" prop="ssoType">
                <el-select
                    v-model="formData.ssoType"
                    placeholder="请选择"
                >
                    <el-option
                        v-for="item in typeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="clientId" prop="clientId">
                <el-input v-model="formData.clientId" maxlength="2000" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="clientSecret" prop="clientSecret">
                <el-input v-model="formData.clientSecret" maxlength="2000" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="scope">
                <el-input v-model="formData.scope" maxlength="2000" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="authUrl" prop="authUrl">
                <el-input v-model="formData.authUrl" maxlength="2000" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="accessTokenUrl" prop="accessTokenUrl">
                <el-input v-model="formData.accessTokenUrl" maxlength="2000" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="redirectUrl" prop="redirectUrl">
                <el-input v-model="formData.redirectUrl" maxlength="2000" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="userUrl" prop="userUrl">
                <el-input v-model="formData.userUrl" maxlength="2000" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="authJsonPath" prop="authJsonPath">
                <el-input v-model="formData.authJsonPath" maxlength="2000" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="备注">
                <el-input v-model="formData.remark" show-word-limit type="textarea" maxlength="200"
                    :autosize="{ minRows: 4, maxRows: 4 }" placeholder="请输入" />
            </el-form-item>
        </el-form>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'

interface ConfigParam {
    name: string
    ssoType: string
    remark: string
    authJsonPath: string
    scope: string
    clientId: string
    clientSecret: string
    accessTokenUrl: string
    authUrl: string
    userUrl: string
    redirectUrl: string
    id?: string
}

interface Option {
    label: string
    value: string
}

const form = ref<FormInstance>()
const callback = ref<any>()
const renderSence = ref('new')
const typeList = ref<Option[]>([
    {
        label: 'Github',
        value: 'GITHUB'
    },
    {
        label: 'Keycloak',
        value: 'KEYCLOAK'
    }
])
const modelConfig = reactive({
    title: '添加配置',
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
const formData = reactive<ConfigParam>({
    name: '',
    ssoType: '',
    remark: '',
    authJsonPath: '',
    scope: '',
    clientId: '',
    clientSecret: '',
    accessTokenUrl: '',
    authUrl: '',
    userUrl: '',
    redirectUrl: '',
    id: ''
})
const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入名称', trigger: ['change']}],
    ssoType: [{ required: true, message: '请选择类型', trigger: ['change']}],
    authJsonPath: [{ required: true, message: '请输入authJsonPath', trigger: ['change']}],
    scope: [{ required: true, message: '请输入scope', trigger: ['change']}],
    clientId: [{ required: true, message: '请输入clientId', trigger: ['change']}],
    clientSecret: [{ required: true, message: '请输入clientSecret', trigger: ['change']}],
    accessTokenUrl: [{ required: true, message: '请输入accessTokenUrl', trigger: ['change']}],
    authUrl: [{ required: true, message: '请输入authUrl', trigger: ['change']}],
    userUrl: [{ required: true, message: '请输入userUrl', trigger: ['change']}],
    redirectUrl: [{ required: true, message: '请输入redirectUrl', trigger: ['change']}]
})

function showModal(cb: () => void, data: any): void {
    callback.value = cb
    modelConfig.visible = true
    if (data) {
        Object.keys(formData).forEach((key: string) => {
            formData[key] = data[key]
        })
        modelConfig.title = '编辑配置'
        renderSence.value = 'edit'
    } else {
        Object.keys(formData).forEach((key: string) => {
            formData[key] = ''
        })
        modelConfig.title = '添加配置'
        renderSence.value = 'new'
    }
    nextTick(() => {
        form.value?.resetFields()
    })
}

function okEvent() {
    form.value?.validate((valid: boolean) => {
        if (valid) {
            modelConfig.okConfig.loading = true
            callback.value({
                ...formData,
                id: formData.id ? formData.id : undefined
            }).then((res: any) => {
                modelConfig.okConfig.loading = false
                if (res === undefined) {
                    modelConfig.visible = false
                } else {
                    modelConfig.visible = true
                }
            }).catch(() => {
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
.add-computer-group {
    padding: 12px 20px 0 20px;
    box-sizing: border-box;
}
</style>