<template>
    <BlockModal :model-config="modelConfig">
        <div class="share-form-container">
            <!-- <div class="img-code">

            </div> -->
            <div class="share-form">
                <span class="label">链接：</span>
                <span class="url">
                    <EllipsisTooltip class="url-show" :label="url" />
                </span>
                <span class="copy-url" id="share-url" :data-clipboard-text="url" @click="copyUrlEvent('share-url')">复制</span>
            </div>
        </div>
    </BlockModal>
</template>
  
<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import EllipsisTooltip from '@/components/ellipsis-tooltip/ellipsis-tooltip.vue'
import Clipboard from 'clipboard'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/store/useAuth'

const authStore = useAuthStore()
const url = ref('')

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
    const params = {
        formId: card.id,
        formVersion: card.formVersion,
        tenantId: authStore.tenantId
    }
    url.value = `${location.origin}/share/${window.btoa(JSON.stringify(params))}`
}

function copyUrlEvent(id: string) {
    let clipboard = new Clipboard('#' + id)
    clipboard.on('success', () => {
        ElMessage.success('复制成功')
        clipboard.destroy()
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
.share-form-container {
    display: flex;
    justify-content: space-around;
    flex-direction: column;
    align-items: center;
    height: 160px;
    padding: 0 20px;
    .img-code {
        border: 1px solid red;
        height: 100px;
        width: 100px;
    }
    .share-form {
        display: flex;
        width: 100%;
        .label {
            color: getCssVar('color', 'primary');
            margin-right: 12px;
            font-size: 12px;
            min-width: 40px;
        }
        .url {
            color: getCssVar('color', 'primary', 'light-5');
            font-size: 12px;
            max-width: 400px;
            .url-show {
                max-width: 100%;
            }
        }
        .copy-url {
            font-size: 12px;
            color: getCssVar('color', 'primary');
            cursor: pointer;
            &:hover {
                text-decoration: underline;
            }

        }
    }
}
</style>
