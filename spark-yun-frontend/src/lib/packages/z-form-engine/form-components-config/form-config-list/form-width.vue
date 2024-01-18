<template>
    <el-form-item label="宽度" class="form-width">
        <el-radio-group v-model="formData" size="small">
            <el-radio-button :label="1">25%</el-radio-button>
            <el-radio-button :label="2">50%</el-radio-button>
            <el-radio-button :label="3">75%</el-radio-button>
            <el-radio-button :label="4">100%</el-radio-button>
        </el-radio-group>
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

<style lang="scss">
.form-width {
    .el-radio-group {
        width: 100%;
        .el-radio-button {
            width: 25%;
            .el-radio-button__inner {
                width: 100%;
            }
        }
        .el-radio-button:first-child .el-radio-button__inner {
            border-radius: 2px 0 0 2px;
        }
        .el-radio-button:last-child .el-radio-button__inner {
            border-radius: 0 2px 2px 0;
        }
    }
}
</style>
