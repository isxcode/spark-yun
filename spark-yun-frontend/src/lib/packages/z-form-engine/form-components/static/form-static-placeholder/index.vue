<template>
    <form-render-item
        class="form-static-placeholder"
        :class="{ 'form-static-placeholder__dragger': isDragger }"
        :formConfig="formConfig"
        :isDragger="isDragger"
    >
        <span
            :style="{ color: formConfig.colorPicker}"
            class="text-container"
        >{{formConfig.label}}</span>
    </form-render-item>
</template>
<script lang="ts" setup>
import { defineProps, defineEmits, computed, ref, watch } from 'vue'
import FormRenderItem from '../../form-render-item/index.vue'

const props = defineProps(['renderSence', 'modelValue', 'formConfig', 'isDragger'])
</script>

<style lang="scss">
.form-static-placeholder {
    padding: 0 0 0 4px !important;
    &.form-static-placeholder__dragger {
        padding: 12px 18px !important;
    }
    .form-render-item__btn {
        top: -4px;
        right: -8px;
    }
    .el-form-item__label {
        display: none !important;
    }
}
</style>