<template>
    <el-form-item label="默认值" class="form-default-time">
        <el-time-picker
            v-model="formData"
            placeholder="请选择"
            value-format="HH:mm:ss"
            format="HH:mm:ss"
        />
    </el-form-item>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, computed } from 'vue'

const props = defineProps(['modelValue'])
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
.form-default-time {
    width: 100%;
    .el-date-editor {
        &.el-input {
            width: 100%;
            .el-input__inner {
                padding-left: 0 !important;
            }
        }
        .el-input__wrapper {
            padding: 0 12px 0 8px !important;
        }
    }
}
</style>
