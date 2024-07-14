<template>
    <div class="zqy-work-item zqy-work-api">
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData">
            <div class="zqy-work-container">
                <div class="sql-code-container">
                    <div class="sql-option-container">
                        <div class="btn-box" @click="goBack">
                            <el-icon>
                                <RefreshLeft />
                            </el-icon>
                            <span class="btn-text">返回</span>
                        </div>
                        <div class="btn-box" @click="runWorkData">
                            <el-icon v-if="!runningLoading">
                                <VideoPlay />
                            </el-icon>
                            <el-icon v-else class="is-loading">
                                <Loading />
                            </el-icon>
                            <span class="btn-text">运行</span>
                        </div>
                        <div v-if="workConfig.workType === 'API'" class="btn-box" @click="terWorkData">
                            <el-icon v-if="!terLoading">
                                <Close />
                            </el-icon>
                            <el-icon v-else class="is-loading">
                                <Loading />
                            </el-icon>
                            <span class="btn-text">中止</span>
                        </div>
                        <div class="btn-box" @click="saveData">
                            <el-icon v-if="!saveLoading">
                                <Finished />
                            </el-icon>
                            <el-icon v-else class="is-loading">
                                <Loading />
                            </el-icon>
                            <span class="btn-text">保存</span>
                        </div>
                        <div class="btn-box" @click="setConfigData">
                            <el-icon>
                                <Setting />
                            </el-icon>
                            <span class="btn-text">配置</span>
                        </div>
                        <div class="btn-box" @click="locationNode">
                            <el-icon>
                                <Position />
                            </el-icon>
                            <span class="btn-text">定位</span>
                        </div>
                    </div>
                    <!-- 这里是表单部分 -->
                    <el-form ref="form" label-position="top" label-width="70px" :model="apiWorkConfig" :rules="rules">
                        <el-form-item prop="requestUrl" label="接口地址">
                            <el-input v-model="apiWorkConfig.requestUrl" clearable placeholder="请输入"
                                maxlength="1000"></el-input>
                        </el-form-item>
                        <el-form-item label="请求方式" prop="requestType">
                            <el-select v-model="apiWorkConfig.requestType" placeholder="请选择" @change="requestTypeChange">
                                <el-option label="GET" value="GET"/>
                                <el-option label="POST" value="POST"/>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="请求头" prop="requestHeader">
                            <span class="add-btn">
                                <el-icon @click="addNewOption('requestHeader')"><CirclePlus /></el-icon>
                            </span>
                            <div class="form-options__list">
                                <div class="form-options__item" v-for="(element, index) in apiWorkConfig.requestHeader">
                                    <div class="input-item">
                                        <span class="item-label">键</span>
                                        <el-input v-model="element.label" placeholder="请输入"></el-input>
                                    </div>
                                    <div class="input-item">
                                        <span class="item-label">值</span>
                                        <el-input v-model="element.value" placeholder="请输入"></el-input>
                                    </div>
                                    <div class="option-btn">
                                        <el-icon v-if="apiWorkConfig.requestHeader.length > 1" class="remove" @click="removeItem(index, 'requestHeader')"><CircleClose /></el-icon>
                                    </div>
                                </div>
                            </div>
                        </el-form-item>
                        <el-form-item label="请求参数" prop="requestParam">
                            <span class="add-btn">
                                <el-icon @click="addNewOption('requestParam')"><CirclePlus /></el-icon>
                            </span>
                            <div class="form-options__list">
                                <div class="form-options__item" v-for="(element, index) in apiWorkConfig.requestParam">
                                    <div class="input-item">
                                        <span class="item-label">键</span>
                                        <el-input v-model="element.label" placeholder="请输入"></el-input>
                                    </div>
                                    <div class="input-item">
                                        <span class="item-label">值</span>
                                        <el-input v-model="element.value" placeholder="请输入"></el-input>
                                    </div>
                                    <div class="option-btn">
                                        <el-icon v-if="apiWorkConfig.requestParam.length > 1" class="remove" @click="removeItem(index, 'requestParam')"><CircleClose /></el-icon>
                                    </div>
                                </div>
                            </div>
                        </el-form-item>
                        <el-form-item label="请求体" :class="{ 'show-screen__full': reqBodyFullStatus }" v-if="apiWorkConfig.requestType === 'POST'">
                            <span class="format-json" @click="formatterJsonEvent(apiWorkConfig, 'requestBody')">格式化JSON</span>
                            <el-icon class="modal-full-screen" @click="fullScreenEvent"><FullScreen v-if="!reqBodyFullStatus" /><Close v-else /></el-icon>
                            <code-mirror v-model="apiWorkConfig.requestBody" basic :lang="jsonLang"/>
                        </el-form-item>
                    </el-form>
                </div>
                <div class="log-show log-show-datasync">
                    <el-tabs v-model="activeName" @tab-change="tabChangeEvent">
                        <template v-for="tab in tabList" :key="tab.code">
                            <el-tab-pane v-if="!tab.hide" :label="tab.name" :name="tab.code" />
                        </template>
                    </el-tabs>
                    <component :is="currentTab" ref="containerInstanceRef" class="show-container" />
                </div>
            </div>
        </LoadingPage>
        <!-- 配置 -->
        <config-detail ref="configDetailRef"></config-detail>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, markRaw, nextTick } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
