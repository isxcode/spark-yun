<template>
    <BlockModal :model-config="modelConfig">
        <el-form ref="form" class="access-rule-form" label-position="top" :model="formData" :rules="rules">
            <el-form-item label="名称" prop="name">
                <el-input v-model="formData.name" maxlength="200" placeholder="请输入规则名称" />
            </el-form-item>
            <el-form-item label="规则类型" prop="ruleType">
                <el-select v-model="formData.ruleType" placeholder="请选择" :filterable="true">
                    <el-option label="白名单" value="WHITELIST" />
                    <el-option label="黑名单" value="BLACKLIST" />
                </el-select>
            </el-form-item>
            <el-form-item label="IP地址" prop="ipAddress">
                <el-input
                    v-model="formData.ipAddress"
                    type="textarea"
                    maxlength="2000"
                    :autosize="{ minRows: 4, maxRows: 8 }"
                    placeholder="每行一个IP地址，支持正则表达式&#10;例如：&#10;192.168.1.1&#10;192.168.1.*&#10;10\.0\.0\.[1-9]"
                />
            </el-form-item>
            <el-form-item label="备注">
                <el-input
                    v-model="formData.remark"
                    type="textarea"
                    maxlength="200"
                    :autosize="{ minRows: 4, maxRows: 4 }"
                    placeholder="请输入"
                />
            </el-form-item>
        </el-form>
        <template #customLeft>
            <el-button @click="closeEvent">取消</el-button>
            <el-button :loading="okLoading" type="primary" @click="okEvent">确定</el-button>
        </template>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'

const form = ref<FormInstance>()
const callback = ref<any>()
const okLoading = ref<boolean>(false)

const modelConfig = reactive({
    title: '添加规则',
    visible: false,
    width: '520px',
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
        disabled: false,
        hide: true
    },
    needScale: false,
    zIndex: 1100,
    closeOnClickModal: false
})

const formData = reactive<any>({
    id: '',
    name: '',
    ruleType: '',
    ipAddress: '',
    remark: ''
})

const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入规则名称', trigger: ['blur', 'change'] }],
    ruleType: [{ required: true, message: '请选择规则类型', trigger: ['blur', 'change'] }],
    ipAddress: [{ required: true, message: '请输入IP地址', trigger: ['blur', 'change'] }]
})

function showModal(cb: () => void, data: any): void {
    callback.value = cb
    modelConfig.visible = true
    if (data) {
        formData.id = data.id
        formData.name = data.name
        formData.ruleType = data.ruleType
        formData.ipAddress = data.ipAddress
        formData.remark = data.remark
        modelConfig.title = '编辑规则'
    } else {
        Object.keys(formData).forEach((key) => {
            formData[key] = ''
        })
        modelConfig.title = '添加规则'
    }
    nextTick(() => {
        form.value?.resetFields()
    })
}

function okEvent() {
    form.value?.validate((valid) => {
        if (valid) {
            okLoading.value = true
            callback
                .value({
                    ...formData,
                    id: formData.id ? formData.id : undefined
                })
                .then((res: any) => {
                    okLoading.value = false
                    modelConfig.visible = false
                    ElMessage.success(res.msg)
                })
                .catch(() => {
                    okLoading.value = false
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
.access-rule-form {
    box-sizing: border-box;
    padding: 12px 20px 0 20px;
    width: 100%;
}
</style>

