<template>
    <div class="task-list-etl">
        <el-scrollbar>
            <div class="list-box">
                <template v-for="task in taskList" :key="task.id">
                    <div
                        class="list-item"
                        :draggable="true"
                        @dragstart="handleDragEnd($event, task)"
                    >
                        <el-icon class="item-icon">
                            <component :is="task.icon" />
                        </el-icon>
                        <div class="item-right">
                            <span class="label-type">
                                <EllipsisTooltip class="label-name-text" :label="task.typeName" />
                            </span>
                        </div>
                    </div>
                </template>
            </div>
        </el-scrollbar>
    </div>
</template>

<script lang="ts" setup>
import { defineEmits, ref } from 'vue'
import EllipsisTooltip from '@/components/ellipsis-tooltip/ellipsis-tooltip.vue'

interface Task {
    type: string
    typeName: string
    icon: string
}

const emit = defineEmits(['handleDragEnd'])
const taskList = ref<Task[]>([
    { type: 'DATA_INPUT', typeName: '数据输入', icon: 'Download' },
    { type: 'DATA_JOIN', typeName: '数据关联', icon: 'Link' },
    { type: 'DATA_UNION', typeName: '数据合并', icon: 'CopyDocument' },
    { type: 'DATA_FILTER', typeName: '数据过滤', icon: 'Filter' },
    { type: 'DATA_TRANSFORM', typeName: '数据转换', icon: 'Switch' },
    { type: 'DATA_ADD_COL', typeName: '新增字段', icon: 'CirclePlus' },
    { type: 'DATA_CUSTOM', typeName: '自定义', icon: 'SetUp' },
    { type: 'DATA_OUTPUT', typeName: '数据输出', icon: 'Upload' },
])

function handleDragEnd(e: any, data: any) {
    emit('handleDragEnd', e, data)
}
</script>

<style lang="scss">
.task-list-etl {
    position: absolute;
    top: 51px;
    left: 0;
    height: calc(100vh - 107px);
    border-right: 1px solid getCssVar('border-color');
    .el-scrollbar {
        width: 100px;
        max-height: calc(100vh - 148px);
        .el-scrollbar__view {
            height: 100%;
            .list-box {
                // padding: 0 4px;
                box-sizing: border-box;
                position: relative;
                height: 100%;
                padding: 8px;

                .list-item {
                    height: 40px;
                    padding: 0 8px;
                    box-sizing: border-box;
                    border: 1px solid getCssVar('border-color');
                    border-radius: 6px;
                    cursor: pointer;
                    font-size: getCssVar('font-size', 'extra-small');
                    position: relative;
                    display: flex;
                    align-items: center;
                    gap: 4px;
                    margin-bottom: 8px;
                    box-shadow: getCssVar('box-shadow', 'lighter');

                    .item-icon {
                        font-size: 14px;
                        flex-shrink: 0;
                        color: getCssVar('color', 'primary');
                    }

                    .item-right {
                        display: flex;
                        flex-direction: column;
                        justify-content: space-between;
                        padding: 2px 0;
                        box-sizing: border-box;
                        font-size: 12px;
                        // height: 80%;
                        .label-type {
                            color: getCssVar('color', 'primary');
                            .label-name-text {
                                max-width: 170px;
                            }
                        }
                        .label-name {
                            color: getCssVar('color', 'info');
                            .label-name-text {
                                max-width: 170px;
                            }
                        }
                    }

                    &.choose-item {
                        background-color: getCssVar('color', 'primary', 'light-8');
                    }

                    &:hover {
                        background-color: getCssVar('color', 'primary', 'light-8');

                        .el-dropdown {
                            display: block;
                        }
                    }

                    .el-dropdown {
                        position: absolute;
                        right: 8px;
                        top: 20px;
                        // display: none;

                        .option-more {
                            font-size: 14px;
                            transform: rotate(90deg);
                            cursor: pointer;
                            color: getCssVar('color', 'info');
                        }
                    }
                }
            }
        }
    }
}
</style>
