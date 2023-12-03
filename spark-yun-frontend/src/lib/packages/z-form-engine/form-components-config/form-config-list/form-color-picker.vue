<template>
    <el-form-item label="颜色" class="form-color-picker">
        <el-color-picker
            v-model="formData"
            color-format="hex"
            :popper-class="'form-color-picker__popper'"
            :predefine="predefine"
            :show-alpha="false"
        />
    </el-form-item>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, computed, ref } from 'vue'

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

const predefine = ref([
    '#000000', '#409EFF', '#9D3939', '#4C83EA', '#EF1F1F', '#09DD37', '#BCBCBC', '#E254FF', '#F5580A', '#28E7E1'
])
</script>

<style lang="scss">
.form-color-picker {
    .el-form-item__content {
        .el-color-picker {
            width: 100%;
            .el-color-picker__trigger {
                width: 50%;
            }
        }
    }
}
.form-color-picker__popper {
    .el-color-dropdown__btns {
        .el-button {
            display: inline-flex;
            height: 32px;
        }
    }
}
</style>
