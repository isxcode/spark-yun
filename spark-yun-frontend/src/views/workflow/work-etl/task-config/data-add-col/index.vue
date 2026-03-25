<template>
    <div class="config-components data-add-col">
        <el-form-item class="form-item-top" label-width="0">
            <div class="form-options__list">
                <div class="form-options__item" v-for="(element, index) in formData.addColEtl" :key="index">
                    <el-form-item :prop="`addColEtl[${index}].colName`" :rules="rules.colName">
                        <el-input v-model="element.colName" clearable placeholder="字段名"></el-input>
                    </el-form-item>
                    <el-form-item :prop="`addColEtl[${index}].colType`" :rules="rules.colType">
                        <el-input v-model="element.colType" clearable placeholder="类型"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-input v-model="element.remark" clearable placeholder="备注"></el-input>
                    </el-form-item>
                    <div class="option-btn">
                        <el-icon v-if="formData.addColEtl.length > 1" class="remove" @click="removeItem(index)">
                            <CircleClose />
                        </el-icon>
                    </div>
                </div>
            </div>
            <div class="transform-condition-actions">
                <el-button link type="primary" size="small" @click="addNewField">
                    <el-icon><Plus /></el-icon>
                    字段
                </el-button>
            </div>
        </el-form-item>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineEmits, computed, onMounted, reactive } from 'vue'
import { FormRules } from 'element-plus'

const props = defineProps<{
    modelValue: any,
    incomeNodes: any
}>()
const emit = defineEmits(['update:modelValue'])

const rules = reactive<FormRules>({
    colName: [{ required: true, message: '字段名不能为空', trigger: ['blur', 'change'] }],
    colType: [{ required: true, message: '类型不能为空', trigger: ['blur', 'change'] }]
})

const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

function addNewField() {
    if (!formData.value.addColEtl) {
        formData.value.addColEtl = []
    }
    formData.value.addColEtl.push({
        colName: '',
        colType: '',
        remark: ''
    })
}

function removeItem(index: number) {
    formData.value.addColEtl.splice(index, 1)
}

onMounted(() => {
    if (props.incomeNodes && props.incomeNodes[0]) {
        const fromAliaCode = props.incomeNodes[0].data.nodeConfigData.aliaCode || ''
        formData.value.outColumnList = props.incomeNodes[0].data.nodeConfigData.outColumnList.filter((item: any) => item.checked !== false).map((column: any) => {
            return {
                colName: column.colName,
                colType: column.colType,
                fromAliaCode: fromAliaCode,
                fromColName: column.colName,
                remark: column.remark
            }
        })
        formData.value.inputEtl = props.incomeNodes[0].data.nodeConfigData.inputEtl
    }
    if (!formData.value.addColEtl || !formData.value.addColEtl.length) {
        formData.value.addColEtl = [{ colName: '', colType: '', remark: '' }]
    }
})
</script>

<style lang="scss">
.data-add-col {
    .transform-condition-actions {
        margin-top: 6px;
        display: flex;
        padding-left: 2px;
    }
}
</style>
