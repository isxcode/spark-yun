<template>
    <el-form-item label="默认值" class="form-default-date">
        <el-date-picker
            v-model="formData"
            placeholder="请选择"
            :type="formConfig.dateType"
            :value-format="formatType[formConfig?.dateType]"
            :format="formatType[formConfig?.dateType]"
        />
    </el-form-item>
</template>

<script lang="ts" setup>
import { ref, defineProps, defineEmits, computed, watch } from 'vue'

const props = defineProps(['renderSence', 'modelValue', 'formConfig'])
const emit = defineEmits(['update:modelValue'])
let formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})
const formatType = ref({
    date: 'YYYY-MM-DD',
    month: 'YYYY-MM',
    year: 'YYYY'
})

watch(() => props.formConfig?.dateType, () => {
    emit('update:modelValue', null)
})
</script>

<style lang="scss">
.form-default-date {
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