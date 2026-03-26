<template>
    <BlockModal :model-config="modelConfig">
        <component
            ref="instanceRef"
            :is="currentComponent(componentType)"
            :preNodes="preNodes"
            :nodeFormData="nodeFormData"
            v-model="tableData"
        ></component>
        <template #customLeft>
            <el-button v-if="showRefreshBtn" type="primary" @click="refreshFields" style="margin-right: auto;">刷新</el-button>
        </template>
    </BlockModal>
</template>

<script lang="ts" setup>
import { computed, nextTick, reactive, ref, shallowRef, markRaw } from 'vue'

import DefaultOutput from './default-output/index.vue'
import DataCustomOutput from './data-custom-output/index.vue'
import DataAddColOutput from './data-add-col-output/index.vue'
import DataJoinOutput from './data-join-output/index.vue'

const Components = {
    DefaultOutput,
    DataCustomOutput,
    DataAddColOutput,
    DataJoinOutput
}

const instanceRef = ref<any>()
const tableData = ref<any[]>([])
const componentType = ref<string>('DEFAULT')
const preNodes = ref<any[]>([])
const nodeFormData = ref<any>({})
const saveCallback = ref<((data: any[]) => void) | null>(null)
const formInstance = shallowRef<any>(Components)
const modelConfig = reactive({
    title: '输出字段',
    visible: false,
    width: '60%',
    customClass: 'output-modal',
    okConfig: {
        title: '保存',
        ok: okEvent,
        disabled: false,
        loading: false,
    },
    cancelConfig: {
        title: '关闭',
        cancel: closeEvent,
        disabled: false,
    },
    zIndex: 1200,
    closeOnClickModal: false,
})

const refreshBtnTypes = ['DATA_JOIN', 'DATA_UNION', 'DATA_FILTER', 'DATA_TRANSFORM', 'DATA_ADD_COL', 'DATA_INPUT', 'DATA_CUSTOM']
const showRefreshBtn = computed(() => refreshBtnTypes.includes(componentType.value))

const currentComponent = computed(() => {
    return (type: string) => {
        const componentConfig = {
            DATA_CUSTOM: 'DataCustomOutput',
            DATA_ADD_COL: 'DataAddColOutput',
            DATA_JOIN: 'DataJoinOutput',
            DEFAULT: 'DefaultOutput'
        }
        return markRaw(formInstance.value[componentConfig[type] || componentConfig['DEFAULT']])
    }
})

function showModal(data: any[], type: string, incomeNodes: any[], formData?: any) {
    tableData.value = data
    componentType.value = type
    preNodes.value = incomeNodes
    nodeFormData.value = formData || {}
    saveCallback.value = (newData: any[]) => {
        // 直接修改原数组引用，确保数据回写到formData.outColumnList
        data.length = 0
        newData.forEach(item => data.push(item))
    }

    modelConfig.visible = true;
}
function refreshFields() {
    if (instanceRef.value && instanceRef.value.refreshFields) {
        instanceRef.value.refreshFields()
    }
}

function okEvent() {
    // 将子组件中的tableData（含checked状态）写回
    if (instanceRef.value && instanceRef.value.getTableData && saveCallback.value) {
        saveCallback.value(instanceRef.value.getTableData())
    }
    modelConfig.visible = false;
}

function closeEvent() {
    modelConfig.visible = false;
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.output-modal {
    // .modal-content {
    //     padding: 12px;
    //     box-sizing: border-box;
    // }
}
</style>