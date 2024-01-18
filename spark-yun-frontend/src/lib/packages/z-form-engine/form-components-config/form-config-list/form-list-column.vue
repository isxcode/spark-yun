<template>
    <el-form-item label="设为表头" class="form-config-column form-config-switch">
        <el-switch
            v-model="formData"
            :width="32"
        >
        </el-switch>
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
