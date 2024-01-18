<template>
    <el-form-item
        ref="elFormItemRef"
        class="form-code-select"
        label="字段绑定"
        prop="formValueCode"
        :rules="formConfig.codeType === 'custom' ? rules : rulesSelect"
    >
        <div class="form-code-select__type">
            <el-radio-group v-model="formConfig.codeType" class="ml-4" @change="codeTypeChange">
                <el-radio label="custom" size="small">字段编码</el-radio>
                <el-radio label="table" size="small">表字段</el-radio>
            </el-radio-group>
        </div>
        <template v-if="formConfig.codeType === 'custom'">
            <el-input v-model="formData" :clearable="true" maxlength="30" placeholder="请输入"></el-input>
        </template>
        <template v-else>
            <el-select
                v-model="formData"
                clearable
                filterable
                placeholder="请选择"
                @visible-change="visibleChange"
            >
                <el-option
                    v-for="item in tableCodeList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
            </el-select>
            <div class="select-loading" v-if="loading">
                <el-icon class="is-loading"><Loading /></el-icon>
            </div>
        </template>
    </el-form-item>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, computed, ref, watch, nextTick } from 'vue'

interface Option {
    label: string
    value: string
}

const props = defineProps(['renderSence', 'modelValue', 'formConfig', 'getTableCodesMethod'])
const emit = defineEmits(['update:modelValue'])
const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})
const rules = ref([
    {
        required: true,
        message: `请输入字段编码`,
        trigger: ['blur', 'change']
    }
])
const rulesSelect = ref([
    {
        required: true,
        message: `请选择表字段`,
        trigger: ['blur', 'change']
    }
])
const tableCodeList = ref<Option[]>([])
const loading = ref(false)
const elFormItemRef = ref()

watch(() => props.formConfig?.uuid, () => {
    nextTick(() => {
        elFormItemRef.value?.validate().catch(() => {
            console.warn('请将组件配置填写完整-字段绑定')
        })
    })
}, {
    immediate: true
})

function visibleChange(e: boolean) {
    if (e && props.getTableCodesMethod && props.getTableCodesMethod instanceof Function) {
        loading.value = true
        props.getTableCodesMethod().then((res: Option[]) => {
            loading.value = false
            tableCodeList.value = res
        }).catch((err: any) => {
            loading.value = false
        })
    }
}
function codeTypeChange() {
    emit('update:modelValue', '')
}
</script>

<style lang="scss">
.form-code-select {
    position: relative;
    .form-code-select__type {
        position: absolute;
        top: -28px;
        right: 0;
        .el-radio-group {
            .el-radio {
                &:first-child {
                    margin-right: 12px;
                }
            }
        }
    }
    .select-loading {
        position: absolute;
        right: 6px;
        width: 26px;
        height: 26px;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: #ffffff;
    }
}
</style>
