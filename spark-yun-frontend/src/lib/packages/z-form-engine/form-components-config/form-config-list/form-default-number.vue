<template>
    <el-form-item label="默认值">
        <el-input-number
            v-model="formData"
            :clearable="true"
            controls-position="right"
            :precision="formConfig.precision"
            placeholder="请输入"
        />
    </el-form-item>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, computed, watch } from 'vue'

const props = defineProps(['renderSence', 'modelValue', 'formConfig'])
const emit = defineEmits(['update:modelValue'])
const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})
watch(() => props.formConfig.precision, () => {
    emit('update:modelValue', null)
})
</script>
