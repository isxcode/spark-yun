<template>
  <BlockModal :model-config="modelConfig">
    <el-form
      ref="form"
      class="add-computer-group"
      label-position="top"
      :model="formData"
      :rules="rules"
    >
      <template v-if="workType === 'SPARK_SQL'">
        <el-form-item
          label="计算引擎"
          prop="clusterId"
        >
          <el-select
            v-model="formData.clusterId"
            placeholder="请选择"
          >
            <el-option
              v-for="item in typeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </template>
      <template v-else>
        <el-form-item
          label="数据源"
          prop="datasourceId"
        >
          <el-select
            v-model="formData.datasourceId"
            placeholder="请选择"
          >
            <el-option
              v-for="item in typeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </template>
      <el-form-item
        v-if="workType === 'SPARK_SQL'"
        label="Spark配置"
      >
        <el-input
          v-model="formData.sparkConfig"
          show-word-limit
          type="textarea"
          :autosize="{ minRows: 4, maxRows: 4 }"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item label="Corn表达式">
        <el-input
          v-model="formData.corn"
          placeholder="请输入"
          show-word-limit
        />
      </el-form-item>
    </el-form>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetDatasourceList } from '@/services/datasource.service'
import { GetComputerGroupList } from '@/services/computer-group.service'

const form = ref<FormInstance>()
const callback = ref<any>()
const typeList = ref([])
const workType = ref('')
const modelConfig = reactive({
  title: '作业属性配置',
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
  clusterId: '',
  datasourceId: '',
  sparkConfig: '',
  corn: ''
})
const rules = reactive<FormRules>({
  datasourceId: [
    {
      required: true,
      message: '请选择数据源',
      trigger: [ 'blur', 'change' ]
    }
  ],
  clusterId: [
    {
      required: true,
      message: '请选择计算引擎',
      trigger: [ 'blur', 'change' ]
    }
  ]
})

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  workType.value = data.workType
  modelConfig.visible = true

  if (workType.value === 'SPARK_SQL') {
    formData.clusterId = data.clusterId
    formData.sparkConfig = data.sparkConfig
    getComputeEngine()
  } else {
    formData.datasourceId = data.datasourceId
    getDataSourceList()
  }
  formData.corn = data.corn
  nextTick(() => {
    form.value?.resetFields()
  })
}

function getDataSourceList() {
  GetDatasourceList({
    page: 0,
    pageSize: 10000,
    searchKeyWord: ''
  })
    .then((res: any) => {
      typeList.value = res.data.content.map((item: any) => {
        return {
          label: item.name,
          value: item.id
        }
      })
    })
    .catch(() => {
      typeList.value = []
    })
}

function getComputeEngine() {
  GetComputerGroupList({
    page: 0,
    pageSize: 10000,
    searchKeyWord: ''
  })
    .then((res: any) => {
      typeList.value = res.data.content.map((item: any) => {
        return {
          label: item.name,
          value: item.id
        }
      })
    })
    .catch(() => {
      typeList.value = []
    })
}

function okEvent() {
  form.value?.validate((valid) => {
    if (valid) {
      modelConfig.okConfig.loading = true
      callback
        .value({
          ...formData
        })
        .then((res: any) => {
          modelConfig.okConfig.loading = false
          if (res === undefined) {
            modelConfig.visible = false
          } else {
            modelConfig.visible = true
          }
        })
        .catch((err: any) => {
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
