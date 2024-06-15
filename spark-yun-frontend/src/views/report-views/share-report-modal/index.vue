<template>
    <BlockModal :model-config="modelConfig">
        <div class="share-form-container">
            <div class="share-form">
                <span class="label">链接：</span>
                <span class="url">
                    <EllipsisTooltip class="url-show" :label="url || '暂无链接'" />
                </span>
                <span v-if="url" class="copy-url" id="share-url" :data-clipboard-text="url"
                    @click="copyUrlEvent('share-url')">复制</span>
            </div>
        </div>
        <div class="share-option-container">
            <div class="valid-day-input">
                <span>生效时间（天）</span>
                <el-input-number
                    v-model="validDay"
                    :min="1"
                    controls-position="right"
                />
            </div>
            <el-button :loading="loading" type="primary" @click="getShareFormUrl">生成分享链接</el-button>
        </div>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import EllipsisTooltip from '@/components/ellipsis-tooltip/ellipsis-tooltip.vue'
// import Clipboard from 'clipboard'
import { ElMessage } from 'element-plus'
import { GetFormLinkConfig, } from '@/services/custom-form.service'

const url = ref('')
const token = ref('')
const cardInfo = ref()
const loading = ref(false)
const validDay = ref(1)

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
    customClass: 'share-form-setting__modal',
    zIndex: 1100,
    closeOnClickModal: false
})

function showModal(card: any): void {
    cardInfo.value = card
    modelConfig.visible = true
}

function getShareFormUrl() {
    loading.value = true
    // GetFormLinkConfig({
    //     formId: cardInfo.value.id,
    //     validDay: validDay.value
    // }).then((res: any) => {
    //     url.value = `${location.origin}/share-report/${res.data.formLinkId}`
    // }).catch(() => {
    // })
}

function copyUrlEvent(id: string) {
    // let clipboard = new Clipboard('#' + id)
    // clipboard.on('success', () => {
    //     ElMessage.success('复制成功')
    //     clipboard.destroy()
    // })
}

function closeEvent() {
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.share-form-setting__modal {
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
    
    .share-option-container {
        padding: 20px;
        box-sizing: border-box;
        padding-top: 0;
        display: flex;
        align-items: center;
        justify-content: space-between;

        .valid-day-input {
            display: flex;
            align-items: center;

            span {
                font-size: 12px;
                color: getCssVar('text-color', 'primary');
            }
            .el-input-number {
                .el-input-number__decrease,.el-input-number__increase {
                    border: 0;
                    background-color: unset;
                }
                .el-input {
                    .el-input__wrapper {
                        box-shadow: none;
                        border-bottom: 1px solid getCssVar('border-color');
                    }
                }
            }
        }
    }
}
</style>
