<template>
    <form-render-item
        class="form-input-select"
        :formConfig="formConfig"
        :isDragger="isDragger"
        :rules="rules"
    >
        <template v-if="formConfig.multiple">
            <el-select
                v-model="formData"
                clearable
                filterable
                :multiple="formConfig.multiple"
                :placeholder="formConfig.placeholder"
                :disabled="formConfig.disabled"
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
        </template>
        <template v-else>
            <el-select
                v-model="formData"
                clearable
                filterable
                :placeholder="formConfig.placeholder"
                :disabled="formConfig.disabled"
                @visible-change="visibleChange"
            >
                <el-option
                    v-for="(item, index) in optionList"
                    :key="index"
                    :label="item.label"
                    :value="item.value"
                />
            </el-select>
        </template>
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

function visibleChange(e: boolean) {
    if (e) {
        console.log('这里请求接口')
    }
}
</script>

<style lang="scss">

</style>