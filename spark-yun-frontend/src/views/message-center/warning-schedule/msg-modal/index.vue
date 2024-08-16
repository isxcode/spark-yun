<template>
    <BlockModal :model-config="modelConfig">
        <el-form ref="form" class="add-computer-group message-modal-res" label-position="top" :model="formData">
            <el-form-item label="详情" :class="{ 'show-screen__full': fullStatus }">
                <el-icon class="modal-full-screen" @click="fullScreenEvent()">
                    <FullScreen v-if="!fullStatus" />
                    <Close v-else />
                </el-icon>
                <code-mirror :disabled="true" ref="responseBodyRef" v-model="formData.content" basic :lang="jsonLang" />
            </el-form-item>
        </el-form>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
// import CodeMirror from 'vue-codemirror6'
import { json } from '@codemirror/lang-json'
import { jsonFormatter } from '@/utils/formatter'

const form = ref<FormInstance>()
const jsonLang = ref<any>(json())
const fullStatus = ref(false)
const modelConfig = reactive({
    title: '查看详情',
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
const formData = reactive({
    content: ''
})
function showModal(data: string): void {
    formData.content = jsonFormatter(data)
    modelConfig.visible = true
}

function fullScreenEvent() {
    fullStatus.value = !fullStatus.value
}
function closeEvent() {
    modelConfig.visible = false
}
defineExpose({
    showModal
})
</script>

<style lang="scss">
.message-modal-res {
    .el-form-item {
        .format-json {
            position: absolute;
            top: -34px;
            right: 20px;
            font-size: 12px;
            color: getCssVar('color', 'primary');
            cursor: pointer;

            &:hover {
                text-decoration: underline;
            }
        }

        &.show-screen__full {
            position: fixed;
            width: 100%;
            height: 100%;
            top: 0;
            left: 0;
            background-color: #ffffff;
            padding: 12px 20px;
            box-sizing: border-box;
            transition: all 0.15s linear;
            z-index: 10;

            .el-form-item__content {
                align-items: flex-start;
                height: 100%;

                .vue-codemirror {
                    height: calc(100% - 36px);
                }
            }
        }

        .el-form-item__content {
            position: relative;
            flex-wrap: nowrap;
            justify-content: space-between;

            .copy-url {
                min-width: 24px;
                font-size: 12px;
                margin-left: 10px;
                color: getCssVar('color', 'primary');
                cursor: pointer;

                &:hover {
                    text-decoration: underline;
                }

            }

            .modal-full-screen {
                position: absolute;
                top: -26px;
                right: 0;
                cursor: pointer;

                &:hover {
                    color: getCssVar('color', 'primary');
                    ;
                }
            }

            .vue-codemirror {
                height: 280px;
                width: 100%;

                .cm-editor {
                    height: 100%;
                    outline: none;
                    border: 1px solid #dcdfe6;
                }

                .cm-gutters {
                    font-size: 12px;
                    font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                }

                .cm-content {
                    font-size: 12px;
                    font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                }

                .cm-tooltip-autocomplete {
                    ul {
                        li {
                            height: 40px;
                            display: flex;
                            align-items: center;
                            font-size: 12px;
                            background-color: #ffffff;
                            font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                        }

                        li[aria-selected] {
                            background: #409EFF;
                        }

                        .cm-completionIcon {
                            margin-right: -4px;
                            opacity: 0;
                        }
                    }
                }
            }
        }
    }
}
</style>