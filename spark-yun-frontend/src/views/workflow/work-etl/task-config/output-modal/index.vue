<template>
    <BlockModal :model-config="modelConfig">
        <component
            ref="instanceRef"
            :is="currentComponent(componentType)"
            :preNodes="preNodes"
            v-model="tableData"
        ></component>
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

const tableData = ref<any[]>([])
const componentType = ref<string>('DEFAULT')
const preNodes = ref<any[]>([])
const formInstance = shallowRef<any>(Components)
const modelConfig = reactive({
    title: '输出字段',
    visible: false,
    width: '60%',
    customClass: 'output-modal',
    cancelConfig: {
        title: '关闭',
        cancel: closeEvent,
        disabled: false,
    },
    zIndex: 1200,
    closeOnClickModal: false,
})

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

function showModal(data: any[], type: string, incomeNodes: any[]) {
    tableData.value = data
    componentType.value = type
    preNodes.value = incomeNodes

    modelConfig.visible = true;
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