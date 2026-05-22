<template>
    <form-render-item
        class="form-input-time"
        :formConfig="formConfig"
        :rules="rules"
        :isDragger="isDragger"
    >
        <el-time-picker
            v-model="formData"
            value-format="HH:mm:ss"
            format="HH:mm:ss"
            :disabled="formConfig.disabled"
            :placeholder="formConfig.placeholder"
            :readonly="isDragger"
            :teleported="true"
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
        message: `请选择${props.formConfig.label}`,
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
.form-input-time {
    width: 100%;
    .el-date-editor {
        width: 100%;
        &.el-input {
            width: 100%;
            .el-input__wrapper {
                width: 100%;
                box-sizing: border-box;
                .el-input__inner {
                    padding-left: 0;
                }
            }
        }
    }
}
</style>