// import ConfigModal from './config-modal/index.vue'
import ConfigDetail from '../workflow-page/config-detail/index.vue'
import PublishLog from '../work-item/publish-log.vue'
import ReturnData from '../work-item/return-data.vue'
import RunningLog from '../work-item/running-log.vue'
import TotalDetail from '../work-item/total-detail.vue'

import { DeleteWorkData, GetWorkItemConfig, PublishWorkData, RunWorkItemConfig, SaveWorkItemConfig, TerWorkItemConfig } from '@/services/workflow.service'
import { ElMessage, ElMessageBox, ElInput, FormRules } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { Loading } from '@element-plus/icons-vue'
import CodeMirror from 'vue-codemirror6'
import {json} from '@codemirror/lang-json'
import { jsonFormatter } from '@/utils/formatter'

interface Option {
  label: string
  value: string
}
const emit = defineEmits(['back', 'locationNode'])

const optionsRule = (rule: any, value: any, callback: any) => {
    const valueList = (value || []).map(v => v.value).filter(v => !!v)
    if (value && value.some((item: Option) => !item.label || !item.value)) {
        callback(new Error('请将键/值输入完整'))
    } else if (value && valueList.length !== Array.from(new Set(valueList)).length) {
        callback(new Error('值重复，请重新输入'))
    } else {
        callback()
    }
}

const props = defineProps<{
    workItemConfig: any,
    workFlowData: any
}>()

const loading = ref(false)
const networkError = ref(false)
const runningLoading = ref(false)
const saveLoading = ref(false)
const terLoading = ref(false)
const publishLoading = ref(false)
const stopLoading = ref(false)
const configDetailRef = ref()
const activeName = ref()
const currentTab = ref()
const instanceId = ref('')
const changeStatus = ref(false)
const jsonLang = ref<any>(json())
const reqBodyFullStatus = ref(false)
const form = ref<FormInstance>()

const containerInstanceRef = ref(null)

let workConfig = reactive({
    workId: '',
    workType: ''
})
let apiWorkConfig = reactive({
    requestUrl: '',     // 接口请求url
    requestType: '',    // 接口请求类型
    requestParam: [
        {
            label: '',
            value: ''
        }
    ],   // 接口请求参数
    requestHeader: [
        {
            label: '',
            value: ''
        }
    ],  // 接口请求头
    requestBody: ''     // 接口请求体
})
const rules = reactive<FormRules>({
    requestUrl: [{ required: true, message: '请输入接口地址', trigger: ['blur', 'change'] }],
    requestType: [{ required: true, message: '请选择请求方式', trigger: ['blur', 'change'] }],
    requestParam: [

    ],
    requestHeader: [

    ]
})

const tabList = reactive([
    {
        name: '提交日志',
        code: 'PublishLog',
        hide: false
    }
])
function initData(id?: string, tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    GetWorkItemConfig({
        workId: props.workItemConfig.id
    })
        .then((res: any) => {
            workConfig = res.data
            workConfig.workType = props.workItemConfig.workType
            if (res.data.apiWorkConfig) {
                Object.keys(apiWorkConfig).forEach((key: string) => {
                    apiWorkConfig[key] = res.data.apiWorkConfig[key]
                })
            }
            nextTick(() => {
                changeStatus.value = false
                containerInstanceRef.value.initData(id || instanceId.value, (status: string) => {
                    // 运行结束
                    if (workConfig.workType === 'API') {
                        tabList.forEach((item: any) => {
                            if (['RunningLog'].includes(item.code)) {
                                item.hide = false
                            }
                        })
                    }
                })
            })
            loading.value = false
            networkError.value = false
        })
        .catch(() => {
            loading.value = false
            networkError.value = false
        })
}

