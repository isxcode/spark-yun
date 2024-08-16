<template>
  <BlockDrawer :drawer-config="drawerConfig">
    <el-scrollbar>
        <div class="work-flow-config realtime-flow-config">
          <!-- 资源配置 -->
          <div class="config-item">
            <div class="item-title">资源配置</div>
            <el-form
              ref="clusterConfigForm"
              label-position="left"
              label-width="120px"
              :model="clusterConfig"
              :rules="clusterConfigRules"
            >
              <!-- <el-form-item label="模式" v-if="!['BASH', 'PYTHON'].includes(workItemConfig.workType)">
                <el-radio-group v-model="clusterConfig.setMode" size="small">
                  <el-radio-button label="SIMPLE">简易</el-radio-button>
                  <el-radio-button label="ADVANCE">高级定义</el-radio-button>
                </el-radio-group>
              </el-form-item> -->
              <el-form-item label="计算集群" prop="clusterId">
                <el-select v-model="clusterConfig.clusterId" placeholder="请选择" @change="clusterIdChangeEvent">
                  <el-option
                    v-for="item in clusterList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="sparkConfig" :class="{ 'show-screen__full': sparkJsonFullStatus }">
                  <el-icon class="modal-full-screen" @click="fullScreenEvent('sparkJsonFullStatus')"><FullScreen v-if="!sparkJsonFullStatus" /><Close v-else /></el-icon>
                  <code-mirror v-model="clusterConfig.sparkConfigJson" basic :lang="lang"/>
              </el-form-item>
              <!-- <el-form-item label="sparkConfig" v-if="clusterConfig.setMode === 'ADVANCE'">
                <code-mirror v-model="clusterConfig.sparkConfigJson" basic :lang="lang"/>
              </el-form-item>
              <el-form-item label="资源等级" v-else>
                <el-select v-model="clusterConfig.resourceLevel" placeholder="请选择">
                  <el-option
                    v-for="item in resourceLevelOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item> -->
            </el-form>
          </div>
          <!-- 函数配置 -->
          <div class="config-item">
            <div class="item-title">函数配置</div>
            <el-form
              label-position="left"
              label-width="120px"
              :model="fileConfig"
            >
              <el-form-item label="函数">
                <el-select v-model="fileConfig.funcList" collapse-tags multiple clearable
                  filterable placeholder="请选择">
                    <el-option v-for="item in fileIdList" :key="item.id" :label="item.funcName"
                        :value="item.id" />
                </el-select>
              </el-form-item>
            </el-form>
          </div>
          <!-- 依赖配置 -->
          <div class="config-item">
            <div class="item-title">依赖配置</div>
            <el-form
              label-position="left"
              label-width="120px"
              :model="fileConfig"
            >
              <el-form-item label="依赖">
                <el-select v-model="fileConfig.libList" collapse-tags multiple clearable
                    filterable placeholder="请选择">
                    <el-option v-for="item in libIdList" :key="item.value" :label="item.label"
                        :value="item.value" />
                </el-select>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-scrollbar>
  </BlockDrawer>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import BlockDrawer from '@/components/block-drawer/index.vue'
import { GetFileCenterList } from '@/services/file-center.service'
import { GetCustomFuncList } from '@/services/custom-func.service'
import { ConifgTimeComputingData, GetTimeComputingDetail } from '@/services/realtime-computing.service';
import { ClusterConfigRules, ResourceLevelOptions} from './config-detail'
import { GetComputerGroupList } from '@/services/computer-group.service'
import { jsonFormatter } from '@/utils/formatter'
import {json} from '@codemirror/lang-json'
// import CodeMirror from 'vue-codemirror6'

const workItemConfig = ref()
const fileIdList = ref([]) // 资源文件
const libIdList = ref([])  // 依赖

const clusterList = ref([])  // 计算集群
const clusterNodeList = ref([])  // 集群节点
const clusterConfigForm = ref<FormInstance>()
const lang = ref<any>(json())
const resourceLevelOptions = ref(ResourceLevelOptions) // 资源等级
// 输入框全屏
const sparkJsonFullStatus = ref(false)

const drawerConfig = reactive({
  title: '配置',
  visible: false,
  width: '400',
  okConfig: {
    title: '确定',
    ok: okEvent,
    disabled: false,
    loading: false,
  },
  cancelConfig: {
    title: '取消',
    cancel: closeEvent,
    disabled: false,
  },
  zIndex: 1100,
  closeOnClickModal: false,
});
// 集群设置
let clusterConfig = reactive({
  setMode: '',       // 模式
  resourceLevel: '',        // 资源等级
  clusterId: '',            // 计算集群
  clusterNodeId: '',        // 集群节点
  enableHive: false,
  datasourceId: '',   // hive数据源
  // sparkConfig: '',
  sparkConfigJson: ''
})
const fileConfig = reactive({
  funcList: [],
  libList: []
})
const clusterConfigRules = reactive<FormRules>(ClusterConfigRules)

