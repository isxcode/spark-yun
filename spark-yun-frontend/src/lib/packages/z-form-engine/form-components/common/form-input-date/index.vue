<template>
    <form-render-item
        class="form-input-date"
        :formConfig="formConfig"
        :rules="rules"
        :isDragger="isDragger"
    >
        <el-date-picker
            v-model="formData"
            :type="formConfig.dateType"
            :value-format="formatType[formConfig.dateType]"
            :format="formatType[formConfig.dateType]"
            :disabled="formConfig.disabled"
            :placeholder="formConfig.placeholder"
            :readonly="isDragger"
            :teleported="false"
        />
    </form-render-item>
</template>
<script lang="ts" setup>
import { defineProps, defineEmits, computed, ref, watch } from 'vue'
import FormRenderItem from '../../form-render-item/index.vue'

const props = defineProps(['renderSence', 'modelValue', 'formConfig', 'isDragger'])
const emit = defineEmits(['update:modelValue'])
const formData = computed({
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
const rules = ref([
    {
        required: true,
        message: `请选择${props.formConfig.label}`,
        trigger: ['blur', 'change']
    }
])
watch(() => props.formConfig.defaultValue, () => {
    if (props.renderSence === 'new') {
        emit('update:modelValue', props.formConfig.defaultValue)
    }
})
</script>

<style lang="scss">
.form-input-date {
    width: 100%;
    .el-date-editor {
        &.el-input {
            width: 100%;
            .el-input__wrapper {
                .el-input__inner {
                    padding-left: 0;
                    pointer-events: none;
                }
            }
        }
    }
}
</style>
