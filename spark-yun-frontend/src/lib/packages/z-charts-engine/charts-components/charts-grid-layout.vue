<template>
    <div class="charts-components" id="chartsComponentsInstance">
        <el-scrollbar>
            <template v-if="componentList.length">
                <grid-layout
                    ref="gridlayoutRef"
                    :layout.sync="componentList"
                    :col-num="colNum"
                    :row-height="10"
                    :is-draggable="renderSence !== 'readonly'"
                    :is-resizable="renderSence !== 'readonly'"
                    :is-mirrored="false"
                    :vertical-compact="true"
                    :margin="[12, 12]"
                    :use-css-transforms="true"
                    @layout-updated="layoutUpdatedEvent"
                >
                    <grid-item
                        v-for="(item, index) in componentList"
                        :ref="(el) => gridItemRef[index] = el"
                        :x="item.x"
                        :y="item.y"
                        :w="item.w"
                        :h="item.h"
                        :i="item.i"
                        :minW="30"
                        :minH="15"
                        :key="item.i"
                        :static="false"
                        @resize="resizeEvent($event, index)"
                    >
                        <ChartsItem
                            :ref="el => chartsItemRef[index] = el"
                            :renderSence="renderSence"
                            :config="item"
                            :getPreviewOption="getPreviewOption"
                            :getRealDataOption="getRealDataOption"
                            @removeChart="removeChart"
                        ></ChartsItem>
                    </grid-item>
                </grid-layout>
            </template>
            <template v-else>
                <EmptyPage></EmptyPage>
            </template>
        </el-scrollbar>
    </div>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, computed, markRaw, ref, shallowRef, watch, nextTick, onMounted } from 'vue'
import ChartsItem from './charts-item.vue'
import EmptyPage from '../empty-page/index.vue'

interface ChartLayout {
    chartName: string
    chartId: string
    type: string
    uuid: string
    typeName: string
    x: number
    y: number
    w: number
    h: number
    i: string
    active: boolean
    option: any,
    webConfig?: any
}

interface MouseXY {
    x: number
    y: number
}
interface DragPos {
    x: number
    y: number
    w: number
    h: number
    i: string
}
const props = defineProps(['renderSence', 'chartList', 'getPreviewOption', 'getRealDataOption'])

const emit = defineEmits(['update:modelValue', 'componentListChange', 'chooseItem', 'removeInstance'])
const colNum = ref(300)
const chartsItemRef = ref<any[]>([])
const gridlayoutRef = ref()
const gridItemRef = ref<any[]>([])
const componentList = ref<ChartLayout[]>([])

watch(() => props.chartList, (v) => {
    componentList.value = v
}, {
    immediate: true
})

let mouseXY: MouseXY = {"x": null, "y": null};
let DragPos: DragPos = {"x": null, "y": null, "w": 1, "h": 1, "i": null};

function layoutUpdatedEvent(newLayout: any) {
    // console.log('newLayout', newLayout)
}

function resizeEvent(e: any, index: number) {
    chartsItemRef.value[index].resizeChart()
}

function resizeAllCharts() {
    componentList.value.forEach((chart: any, index: number) => {
        if (chartsItemRef.value[index] && chartsItemRef.value[index].resizeChart && chartsItemRef.value[index].resizeChart instanceof Function) {
            chartsItemRef.value[index].resizeChart()
        }
    })
}

