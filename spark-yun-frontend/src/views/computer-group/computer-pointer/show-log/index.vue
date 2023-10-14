<template>
    <BlockModal :model-config="modelConfig" @close="closeEvent">
        <div id="content" class="content-box">
            <pre v-if="logMsg">{{ logMsg }}</pre>
        </div>
    </BlockModal>
</template>
  
<script lang="ts" setup>
import { reactive, defineExpose, ref } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'

const logMsg = ref('')

const modelConfig = reactive({
    title: '日志',
    visible: false,
    width: '820px',
    cancelConfig: {
        title: '关闭',
        cancel: closeEvent,
        disabled: false
    },
    needScale: false,
    zIndex: 1100,
    customClass: 'zqy-log-modal',
    closeOnClickModal: false
})

function showModal(log: string): void {
    logMsg.value = log
    modelConfig.visible = true
}

function closeEvent() {
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>
  
<style lang="scss">
.zqy-log-modal {
    .modal-content {
        .content-box {
            min-height: 60vh;
            max-height: 60vh;
            padding: 12px 20px;
            box-sizing: border-box;
            overflow: auto;

            pre {
                color: getCssVar('text-color', 'primary');
                font-size: 12px;
                line-height: 21px;
                margin: 0;
            }
        }
    }
}
</style>
  