<template>
    <el-form-item label="是否禁用" class="form-disabled form-config-switch">
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

<style lang="scss">
.form-config-switch {
    position: relative;
    display: flex;
    align-items: center;
    .el-form-item__content {
        position: absolute;
        right: 0;
        top: -4px;
    }
}
</style>