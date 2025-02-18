<template>
  <div class="zqy-work-item">
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
            <div class="btn-box" @click="terWorkData">
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
            <!-- <div class="btn-box" @click="publishData">
              <el-icon v-if="!publishLoading">
                <Promotion />
              </el-icon>
              <el-icon v-else class="is-loading">
                <Loading />
              </el-icon>
              <span class="btn-text">发布</span>
            </div>
            <div class="btn-box" @click="stopData">
              <el-icon v-if="!stopLoading">
                <Failed />
              </el-icon>
              <el-icon v-else class="is-loading">
                <Loading />
              </el-icon>
              <span class="btn-text">下线</span>
            </div> -->
            <div class="btn-box" @click="locationNode">
              <el-icon>
                <Position />
              </el-icon>
              <span class="btn-text">定位</span>
            </div>
          </div>
          <code-mirror v-model="sqltextData" basic :lang="workConfig.workType === 'PYTHON' ? pythonLang : lang"
            @change="sqlConfigChange" />
        </div>

        <el-collapse v-model="collapseActive" class="work-item-log__collapse" ref="logCollapseRef">
          <el-collapse-item title="查看日志" :disabled="true" name="1">
            <template #title>
              <el-tabs v-model="activeName" @tab-click="changeCollapseUp" @tab-change="tabChangeEvent">
                <template v-for="tab in tabList" :key="tab.code">
                  <el-tab-pane v-if="!tab.hide" :label="tab.name" :name="tab.code" />
                </template>
              </el-tabs>
              <span class="log__collapse">
                <el-icon v-if="isCollapse" @click="changeCollapseDown">
                  <ArrowDown />
                </el-icon>
                <el-icon v-else @click="changeCollapseUp">
                  <ArrowUp />
                </el-icon>
              </span>
            </template>
            <div class="log-show log-show-datasync">
              <component
                :is="currentTab"
                ref="containerInstanceRef"
                class="show-container"
                :showParse="showParse"
                @getJsonParseResult="getJsonParseResult"
              />
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </LoadingPage>
    <!-- 配置 -->
    <config-detail ref="configDetailRef"></config-detail>
    <!-- 解析弹窗 -->
    <ParseModal ref="parseModalRef"></ParseModal>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, markRaw, nextTick, computed } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import ConfigDetail from '../workflow-page/config-detail/index.vue'
import PublishLog from './publish-log.vue'
import ReturnData from './return-data.vue'
import RunningLog from './running-log.vue'
import TotalDetail from './total-detail.vue'
import { sql } from '@codemirror/lang-sql'
import { python } from '@codemirror/lang-python'

import { DeleteWorkData, GetWorkItemConfig, PublishWorkData, RunWorkItemConfig, SaveWorkItemConfig, TerWorkItemConfig } from '@/services/workflow.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { Loading } from '@element-plus/icons-vue'
import ParseModal from '@/components/log-container/parse-modal/index.vue'

const route = useRoute()
const router = useRouter()
const emit = defineEmits(['back', 'locationNode'])

const props = defineProps<{
  workItemConfig: any,
  workFlowData: any
}>()

const lang = ref(sql())
const pythonLang = ref(python())
const loading = ref(false)
const networkError = ref(false)
const runningLoading = ref(false)
const saveLoading = ref(false)
const terLoading = ref(false)
const publishLoading = ref(false)
const stopLoading = ref(false)
// const configModalRef = ref(null)
const configDetailRef = ref()
const activeName = ref()
const currentTab = ref()
const sqltextData = ref('')
const instanceId = ref('')
const changeStatus = ref(false)

const containerInstanceRef = ref(null)

const logCollapseRef = ref()
const collapseActive = ref('0')
const isCollapse = ref(false)
const parseModalRef = ref()

let workConfig = reactive({
  clusterId: '',
  datasourceId: '',
  corn: '',
  name: '',
  sqlScript: '',
  workId: '',
  workType: '',
  workflowId: '',
  applicationId: '',
  sparkConfig: ''
})

const tabList = reactive([
  {
    name: '提交日志',
    code: 'PublishLog',
    hide: false
  },
  {
    name: '运行结果',
    code: 'ReturnData',
    hide: true
  },
  {
    name: '运行日志',
    code: 'RunningLog',
    hide: true
  },
  // {
  //   name: '监控信息',
  //   code: 'TotalDetail',
  //   hide: true
  // }
])

