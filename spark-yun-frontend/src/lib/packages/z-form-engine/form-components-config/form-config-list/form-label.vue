<template>
    <el-form-item label="组件名称">
        <el-input v-model="formData" :clearable="true" maxlength="2000" placeholder="请输入"></el-input>
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
