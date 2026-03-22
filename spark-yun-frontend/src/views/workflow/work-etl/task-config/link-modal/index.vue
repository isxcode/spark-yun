<!--
 * @Author: fancinate 1585546519@qq.com
 * @Date: 2026-03-22 19:32:23
 * @LastEditors: fancinate 1585546519@qq.com
 * @LastEditTime: 2026-03-22 19:35:31
 * @FilePath: /spark-yun/spark-yun-frontend/src/views/workflow/work-etl/task-config/link-modal/index.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
    <BlockModal :model-config="modelConfig">
        
    </BlockModal>
</template>

<script lang="ts" setup>
import { computed, nextTick, reactive, ref, shallowRef, markRaw } from 'vue'

const modelConfig = reactive({
    title: '字段映射',
    visible: false,
    width: '60%',
    customClass: 'output-modal',
    okConfig: {
        title: '确定',
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

function showModal(data: any[]) {

    modelConfig.visible = true;
}

function okEvent() {
    console.log('确定保存')
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