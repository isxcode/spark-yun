<template>
    <div class="custom-node" :class="[`custom-node__${config.data.pageType}`]">
        <div class="flow-node-container" ref="content">
            <!-- <p class="text">{{ config.name }}</p > -->
            <p class="text">
                <EllipsisTooltip :label="config.name" />
            </p >

            <el-icon @click="handleCommand('checkDown')" class="check-children"><CirclePlus /></el-icon>
            <el-dropdown
                trigger="click"
                @command="handleCommand"
                v-if="true || config.data.children && config.data.children.length || config.data.parent && config.data.parent.length">
                <el-icon class="node-option-more">
                    <MoreFilled />
                </el-icon>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="checkUp">查看上游</el-dropdown-item>
                        <el-dropdown-item v-if="config.data.pageType === 'code'" command="showDetail">详情</el-dropdown-item>
                        <!-- <el-dropdown-item v-if="config.data.children" command="checkDown">查看下游</el-dropdown-item> -->
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { onMounted, computed } from 'vue';
import { ElIcon, ElDropdown, ElDropdownMenu, ElDropdownItem } from 'element-plus'
import { MoreFilled, Loading, Clock, VideoPause, CirclePlus } from '@element-plus/icons-vue'
import EllipsisTooltip from '@/components/ellipsis-tooltip/ellipsis-tooltip.vue'
import eventBus from '@/utils/eventBus'

const props = defineProps<{
    config: any
}>()

function handleCommand(command: string) {
    eventBus.emit('lineageEvent', {
        data: props.config,
        type: command
    })
}

onMounted(() => {
    console.log('props参数', props)
})
</script>

<style lang="scss">
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
        width: 100%;
        // padding-right: 8px;
        // padding-left: 8px;
        align-items: center;
        line-height: 30px;
        position: relative;

        p {
            margin: 0;
            font-size: 12px;
            max-width: 114px;
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