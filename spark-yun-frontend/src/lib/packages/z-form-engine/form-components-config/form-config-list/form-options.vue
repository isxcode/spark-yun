<template>
    <el-form-item
        ref="elFormItemRef"
        label="数据字典"
        class="form-options"
        prop="options"
        :rules="rules"
    >
        <span class="add-btn">
            <el-icon @click="addNewOption"><CirclePlus /></el-icon>
        </span>
        <div class="form-options__list">
            <draggable :list="formConfig.options" :animation="150" handle=".move" itemKey="label-value">
                <template #item="{ element, index }">
                    <div class="form-options__item">
                        <div class="input-item">
                            <span class="item-label">键</span>
                            <el-input v-model="element.label" placeholder="请输入"></el-input>
                        </div>
                        <div class="input-item">
                            <span class="item-label">值</span>
                            <el-input v-model="element.value" placeholder="请输入"></el-input>
                        </div>
                        <div class="option-btn">
                            <el-icon v-if="formConfig.options.length > 1" class="remove" @click="removeItem(index)"><CircleClose /></el-icon>
                            <el-icon class="move"><Sort /></el-icon>
                        </div>
                    </div>
                </template>
            </draggable>
        </div>
    </el-form-item>
</template>

<script lang="ts" setup>
import { ref, defineProps, defineEmits, computed, watch, nextTick } from 'vue'
import draggable from 'vuedraggable'

interface Option {
    label: string
    value: string
}

const optionsRule = (rule: any, value: any, callback: any) => {
    const valueList = (value || []).map(v => v.value).filter(v => !!v)
    if (value && value.some((item: Option) => !item.label || !item.value)) {
        callback(new Error('请将键/值输入完整'))
    } else if (value && valueList.length !== Array.from(new Set(valueList)).length) {
        callback(new Error('值重复，请重新输入'))
    } else {
        callback()
    }
}
const elFormItemRef = ref()
const rules = ref([
    { validator: optionsRule, trigger: ['blur', 'change'] }
])
const props = defineProps(['modelValue', 'formConfig'])
const emit = defineEmits(['update:modelValue'])
const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

watch(() => props.formConfig?.uuid, (e) => {
    nextTick(() => {
        elFormItemRef.value?.validate().catch(() => {
            console.warn('请将组件配置填写完整-数据字典')
        })
    })
}, {
    immediate: true
})

function addNewOption() {
    const options: Option[] = props.modelValue
    options.push({
        label: '',
        value: ''
    })
    emit('update:modelValue', options)
    nextTick(() => {
        elFormItemRef.value?.validate().catch(() => {
            console.warn('请将组件配置填写完整-数据字典')
        })
    })
}

function removeItem(index: number) {
    const options: Option[] = [...props.modelValue] 
    options.splice(index, 1)
    emit('update:modelValue', options)
}
</script>

<style lang="scss">
.form-options {
    position: relative;

    .add-btn {
        position: absolute;
        right: 0;
        top: -28px;
        .el-icon {
            color: getCssVar('color', 'primary');
            cursor: pointer;
            font-size: 16px;
        }
    }
    .form-options__list {
        .form-options__item {
            display: flex;
            margin-bottom: 8px;
            .input-item {
                display: flex;
                font-size: 12px;
                margin-right: 8px;
                color: #303133;
                .item-label {
                    margin-right: 8px;
                }
                .el-input {
                    .el-input__wrapper {
                        padding: 0;
                    }
                }
            }
            .option-btn {
                display: flex;
                height: 32px;
                align-items: center;
                .remove {
                    color: red;
                    cursor: pointer;
                    margin-right: 8px;
                    &:hover {
                        color: getCssVar('color', 'primary');
                    }
                }
                .move {
                    color: getCssVar('color', 'primary');
                    &:active {
                        cursor: move;
                    }
                }
            }
        }
    }
}
</style>