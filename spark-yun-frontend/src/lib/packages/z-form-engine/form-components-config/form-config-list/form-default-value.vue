<template>
    <el-form-item label="默认值" :class="{ 'form-config-switch': formConfig.componentType === 'FormInputSwitch' }">
        <template v-if="formConfig.componentType === 'FormInputSwitch'">
            <el-switch
                v-model="formData"
            >
            </el-switch>
        </template>
        <template v-else>
            <el-input v-model="formData" :clearable="true" :maxlength="formConfig.maxlength || 200" placeholder="请输入"></el-input>
        </template>
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
