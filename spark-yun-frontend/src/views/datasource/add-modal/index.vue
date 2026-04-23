<template>
  <BlockModal :model-config="modelConfig">
    <el-form
      ref="form"
      class="add-computer-group"
      label-position="top"
      :model="formData"
      :rules="rules"
    >
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
      <el-form-item
        label="类型"
        prop="dbType"
      >
        <template #label>
          <div style="display: flex; justify-content: space-between; align-items: center; width: 100%;">
            <span>类型</span>
            <el-popover
              placement="top"
              :width="240"
              trigger="click"
              :visible="driverPopoverVisible"
            >
              <template #reference>
                <el-button type="primary" link size="small" @click="openDriverPopover">更换驱动</el-button>
              </template>
              <el-select
                v-model="formData.driverId"
                placeholder="请选择驱动"
                style="width: 100%"
                @visible-change="onDriverSelectVisibleChange"
              >
                <el-option
                  v-for="item in driverIdList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-popover>
          </div>
        </template>
        <el-select
          v-model="formData.dbType"
          placeholder="请选择"
          @change="dbTypeChange"
        >
          <el-option
            v-for="item in typeList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item
        label="连接信息"
        prop="jdbcUrl"
      >
        <template #label>
          <div style="display: flex; justify-content: space-between; align-items: center; width: 100%;">
            <div style="display: flex; align-items: center;">
              <span>连接信息</span>
              <el-tooltip :content="jdbcTip" placement="top">
                <el-icon style="margin-left: 4px; color: var(--el-color-info); font-size: 16px; cursor: pointer;"><QuestionFilled /></el-icon>
              </el-tooltip>
            </div>
            <el-button type="primary" link size="small" @click="openAdvancedConfig">高级配置</el-button>
          </div>
        </template>
        <el-input
          v-model="formData.jdbcUrl"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item
        v-if="formData.dbType === 'DORIS'"
        label="FeNodes"
        prop="feNodes"
        :required="formData.dbType === 'DORIS'"
      >
        <el-input
          v-model="formData.feNodes"
          placeholder="请输入，如 127.0.0.1:8030"
        />
      </el-form-item>
      <el-form-item
        v-if="formData.dbType === 'KAFKA'"
        label="topic"
        prop="kafkaConfig.topic"
      >
        <el-input
          v-model="formData.kafkaConfig.topic"
          maxlength="200"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item v-if="formData.dbType === 'HIVE'" label="hive.metastore.uris">
        <el-tooltip content="thrift://127.0.0.1:${port}，默认端口号9083" placement="top">
            <el-icon style="left: 104px" class="tooltip-msg"><QuestionFilled /></el-icon>
        </el-tooltip>
        <el-input
          v-model="formData.metastoreUris"
          maxlength="100"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item
        v-if="formData.dbType !== 'KAFKA'&& formData.dbType !== 'DUCK_DB'"
        label="用户名"
        prop="username"
      >
        <el-input
          v-model="formData.username"
          maxlength="200"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item
        v-if="formData.dbType === 'KAFKA' || formData.dbType === 'DUCK_DB'"
        label="用户名"
      >
        <el-input
          v-model="formData.username"
          maxlength="200"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item
        label="密码"
      >
        <el-input
          v-model="formData.passwd"
          maxlength="100"
          type="password"
          show-password
          placeholder="请输入"
        />
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
    </el-form>
    <template #customLeft>
      <div class="test-button">
        <el-button :loading="testLoading" type="primary" @click="testFun">连接测试</el-button>
        <el-popover
          placement="right"
          title="测试结果"
          :width="400"
          trigger="hover"
          popper-class="message-error-tooltip"
          :content="testResult?.connectLog"
          v-if="testResult?.connectLog"
        >
          <template #reference>
            <el-icon class="hover-tooltip" v-if="!testResult?.canConnect"><WarningFilled /></el-icon>
            <el-icon class="hover-tooltip success" v-else><SuccessFilled /></el-icon>
          </template>
        </el-popover>

      </div>
    </template>
  </BlockModal>
  <el-dialog
    v-model="advancedConfigVisible"
    title="高级配置"
    width="520px"
    :close-on-click-modal="false"
    append-to-body
    class="advanced-config-dialog"
  >
    <div class="advanced-config-content">
      <div
        v-for="(item, index) in connectConfigList"
        :key="index"
        class="advanced-config-row"
      >
        <el-input v-model="item.key" placeholder="Key" style="flex: 1;" />
        <el-input v-model="item.value" placeholder="Value" style="flex: 1; margin-left: 8px;" />
        <el-button type="danger" link @click="removeConfigItem(index)" style="margin-left: 8px;">
          <el-icon><Delete /></el-icon>
        </el-button>
      </div>
    </div>
    <template #footer>
      <div class="advanced-config-footer">
        <el-button type="primary" link @click="addConfigItem">添加配置</el-button>
        <div>
          <el-button @click="advancedConfigVisible = false">取消</el-button>
          <el-button type="primary" @click="saveAdvancedConfig">确定</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick, computed } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { Delete, Plus } from '@element-plus/icons-vue'
