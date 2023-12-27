<template>
    <BlockModal :model-config="modelConfig">
        <div class="share-form-container">
            <div class="img-code">

            </div>
            <div class="share-form">
                <span class="label">链接：</span>
                <span class="url">https://zhiqingyun-demo.isxcode.com/home/computer-group</span>
            </div>
        </div>
    </BlockModal>
</template>
  
<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'

const modelConfig = reactive({
    title: '分享表单',
    visible: false,
    width: '520px',
    cancelConfig: {
        title: '关闭',
        cancel: closeEvent,
        disabled: false
    },
    needScale: false,
    zIndex: 1100,
    closeOnClickModal: false
})

function showModal(card: any): void {
    modelConfig.visible = true
    console.log('card', card)
}

function closeEvent() {
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>
  
<style lang="scss">
.share-form-container {
    display: flex;
    justify-content: space-around;
    flex-direction: column;
    align-items: center;
    height: 160px;
    .img-code {
        border: 1px solid red;
        height: 100px;
        width: 100px;
    }
    .share-form {
        .label {
            color: getCssVar('color', 'primary');
            margin-right: 12px;
            font-size: 12px;
        }
        .url {
            color: getCssVar('color', 'primary', 'light-5');
            font-size: 12px;
        }
    }
}
</style>
