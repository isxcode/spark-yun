<template>
    <el-form-item label="日期类型">
        <el-select v-model="formData" placeholder="请选择">
            <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
        </el-select>
    </el-form-item>
</template>

<script lang="ts" setup>
import { ref, defineProps, defineEmits, computed } from 'vue'

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
const options = ref([
    {
        label: '日',
        value: 'date'
    },
    {
        label: '月',
        value: 'month'
    },
    {
        label: '年',
        value: 'year'
    }
])
</script>
