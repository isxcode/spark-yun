<template>
    <BlockModal :model-config="modelConfig">
        <template #customLeft>
            <el-button class="footer-share-copy-btn" :disabled="!url" id="share-url" :data-clipboard-text="url" @click="copyUrlEvent('share-url')">一键复制</el-button>
            <el-button class="footer-share-generate-btn" :loading="loading" type="primary" @click="getShareFormUrl">生成链接</el-button>
        </template>
        <div class="share-form-container">
            <!-- <div class="img-code">

            </div> -->
            <div class="share-form">
                <span class="url">
                    <a v-if="url" class="url-link" :href="url" target="_blank" rel="noopener noreferrer">
                        <EllipsisTooltip class="url-show" :label="url" />
                    </a>
                    <EllipsisTooltip v-else class="url-show" label="暂无链接" />
                </span>
            </div>
        </div>
        <div class="share-option-container">
            <div class="valid-day-input">
                <span>有效时间（天）</span>
                <el-input-number
                    v-model="validDay"
                    :min="1"
                    controls-position="right"
                />
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
import { GetFormLinkConfig, ShareFormGetCustomToken } from '@/services/custom-form.service'

const authStore = useAuthStore()
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
    GetFormLinkConfig({
        formId: cardInfo.value.id,
        validDay: validDay.value
    }).then((res: any) => {
        // token.value = res.data.token
        loading.value = false
        // const params = {
        //     formId: cardInfo.value.id,
        //     formVersion: cardInfo.value.formVersion,
        //     tenantId: authStore.tenantId,
        //     token: token.value
        // }
        url.value = `${location.origin}/share/${res.data.formLinkId}`
    }).catch(() => {
        loading.value = false
    })
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
.share-form-setting__modal {
    .footer-share-copy-btn {
        margin-right: 8px;
    }

    .footer-share-generate-btn {
        margin-right: 8px;
    }

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
            width: 100%;
            background: getCssVar('fill-color', 'light');
            border: 1px dashed getCssVar('border-color');
            border-radius: 6px;
            padding: 10px 12px;
            box-sizing: border-box;

            .url {
                color: getCssVar('text-color', 'regular');
                font-size: 12px;
                width: 100%;
    
                .url-show {
                    max-width: 100%;
                }

                .url-link {
                    color: getCssVar('color', 'primary');
                    text-decoration: none;
                }

                .url-link:hover {
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
