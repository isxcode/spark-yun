<template>
    <el-form-item
        class="form-render-item"
        :class="{'form-render-item__edit': !isDragger, 'form-input__dragger': isDragger }"
        :label="formConfig.label || ' '"
        :style="{ 'width': '100%' }"
        :prop="!isDragger ? formConfig.formValueCode : ''"
        :rules="rulesList"
    >
        <div class="form-render-item__btn">
            <el-icon @click="removeInstance"><Delete /></el-icon>
        </div>
        <div v-if="isDragger && formConfig.type !== 'static'" class="form-render-item__mark"></div>
        <slot></slot>
    </el-form-item>
</template>

<script lang="ts" setup>
import { defineProps, computed, defineEmits } from 'vue'

const emit = defineEmits(['removeInstance'])
const props = defineProps(['renderSence', 'formData', 'formConfig', 'isDragger', 'rules', 'customRules'])
const componentWidth = computed(() => {
    return `${props.formConfig.width * 25}%`
})

const rulesList = computed(() => {
    const customRulesList = props.customRules && props.customRules.length ? props.customRules : []
    if (props.formConfig.required) {
        return [...props.rules, ...customRulesList]
    } else {
        return [...customRulesList]
    }
})

function removeInstance() {
    emit('removeInstance', props.formConfig)
}
</script>

<style lang="scss">
.form-render-item {
    margin-bottom: 12px;
    display: inline-block !important;
    padding: 12px 18px;
    box-sizing: border-box;
    margin-bottom: 0 !important;
    position: relative;
    .form-render-item__btn {
        position: absolute;
        top: -28px;
        right: 0;
        height: 16px;
        width: 30px;
        z-index: 1;
        display: none;
        align-items: center;
        justify-content: flex-end;
        .el-icon {
            cursor: pointer;
            color: red;
        }
    }
    .form-render-item__mark {
        position: absolute;
        width: 100%;
        height: 42px;
        opacity: 0;
        z-index: 10;
    }
    &.form-render-item__edit {
        padding: 12px 8px;
        .el-form-item__label {
            margin-bottom: 0 !important;
        }
    }
    .el-form-item__label {
        font-size: 12px;
        line-height: 12px;
        min-height: 12px;
        height: 22px;
        display: inline-block;
        position: relative;
        pointer-events: none;
        &::before {
            position: absolute;
            left: -8px;
        }
    }
    .el-form-item__content {
        display: block;
        .el-input {
            font-size: 12px;
            height: 32px;
            .el-input__inner {
                height: 100%;
                border-radius: 2px;
                padding: 0 8px;
            }
        }
    }
}
</style>
