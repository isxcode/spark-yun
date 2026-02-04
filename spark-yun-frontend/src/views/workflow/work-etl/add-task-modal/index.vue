<template>
    <BlockModal :model-config="modelConfig">
        <el-form ref="form" class="form-container" label-position="top" :model="formData" :rules="rules">
            <el-form-item label="名称" prop="name">
                <el-input v-model="formData.name" maxlength="100" placeholder="请输入" show-word-limit />
            </el-form-item>
            <el-form-item label="编码" prop="aliaCode">
                <el-input v-model="formData.aliaCode" maxlength="100" placeholder="请输入" show-word-limit />
            </el-form-item>
            <el-form-item label="备注">
                <el-input v-model="formData.remark" show-word-limit type="textarea" maxlength="200"
                    :autosize="{ minRows: 4, maxRows: 4 }" placeholder="请输入" />
            </el-form-item>
            <!-- <el-form-item>
                <el-button type="primary" @click="showConfigEvent">任务配置</el-button>
            </el-form-item> -->
        </el-form>
        <TaskConfig ref="taskConfigRef"></TaskConfig>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetComputerGroupList } from '@/services/computer-group.service';
import TaskConfig from '../task-config/index.vue'

interface FormData {
    name: string        // 名称
    type: string        // 类型
    aliaCode: string    // 编码
    remark: string      // 备注
}

const form = ref<FormInstance>()
const callback = ref<any>()
const taskConfigRef = ref<any>()
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
const formData = reactive<FormData>({
    name: '',
    remark: '',
    aliaCode: '',
    id: ''
})
const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入名称', trigger: ['blur', 'change'] }],
    aliaCode: [{ required: true, message: '请输入编码', trigger: ['blur', 'change'] }],
})

function showModal(cb: () => void, data?: any): void {
    callback.value = cb
    modelConfig.visible = true
    if (data) {
        formData.name = data.name
        formData.aliaCode = data.aliaCode
        formData.remark = data.remark
        formData.id = data.id
        modelConfig.title = '编辑'
    } else {
        formData.name = ''
        formData.aliaCode = ''
        formData.remark = ''
        formData.id = ''
        modelConfig.title = '编辑任务'
    }

    nextTick(() => {
        form.value?.resetFields()
    })
}

function okEvent() {
    form.value?.validate((valid) => {
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

function showConfigEvent() {
    taskConfigRef.value.showModal(() => {
        return new Promise((resolve: any) => {
            resolve()
        })
    })
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.form-container {
    padding: 12px 20px 0 20px;
    box-sizing: border-box;
}
</style>