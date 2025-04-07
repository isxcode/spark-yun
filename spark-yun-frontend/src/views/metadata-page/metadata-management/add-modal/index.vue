<template>
  <BlockModal :model-config="modelConfig">
    <el-form ref="form" class="add-computer-group acquisition-task-add" label-position="top" :model="formData"
      :rules="rules">
      <el-form-item label="采集任务名称" prop="name">
        <el-input v-model="formData.name" maxlength="200" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="数据源类型" prop="dbType">
        <el-select :disabled="renderSence === 'edit'" v-model="formData.dbType" placeholder="请选择" @change="dbTypeChangeEvent">
          <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="数据源" prop="datasourceId">
        <el-select :disabled="renderSence === 'edit'" v-model="formData.datasourceId" placeholder="请选择" @visible-change="getDataSourceList">
          <el-option v-for="item in dataSourceList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="表" prop="collectType">
        <el-radio-group v-model="formData.collectType" @change="collectTypeChangeEvent">
          <el-radio :label="'ALL_TABLE'">所有表</el-radio>
          <el-radio :label="'CUSTOM_TABLE'">指定表</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="formData.collectType === 'CUSTOM_TABLE'" label="正则表达式" prop="tablePattern">
        <el-input v-model="formData.tablePattern" maxlength="200" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="formData.remark" type="textarea" maxlength="200" :autosize="{ minRows: 4, maxRows: 4 }"
          placeholder="请输入" />
      </el-form-item>
    </el-form>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref } from 'vue'
import { GetDatasourceList } from '@/services/datasource.service'
import { ElMessage, FormInstance, FormRules } from 'element-plus'

interface Option {
  label: string
  value: string
}

const form = ref<FormInstance>()
const callback = ref<any>()
const dataSourceList = ref<Option[]>([])
const typeList = ref<Option[]>([
  {
    label: 'Mysql',
    value: 'MYSQL',
  },
  {
    label: 'Oracle',
    value: 'ORACLE',
  },
  {
    label: 'SqlServer',
    value: 'SQL_SERVER',
  },
  {
    label: 'PostgreSql',
    value: 'POSTGRE_SQL',
  },
  {
    label: 'Clickhouse',
    value: 'CLICKHOUSE',
  },
  {
    label: 'Hive',
    value: 'HIVE',
  },
  {
    label: 'H2',
    value: 'H2',
  },
  // {
  //   label: 'Kafka',
  //   value: 'KAFKA',
  // },
  {
    label: 'HanaSap',
    value: 'HANA_SAP',
  },
  {
    label: '达梦',
    value: 'DM',
  },
  {
    label: 'Doris',
    value: 'DORIS',
  },
  {
    label: 'OceanBase',
    value: 'OCEANBASE',
  },
  {
    label: 'TiDB',
    value: 'TIDB',
  },
  {
    label: 'StarRocks',
    value: 'STAR_ROCKS',
  },
])
const renderSence = ref('new')
const modelConfig = reactive({
  title: '立即采集',
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
  name: '',
  dbType: '',
  datasourceId: '',
  collectType: 'ALL_TABLE',
  tablePattern: '',
  remark: '',
  id: ''
})
const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入采集任务名称', trigger: ['blur', 'change'] }],
  dbType: [{ required: true, message: '请选择数据源类型', trigger: ['blur', 'change'] }],
  datasourceId: [{ required: true, message: '请选择数据源', trigger: ['blur', 'change'] }],
  collectType: [{ required: true, message: '请选择表', trigger: ['blur', 'change'] }],
  tablePattern: [{ required: true, message: '请输入正则表达式', trigger: ['blur', 'change'] }]
})

function showModal(cb: () => void, data: any): void {
  Object.keys(formData).forEach((key: string) => {
    formData[key] = ''
    formData.collectType = 'ALL_TABLE'
  })
  if (data && data.datasourceId && data.dbType) {
    formData.datasourceId = data.datasourceId
    formData.dbType = data.dbType
    formData.datasourceId && getDataSourceList(true)
    renderSence.value = 'edit'
  } else {
    renderSence.value = 'new'
  }

  callback.value = cb
  modelConfig.visible = true
}

function okEvent() {
  form.value?.validate((valid) => {
    if (valid) {
      modelConfig.okConfig.loading = true
      callback.value(formData).then((res: any) => {
        modelConfig.okConfig.loading = false
        if (res === undefined) {
          modelConfig.visible = false
        } else {
          modelConfig.visible = true
        }
      }).catch((err: any) => {
        modelConfig.okConfig.loading = false
      })
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
}

function collectTypeChangeEvent() {
  formData.tablePattern = ''
}

function dbTypeChangeEvent() {
  formData.datasourceId = ''
}

function getDataSourceList(e: boolean, searchType?: string) {
  if (e) {
    GetDatasourceList({
        page: 0,
        pageSize: 10000,
        searchKeyWord: searchType || ''
    }).then((res: any) => {
      dataSourceList.value = res.data.content.filter((item: any) => item.dbType !== 'KAFKA' && formData.dbType == item.dbType).map((item: any) => {
          return {
              label: item.name,
              value: item.id
          }
      })
    }).catch(() => {
      dataSourceList.value = []
    })
  }
}

function closeEvent() {
  modelConfig.visible = false
}

defineExpose({
  showModal
})
</script>

<style lang="scss">
.acquisition-task-add {
  .el-form-item {
    .el-form-item__content {
      position: relative;
      flex-wrap: nowrap;
      justify-content: space-between;
      .time-num-input {
        height: 36px;
        .el-input-number__decrease {
          top: 16px
        }
      }
    }
  }
  .cron-config {
    border: 1px solid getCssVar('border-color');
    padding: 8px 12px;
    margin-bottom: 12px;
    border-radius: 5px;
  }
}
</style>