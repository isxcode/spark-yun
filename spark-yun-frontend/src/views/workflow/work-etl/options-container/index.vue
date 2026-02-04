<template>
    <div class="options-container">
        <div class="btn-box" @click="optionsEvent('goBack')">
            <el-icon>
                <RefreshLeft />
            </el-icon>
            <span class="btn-text">返回</span>
        </div>
        <div class="btn-box" @click="optionsEvent('saveData')">
            <el-icon v-if="!btnLoadingConfig.saveLoading">
                <Finished />
            </el-icon>
            <el-icon v-else class="is-loading">
                <Loading />
            </el-icon>
            <span class="btn-text">保存</span>
        </div>
        <div class="btn-box" @click="optionsEvent('runWorkData')">
            <el-icon v-if="!btnLoadingConfig.runningLoading">
                <VideoPlay />
            </el-icon>
            <el-icon v-else class="is-loading">
                <Loading />
            </el-icon>
            <span class="btn-text">运行</span>
        </div>
        <div class="btn-box" @click="optionsEvent('terWorkData')">
            <el-icon v-if="!btnLoadingConfig.stopWorkFlowLoading">
                <Close />
            </el-icon>
            <el-icon v-else class="is-loading">
                <Loading />
            </el-icon>
            <span class="btn-text">中止</span>
        </div>
        <div class="btn-box" @click="optionsEvent('setConfigData')">
            <el-icon>
                <Setting />
            </el-icon>
            <span class="btn-text">配置</span>
        </div>
        <div class="btn-box" @click="optionsEvent('locationNode')">
            <el-icon>
                <Position />
            </el-icon>
            <span class="btn-text">定位</span>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { defineEmits, reactive } from 'vue'

// const emit = defineEmits(['saveData', 'runWorkData', 'terWorkData', 'setConfigData', 'locationNode'])
const emit = defineEmits(['optionsEvent'])

const btnLoadingConfig = reactive({
    runningLoading: false,
    reRunLoading: false,
    saveLoading: false,
    publishLoading: false,
    stopWorkFlowLoading: false,
    importLoading: false,
    exportLoading: false,
    stopLoading: false
})

function optionsEvent(e: string) {
    emit('optionsEvent', e)
}

function closeLoading() {
    btnLoadingConfig.runningLoading = false
    btnLoadingConfig.reRunLoading = false
    btnLoadingConfig.saveLoading = false
    btnLoadingConfig.publishLoading = false
    btnLoadingConfig.stopWorkFlowLoading = false
    btnLoadingConfig.importLoading = false
    btnLoadingConfig.exportLoading = false
    btnLoadingConfig.stopLoading = false
}

defineExpose({
    closeLoading,
    btnLoadingConfig
})
</script>

<style lang="scss">
.options-container {
    height: 50px;
    display: flex;
    align-items: center;
    color: getCssVar('color', 'primary', 'light-5');
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    padding-left: 20px;
    z-index: 10;
    border-bottom: 1px solid getCssVar('border-color');

    .btn-box {
        font-size: getCssVar('font-size', 'extra-small');
        display: flex;
        cursor: pointer;
        width: 48px;
        margin-right: 8px;

        &.btn-box__4 {
            width: 70px;
        }

        .btn-text {
            margin-left: 4px;
        }

        &:hover {
            color: getCssVar('color', 'primary');
        }
    }
}
</style>