import { GetDefaultDriverData, GetDriverListData } from '@/services/driver-management.service'
import { TestDatasourceData } from '@/services/datasource.service'
import { getVipLicenseEnabled } from '@/utils/vip-license'

const form = ref<FormInstance>()
const callback = ref<any>()
const driverIdList = ref([])
const testLoading = ref(false)
const testResult = ref()
const driverPopoverVisible = ref(false)
const advancedConfigVisible = ref(false)
const connectConfigList = ref<Array<{ key: string; value: string }>>([])
const licenseEnabled = ref(true)
const isEditMode = ref(false)
const modelConfig = reactive({
  title: '新建数据源',
  visible: false,
  width: '520px',
  customClass: 'datasource-add-modal',
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
  driverId: '',
  metastoreUris: '',
  feNodes: '',
  jdbcUrl: '',
  kafkaConfig: {
    topic: '',
  },
  username: '',
  passwd: '',
  remark: '',
  connectConfig: {} as Record<string, string>,
  id: ''
})
const allTypeList = [
  {
    label: 'Clickhouse',
    value: 'CLICKHOUSE'
  },
  {
    label: 'Db2',
    value: 'DB2'
  },
  {
    label: 'Doris',
    value: 'DORIS'
  },
  {
    label: 'DuckDB',
    value: 'DUCK_DB'
  },
  {
    label: '达梦',
    value: 'DM'
  },
  {
    label: 'Gauss',
    value: 'GAUSS'
  },
  {
    label: 'Gbase',
    value: 'GBASE'
  },
  {
    label: 'Greenplum',
    value: 'GREENPLUM'
  },
  {
    label: 'H2',
    value: 'H2'
  },
  {
    label: 'HanaSap',
    value: 'HANA_SAP'
  },
  {
    label: 'Hive',
    value: 'HIVE'
  },
  {
    label: 'Impala',
    value: 'IMPALA'
  },
  {
    label: 'Kafka',
    value: 'KAFKA'
  },
  {
    label: 'Mysql',
    value: 'MYSQL'
  },
  {
    label: 'OceanBase',
    value: 'OCEANBASE'
  },
  {
    label: 'OpenGauss',
    value: 'OPEN_GAUSS'
  },
  {
    label: 'Oracle',
    value: 'ORACLE'
  },
  {
    label: 'PostgreSql',
    value: 'POSTGRE_SQL'
  },
  {
    label: 'Presto',
    value: 'PRESTO'
  },
  {
    label: 'SelectDB',
    value: 'SELECT_DB'
  },
  {
    label: 'SqlServer',
    value: 'SQL_SERVER'
  },
  {
    label: 'StarRocks',
    value: 'STAR_ROCKS'
  },
  {
    label: 'Sybase',
    value: 'SYBASE'
  },
  {
    label: 'TDengine',
    value: 'T_DENGINE'
  },
  {
    label: 'TiDB',
    value: 'TIDB'
  },
  {
    label: 'Trino',
    value: 'TRINO'
  }
]
const limitedTypeList = [
  { label: 'Clickhouse', value: 'CLICKHOUSE' },
  { label: 'Db2', value: 'DB2' },
  { label: 'Dm', value: 'DM' },
  { label: 'Doris', value: 'DORIS' },
  { label: 'Gauss', value: 'GAUSS' },
  { label: 'Gbase', value: 'GBASE' },
  { label: 'Greenplum', value: 'GREENPLUM' },
  { label: 'H2', value: 'H2' },
  { label: 'Hana', value: 'HANA_SAP' },
  { label: 'Hive', value: 'HIVE' },
  { label: 'Impala', value: 'IMPALA' },
  { label: 'Mysql', value: 'MYSQL' },
  { label: 'Oceanbase', value: 'OCEANBASE' },
  { label: 'OpenGauss', value: 'OPEN_GAUSS' },
  { label: 'Oracle', value: 'ORACLE' },
  { label: 'Postgres', value: 'POSTGRE_SQL' },
  { label: 'Presto', value: 'PRESTO' },
  { label: 'SqlServer', value: 'SQL_SERVER' },
  { label: 'StarRocks', value: 'STAR_ROCKS' },
  { label: 'Sybase', value: 'SYBASE' },
  { label: 'Tidb', value: 'TIDB' },
  { label: 'Trino', value: 'TRINO' }
]
const typeList = computed(() => {
  if (licenseEnabled.value || isEditMode.value) {
    return allTypeList
  }
  return limitedTypeList
})
const jdbcTipsMap: Record<string, string> = {
  MYSQL: 'jdbc:mysql://127.0.0.1:3306/db_name',
  ORACLE: 'jdbc:oracle:thin:@127.0.0.1:1521/db_name',
  SQL_SERVER: 'jdbc:sqlserver://127.0.0.1:1433;databaseName=db_name',
  POSTGRE_SQL: 'jdbc:postgresql://127.0.0.1:5432/db_name',
  CLICKHOUSE: 'jdbc:clickhouse://127.0.0.1:8123/db_name',
  HIVE: 'jdbc:hive2://127.0.0.1:10000/db_name',
  IMPALA: 'jdbc:hive2://127.0.0.1:21050/db_name;auth=noSasl',
  TRINO: 'jdbc:trino://127.0.0.1:8080/${configName}/db_name',
  PRESTO: 'jdbc:presto://127.0.0.1:8080/${configName}/db_name',
  HANA_SAP: 'jdbc:sap://127.0.0.1:30015/db_name',
  DM: 'jdbc:dm://127.0.0.1:5236?schema=db_name',
  DORIS: 'jdbc:mysql://127.0.0.1:9030/db_name',
  OCEANBASE: 'jdbc:oceanbase://127.0.0.1:2881/db_name',
  TIDB: 'jdbc:mysql://127.0.0.1:4000/db_name',
  STAR_ROCKS: 'jdbc:mysql://127.0.0.1:9030/db_name',
  DB2: 'jdbc:db2://127.0.0.1:50000/db_name',
  T_DENGINE: 'jdbc:TAOS-WS://127.0.0.1:6041/db_name',
  DUCK_DB: 'jdbc:duckdb:/path',
  SELECT_DB: 'jdbc:mysql://127.0.0.1:9030/db_name',
  KAFKA: '127.0.0.1:9092',
  GAUSS: 'jdbc:gaussdb://127.0.0.1:8000/db_name',
  GBASE: 'jdbc:gbase://127.0.0.1:5258/db_name',
  GREENPLUM: 'jdbc:pivotal:greenplum://127.0.0.1:5432;DatabaseName=db_name',
  H2: 'jdbc:h2:tcp://127.0.0.1:9092/db_name',
  OPEN_GAUSS: 'jdbc:opengauss://127.0.0.1:5432/db_name',
  SYBASE: 'jdbc:sybase:Tds:127.0.0.1:5000/db_name',
}
const jdbcTip = computed(() => {
  return jdbcTipsMap[formData.dbType] || '请先选择数据源类型'
})
const rules = reactive<FormRules>({
  name: [
    {
      required: true,
      message: '请输入数据源名称',
      trigger: [ 'blur', 'change' ]
    }
  ],
  dbType: [
    {
      required: true,
      message: '请选择数据库类型',
      trigger: [ 'blur', 'change' ]
    }
  ],
  jdbcUrl: [
    {
      required: true,
      message: '请输入jdbc连接信息',
      trigger: [ 'blur', 'change' ]
    }
  ],
  feNodes: [
    {
      validator: (_rule: any, value: string, callback: (error?: Error) => void) => {
        if (formData.dbType === 'DORIS' && !value) {
          callback(new Error('请输入FeNodes'))
          return
        }
        callback()
      },
      trigger: [ 'blur', 'change' ]
    }
  ],
  'kafkaConfig.topic': [
    {
      required: true,
      message: '请输入topic',
      trigger: [ 'blur', 'change' ]
    }
  ],
  username: [
    {
      required: true,
      message: '请输入用户名',
      trigger: [ 'blur', 'change' ]
    }
  ],
  passwd: [
    {
      required: true,
      message: '请输入密码',
      trigger: [ 'blur', 'change' ]
    }
  ]
})

