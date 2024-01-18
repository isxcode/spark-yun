<template>
  <BlockModal :model-config="modelConfig">
    <el-form
      ref="form"
      class="custom-api-form"
      label-position="top"
      :model="formData"
      :rules="rules"
    >
      <div class="api-item">
        <!-- 基础配置 -->
        <div class="item-title">基础配置</div>
        <el-form-item
          label="名称"
          prop="name"
        >
          <el-input
            v-model="formData.name"
            maxlength="200"
            placeholder="请输入"
          />
        </el-form-item>
        <el-form-item label="请求方式" prop="apiType">
          <el-select v-model="formData.apiType" placeholder="请选择">
            <el-option label="GET" value="GET"/>
            <el-option label="POST" value="POST"/>
          </el-select>
        </el-form-item>
        <el-form-item label="自定义访问路径" prop="path">
          <el-input
            v-model="formData.path"
            maxlength="1000"
            placeholder="请输入"
          />
        </el-form-item>
        <!-- <el-form-item label="计算集群" prop="clusterId">
          <el-select
            v-model="formData.clusterId"
            placeholder="请选择"
            @visible-change="getClusterList"
          >
            <el-option
              v-for="item in clusterList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item> -->
        <el-form-item label="数据源" prop="datasourceId" v-if="!isEdit">
          <el-select
            v-model="formData.datasourceId"
            placeholder="请选择"
            @visible-change="getDataSourceList"
          >
            <el-option
              v-for="item in dataSourceList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            maxlength="200"
            :autosize="{ minRows: 4, maxRows: 4 }"
            placeholder="请输入"
          />
        </el-form-item>
      </div>
      <div class="api-item">
        <!-- 接口配置 -->
        <div class="item-title">请求配置</div>
        <el-form-item label="请求头模式">
          <el-radio-group v-model="formData.tokenType" size="small">
            <el-radio-button label="ANONYMOUS">任何人访问</el-radio-button>
            <el-radio-button label="SYSTEM">系统认证</el-radio-button>
            <el-radio-button label="CUSTOM">自定义</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formData.tokenType === 'CUSTOM'" label="请求头设置" prop="headerToken" :class="{ 'show-screen__full': reqHeaderFullStatus }">
          <el-icon class="modal-full-screen" @click="fullScreenEvent('reqHeaderFullStatus')"><FullScreen v-if="!reqHeaderFullStatus" /><Close v-else /></el-icon>
          <code-mirror v-model="formData.headerToken" basic :lang="jsonLang"/>
        </el-form-item>
        <el-form-item label="请求体设置" prop="reqBody" :class="{ 'show-screen__full': reqBodyFullStatus }">
          <el-icon class="modal-full-screen" @click="fullScreenEvent('reqBodyFullStatus')"><FullScreen v-if="!reqBodyFullStatus" /><Close v-else /></el-icon>
          <code-mirror v-model="formData.reqBody" basic :lang="jsonLang"/>
        </el-form-item>
        <el-form-item label="SQL设置" prop="apiSql" :class="{ 'show-screen__full': sqlFullStatus }">
          <el-icon class="modal-full-screen" @click="fullScreenEvent('sqlFullStatus')"><FullScreen v-if="!sqlFullStatus" /><Close v-else /></el-icon>
          <code-mirror v-model="formData.apiSql" basic :lang="sqlLang"/>
        </el-form-item>
        <el-form-item label="开启分页">
          <el-switch v-model="formData.pageType" />
        </el-form-item>
        <el-form-item label="返回体设置（成功/失败）" prop="resBody" :class="{ 'show-screen__full': respBodyFullStatus }">
          <el-icon class="modal-full-screen" @click="fullScreenEvent('respBodyFullStatus')"><FullScreen v-if="!respBodyFullStatus" /><Close v-else /></el-icon>
          <code-mirror v-model="formData.resBody" basic :lang="jsonLang"/>
        </el-form-item>
      </div>
    </el-form>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetDatasourceList } from '@/services/datasource.service'
import { GetComputerGroupList } from '@/services/computer-group.service'
import CodeMirror from 'vue-codemirror6'
import {json} from '@codemirror/lang-json'
import {sql} from '@codemirror/lang-sql'

const form = ref<FormInstance>()
const callback = ref<any>()
const uploadRef = ref()
const clusterList = ref([])  // 计算集群
const dataSourceList = ref([])  // 数据源

const jsonLang = ref<any>(json())
const sqlLang = ref<any>(sql())

const reqHeaderFullStatus = ref(false)
const reqBodyFullStatus = ref(false)
const sqlFullStatus = ref(false)
const respBodyFullStatus = ref(false)
const isEdit = ref(false)