function tabChangeEvent(e: string) {
    const lookup = {
        PublishLog: PublishLog,
        ReturnData: ReturnData,
        RunningLog: RunningLog,
        TotalDetail: TotalDetail
    }
    activeName.value = e
    currentTab.value = markRaw(lookup[e])
    nextTick(() => {
        containerInstanceRef.value.initData(instanceId.value)
    })
}

// 返回
function goBack() {
    if (changeStatus.value) {
        ElMessageBox.confirm('作业尚未保存，是否确定要返回吗？', '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            emit('back', props.workItemConfig.id)
        })
    } else {
        emit('back', props.workItemConfig.id)
    }
}
function locationNode() {
    if (changeStatus.value) {
        ElMessageBox.confirm('作业尚未保存，是否确定要返回吗？', '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            emit('locationNode', props.workItemConfig.id)
        })
    } else {
        emit('locationNode', props.workItemConfig.id)
    }
}

// 运行
function runWorkData() {
    if (changeStatus.value) {
        ElMessageBox.confirm('作业尚未保存，是否确定要运行作业？', '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            runningLoading.value = true
            RunWorkItemConfig({
                workId: props.workItemConfig.id
            })
                .then((res: any) => {
                    runningLoading.value = false
                    instanceId.value = res.data.instanceId
                    ElMessage.success(res.msg)
                    initData(res.data.instanceId, true)

                    // 点击运行，默认跳转到提交日志tab
                    activeName.value = 'PublishLog'
                    currentTab.value = markRaw(PublishLog)
                })
                .catch(() => {
                    runningLoading.value = false
                })
        })
    } else {
        tabList.forEach((item: any) => {
            if (['RunningLog', 'TotalDetail', 'ReturnData'].includes(item.code)) {
                item.hide = true
            }
        })
        runningLoading.value = true
        RunWorkItemConfig({
            workId: props.workItemConfig.id
        })
            .then((res: any) => {
                runningLoading.value = false
                instanceId.value = res.data.instanceId
                ElMessage.success(res.msg)
                initData(res.data.instanceId, true)

                // 点击运行，默认跳转到提交日志tab
                activeName.value = 'PublishLog'
                currentTab.value = markRaw(PublishLog)
            })
            .catch(() => {
                runningLoading.value = false
            })
    }
}

// 终止
function terWorkData() {
    if (!instanceId.value) {
        ElMessage.warning('暂无可中止的作业')
        return
    }
    terLoading.value = true
    TerWorkItemConfig({
        workId: props.workItemConfig.id,
        instanceId: instanceId.value
    })
        .then((res: any) => {
            terLoading.value = false
            ElMessage.success(res.msg)
            initData('', true)
        })
        .catch(() => {
            terLoading.value = false
        })
}

// 保存配置
function saveData() {
    form.value?.validate((valid) => {
        if (valid) {
            saveLoading.value = true
            SaveWorkItemConfig({
                workId: props.workItemConfig.id,
                apiWorkConfig: apiWorkConfig
            }).then((res: any) => {
                changeStatus.value = false
                ElMessage.success(res.msg)
                saveLoading.value = false
            }).catch(() => {
                saveLoading.value = false
            })
        } else {
            ElMessage.warning('请将表单输入完整')
        }
    })
}

// 发布
function publishData() {
    publishLoading.value = true
    PublishWorkData({
        workId: props.workItemConfig.id
    }).then((res: any) => {
        ElMessage.success(res.msg)
        publishLoading.value = false
    })
        .catch((error: any) => {
            publishLoading.value = false
        })
}

// 下线
function stopData() {
    stopLoading.value = true
    DeleteWorkData({
        workId: props.workItemConfig.id
    }).then((res: any) => {
        ElMessage.success(res.msg)
        stopLoading.value = false
    })
        .catch((error: any) => {
            stopLoading.value = false
        })
}

