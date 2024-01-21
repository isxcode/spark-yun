<template>
    <el-form-item label="文本长度" class="form-width">
        <el-input :disabled="formConfig.codeType === 'table'" v-model.number="formData" :clearable="true" maxlength="30" placeholder="请输入"></el-input>
    </el-form-item>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, computed } from 'vue'

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
</script>