async function showModal(cb: () => void, data: any): Promise<void> {
  callback.value = cb
  licenseEnabled.value = await getVipLicenseEnabled()
  isEditMode.value = !!data
  modelConfig.visible = true

  testResult.value = {
    canConnect: null,
    connectLog: ''
  }
  if (data) {
    formData.name = data.name
    formData.dbType = data.dbType
    formData.jdbcUrl = data.jdbcUrl
    formData.feNodes = data.feNodes || ''
    formData.username = data.username
    formData.passwd = data.passwd
    formData.remark = data.remark
    formData.driverId = data.driverId
    formData.kafkaConfig = {
      topic: data?.kafkaConfig?.topic
    }
    formData.metastoreUris = data.metastoreUris
    formData.connectConfig = data.connectConfig || {}
    formData.id = data.id
    modelConfig.title = '编辑数据源'
    // 回显高级配置
    connectConfigList.value = Object.entries(formData.connectConfig).map(([key, value]) => ({ key, value: value as string }))
  } else {
    formData.name = ''
    formData.dbType = ''
    formData.jdbcUrl = ''
    formData.feNodes = ''
    formData.username = ''
    formData.passwd = ''
    formData.remark = ''
    formData.kafkaConfig = {
      topic: ''
    }
    formData.driverId = ''
    formData.metastoreUris = ''
    formData.connectConfig = {}
    formData.id = ''
    modelConfig.title = '新建数据源'
    connectConfigList.value = []
  }
  getDriverIdList(true)
  nextTick(() => {
    form.value?.resetFields()
  })
}

