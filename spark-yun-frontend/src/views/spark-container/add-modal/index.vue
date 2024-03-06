<template>
  <BlockModal :model-config="modelConfig">
    <el-form ref="form" class="add-computer-group" label-position="top" :model="formData" :rules="rules">
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
        <el-select v-model="formData.resourceLevel" placeholder="请选择" @change="resourceLevelChange">
          <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="sparkConfig" prop="sparkConfig" v-if="formData.resourceLevel === 'CUSTOM'">
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
import CodeMirror from 'vue-codemirror6'
import {json} from '@codemirror/lang-json'

const form = ref<FormInstance>()
const callback = ref<any>()
const clusterList = ref([])  // 计算集群
const dataSourceList = ref([])
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

function resourceLevelChange(e: string) {
  formData.sparkConfig = ''
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

function closeEvent() {
  modelConfig.visible = false
}

defineExpose({
  showModal
})
</script>

<style lang="scss">
.add-computer-group {
  padding: 12px 20px 0 20px;
  box-sizing: border-box;
}
</style>
