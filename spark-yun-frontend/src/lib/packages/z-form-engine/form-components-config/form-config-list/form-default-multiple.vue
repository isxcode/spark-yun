<template>
    <el-form-item label="默认值" :class="{ 'form-config-switch': formConfig.componentType === 'FormInputSwitch' }">
        <el-select
            v-if="formConfig.multiple"
            v-model="formData"
            :clearable="true"
            filterable
            placeholder="请选择"
            :multiple="formConfig.multiple"
            :collapse-tags="formConfig.multiple"
            :collapse-tags-tooltip="formConfig.multiple"
            @visible-change="visibleChange"
        >
            <el-option
                v-for="(item, index) in optionList"
                :key="index"
                :label="item.label"
                :value="item.value"
            />
        </el-select>
        <el-select
            v-else
            v-model="formData"
            :clearable="true"
            filterable
            placeholder="请选择"
            @visible-change="visibleChange"
        >
            <el-option
                v-for="(item, index) in optionList"
                :key="index"
                :label="item.label"
                :value="item.value"
            />
        </el-select>
    </el-form-item>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, computed, watch, nextTick } from 'vue'

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
const optionList = computed(() => {
    return props.formConfig.options.filter(o => o.label && o.value)
})
watch(() => props.formConfig?.options, () => {
    emit('update:modelValue', props.formConfig?.multiple ? [] : '')
}, {
    deep: true
})
watch(() => props.formConfig?.multiple, (e) => {
    emit('update:modelValue', e ? [] : '')
})
function visibleChange(e: boolean) {
    if (e) {
        console.log('后续用来请求关联表单')
    }
}
</script>