// 配置打开
function setConfigData() {
    configDetailRef.value.showModal(props.workItemConfig)
}

function addNewOption(type: string) {
    if (type === 'requestHeader') {
        apiWorkConfig.requestHeader.push({
            label: '',
            value: ''
        })
    } else if (type === 'requestParam') {
        apiWorkConfig.requestParam.push({
            label: '',
            value: ''
        })
    }
}
function removeItem(index: number, type: string) {
    if (type === 'requestHeader') {
        apiWorkConfig.requestHeader.splice(index, 1)
    } else if (type === 'requestParam') {
        apiWorkConfig.requestParam.splice(index, 1)
    }
}
function formatterJsonEvent(formData: any, key: string) {
    try {
        formData[key] = jsonFormatter(formData[key])
    } catch (error) {
        console.error('请检查输入的JSON格式是否正确', error)
        ElMessage.error('请检查输入的JSON格式是否正确')
    }
}
function fullScreenEvent() {
    reqBodyFullStatus.value = !reqBodyFullStatus.value
}
function requestTypeChange() {
    apiWorkConfig.requestBody = ''
}

onMounted(() => {
    initData()
    activeName.value = 'PublishLog'
    currentTab.value = markRaw(PublishLog)
})
</script>

<style lang="scss">
.zqy-work-api {
    .zqy-loading {
        overflow-y: auto;
    }
    .el-form-item {
        .modal-full-screen {
            position: absolute;
            top: -26px;
            right: 0;
            cursor: pointer;
            &:hover {
                color: getCssVar('color', 'primary');
            }
        }
        .format-json {
            position: absolute;
            top: -34px;
            right: 20px;
            font-size: 12px;
            color: getCssVar('color', 'primary');
            cursor: pointer;
            &:hover {
                text-decoration: underline;
            }
        }
        &.show-screen__full {
            position: fixed;
            width: 100%;
            height: 100%;
            top: 0;
            left: 0;
            background-color: #ffffff;
            padding: 12px 20px;
            box-sizing: border-box;
            transition: all 0.15s linear;
            z-index: 10;
            .el-form-item__content {
                align-items: flex-start;
                height: 100%;
                .modal-full-screen {
                    position: absolute;
                    top: -26px;
                    right: 0;
                    cursor: pointer;
                    &:hover {
                        color: getCssVar('color', 'primary');
                    }
                }
                .vue-codemirror {
                    height: calc(100% - 36px);
                }
            }
        }
        .el-form-item__content {
            .vue-codemirror {
                height: 130px;
                width: 100%;

                .cm-editor {
                height: 100%;
                outline: none;
                border: 1px solid #dcdfe6;
                }

                .cm-gutters {
                font-size: 12px;
                font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                }

                .cm-content {
                font-size: 12px;
                font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                }

                .cm-tooltip-autocomplete {
                ul {
                    li {
                    height: 40px;
                    display: flex;
                    align-items: center;
                    font-size: 12px;
                    background-color: #ffffff;
                    font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                    }

                    li[aria-selected] {
                    background: #409EFF;
                    }

                    .cm-completionIcon {
                    margin-right: -4px;
                    opacity: 0;
                    }
                }
                }
            }
            .add-btn {
                position: absolute;
                right: 0;
                top: -32px;
                .el-icon {
                    color: getCssVar('color', 'primary');
                    cursor: pointer;
                    font-size: 16px;
                }
            }
            .form-options__list {
                width: 100%;
                max-height: 210px;
                overflow: auto;
                .form-options__item {
                    display: flex;
                    margin-bottom: 8px;
                    .input-item {
                        display: flex;
                        font-size: 12px;
                        margin-right: 8px;
                        color: #303133;
                        width: 100%;
                        .item-label {
                            margin-right: 8px;
                        }
                        .el-input {
                            .el-input__wrapper {
                                // padding: 0;
                            }
                        }
                    }
                    .option-btn {
                        display: flex;
                        height: 32px;
                        align-items: center;
                        margin-right: -8px;
                        .remove {
                            color: red;
                            cursor: pointer;
                            margin-right: 8px;
                            &:hover {
                                color: getCssVar('color', 'primary');
                            }
                        }
                        .move {
                            color: getCssVar('color', 'primary');
                            &:active {
                                cursor: move;
                            }
                        }
                    }
                }
            }
        }
    }
}
</style>