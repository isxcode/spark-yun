<template>
    <div class="config-components data-join data-filter">
        <!-- 根分组 -->
        <div class="filter-root-group">
            <filter-group
                :items="formData.filterEtl"
                :col-name-options="colNameOptions"
                prop-prefix="filterEtl"
                :depth="0"
            />
            <div class="filter-group-actions">
                <el-button link type="primary" size="small" @click="addCondition">+ 条件</el-button>
                <el-button link type="primary" size="small" @click="addGroup">+ 分组</el-button>
            </div>
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
    formData.value.filterEtl.push({
        filterWay: 'AND',
        filterType: 'CONDITION_FILTER',
        filterColumn: '',
        filterCondition: '',
        filterValue: '',
        customFilter: ''
    })
}

function addGroup() {
    formData.value.filterEtl.push({
        filterWay: 'AND',
        groupFilter: [{
            filterType: 'CONDITION_FILTER',
            filterColumn: '',
            filterCondition: '',
            filterValue: '',
            customFilter: ''
        }]
    })
}

onMounted(() => {
    if (props.incomeNodes && props.incomeNodes[0]) {
        colNameOptions.value = props.incomeNodes[0].data.nodeConfigData.outColumnList.filter((item: any) => item.checked !== false).map((column: any) => {
            return {
                label: column.colName,
                value: column.colName
            }
        })
        if (!formData.value.outColumnList || !formData.value.outColumnList.length) {
            const fromAliaCode = props.incomeNodes[0].data.nodeConfigData.aliaCode || ''
            formData.value.outColumnList = props.incomeNodes[0].data.nodeConfigData.outColumnList.filter((item: any) => item.checked !== false).map((column: any) => {
                return {
                    colName: column.colName,
                    fromAliaCode: fromAliaCode,
                    fromColName: column.colName,
                    colType: column.colType,
                    remark: column.remark,
                    checked: true
                }
            })
        }
        tableConfig.tableData = formData.value.outColumnList
        formData.value.inputEtl = props.incomeNodes[0].data.nodeConfigData.inputEtl
    }
})
</script>

<style lang="scss">
.data-filter {
    .filter-root-group {
        border: 1px solid #ffd699;
        border-left: 3px solid #ff9800;
        border-radius: 4px;
        padding: 10px;
        background: #fffaf2;
        margin-top: 8px;

        .filter-group-actions {
            margin-top: 6px;
            display: flex;
            gap: 0;
            padding-left: 2px;
        }
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
            gap: 0;
            padding: 15px 10px;
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

                .el-form-item__content {
                    margin-left: 10px !important;
                    padding-left: 0;
                }
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

        // 分组块（背景色和左边框色由动态内联样式控制）
        .filter-group-block {
            border: 1px solid #d9ecff;
            border-left: 3px solid #409eff;
            border-radius: 4px;
            padding: 10px;
            position: relative;
            background: #f9fbff;

            > .filter-remove-btn {
                position: absolute;
                right: -8px;
                top: -8px;
                font-size: 16px;
                color: #c0c4cc;
                cursor: pointer;
                z-index: 1;
                transition: color 0.2s;
                background: #fff;
                border-radius: 50%;

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
        }
    }
}
</style>

