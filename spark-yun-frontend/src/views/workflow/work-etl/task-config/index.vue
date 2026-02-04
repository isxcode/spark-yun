<template>
    <BlockDrawer :drawer-config="drawerConfig">
        <el-scrollbar>
            <el-form
              ref="formRef"
              label-position="left"
              label-width="120px"
              :model="formData"
            >
                <!-- 数据输入 -->
                <template v-if="formData.type === 'DATA_INPUT'">
                    <DataInput ref="instanceRef" v-model="formData" :incomeNodes="incomeNodes"></DataInput>
                </template>
                <!-- 数据输出 -->
                <template v-if="formData.type === 'DATA_OUTPUT'">
                    <DataOutput ref="instanceRef" v-model="formData" :incomeNodes="incomeNodes"></DataOutput>
                </template>
            </el-form>
        </el-scrollbar>
    </BlockDrawer>
</template>

<script lang="ts" setup>
import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import BlockDrawer from '@/components/block-drawer/index.vue'

import DataInput from './data-input/index.vue'
import DataOutput from './data-output/index.vue'

const formRef = ref<FormInstance>()
const callback = ref<any>()
const incomeNodes = ref<any>()
const formData = ref<any>({})
const instanceRef = ref<any>()
const drawerConfig = reactive({
    title: '配置详情',
    visible: false,
    width: '600',
    customClass: 'etl-task-config',
    okConfig: {
        title: '确定',
        ok: okEvent,
        disabled: false,
        loading: false,
    },
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
        disabled: false,
    },
    zIndex: 1100,
    closeOnClickModal: false,
})

function showModal(prevNode: any, cb: () => void, data: any) {
    callback.value = cb
    incomeNodes.value = prevNode
    drawerConfig.title = data.typeName

    formData.value = data
    drawerConfig.visible = true;
}

function okEvent() {
    instanceRef.value.setData && instanceRef.value.setData()
    formRef.value?.validate((valid: boolean) => {
        if (valid) {
            callback.value(formData.value).then((res: any) => {
                drawerConfig.okConfig.loading = false
                if (res === undefined) {
                    drawerConfig.visible = false
                } else {
                    drawerConfig.visible = true
                }
            }).catch((err: any) => {
                drawerConfig.okConfig.loading = false
            })
        } else {
            ElMessage.warning('请将表单输入完整')
        }
    })
}

function closeEvent() {
    drawerConfig.visible = false;
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.etl-task-config {

}
</style>