const modelConfig = reactive({
  title: '添加接口',
  visible: false,
  width: '60%',
  okConfig: {
    title: '确定',
    ok: okEvent,
    disabled: false,
    loading: false
  },
  cancelConfig: {
    title: '取消',
    cancel: closeEvent,
    disabled: false
  },
  needScale: false,
  zIndex: 1100,
  closeOnClickModal: false
})
const formData = reactive({
  id: '',
  // 基础配置
  name: '',             // 名称
  apiType: '',           // 请求方式
  path: '',              // 自定义访问路径
  // clusterId: '',        // 计算集群
  datasourceId: '',     // 数据源
  remark: '',           // 备注
  // 请求配置
  tokenType: 'ANONYMOUS',    // 请求头模式
  headerToken: null,   // 请求头设置
  reqBody: null,     // 请求体设置
  apiSql: null,      // SQL设置
  pageType: false,  // 是否分页
  resBody: null    // 返回体设置（成功/失败）
})
const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入名称', trigger: [ 'blur', 'change' ]}],
  apiType: [{ required: true, message: '请选择请求方式', trigger: [ 'blur', 'change' ]}],
  path: [{ required: true, message: '请输入自定义访问路径', trigger: [ 'blur', 'change' ]}],
  // clusterId: [{ required: true, message: '请选择计算集群', trigger: [ 'blur', 'change' ]}],
  datasourceId: [{ required: true, message: '请选择数据源', trigger: [ 'blur', 'change' ]}],
  headerToken: [{ required: true, message: '请输入请求头设置', trigger: [ 'blur', 'change' ]}],
  reqBody: [{ required: true, message: '请输入请求体设置', trigger: [ 'blur', 'change' ]}],
  apiSql: [{ required: true, message: '请输入SQL设置', trigger: [ 'blur', 'change' ]}],
  resBody: [{ required: true, message: '请输入返回体设置（成功/失败）', trigger: [ 'blur', 'change' ]}],
})

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  modelConfig.visible = true
  if (data) {
    Object.keys(formData).forEach(key => {
      formData[key] = data[key]
    })
    isEdit.value = true
    modelConfig.title = '编辑接口'
  } else {
    Object.keys(formData).forEach(key => {
      formData[key] = null
    })
    formData.tokenType = 'ANONYMOUS'
    formData.pageType = false
    isEdit.value = false
    modelConfig.title = '添加接口'
  }
  nextTick(() => {
    form.value?.resetFields()
  })
}

// 查询计算集群
function getClusterList(e: boolean) {
  if (e) {
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
}

// 查询数据源
function getDataSourceList(e: boolean, searchType?: string) {
    if (e) {
        GetDatasourceList({
            page: 0,
            pageSize: 10000,
            searchKeyWord: searchType || ''
        }).then((res: any) => {
            dataSourceList.value = res.data.content.map((item: any) => {
                return {
                label: item.name,
                value: item.id
                }
            })
        })
        .catch(() => {
            dataSourceList.value = []
        })
    }
}

function okEvent() {
  form.value?.validate((valid) => {
    if (valid) {
      modelConfig.okConfig.loading = true
      callback
        .value({
          ...formData,
          id: formData.id ? formData.id : undefined
        })
        .then((res: any) => {
          modelConfig.okConfig.loading = false
          if (res === undefined) {
            modelConfig.visible = false
          } else {
            modelConfig.visible = true
          }
        })
        .catch(() => {
          modelConfig.okConfig.loading = false
        })
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
}

function closeEvent() {
  modelConfig.visible = false
}

function fullScreenEvent(type: string) {
  if (type === 'reqHeaderFullStatus') {
    reqHeaderFullStatus.value = !reqHeaderFullStatus.value
  } else if (type === 'reqBodyFullStatus') {
    reqBodyFullStatus.value = !reqBodyFullStatus.value
  } else if (type === 'respBodyFullStatus') {
    respBodyFullStatus.value = !respBodyFullStatus.value
  } else if (type === 'sqlFullStatus') {
    sqlFullStatus.value = !sqlFullStatus.value
  }
}

defineExpose({
  showModal
})
</script>

<style lang="scss">
.custom-api-form {
  box-sizing: border-box;
  padding: 12px 20px 0 20px;
  width: 100%;

  .api-item {
    .item-title {
      font-size: 14px;
      padding-bottom: 12px;
      margin-bottom: 12px;
      box-sizing: border-box;
      border-bottom: 1px solid #ebeef5;
      font-weight: bolder;
      color: getCssVar('color', 'primary');
    }
  }
  .el-form-item {
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
            color: getCssVar('color', 'primary');;
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
    }
  }
}
</style>
