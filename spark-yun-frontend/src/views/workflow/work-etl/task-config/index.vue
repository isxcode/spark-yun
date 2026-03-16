<template>
    <BlockModal :model-config="modelConfig">
        <el-scrollbar>
            <el-form
              ref="formRef"
              label-position="left"
              label-width="120px"
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
            <el-button type="primary" @click="showFieldsModal" style="margin-right: auto;">输出字段</el-button>
        </template>
    </BlockModal>
    <el-dialog
        v-model="fieldsDialogVisible"
        title="输出字段"
        width="50%"
        :z-index="1200"
        :append-to-body="true"
        :destroy-on-close="true"
    >
        <div style="max-height: 500px;">
            <BlockTable :table-config="fieldsTableConfig" />
        </div>
        <template #footer>
            <el-button type="primary" @click="fieldsDialogVisible = false">关闭</el-button>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { computed, nextTick, reactive, ref, shallowRef, markRaw } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import BlockModal from '@/components/block-modal/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import { cloneDeep, clone } from 'lodash-es'

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
const fieldsDialogVisible = ref(false)
const fieldsTableConfig = reactive({
    tableData: [],
    colConfigs: [
        {
            prop: 'colName',
            title: '字段名',
            minWidth: 120,
            showOverflowTooltip: true
        },
        {
            prop: 'colType',
            title: '类型',
            minWidth: 80,
            showOverflowTooltip: true
        },
        {
            prop: 'remark',
            title: '备注',
            minWidth: 100,
            showOverflowTooltip: true
        }
    ],
    seqType: 'seq',
    loading: false
})
const formData = ref<any>({})
const instanceRef = ref<any>()
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
    fieldsTableConfig.tableData = formData.value.outColumnList || []
    fieldsDialogVisible.value = true
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
