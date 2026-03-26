<template>
    <BlockModal :model-config="modelConfig">
        <el-scrollbar>
            <el-form
              ref="formRef"
              label-position="left"
              label-width="80px"
              :model="formData"
            >
                <div class="main-config-container">
                    <component
                        ref="instanceRef"
                        :is="currentComponent(formData.type)"
                        v-model="formData"
                        :incomeNodes="incomeNodes"
                    ></component>
                </div>
            </el-form>
        </el-scrollbar>
        <template #customLeft>
            <el-button v-if="formData.type !== 'DATA_OUTPUT'" type="primary" @click="showFieldsModal" style="margin-right: auto;">输出字段</el-button>
            <el-button v-else type="primary" @click="showLinkModal" style="margin-right: auto;">字段映射</el-button>
        </template>
    </BlockModal>
    <OutputModal ref="outputModalRef"></OutputModal>
    <LinkModal ref="linkModalRef"></LinkModal>
</template>

<script lang="ts" setup>
import { computed, nextTick, reactive, ref, shallowRef, markRaw } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import BlockModal from '@/components/block-modal/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import { cloneDeep, clone } from 'lodash-es'
import OutputModal from './output-modal/index.vue'
import LinkModal from './link-modal/index.vue'

import DataInput from './data-input/index.vue'
import DataOutput from './data-output/index.vue'
import DataTransfrom from './data-transform/index.vue'
import DataJoin from './data-join/index.vue'
import DataUnion from './data-union/index.vue'
import DataFilter from './data-filter/index.vue'
import DataAddCol from './data-add-col/index.vue'
import DataCustom from './data-custom/index.vue'

const Components = {
    DataInput,
    DataOutput,
    DataTransfrom,
    DataJoin,
    DataUnion,
    DataFilter,
    DataAddCol,
    DataCustom
}

const formRef = ref<FormInstance>()
const callback = ref<any>()
const incomeNodes = ref<any>()

const formData = ref<any>({})
const instanceRef = ref<any>()

// 字段映射
const linkModalRef = ref<any>()
const outputModalRef = ref<any>()
const formInstance = shallowRef<any>(Components)
const modelConfig = reactive({
    title: '配置详情',
    visible: false,
    width: '60%',
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
const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入名称', trigger: ['blur', 'change'] }],
    aliaCode: [{ required: true, message: '请输入编码', trigger: ['blur', 'change'] }],
})

const currentComponent = computed(() => {
    return (type: string) => {
        const componentConfig = {
            DATA_INPUT: 'DataInput',
            DATA_OUTPUT: 'DataOutput',
            DATA_JOIN: 'DataJoin',
            DATA_UNION: 'DataUnion',
            DATA_FILTER: 'DataFilter',
            DATA_TRANSFORM: 'DataTransfrom',
            DATA_ADD_COL: 'DataAddCol',
            DATA_CUSTOM: 'DataCustom'
        }
        return markRaw(formInstance.value[componentConfig[type]])
    }
})

function showFieldsModal() {
    const tableData = formData.value.outColumnList || []
    outputModalRef.value.showModal(tableData, formData.value.type, incomeNodes.value, formData.value)
}
// 字段映射
function showLinkModal() {
    linkModalRef.value.showModal(formData.value, incomeNodes.value)
}

function showModal(prevNode: any, cb: () => void, data: any) {
    callback.value = cb
    incomeNodes.value = prevNode
    modelConfig.title = data.typeName

    formData.value = cloneDeep(data)
    modelConfig.visible = true;
}

function okEvent() {
    instanceRef.value.setData && instanceRef.value.setData()
    formRef.value?.validate((valid: boolean) => {
        if (valid) {
            callback.value(formData.value).then((res: any) => {
                modelConfig.okConfig.loading = false
                if (res === undefined) {
                    modelConfig.visible = false
                } else {
                    modelConfig.visible = true
                }
            }).catch((err: any) => {
                modelConfig.okConfig.loading = false
            })
        } else {
            ElMessage.warning('请将表单输入完整')
        }
    })
}

function closeEvent() {
    modelConfig.visible = false;
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.etl-task-config {
    .modal-content {
        padding: 12px 0;
        box-sizing: border-box;
    }
    .el-form {
        .base-container {
            padding: 0 20px;
            box-sizing: border-box;
            // margin-bottom: 12px;
            padding-top: 8px;
            .base-title {
                font-size: 14px;
                font-weight: bold;
                height: 32px;
                display: flex;
                align-items: center;
                position: relative;
                border-bottom: 1px solid #e9eaec;
                margin-bottom: 12px;
                padding-left: 12px;
                &::before {
                    content: '';
                    height: 20px;
                    width: 2px;
                    background-color: getCssVar('color', 'primary');
                    position: absolute;
                    left: 0px;
                    top: 5px;
                }
            }
        }
        .main-config-container {
            padding: 0 20px;
            box-sizing: border-box;
            margin-bottom: 12px;
            // padding-bottom: 50px;
            .config-components {
                padding: 0;
            }
            .base-title {
                font-size: 14px;
                font-weight: bold;
                height: 32px;
                display: flex;
                align-items: center;
                position: relative;
                border-bottom: 1px solid #e9eaec;
                margin-bottom: 12px;
                padding-left: 12px;
                &::before {
                    content: '';
                    height: 20px;
                    width: 2px;
                    background-color: getCssVar('color', 'primary');
                    position: absolute;
                    left: 0px;
                    top: 5px;
                }
            }
        }
    }
}
</style>
