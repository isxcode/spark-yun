<template>
    <form-render-item
        class="form-input-email"
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
        />
    </form-render-item>
</template>
<script lang="ts" setup>
import { defineProps, defineEmits, computed, ref, watch } from 'vue'
import FormRenderItem from '../../form-render-item/index.vue'

const checkMail = (rule: any, value: any, callback: any) => {
    const emailReg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/
    if (value && !emailReg.test(value)) {
        callback(new Error('请输入正确的邮箱'))
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
    { validator: checkMail, trigger: ['blur', 'change'] }
])
watch(() => props.formConfig.defaultValue, () => {
    if (props.renderSence === 'new') {
        emit('update:modelValue', props.formConfig.defaultValue)
    }
}, {
    immediate: true
})
</script>

<style lang="scss">
.form-input-email {
    &.form-input__dragger {
        .el-form-item__content {
            .el-input {
                pointer-events: none;
            }
        }
    }
}
</style>