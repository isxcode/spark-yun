<template>
    <div class="config-components">
        <el-form-item class="form-item-top" label="过滤条件">
            <span class="add-btn">
                <el-icon @click="addNewOption">
                    <CirclePlus />
                </el-icon>
            </span>
            <div class="form-options__list">
                <div class="form-options__item" v-for="(element, index) in formData.filterEtl">
                    <el-form-item :prop="`filterEtl[${index}].colName`" :rules="rules.colName">
                        <el-select
                            v-model="element.colName"
                            filterable
                            clearable
                            placeholder="请选择字段"
                        >
                            <el-option
                                v-for="item in colNameOptions"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item :prop="`filterEtl[${index}].filterCondition`" :rules="rules.filterCondition">
                        <el-select v-model="element.filterCondition" placeholder="请选择条件">
                            <el-option
                                v-for="item in filterConditionOptions"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item
                        v-if="!['IS NULL', 'IS NOT NULL'].includes(element.filterCondition)"
                        :prop="`filterEtl[${index}].filterValue`"
                        :rules="rules.filterValue"
                    >
                        <el-input
                            v-model="element.filterValue"
                            clearable
                            placeholder="请输入值"
                        ></el-input>
                    </el-form-item>
                    <div class="option-btn">
                        <el-icon v-if="formData.filterEtl.length > 1" class="remove"
                            @click="removeItem(index)">
                            <CircleClose />
                        </el-icon>
                    </div>
                </div>
            </div>
        </el-form-item>
        <el-form-item label="输出字段">
            <div style="max-height: 444px; width: 100%;">
                <BlockTable :table-config="tableConfig" />
            </div>
        </el-form-item>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineEmits, computed, onMounted, reactive } from 'vue'
import { FormRules } from 'element-plus'
import { FilterConditionOptions, TableConfig } from './config.ts'

interface Option {
    label: string
    value: string
}

const props = defineProps<{
    modelValue: any,
    incomeNodes: any
}>()
const emit = defineEmits(['update:modelValue'])

const colNameOptions = ref<Option[]>([])
const filterConditionOptions = ref(FilterConditionOptions)
const tableConfig = reactive(TableConfig)

const rules = reactive<FormRules>({
    colName: [{ required: true, message: '字段不能为空', trigger: ['blur', 'change'] }],
    filterCondition: [{ required: true, message: '条件不能为空', trigger: ['blur', 'change'] }],
    filterValue: [{ required: true, message: '值不能为空', trigger: ['blur', 'change'] }]
})

const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

function addNewOption() {
    formData.value.filterEtl.push({
        colName: '',
        filterCondition: '',
        filterValue: ''
    })
}

function removeItem(index: number) {
    formData.value.filterEtl.splice(index, 1)
}

onMounted(() => {
    if (props.incomeNodes && props.incomeNodes[0]) {
        colNameOptions.value = props.incomeNodes[0].data.nodeConfigData.outColumnList.map((column) => {
            return {
                label: column.colName,
                value: column.colName
            }
        })
        formData.value.outColumnList = props.incomeNodes[0].data.nodeConfigData.outColumnList.map((column) => {
            return {
                colName: column.colName,
                colType: column.colType,
                remark: column.remark
            }
        })
        tableConfig.tableData = formData.value.outColumnList
        formData.value.inputEtl = props.incomeNodes[0].data.nodeConfigData.inputEtl
    }
})
</script>

