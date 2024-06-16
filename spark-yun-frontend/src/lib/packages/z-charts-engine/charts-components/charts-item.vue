<template>
    <div class="charts-item">
        <el-icon v-if="renderSence !== 'readonly'" class="draggable-options__remove" @click="removeChart"><DeleteFilled /></el-icon>
        <div class="charts-container" :class="{ 'moving-container': config.i === 'drop'}" :id="currentChartId"></div>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineProps, onMounted, computed, onUnmounted, defineEmits, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps(['config', 'renderSence', 'getPreviewOption', 'getRealDataOption'])
const emit = defineEmits(['removeChart'])
let myChart: any = null

const timer = ref()

const currentChartId = computed(() => {
    return props.config.uuid
})

function resizeChart() {
    myChart.resize()
}

function removeChart() {
    emit('removeChart', props.config)
}

onMounted(async () => {
	if (props.config.i !== 'drop') {
		myChart = echarts.init(document.getElementById(currentChartId.value))

        // 拖拽时获取预览数据
        if (props.renderSence === 'edit' && props.getPreviewOption && props.getPreviewOption instanceof Function) {
            const option = await props.getPreviewOption(props.config)
            props.config.webConfig = option.webConfig
            myChart.setOption(option.exampleData);
            setTimeout(() => {
                myChart.resize()
            })
        } else if (props.renderSence === 'readonly' && props.getRealDataOption && props.getRealDataOption instanceof Function) {
            const option = await props.getRealDataOption(props.config)
            myChart.setOption(option);
            setTimeout(() => {
                myChart.resize()
            })
            if (props.config?.webConfig?.time) {
                timer.value = setInterval(() => {
                    props.getRealDataOption(props.config).then(res => {
                        myChart.setOption(res);
                    })
                }, props.config?.webConfig?.time * 1000)
            }
        }
		window.addEventListener('resize', resizeChart)
	}

})

onUnmounted(() => {
    window.removeEventListener('resize', resizeChart)
    if (timer.value) {
        clearInterval(timer.value)
    }
    timer.value = null
})

defineExpose({
    resizeChart
})
</script>

<style lang="scss">
.charts-item {
    width: 100%;
    height: 100%;
    position: relative;
    background-color: #ffffff;
    box-shadow: getCssVar('box-shadow', 'lighter');
    border-radius: 4px;
    // display: inline-block;
    // border: 1px solid #d5d5d5;
    // padding: 4px;
    // margin: 4px 8px;
    .draggable-options__remove {
        position: absolute;
        right: 12px;
        top: 12px;
        font-size: 16px;
        cursor: pointer;
        z-index: 100;
        color: #f89898;
        transition: all 0.3s linear;
        &:hover {
            color: #F56C6C;
            transition: all 0.3s linear;
        }
    }
    .charts-item-header {
        font-size: 14px;
        display: flex;
        align-items: center;
        padding-left: 30px;
        height: 40px;
    }
    .charts-container {
        height: 100%;
        width: 100%;
		&.moving-container {
			background-color: #fdd;
		}
    }
}
</style>
