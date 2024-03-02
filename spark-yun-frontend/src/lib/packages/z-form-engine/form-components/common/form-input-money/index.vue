<template>
    <form-render-item
        class="form-input-money"
        :formConfig="formConfig"
        :rules="rules"
        :isDragger="isDragger"
    >
        <el-input
            v-model="formData"
            :disabled="formConfig.disabled"
            :placeholder="formConfig.placeholder"
            :maxlength="formConfig.maxlength"
            :readonly="isDragger"
            @focus="focusEvent"
            @input="inputEvent"
            @blur="blurEvent"
        />
    </form-render-item>
</template>
<script lang="ts" setup>
import { defineProps, defineEmits, computed, ref, watch } from 'vue'
import FormRenderItem from '../../form-render-item/index.vue'

const validateAssetValue = (value: string): string => {
    let val = value?.replace(/,/g, '').replace(/[^0-9.]/g, '')
    let valueNum = Number(val)
    return value && isNaN(valueNum) ? '' : val
}

const props = defineProps(['renderSence', 'modelValue', 'formConfig', 'isDragger'])
const emit = defineEmits(['update:modelValue'])
const formData = computed({
    get() {
        if (focusStatus.value) {
            return props.modelValue
        } else {
            return props.modelValue !== null && props.modelValue !== undefined && props.modelValue !== '' ? Number(props.modelValue).toLocaleString('zh-CN', {'minimumFractionDigits':2, 'maximumFractionDigits':2}) : ''
        }
    },
    set(value) {
        emit('update:modelValue', value)
    }
})
const focusStatus = ref(false)
const rules = ref([
    {
        required: true,
        message: `请输入${props.formConfig.label}`,
        trigger: ['blur', 'change']
    }
])
watch(() => props.formConfig.defaultValue, () => {
    if (props.renderSence === 'new') {
        emit('update:modelValue', props.formConfig.defaultValue)
    }
})
function focusEvent(e: any) {
    focusStatus.value = true
    emit('update:modelValue', validateAssetValue(e.target.value))
}
function inputEvent(input: string) {
    emit('update:modelValue', validateAssetValue(input))
}

function blurEvent(e: any) {
    focusStatus.value = false
    let value = e.target.value || ''
    emit('update:modelValue', value ? parseFloat(validateAssetValue(value)) : null)
}
</script>

<style lang="scss">
.form-input-money {
    .el-form-item__content {
        .el-input {
            .el-input__wrapper {
                .el-input__inner {
                    text-align: right !important;
                }
            }
        }
    }
}
</style>