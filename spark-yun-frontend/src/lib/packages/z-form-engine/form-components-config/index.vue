<template>
    <div class="form-components-config">
        <el-scrollbar>
            <el-form
                v-if="formData"
                class="form-config"
                ref="formConfigRef"
                :model="formData"
                :label-position="'top'"
                @validate="validateChange"
            >
                <template v-for="(config, index) in configList" :key="index">
                    <component
                        v-model="formData[getConfigComponentKey(config)]"
                        :formConfig="formConfig"
                        :is="getConfigComponentName(config)"
                        :getTableCodesMethod="getTableCodesMethod"
                        @formConfigChange="formConfigChange"
                    ></component>
                </template>
            </el-form>
            <template v-else>
                <empty-page></empty-page>
            </template>
        </el-scrollbar>
    </div>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, computed, ref, shallowRef, markRaw } from 'vue'
import FormSetConfig from './form-set-config'
import FormConfigConmponents from './form-config-components'

const props = defineProps(['modelValue', 'configList', 'formConfig', 'getTableCodesMethod'])
const emit = defineEmits(['update:modelValue', 'componentListChange', 'formConfigChange'])
const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})
const formSetConfig = ref(FormSetConfig)
const formConfigConmponents = shallowRef(FormConfigConmponents)

const getConfigComponentName = computed(() => {
    return (code: string) => {
        let configInstance = null
        try {
            configInstance = formSetConfig.value.find(item => item.formTypeCode === code)
        } catch (error) {
            console.error('请检查配置组件是否注册')
        }
        return markRaw(formConfigConmponents.value[configInstance.formInstanceName])
    }
})
const getConfigComponentKey = computed(() => {
    return (code: string) => {
        let configInstance
        try {
            configInstance = formSetConfig.value.find(item => item.formTypeCode === code)
        } catch (error) {
            console.error('请检查配置组件是否注册')
        }
        return configInstance.formConfigValueCode
    }
})

function formConfigChange(e: any) {
    emit('formConfigChange', e)
}

function validateChange(prop: string, isValid: boolean, message: string): void {
    props.formConfig.valid = isValid
}
</script>

<style lang="scss">
.form-components-config {
    min-width: 270px;
    width: 270px;
    height: 100%;
    overflow: auto;
    .el-form {
        padding: 8px 12px;
        box-sizing: border-box;
        .el-form-item {
            margin-bottom: 16px;
            .el-form-item__label {
                font-size: 12px;
                line-height: 12px !important;
                min-height: 12px !important;
                display: inline-block;
                position: relative;
                &::before {
                    position: absolute;
                    left: -8px;
                }
            }
            .el-form-item__content {
                .el-input {
                    font-size: 12px;
                    height: 32px;
                    .el-input__inner {
                        height: 100%;
                        border-radius: 2px;
                        padding: 0 8px;
                    }
                    .el-input__wrapper {
                        padding: 0 12px 0 0;
                    }
                }
            }
        }
    }
}
</style>