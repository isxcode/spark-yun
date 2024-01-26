<template>
    <el-form-item label="是否为主键" class="form-isPrimaryColumn form-config-switch">
        <el-switch
            v-model="formData"
            :width="32"
            :disabled="formConfig.codeType === 'table'"
        >
        </el-switch>
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
