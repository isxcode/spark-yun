<template>
    <form-render-item
        class="form-input-text"
        :formConfig="formConfig"
        :rules="rules"
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
watch(() => props.formConfig.defaultValue, () => {
    if (props.renderSence === 'new') {
        emit('update:modelValue', props.formConfig.defaultValue)
    }
})
</script>

<style lang="scss">
.form-input-text {
    &.form-input__dragger {
        .el-form-item__content {
            .el-input {
                pointer-events: none;
            }
        }
    }
}
</style>