<template>
    <div class="z-tree-node">
        <div class="node-info">
            <div class="node-label">
                <el-icon><CollectionTag /></el-icon>
                <EllipsisTooltip class="label-text" :label="params.name" />
            </div>
            <div class="node-desc">
                <EllipsisTooltip :label="params.remark" />
            </div>
        </div>
        <div class="node-loading" v-if="loading">
            <el-icon class="is-loading">
                <Loading />
            </el-icon>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { defineProps, onMounted } from 'vue'
import { CollectionTag } from '@element-plus/icons-vue'
import EllipsisTooltip from '@/components/ellipsis-tooltip/ellipsis-tooltip.vue'
import { ElIcon } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'

const props = defineProps(['params', 'loading'])
</script>

<style lang="scss">
.z-tree-node {
    height: 72px;
    width: 140px;
    border: 1px solid getCssVar('color', 'primary', 'light-5');
    border-radius: 5px;
    display: flex;
    justify-content: space-between;
    box-sizing: border-box;
    position: relative;
    .node-info {
        height: 100%;
        width: 100%;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .node-label {
            font-size: 12px;
            height: 24px;
            background-color: getCssVar('color', 'primary');
            color: #ffffff;
            display: flex;
            align-items: center;
            padding-left: 4px;
            border-radius: 4px 4px 0 0;
            .label-text {
                margin-left: 2px;
                max-width: calc(100% - 20px);
            }
        }
        .node-desc {
            font-size: 12px;
            color: #3c3c3c;
            height: 48px;
            padding: 4px;
            box-sizing: border-box;
            display: flex;
            align-items: center;
            color: #000000;
            .ellipsis-tooltip {
                max-width: 100%;
            }
        }
    }
    .icon-container {
        height: 50px;
        position: absolute;
        top: 0;
        right: 0;
        display: flex;
        align-items: center;
        justify-items: center;
        .node-loading {
            margin-right: 4px;
        }
        .node-option-more {
            font-size: 14px;
            /* transform: rotate(90deg); */
            cursor: pointer;
            /* color: getCssVar('color', 'info'); */
            display: flex;
            margin-right: 8px;
            align-items: center;
            justify-items: center;
        }
    }
    .node-loading {
        position: absolute;
        top: 0;
        left: 0;
        background-color: #FFFFFF90;
        height: 100%;
        width: 100%;
        border-radius: 5px;
        display: flex;
        justify-content: center;
        align-items: center;
        .el-icon {
            color: getCssVar('color', 'primary');
        }
    }
}
</style>