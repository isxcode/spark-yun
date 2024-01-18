<template>
    <form-render-item
        class="form-input-number"
        :formConfig="formConfig"
        :rules="rules"
        :isDragger="isDragger"
    >
        <el-input-number
            v-model="formData"
            clearable
            :disabled="formConfig.disabled"
            :placeholder="formConfig.placeholder"
            :readonly="isDragger"
            :min="formConfig.min"
            :max="formConfig.max"
            :precision="formConfig.precision"
            controls-position="right"
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
.form-input-number {
    .el-form-item__content {
        .el-input-number {
            line-height: 28px;
            display: inline-block;
        }
    }
}
</style>