function showModal(data?: any) {
  if (!data) {
    // clusterConfig.setMode = 'SIMPLE'
  } else {
    getClusterList()
    // 获取函数配置和依赖配置
    getFuncList()
    getFileCenterList()

    workItemConfig.value = data
    getConfigDetailData()
  }

  drawerConfig.visible = true;
}

function getFileCenterList() {
    GetFileCenterList({
        page: 0,
        pageSize: 10000,
        searchKeyWord: '',
        type: 'LIB'
    }).then((res: any) => {
        libIdList.value = res.data.content.map(item => {
            return {
                label: item.fileName,
                value: item.id
            }
        })
    }).catch(() => {
        libIdList.value = []
    })
}
function getFuncList() {
    GetCustomFuncList({
        page: 0,
        pageSize: 10000,
        searchKeyWord: ''
    }).then((res: any) => {
        fileIdList.value = res.data.content
    }).catch(() => {
        fileIdList.value = []
    })
}
function getClusterList() {
  GetComputerGroupList({
    page: 0,
    pageSize: 10000,
    searchKeyWord: ''
  }).then((res: any) => {
    clusterList.value = res.data.content.map((item: any) => {
      return {
        label: item.name,
        value: item.id
      }
    })
  }).catch(() => {
    clusterList.value = []
  })
}

function clusterIdChangeEvent() {
  clusterConfig.clusterNodeId = ''
}

function getConfigDetailData() {
  GetTimeComputingDetail({
    id: workItemConfig.value.id
  }).then((res: any) => {
    // if (res.data.clusterConfig) {
    //   Object.keys(clusterConfig).forEach((key: string) => {
    //     if (key !== 'datasourceId') {
    //       clusterConfig[key] = res.data.clusterConfig[key]
    //     }
    //   })
    // }
    clusterConfig.sparkConfigJson = jsonFormatter(res.data.sparkConfigJson || res.data.sparkConfig)
    clusterConfig.clusterId = res.data.clusterId
    fileConfig.funcList = res.data.funcConfig || []
    fileConfig.libList = res.data.libConfig || []
  }).catch((err: any) => {
    console.error(err)
  })
}

function okEvent() {
  let status = true
  const formArr = [clusterConfigForm]
  formArr.forEach(f => {
    f.value?.validate((valid: boolean) => {
      if (!valid) {
        status = false
      }
    })
  })
  setTimeout(() => {
    if (status) {
      ConifgTimeComputingData({
        realId: workItemConfig.value.id,
        clusterId: clusterConfig.clusterId,
        sparkConfigJson: clusterConfig.sparkConfigJson,
        ...fileConfig
      }).then((res: any) => {
        ElMessage.success('保存成功')
        drawerConfig.visible = false;
      }).catch(err => {
        console.error(err)
      })
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
}
// 全屏
function fullScreenEvent(type: string) {
  if (type === 'sparkJsonFullStatus') {
    sparkJsonFullStatus.value = !sparkJsonFullStatus.value
  }
}

function closeEvent() {
  drawerConfig.visible = false;
}

defineExpose({
  showModal,
})
</script>

<style lang="scss">
.realtime-flow-config {
  padding: 12px;

  &.realtime-flow-config {
    .el-form-item {
      position: relative;
      // 全屏样式
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
        display: flex;
        flex-direction: column;
        .el-form-item__content {
          align-items: flex-start;
          height: 100%;
          margin-top: 18px;
          .modal-full-screen {
            top: -36px;
            right: 0;
            left: unset;
          }
          .vue-codemirror {
            height: calc(100% - 48px);
          }
        }
      }
      .modal-full-screen {
          top: 9px;
          left: 24px;
      }
    }
  }
  .config-item {
    .item-title {
      font-size: 12px;
      padding-bottom: 12px;
      box-sizing: border-box;
      border-bottom: 1px solid #ebeef5;
    }
    .el-form {
      padding: 12px 0 0;
      box-sizing: border-box;
      .el-form-item {
        .el-form-item__content {
          .el-radio-group {
            justify-content: flex-end;
          }
          .vue-codemirror {
            height: 100px;
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
        }
      }
    }
  }
  .el-divider {
    margin: 0 0 12px;
  }
}
.config-detail-form {
  padding: 12px 20px 0 20px;
  box-sizing: border-box;
  &.el-form {
    .el-form-item {
      .el-form-item__content {
        .el-date-editor {
          width: 100%;
          .el-input__wrapper {
            width: 100%;
          }
        }
        .el-select {
          width: 100%;
        }
        .el-input-number {
          width: 100%;
          .el-input__wrapper {
            .el-input__inner {
              text-align: left;
            }
          }
        }
      }
    }
  }
}
</style>