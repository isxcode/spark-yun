<template>
    <form-render-item
        class="form-input-switch"
        :formConfig="formConfig"
        :isDragger="isDragger"
    >
        <el-switch
            v-model="formData"
            :disabled="formConfig.disabled"
        >
        </el-switch>
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

watch(() => props.formConfig.defaultValue, () => {
    if (props.renderSence === 'new') {
        emit('update:modelValue', props.formConfig.defaultValue)
    }
})
</script>

<style lang="scss">
.form-input-switch {
    &.form-input__dragger {
        .el-form-item__content {
            .el-switch {
                align-items: flex-start;
                pointer-events: none;
            }
        }
    }
}
</style>