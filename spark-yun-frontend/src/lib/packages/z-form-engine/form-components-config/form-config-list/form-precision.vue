<template>
    <el-form-item label="数值精度">
        <el-input-number
            v-model="formData"
            :clearable="true"
            controls-position="right"
            placeholder="请输入"
        />
    </el-form-item>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, computed } from 'vue'

const props = defineProps(['renderSence', 'modelValue', 'formConfig',])
const emit = defineEmits(['update:modelValue'])
const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})
</script>
