<template>
    <div class="config-components data-join data-filter">
        <div class="config-label">
            <span>过滤条件</span>
        </div>
        <!-- 递归渲染过滤条件 -->
        <filter-group
            :items="formData.filterEtl"
            :col-name-options="colNameOptions"
            prop-prefix="filterEtl"
            :depth="0"
        />
        <!-- 操作按钮 -->
        <div class="filter-actions">
            <el-button size="small" @click="addCondition">添加条件</el-button>
            <el-button size="small" @click="addGroup">添加分组</el-button>
        </div>
        <el-form-item v-show="false" label="输出字段">
            <div style="max-height: 444px; width: 100%;">
                <BlockTable :table-config="tableConfig" />
            </div>
        </el-form-item>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineEmits, computed, onMounted, reactive } from 'vue'
import { TableConfig } from './config.ts'
import FilterGroup from './filter-group.vue'

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
const tableConfig = reactive(TableConfig)

const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

function addCondition() {
    const item: any = {
        filterType: 'CONDITION_FILTER',
        filterColumn: '',
        filterCondition: '',
        filterValue: '',
        customFilter: ''
    }
    if (formData.value.filterEtl.length > 0) {
        item.filterWay = 'AND'
    }
    formData.value.filterEtl.push(item)
}

function addGroup() {
    const item: any = {
        groupFilter: [{
            filterType: 'CONDITION_FILTER',
            filterColumn: '',
            filterCondition: '',
            filterValue: ''
        }]
    }
    if (formData.value.filterEtl.length > 0) {
        item.filterWay = 'AND'
    }
    formData.value.filterEtl.push(item)
}

onMounted(() => {
    if (props.incomeNodes && props.incomeNodes[0]) {
        colNameOptions.value = props.incomeNodes[0].data.nodeConfigData.outColumnList.map((column: any) => {
            return {
                label: column.colName,
                value: column.colName
            }
        })
        formData.value.outColumnList = props.incomeNodes[0].data.nodeConfigData.outColumnList.map((column: any) => {
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

<style lang="scss">
.data-filter {
    .filter-actions {
        margin-top: 12px;
        display: flex;
        gap: 4px;
    }

    .filter-group-container {
        // AND/OR 徽章
        .filter-way-badge {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2px 0;

            .way-tag {
                display: inline-block;
                padding: 0 10px;
                height: 20px;
                line-height: 20px;
                font-size: 11px;
                font-weight: 600;
                border-radius: 10px;
                cursor: pointer;
                user-select: none;
                background: #e8f4ff;
                color: #409eff;
                border: 1px solid #b3d8ff;
                transition: all 0.2s;

                &:hover {
                    background: #d9ecff;
                }

                &.way-or {
                    background: #fef0e6;
                    color: #e6a23c;
                    border-color: #f5dab1;

                    &:hover {
                        background: #fde5d0;
                    }
                }
            }
        }

        // 条件行
        .filter-row {
            display: flex;
            align-items: center;
            gap: 4px;
            padding: 6px 10px;
            border: 1px solid #e9eaec;
            border-radius: 4px;
            background: #fff;

            .filter-type-select {
                flex-shrink: 0;
                width: 90px;
            }

            .el-form-item {
                flex: 1;
                margin-bottom: 0;
                min-width: 0;
            }

            .custom-form-item {
                flex: 1;
            }

            .filter-remove-btn {
                flex-shrink: 0;
                font-size: 14px;
                color: #c0c4cc;
                cursor: pointer;
                margin-left: 4px;
                transition: color 0.2s;

                &:hover {
                    color: #f56c6c;
                }
            }
        }

        // 分组块
        .filter-group-block {
            border: 1px solid #d9ecff;
            border-left: 3px solid #409eff;
            border-radius: 4px;
            padding: 10px;
            position: relative;
            background: #f9fbff;

            > .filter-remove-btn {
                position: absolute;
                right: 4px;
                top: 4px;
                font-size: 14px;
                color: #c0c4cc;
                cursor: pointer;
                z-index: 1;
                transition: color 0.2s;

                &:hover {
                    color: #f56c6c;
                }
            }

            .filter-group-actions {
                margin-top: 6px;
                display: flex;
                gap: 0;
                padding-left: 2px;
            }

            // 嵌套分组样式递进
            .filter-group-block {
                border-left-color: #e6a23c;
                background: #fffcf5;

                .filter-group-block {
                    border-left-color: #67c23a;
                    background: #f9fff5;
                }
            }
        }
    }
}
</style>