function testFun() {
  form.value?.validate((valid: boolean) => {
    if (valid) {
      testLoading.value = true
      TestDatasourceData({
        ...formData
      }).then((res: any) => {
        testLoading.value = false
        testResult.value = res.data
        ElMessage.success(res.msg)
      }).catch(() => {
        testLoading.value = false
      })
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
}

function openDriverPopover() {
  if (!formData.dbType) {
    ElMessage.warning('请先选择数据库类型')
    return
  }
  getDriverIdList(true)
  driverPopoverVisible.value = !driverPopoverVisible.value
}

function onDriverSelectVisibleChange(visible: boolean) {
  if (visible) {
    getDriverIdList(true)
  } else {
    driverPopoverVisible.value = false
  }
}

function getDriverIdList(e: boolean) {
  driverIdList.value = []
  if (e && formData.dbType) {
    GetDriverListData({
      page: 0,
      pageSize: 10000,
      searchKeyWord: formData.dbType
    }).then((res: any) => {
      driverIdList.value = res.data.content
    }).catch((error: any) => {
    })
  }
}

function dbTypeChange(e: string) {
  formData.metastoreUris = ''
  formData.driverId = ''
  formData.jdbcUrl = jdbcTipsMap[e] || ''
  if (e !== 'DORIS') {
    formData.feNodes = ''
  }

  getDriverIdList(true)
  getDefaultDriver(e)
}

function getDefaultDriver(e: string) {
  if (e) {
    GetDefaultDriverData({
      dbType: e
    }).then((res: any) => {
      formData.driverId = res.data.id
    }).catch((error: any) => {
    })
  }
}

function okEvent() {
  form.value?.validate((valid: boolean) => {
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
            driverPopoverVisible.value = false
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

function openAdvancedConfig() {
  if (connectConfigList.value.length === 0) {
    connectConfigList.value.push({ key: '', value: '' })
  }
  advancedConfigVisible.value = true
}

function addConfigItem() {
  connectConfigList.value.push({ key: '', value: '' })
}

function removeConfigItem(index: number) {
  connectConfigList.value.splice(index, 1)
}

function saveAdvancedConfig() {
  // 过滤空行
  const validItems = connectConfigList.value.filter(item => item.key.trim() !== '')
  // 校验key唯一性
  const keys = validItems.map(item => item.key.trim())
  const uniqueKeys = new Set(keys)
  if (keys.length !== uniqueKeys.size) {
    ElMessage.warning('配置项Key不能重复')
    return
  }
  // 转为对象
  const config: Record<string, string> = {}
  validItems.forEach(item => {
    config[item.key.trim()] = item.value.trim()
  })
  formData.connectConfig = config
  connectConfigList.value = validItems
  advancedConfigVisible.value = false
}

function closeEvent() {
  driverPopoverVisible.value = false
  advancedConfigVisible.value = false
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
  .tooltip-msg {
      position: absolute;
      top: -28px;
      color: getCssVar('color', 'info');
      font-size: 16px;
  }
}
.datasource-add-modal {
  .test-button {
    position: absolute;
    left: 20px;
    bottom: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .hover-tooltip {
    margin-left: 8px;
    font-size: 16px;
    color: getCssVar('color', 'danger');
    &.success {
      color: getCssVar('color', 'success');
    }
  }
}
.advanced-config-content {
  .advanced-config-row {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
  }
}
.advanced-config-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;

  > div {
    display: flex;
    align-items: center;
  }
}
</style>
