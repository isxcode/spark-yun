<template>
    <el-form-item
        ref="elFormItemRef"
        class="form-config-switch-info"
        label="文字描述"
        prop="switchInfo"
        :rules="rules"
    >
        <div class="info-item">
            <span class="info-item__label">开启</span>
            <el-input v-model="formData.open" :clearable="true" maxlength="2000" placeholder="请输入"></el-input>
        </div>
        <div class="info-item item-last">
            <span class="info-item__label">关闭</span>
            <el-input v-model="formData.close" :clearable="true" maxlength="2000" placeholder="请输入"></el-input>
        </div>
    </el-form-item>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, computed, ref, watch, nextTick } from 'vue'

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
const configRule = (rule: any, value: any, callback: any) => {
    if (!value || !value.open || !value.close) {
        callback(new Error('请将开启/关闭状态文字描述输入完整'))
    } else if (value.open === value.close) {
        callback(new Error('开启与关闭状态文字描述不能重复'))
    } else {
        callback()
    }
}
const elFormItemRef = ref()
const rules = ref([
    { validator: configRule, trigger: ['blur', 'change'] }
])
watch(() => props.formConfig?.uuid, (e) => {
    nextTick(() => {
        elFormItemRef.value?.validate().catch(() => {
            console.warn('请将组件配置填写完整-文字描述')
        })
    })
}, {
    immediate: true
})
</script>

<style lang="scss">
.form-config-switch-info {
    .info-item {
        display: flex;
        align-items: center;
        width: 100%;
        .info-item__label {
            width: 50px;
            font-size: 12px;
            color: #606266;
        }
        &.item-last {
            margin-top: 12px;
        }
    }
}
</style>