function startMoveEvent(e: any) {
    let parentRect: any = document.getElementById('chartsComponentsInstance')?.getBoundingClientRect()
    if (!parentRect) {
        return
    }
    let mouseInGrid = false
    if (((mouseXY.x > parentRect.left) && (mouseXY.x < parentRect.right)) && ((mouseXY.y > parentRect.top) && (mouseXY.y < parentRect.bottom))) {
        mouseInGrid = true;
    }
    if (mouseInGrid === true && (componentList.value.findIndex(item => item.i === 'drop')) === -1) {
        componentList.value.push({
            ...e,
            x: (componentList.value.length * 2) % (colNum.value),
            y: componentList.value.length + (colNum.value),
            i: 'drop'
        })
        console.log('这里塞值', componentList.value)
    }
    let index = componentList.value.findIndex(item => item.i === 'drop')
    if (index !== -1) {
        nextTick(() => {
            try {
                gridItemRef.value[componentList.value.length - 1].style.display = "none"
            } catch (error) {
                console.error('拖拽获取子图表报错', error)
            }
            let el: any = gridItemRef.value[index]
            el.dragging = {"top": mouseXY.y - parentRect.top, "left": mouseXY.x - parentRect.left};
            let new_pos = el.calcXY(mouseXY.y - parentRect.top, mouseXY.x - parentRect.left);
            if (mouseInGrid === true) {
                gridlayoutRef.value.dragEvent('dragstart', 'drop', new_pos.x, new_pos.y, 1, 1);
                DragPos.i = String(index);
                DragPos.x = componentList.value[index].x;
                DragPos.y = componentList.value[index].y;
            }
            if (mouseInGrid === false) {
                gridlayoutRef.value.dragEvent('dragend', 'drop', new_pos.x, new_pos.y, 1, 1);
                componentList.value = componentList.value.filter(obj => obj.i !== 'drop');
            }
        })
    }
}
function endMoveEvent(e: any) {
    let parentRect: any = document.getElementById('chartsComponentsInstance')?.getBoundingClientRect()
    if (!parentRect) {
        return
    }
    let mouseInGrid = false;
    if (((mouseXY.x > parentRect.left) && (mouseXY.x < parentRect.right)) && ((mouseXY.y > parentRect.top) && (mouseXY.y < parentRect.bottom))) {
        mouseInGrid = true;
    }
    if (mouseInGrid === true) {

        componentList.value = componentList.value.filter(obj => obj.i !== 'drop');
        componentList.value.push({
            ...e,
            x: DragPos.x,
            y: DragPos.y
        });
        gridlayoutRef.value.dragEvent('dragend', DragPos.i, DragPos.x,DragPos.y,1,1);
        try {
            gridItemRef.value[componentList.value.length - 1].style.display = 'block'
        } catch (error) {
            console.error('拖拽结束报错', error)
        }
    }
}

function removeChart(e: any): void {
    componentList.value = componentList.value.filter((c: any) => e.uuid !== c.uuid)
}

function getComponentList(): ChartLayout[] {
    return componentList.value
}

onMounted(() => {
    document.addEventListener("dragover", function (e) {
        mouseXY.x = e.clientX;
        mouseXY.y = e.clientY;
    }, false);
})

defineExpose({
    startMoveEvent,
    endMoveEvent,
    resizeAllCharts,
    getComponentList
})
</script>

<style lang="scss">
.charts-components {
    width: 100%;
    height: 100%;
    // padding: 12px;
    box-sizing: border-box;
    overflow-y: auto;
    overflow-x: hidden;
    .el-scrollbar {
        .el-scrollbar__wrap {
            max-height: calc(100vh - 55px);
            .el-scrollbar__view {
                // padding: 12px;
                // box-sizing: border-box;
            }
        }
    }
    &.form-components__dragger {
        border-left: 1px solid #dcdfe6;
        border-right: 1px solid #dcdfe6;
    }
    .form-dragger-component {
        z-index: 10;
        height: 100%;
        width: 100%;
        position: relative;
        .choose-item {
            // background-color: getCssVar('color', 'white');
            margin-bottom: 0px;
            .el-input__inner {
                pointer-events: none !important;
            }
            &:active {
                background-color: #fdd;
            }
            &.choose-item__active {
                background-color: getCssVar('color', 'primary', 'light-8') !important;
                &.form-render-item {
                    .form-render-item__btn {
                        display: flex;
                    }
                }
            }
        }
    }
    .el-scrollbar {
        .el-scrollbar__wrap {
            .el-scrollbar__view {
                height: 100%;
                padding-bottom: 20px;
                box-sizing: border-box;
            }
        }
    }

    .vue-grid-layout {
        // &:hover {
        //     cursor: move;
        // }
        .vue-grid-item {
            border: 1px solid #d5d5d5;
            border-radius: 4px;
        }
    }
    .vue-grid-item.vue-grid-placeholder {
        background: getCssVar('color', 'primary', 'light-8');
        opacity: 0.2;
        transition-duration: 100ms;
        z-index: 2;
        min-height: 340px;
        // -webkit-user-select: none;
        // -moz-user-select: none;
        // -ms-user-select: none;
        // -o-user-select: none;
        // user-select: none;
    }  
}
</style>
