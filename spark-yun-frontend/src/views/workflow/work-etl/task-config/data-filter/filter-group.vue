<template>
    <div class="filter-group-container">
        <template v-for="(item, index) in items" :key="index">
            <!-- AND/OR 连接徽章（非第一项显示） -->
            <div v-if="index > 0" class="filter-way-badge">
                <span
                    class="way-tag"
                    :class="{ 'way-or': items[index].filterWay === 'OR' }"
                    @click="toggleFilterWay(index)"
                >{{ items[index].filterWay || 'AND' }}</span>
            </div>
            <!-- 分组条件 -->
            <div v-if="item.groupFilter" class="filter-group-block" :style="getGroupStyle()">
                <el-icon v-if="items.length > 1" class="filter-remove-btn" @click="removeItem(index)">
                    <CircleClose />
                </el-icon>
                <filter-group
                    :items="item.groupFilter"
                    :col-name-options="colNameOptions"
                    :prop-prefix="`${propPrefix}[${index}].groupFilter`"
                    :depth="depth + 1"
                />
                <div class="filter-group-actions">
                    <el-button link type="primary" size="small" @click="addConditionToGroup(item.groupFilter)">+ 条件</el-button>
                    <el-button link type="primary" size="small" @click="addGroupToGroup(item.groupFilter)">+ 分组</el-button>
                </div>
            </div>
            <!-- 条件/自定义过滤行 -->
            <div v-else class="filter-row">
                <el-select
                    v-model="items[index].filterType"
                    class="filter-type-select"
                    @change="onFilterTypeChange(index)"
                >
                    <el-option label="字段" value="CONDITION_FILTER"/>
                    <el-option label="自定义" value="CUSTOM_FILTER"/>
                </el-select>
                <template v-if="item.filterType === 'CONDITION_FILTER'">
                    <el-form-item :prop="`${propPrefix}[${index}].filterColumn`" :rules="rules.filterColumn">
                        <el-select v-model="items[index].filterColumn" filterable clearable placeholder="字段">
                            <el-option v-for="opt in colNameOptions" :key="opt.value" :label="opt.label" :value="opt.value"/>
                        </el-select>
                    </el-form-item>
                    <el-form-item :prop="`${propPrefix}[${index}].filterCondition`" :rules="rules.filterCondition">
                        <el-select v-model="items[index].filterCondition" placeholder="条件">
                            <el-option v-for="opt in FilterConditionOptions" :key="opt.value" :label="opt.label" :value="opt.value"/>
                        </el-select>
                    </el-form-item>
                    <el-form-item
                        v-if="!['is_null', 'is_not_null'].includes(item.filterCondition)"
                        :prop="`${propPrefix}[${index}].filterValue`"
                        :rules="rules.filterValue"
                    >
                        <el-input v-model="items[index].filterValue" clearable placeholder="值"></el-input>
                    </el-form-item>
                </template>
                <template v-else>
                    <el-form-item :prop="`${propPrefix}[${index}].customFilter`" :rules="rules.customFilter" class="custom-form-item">
                        <el-input v-model="items[index].customFilter" clearable placeholder="例如：age > 12 AND name = 'test'"></el-input>
                    </el-form-item>
                </template>
                <el-icon v-if="items.length > 1" class="filter-remove-btn" @click="removeItem(index)">
                    <CircleClose />
                </el-icon>
            </div>
        </template>
    </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue'
import { FormRules } from 'element-plus'
import { FilterConditionOptions, groupColorPalette } from './config.ts'

defineOptions({ name: 'FilterGroup' })

const props = defineProps<{
    items: any[]
    colNameOptions: any[]
    propPrefix: string
    depth: number
}>()

// 根据嵌套深度分配颜色，确保相邻父子层级颜色不同
function getGroupStyle() {
    const palette = groupColorPalette[props.depth % groupColorPalette.length]
    return {
        borderLeftColor: palette.border,
        backgroundColor: palette.background,
        borderColor: palette.borderOuter,
        borderLeftWidth: '3px',
        borderLeftStyle: 'solid'
    }
}

const rules = reactive<FormRules>({
    filterColumn: [{ required: true, message: '字段不能为空', trigger: ['blur', 'change'] }],
    filterCondition: [{ required: true, message: '条件不能为空', trigger: ['blur', 'change'] }],
    filterValue: [{ required: true, message: '值不能为空', trigger: ['blur', 'change'] }],
    customFilter: [{ required: true, message: '自定义条件不能为空', trigger: ['blur', 'change'] }]
})

function toggleFilterWay(index: number) {
    props.items[index].filterWay = props.items[index].filterWay === 'AND' ? 'OR' : 'AND'
}

function onFilterTypeChange(index: number) {
    const item = props.items[index]
    // 切换类型时重置字段
    item.filterColumn = ''
    item.filterCondition = ''
    item.filterValue = ''
    item.customFilter = ''
}

function removeItem(index: number) {
    props.items.splice(index, 1)
}

function addConditionToGroup(list: any[]) {
    list.push({ filterWay: 'AND', filterType: 'CONDITION_FILTER', filterColumn: '', filterCondition: '', filterValue: '', customFilter: '' })
}

function addGroupToGroup(list: any[]) {
    list.push({ filterWay: 'AND', groupFilter: [{ filterType: 'CONDITION_FILTER', filterColumn: '', filterCondition: '', filterValue: '', customFilter: '' }] })
}
</script>

