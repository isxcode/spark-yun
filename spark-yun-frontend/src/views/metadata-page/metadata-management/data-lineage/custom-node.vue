<template>
    <div class="custom-node" :class="[`custom-node__${config.data.pageType}`]">
        <div class="flow-node-container" ref="content">
            <!-- <p class="text">{{ config.name }}</p > -->
            <div class="box-container" v-if="config.data.pageType === 'datasource'">
                <p class="text" v-if="config.data.dbName">
                    <span class="label-3">数据源：</span>
                    <EllipsisTooltip class="ellipsis-3" :label="config.data.dbName" />
                </p >
                <p class="text" v-if="config.data.dbType">
                    <span class="label-2">类型：</span>
                    <EllipsisTooltip class="ellipsis-2" :label="config.data.dbType" />
                </p >
            </div>
            <div class="box-container" v-if="config.data.pageType === 'table'">
                <p class="text" v-if="config.data.dbType">
                    <span class="label-5">数据源类型：</span>
                    <EllipsisTooltip class="ellipsis-5" :label="config.data.dbType" />
                </p >
                <p class="text" v-if="config.data.dbName">
                    <span class="label-3">数据源：</span>
                    <EllipsisTooltip class="ellipsis-3" :label="config.data.dbName" />
                </p >
                <p class="text" v-if="config.data.tableName">
                    <span class="label-2">表名：</span>
                    <EllipsisTooltip class="ellipsis-2" :label="config.data.tableName" />
                </p >
            </div>
            <div class="box-container" v-if="config.data.pageType === 'code'">
                <p class="text" v-if="config.data.dbType">
                    <span class="label-5">数据源类型：</span>
                    <EllipsisTooltip class="ellipsis-5" :label="config.data.dbType" />
                </p >
                <p class="text" v-if="config.data.dbName">
                    <span class="label-3">数据源：</span>
                    <EllipsisTooltip class="ellipsis-3" :label="config.data.dbName" />
                </p >
                <p class="text" v-if="config.data.tableName">
                    <span class="label-2">表名：</span>
                    <EllipsisTooltip class="ellipsis-2" :label="config.data.tableName" />
                </p >
                <p class="text" v-if="config.data.columnName">
                    <span class="label-2">字段：</span>
                    <EllipsisTooltip class="ellipsis-2" :label="config.data.columnName" />
                </p >
            </div>

            <!-- <el-icon @click="handleCommand('checkDown')" class="check-children"><CirclePlus /></el-icon> -->
            <el-dropdown
                trigger="click"
                @command="handleCommand"
                v-if="!parentStatus && !config.data.parentStatus || !childrenStatus && !config.data.childrenStatus ||
                    config.data.pageType === 'code' && !isCode"
                >
                <el-icon class="node-option-more">
                    <MoreFilled />
                </el-icon>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item v-if="!parentStatus && !config.data.parentStatus" command="checkUp">查看上游</el-dropdown-item>
                        <el-dropdown-item v-if="!childrenStatus && !config.data.childrenStatus" command="checkDown">查看下游</el-dropdown-item>
                        <el-dropdown-item v-if="config.data.pageType === 'code' && !isCode" command="showDetail">详情</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { onMounted, computed, ref } from 'vue';
import { ElIcon, ElDropdown, ElDropdownMenu, ElDropdownItem } from 'element-plus'
import { MoreFilled, Loading, Clock, VideoPause, CirclePlus } from '@element-plus/icons-vue'
import EllipsisTooltip from '@/components/ellipsis-tooltip/ellipsis-tooltip.vue'
import eventBus from '@/utils/eventBus'

const props = defineProps<{
    config: any,
    isCode: boolean
}>()

const parentStatus = ref<boolean>(false)
const childrenStatus = ref<boolean>(false)

function handleCommand(command: string) {
    if (command === 'checkUp') {
        parentStatus.value = true
    }
    if (command === 'checkDown') {
        childrenStatus.value = true
    }
    eventBus.emit('lineageEvent', {
        data: props.config,
        type: command
    })
}

onMounted(() => {
    // console.log('props参数', props)
})
</script>

<style lang="scss">
.key {
    height: auto !important;
}
.custom-node {
    position: relative;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    width: 146px;
    height: 100%;
    background-color: #fff;
    border: 1px solid #c2c8d5;
    border-left: 4px solid #5F95FF;
    border-radius: 4px;
    box-shadow: 0 2px 5px 1px rgba(0, 0, 0, 0.06);
    &.custom-node__datasource {
        border-left: 4px solid #9f26e1;
    }
    &.custom-node__table {
        border-left: 4px solid #f5b041;
    }
    &.custom-node__code {
        border-left: 4px solid #52c41a;
    }
    // left: -56px;
    .flow-node-container {
        display: flex;
        justify-content: space-between;
        // flex-direction: column;
        width: 100%;
        // padding-right: 8px;
        // padding-left: 8px;
        align-items: center;
        line-height: 24px;
        position: relative;

        .text {
            margin: 0;
            font-size: 12px;
            max-width: 114px;
            display: flex;
            .label-2 {
                min-width: 38px;
            }
            .label-3 {
                min-width: 42px;
            }
            .label-5 {
                min-width: 62px;
            }

            .ellipsis-2 {
                max-width: 92px;
            }
            .ellipsis-3 {
                max-width: 92px;
            }
            .ellipsis-5 {
                max-width: 62px;
            }
        }

        .check-children {
            position: absolute;
            right: -8px;
            top: 8px;
            background-color: #ffffff;
            box-shadow: 0 0 1px #ffffff;
            color: getCssVar('color', 'primary');
            cursor: pointer;
        }

        .node-option-more {
            font-size: 14px;
            width: 20px;
            transform: rotate(90deg);
            cursor: pointer;
            color: #9599a2;
        }
    }
}

</style>