const showParse = computed(() => {
  return ['CURL', 'QUERY_JDBC', 'SPARK_SQL', 'BASH', 'PYTHON'].includes(props.workItemConfig.workType)
})
function initData(id?: string, tableLoading?: boolean) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  GetWorkItemConfig({
    workId: props.workItemConfig.id
  })
    .then((res: any) => {
      workConfig = res.data
      workConfig.workType = props.workItemConfig.workType
      if (!tableLoading) {
        sqltextData.value = res.data.script
      }
      nextTick(() => {
        changeStatus.value = false
        containerInstanceRef.value.initData(id || instanceId.value, (status: string) => {

          if (id) {
            // 运行结束
            if (workConfig.workType === 'SPARK_SQL' || workConfig.workType === 'PY_SPARK') {
              tabList.forEach((item: any) => {
                if (['RunningLog', 'TotalDetail'].includes(item.code)) {
                  item.hide = false
                }
                if (item.code === 'ReturnData') {
                  item.hide = status === 'FAIL' ? true : false
                }
              })
            } else if (['QUERY_JDBC', 'SPARK_CONTAINER_SQL', 'PRQL', 'CURL', 'BASH', 'PYTHON'].includes(workConfig.workType)) {
              tabList.forEach((item: any) => {
                if (['ReturnData'].includes(item.code)) {
                  item.hide = status === 'FAIL' ? true : false
                }
              })
            }
            if (['CURL'].includes(workConfig.workType)) {
              tabList.forEach((item: any) => {
                if (['RunningLog'].includes(item.code)) {
                  item.hide = false
                }
              })
            }
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
      tabList.forEach((item: any) => {
        if (['RunningLog', 'TotalDetail', 'ReturnData'].includes(item.code)) {
          item.hide = true
        }
      })
      runningLoading.value = true
      // 点击运行，默认跳转到提交日志tab
      activeName.value = 'PublishLog'
      currentTab.value = markRaw(PublishLog)
      RunWorkItemConfig({
        workId: props.workItemConfig.id
      })
        .then((res: any) => {
          runningLoading.value = false
          instanceId.value = res.data.instanceId
          ElMessage.success(res.msg)
          initData(res.data.instanceId, true)
          nextTick(() => {
            changeCollapseUp()
          })
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
        nextTick(() => {
          changeCollapseUp()
        })
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
  saveLoading.value = true
  SaveWorkItemConfig({
    script: sqltextData.value,
    workId: props.workItemConfig.id,
    datasourceId: workConfig.datasourceId,
    // sparkConfig: workConfig.sparkConfig,
    // clusterId: workConfig.clusterId,
    // corn: workConfig.corn
  })
    .then((res: any) => {
      changeStatus.value = false
      ElMessage.success(res.msg)
      saveLoading.value = false
    })
    .catch(() => {
      saveLoading.value = false
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
  configDetailRef.value.showModal(props.workItemConfig, () => {
    initData()
  })
}
function changeCollapseDown() {
  logCollapseRef.value.setActiveNames('0')
  isCollapse.value = false
}
function changeCollapseUp(e: any) {
  if (e && e.paneName === activeName.value && isCollapse.value) {
    changeCollapseDown()
  } else {
    logCollapseRef.value.setActiveNames('1')
    isCollapse.value = true
  }
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

function sqlConfigChange(e: string) {
  changeStatus.value = true
}

function getJsonParseResult() {
  if (['CURL'].includes(props.workItemConfig.workType)) {
    parseModalRef.value.showModal(instanceId.value, 'jsonPath')
  } else if (['QUERY_JDBC', 'SPARK_SQL'].includes(props.workItemConfig.workType)) {
    parseModalRef.value.showModal(instanceId.value, 'tablePath')
  } else if (['BASH', 'PYTHON'].includes(props.workItemConfig.workType)) {
    parseModalRef.value.showModal(instanceId.value, 'regexPath')
  }
}

onMounted(() => {
  initData()
  activeName.value = 'PublishLog'
  currentTab.value = markRaw(PublishLog)
})
</script>

<style lang="scss">
.zqy-work-item {
  .zqy-loading {
    padding: 0 20px;
    box-sizing: border-box;
    height: calc(100vh - 55px);
  }

  .zqy-work-container {
    .sql-code-container {
      .vue-codemirror {
        height: calc(100vh - 174px);
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

          // display: none !important;
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

      .sql-option-container {
        height: 50px;
        display: flex;
        align-items: center;
        color: getCssVar('color', 'primary', 'light-5');

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
            ;
          }
        }
      }

    }

    .work-item-log__collapse {
      position: absolute;
      left: 0;
      right: 0;
      bottom: -2px;
      z-index: 100;

      .el-collapse-item__header {
        // padding-left: 20px;
        cursor: default;
      }

      .el-collapse-item__arrow {
        display: none;
      }

      .el-collapse-item__content {
        padding-bottom: 14px;
      }

      .log__collapse {
        position: absolute;
        right: 20px;
        cursor: pointer;
      }

      .el-tabs {
        width: 100%;
        // padding: 0 20px;
        height: 40px;
        box-sizing: border-box;

        .el-tabs__item {
          font-size: getCssVar('font-size', 'extra-small');
        }

        .el-tabs__nav-scroll {
          padding-left: 20px;
          box-sizing: border-box;
        }

        .el-tabs__content {
          height: 0;
        }

        .el-tabs__nav-scroll {
          border-bottom: 1px solid getCssVar('border-color');
        }
      }

      .log-show {
        padding: 0 20px;
        box-sizing: border-box;
        overflow: auto;

        &.log-show-datasync {
          height: calc(100vh - 368px);
          .zqy-download-log {
            right: 40px;
            top: 12px;
          }
        }

        pre {
          width: 100px;
        }

        .show-container {
          height: calc(100vh - 368px);
          overflow: auto;
        }

        .empty-page {
          height: 80%;
        }
      }
    }
  }
}
</style>
