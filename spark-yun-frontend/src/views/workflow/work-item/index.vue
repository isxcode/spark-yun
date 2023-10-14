<template>
  <!-- <Breadcrumb :bread-crumb-list="breadCrumbList" /> -->
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
            <div v-if="workConfig.workType === 'SPARK_SQL'" class="btn-box" @click="terWorkData">
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
                <RefreshLeft />
              </el-icon>
              <span class="btn-text">定位</span>
            </div>
          </div>
          <code-mirror v-model="sqltextData" basic :lang="lang" />
        </div>
        <div class="log-show">
          <el-tabs v-model="activeName" @tab-change="tabChangeEvent">
            <template v-for="tab in tabList" :key="tab.code">
              <el-tab-pane v-if="!tab.hide" :label="tab.name" :name="tab.code" />
            </template>
          </el-tabs>
          <component :is="currentTab" ref="containerInstanceRef" class="show-container" />
        </div>
      </div>
    </LoadingPage>
    <ConfigModal ref="configModalRef" />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, markRaw } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import ConfigModal from './config-modal/index.vue'
import PublishLog from './publish-log.vue'
import ReturnData from './return-data.vue'
import RunningLog from './running-log.vue'
import TotalDetail from './total-detail.vue'
import CodeMirror from 'vue-codemirror6'
import { sql } from '@codemirror/lang-sql'

import { GetWorkItemConfig, RunWorkItemConfig, SaveWorkItemConfig, TerWorkItemConfig } from '@/services/workflow.service'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { nextTick } from 'vue'

const route = useRoute()
const router = useRouter()
const emit = defineEmits(['back', 'locationNode'])

const props = defineProps<{
  workItemConfig: any,
  workFlowData: any
}>()

const lang = ref(sql())
const loading = ref(false)
const networkError = ref(false)
const runningLoading = ref(false)
const saveLoading = ref(false)
const terLoading = ref(false)
const configModalRef = ref(null)
const activeName = ref()
const currentTab = ref()
const sqltextData = ref('')
const instanceId = ref('')

const containerInstanceRef = ref(null)

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

const breadCrumbList = reactive([
  {
    name: '作业流',
    code: 'workflow'
  },
  {
    name: '作业',
    code: 'workflow-detail',
    query: {
      // id: route.query.workflowId
      id: props.workFlowData.id
    }
  },
  {
    name: '作业详情',
    code: 'work-item'
  }
])
const tabList = reactive([
  {
    name: '提交日志',
    code: 'PublishLog',
    hide: false
  },
  {
    name: '数据返回',
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
function initData(id?: string) {
  loading.value = true
  networkError.value = networkError.value || false
  GetWorkItemConfig({
    workId: props.workItemConfig.id
  })
    .then((res: any) => {
      workConfig = res.data
      sqltextData.value = res.data.sqlScript
      nextTick(() => {
        containerInstanceRef.value.initData(id || instanceId.value, (status: string) => {
          // 运行结束
          if (workConfig.workType === 'SPARK_SQL') {
            tabList.forEach((item: any) => {
              if (['RunningLog', 'TotalDetail'].includes(item.code)) {
                item.hide = false
              }
              if (item.code === 'ReturnData') {
                item.hide = status === 'FAIL' ? true : false
              }
            })
          } else if (workConfig.workType === 'QUERY_JDBC') {
            tabList.forEach((item: any) => {
              if (['ReturnData'].includes(item.code)) {
                item.hide = status === 'FAIL' ? true : false
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
  emit('back', props.workItemConfig.id)
}
function locationNode() {
  emit('locationNode', props.workItemConfig.id)
}

// 运行
function runWorkData() {
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
      initData(res.data.instanceId)

      // 点击运行，默认跳转到提交日志tab
      activeName.value = 'PublishLog'
      currentTab.value = markRaw(PublishLog)
    })
    .catch(() => {
      runningLoading.value = false
    })
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
      initData()
    })
    .catch(() => {
      terLoading.value = false
    })
}

// 保存配置
function saveData() {
  saveLoading.value = true
  SaveWorkItemConfig({
    sqlScript: sqltextData.value,
    workId: props.workItemConfig.id,
    datasourceId: workConfig.datasourceId,
    sparkConfig: workConfig.sparkConfig,
    clusterId: workConfig.clusterId,
    corn: workConfig.corn
  })
    .then((res: any) => {
      ElMessage.success(res.msg)
      saveLoading.value = false
    })
    .catch(() => {
      saveLoading.value = false
    })
}

// 配置打开
function setConfigData() {
  configModalRef.value.showModal((formData: any) => {
    return new Promise((resolve: any, reject: any) => {
      SaveWorkItemConfig({
        sqlScript: sqltextData.value,
        workId: props.workItemConfig.id,
        datasourceId: formData.datasourceId,
        clusterId: formData.clusterId,
        sparkConfig: formData.sparkConfig,
        corn: formData.corn,
      })
        .then((res: any) => {
          ElMessage.success(res.msg)
          initData()
          resolve()
        })
        .catch((error: any) => {
          reject(error)
        })
    })
  }, workConfig)
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
    height: calc(100vh - 116px);
  }

  .zqy-work-container {
    .sql-code-container {
      .vue-codemirror {
        // height: calc(100vh - 544px);
        height: 190px;
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
        height: getCssVar('menu', 'item-height');
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
            color: getCssVar('color', 'primary');;
          }
        }
      }

      .el-textarea {
        .el-textarea__inner {
          border-radius: getCssVar('border-radius', 'small');
          font-size: getCssVar('font-size', 'extra-small');
        }
      }
    }

    .log-show {
      .el-tabs {
        .el-tabs__item {
          font-size: getCssVar('font-size', 'extra-small');
        }

        .el-tabs__content {
          height: 0;
        }

        .el-tabs__nav-scroll {
          border-bottom: 1px solid getCssVar('border-color');
        }
      }

      .show-container {
        height: calc(100vh - 420px);
        overflow: auto;
      }
    }
  }
}
</style>
