<template>
    <form-render-item
        class="form-input-phone"
        :formConfig="formConfig"
        :rules="rules"
        :customRules="customRules"
        :isDragger="isDragger"
    >
        <el-input
            v-model="formData"
            clearable
            :disabled="formConfig.disabled"
            :placeholder="formConfig.placeholder"
            :maxlength="formConfig.maxlength"
            :readonly="isDragger"
            @input="inputEvent"
        />
    </form-render-item>
</template>
<script lang="ts" setup>
import { defineProps, defineEmits, computed, ref, watch } from 'vue'
import FormRenderItem from '../../form-render-item/index.vue'

const validateAssetValue = (value: string): string => {
    let val = value?.replace(/[^0-9]/g, '')
    return val
}

const checkPhone = (rule: any, value: any, callback: any) => {
    const phoneReg = /^1[34578]\d{9}$$/
    if (value && !phoneReg.test(value)) {
        callback(new Error('电话号码格式不正确'))
    } else {
        callback()
    }
}

const props = defineProps(['renderSence', 'modelValue', 'formConfig', 'isDragger'])
const emit = defineEmits(['update:modelValue'])
const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})
const rules = ref([
    {
        required: true,
        message: `请输入${props.formConfig.label}`,
        trigger: ['blur', 'change']
    }
])
const customRules = ref([
    { validator: checkPhone, trigger: ['blur', 'change'] }
])
watch(() => props.formConfig.defaultValue, () => {
    if (props.renderSence === 'new') {
        emit('update:modelValue', props.formConfig.defaultValue)
    }
}, {
    immediate: true
})

function inputEvent(input: string) {
    emit('update:modelValue', validateAssetValue(input))
}
</script>

<style lang="scss">
.form-input-phone {
    &.form-input__dragger {
        .el-form-item__content {
            .el-input {
                pointer-events: none;
            }
        }
    }
}
</style>