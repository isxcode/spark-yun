<template>
  <BlockModal :model-config="modelConfig">
    <el-form ref="form" class="spark-container-modal" label-position="top" :model="formData" :rules="rules">
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" maxlength="200" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="集群" prop="clusterId">
        <el-select v-model="formData.clusterId" placeholder="请选择" @visible-change="getClusterList">
          <el-option
            v-for="item in clusterList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="数据源" :prop="'datasourceId'">
        <el-select
          v-model="formData.datasourceId"
          placeholder="请选择"
          @visible-change="getDataSourceList($event, 'HIVE')"
        >
          <el-option
            v-for="item in dataSourceList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="资源类型" prop="resourceLevel">
        <el-select v-model="formData.resourceLevel" placeholder="请选择">
          <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="Spark配置" prop="sparkConfig" v-if="formData.resourceLevel === 'CUSTOM'" :class="{ 'show-screen__full': fullStatus }">
        <span class="format-json" @click="formatterJsonEvent(formData, 'sparkConfig')">格式化JSON</span>
        <el-icon class="modal-full-screen" @click="fullScreenEvent">
          <FullScreen v-if="!fullStatus" />
          <Close v-else />
        </el-icon>
        <code-mirror v-model="formData.sparkConfig" basic :lang="lang"/>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="formData.remark" type="textarea" maxlength="200" :autosize="{ minRows: 4, maxRows: 4 }"
          placeholder="请输入" />
      </el-form-item>
    </el-form>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetComputerGroupList } from '@/services/computer-group.service';
import { GetDatasourceList } from '@/services/datasource.service'
// import CodeMirror from 'vue-codemirror6'
import {json} from '@codemirror/lang-json'
import { jsonFormatter } from '@/utils/formatter'

const form = ref<FormInstance>()
const callback = ref<any>()
const clusterList = ref([])  // 计算集群
const dataSourceList = ref([])
const fullStatus = ref(false)
const modelConfig = reactive({
  title: '添加容器',
  visible: false,
  width: '520px',
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
  name: '',           // 容器名称
  clusterId: '',      // 集群
  datasourceId: '',   // 数据源
  sparkConfig: '',    // spark配置
  resourceLevel: '',  // 资源等级
  remark: ''
})
const typeList = reactive([
  {
    label: '高',
    value: 'HIGH',
  },
  {
    label: '中',
    value: 'MEDIUM',
  },
  {
    label: '低',
    value: 'LOW',
  },
  {
    label: '自定义',
    value: 'CUSTOM',
  }
]);
const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入数据源名称', trigger: ['blur', 'change']}],
  clusterId: [{ required: true, message: '请选择集群', trigger: ['blur', 'change']}],
  datasourceId: [{ required: true, message: '请选择数据源', trigger: ['blur', 'change']}],
  resourceLevel: [{ required: true, message: '请选择资源类型', trigger: ['blur', 'change']}],
  sparkConfig: [{ required: true, message: '请输入sparkConfig', trigger: ['blur', 'change']}]
})

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  modelConfig.visible = true
  getClusterList(true)
  getDataSourceList(true, 'HIVE')
  if (data) {
    formData.name = data.name
    formData.clusterId = data.clusterId
    formData.datasourceId = data.datasourceId
    formData.sparkConfig = data.sparkConfig
    formData.resourceLevel = data.resourceLevel
    formData.remark = data.remark
    formData.id = data.id
    modelConfig.title = '编辑容器'
  } else {
    formData.name = ''
    formData.clusterId = ''
    formData.datasourceId = ''
    formData.sparkConfig = ''
    formData.resourceLevel = ''
    formData.id = ''
    modelConfig.title = '添加容器'
  }
  nextTick(() => {
    form.value?.resetFields()
  })
}


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

function getDataSourceList(e: boolean, searchType?: string) {
  if (e) {
    GetDatasourceList({
      page: 0,
      pageSize: 10000,
      searchKeyWord: 'HIVE'
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
      callback.value({
        ...formData,
        id: formData.id ? formData.id : undefined
      }).then((res: any) => {
        modelConfig.okConfig.loading = false
        if (res === undefined) {
          modelConfig.visible = false
        } else {
          modelConfig.visible = true
        }
      }).catch(() => {
        modelConfig.okConfig.loading = false
      })
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
}

function formatterJsonEvent(formData: any, key: string) {
  if (!formData[key]) {
    ElMessage.error('请输入JSON')
    return
  }
  try {
    formData[key] = jsonFormatter(formData[key])
  } catch (error) {
    console.error('请检查输入的JSON格式是否正确', error)
    ElMessage.error('请检查输入的JSON格式是否正确')
  }
}
function fullScreenEvent() {
  fullStatus.value = !fullStatus.value
}
function closeEvent() {
  modelConfig.visible = false
}

defineExpose({
  showModal
})
</script>

<style lang="scss">
.spark-container-modal {
  padding: 12px 20px 0 20px;
  box-sizing: border-box;
  .el-form-item {
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
        .vue-codemirror {
          height: calc(100% - 36px);
        }
      }
    }
    .el-form-item__content {
      position: relative;
      flex-wrap: nowrap;
      justify-content: space-between;

      .copy-url {
          min-width: 24px;
          font-size: 12px;
          margin-left: 10px;
          color: getCssVar('color', 'primary');
          cursor: pointer;
          &:hover {
              text-decoration: underline;
          }

      }
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
