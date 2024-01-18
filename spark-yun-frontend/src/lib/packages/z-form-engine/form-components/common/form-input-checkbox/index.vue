<template>
    <form-render-item
        class="form-input-radio"
        :formConfig="formConfig"
        :rules="rules"
        :isDragger="isDragger"
    >
        <el-checkbox-group v-model="formData" :disabled="formConfig.disabled">
            <el-checkbox v-for="item in optionList" :key="item.value" :label="item.value">{{ item.label }}</el-checkbox>
        </el-checkbox-group>
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
const optionList = computed(() => {
    return props.formConfig.options.filter(o => o.label && o.value)
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
.form-input-radio {
    .el-form-item__content {
        .el-checkbox-group {
            .el-checkbox__label {
                font-size: 12px;
            }    
        }  
    }
}
</style>
