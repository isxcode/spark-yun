<template>
    <BlockDrawer :drawer-config="drawerConfig">
        <el-scrollbar>
            <el-form
              ref="formRef"
              label-position="left"
              label-width="120px"
              :model="formData"
            >
                <div class="base-container">
                    <div class="base-title">基础信息</div>
                    <el-form-item label="名称" prop="name" :rules="rules.name">
                        <el-input v-model="formData.name" maxlength="100" placeholder="请输入" />
                    </el-form-item>
                    <el-form-item label="编码" prop="aliaCode" :rules="rules.aliaCode">
                        <el-input disabled v-model="formData.aliaCode" />
                    </el-form-item>
                    <el-form-item label="备注">
                        <el-input
                            v-model="formData.remark"
                            show-word-limit
                            type="textarea"
                            maxlength="200"
                            :resize="'none'"
                            :autosize="{ minRows: 4, maxRows: 4 }"
                            placeholder="请输入"
                        />
                    </el-form-item>
                </div>
                <div class="main-config-container">
                    <div class="base-title">节点信息</div>
                    <component
                        ref="instanceRef"
                        :is="currentComponent(formData.type)"
                        v-model="formData"
                        :incomeNodes="incomeNodes"
                    ></component>
                </div>
            </el-form>
        </el-scrollbar>
    </BlockDrawer>
</template>

<script lang="ts" setup>
import { computed, nextTick, reactive, ref, shallowRef, markRaw } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import BlockDrawer from '@/components/block-drawer/index.vue'
import { cloneDeep, clone } from 'lodash-es'

import DataInput from './data-input/index.vue'
import DataOutput from './data-output/index.vue'
import DataTransfrom from './data-transform/index.vue'
import DataJoin from './data-join/index.vue'
import DataUnion from './data-union/index.vue'
import DataAddCol from './data-add-col/index.vue'
import DataCustom from './data-custom/index.vue'

const Components = {
    DataInput,
    DataOutput,
    DataTransfrom,
    DataJoin,
    DataUnion,
    DataAddCol,
    DataCustom
}

const formRef = ref<FormInstance>()
const callback = ref<any>()
const incomeNodes = ref<any>()
const formData = ref<any>({})
const instanceRef = ref<any>()
const formInstance = shallowRef<any>(Components)
const drawerConfig = reactive({
    title: '配置详情',
    visible: false,
    width: '800',
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
            DATA_TRANSFORM: 'DataTransfrom',
            DATA_ADD_COL: 'DataAddCol',
            DATA_CUSTOM: 'DataCustom'
        }
        return markRaw(formInstance.value[componentConfig[type]])
    }
})

function showModal(prevNode: any, cb: () => void, data: any) {
    callback.value = cb
    incomeNodes.value = prevNode
    drawerConfig.title = data.typeName

    formData.value = cloneDeep(data)
    drawerConfig.visible = true;
}

function okEvent() {
    instanceRef.value.setData && instanceRef.value.setData()
    formRef.value?.validate((valid: boolean) => {
        if (valid) {
            callback.value(formData.value).then((res: any) => {
                drawerConfig.okConfig.loading = false
                if (res === undefined) {
                    drawerConfig.visible = false
                } else {
                    drawerConfig.visible = true
                }
            }).catch((err: any) => {
                drawerConfig.okConfig.loading = false
            })
        } else {
            ElMessage.warning('请将表单输入完整')
        }
    })
}

function closeEvent() {
    drawerConfig.visible = false;
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
