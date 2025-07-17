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
        label="驱动"
        prop="driverId"
      >
        <el-select
          v-model="formData.driverId"
          placeholder="请选择"
          @visible-change="getDriverIdList"
        >
          <el-option
            v-for="item in driverIdList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="连接信息"
        prop="jdbcUrl"
      >
        <el-tooltip placement="top">
            <template #content>
              <pre>
mysql: jdbc:mysql://${host}:${ip}/${database}
oracle: jdbc:oracle:thin:@${host}:${ip}/${database}
sqlserver: jdbc:sqlserver://${host}:${ip};databaseName=${database}
postgre: jdbc:postgresql://${host}:${ip}/${database}
clickhouse: jdbc:clickhouse://${host}:${ip}/${database}
hive: jdbc:hive2://${host}:${ip}/${database}
sap: jdbc:sap://${host}:${ip}/${database}
达梦: jdbc:dm://${host}:${ip}/${database}
doris: jdbc:mysql://${host}:${ip}/${database}
oceanbase: jdbc:oceanbase://${host}:${ip}/${database}
tidb: jdbc:mysql://${host}:${ip}/${database}
starrocks: jdbc:mysql://${host}:${ip}/${database}
db2: jdbc:db2://${host}:${ip}/${database}
              </pre>
            </template>
            <el-icon style="left: 50px" class="tooltip-msg"><QuestionFilled /></el-icon>
        </el-tooltip>
        <el-input
          v-model="formData.jdbcUrl"
          placeholder="请输入"
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
        <el-tooltip content="thrift://${host}:${port}，默认端口号9083" placement="top">
            <el-icon style="left: 104px" class="tooltip-msg"><QuestionFilled /></el-icon>
        </el-tooltip>
        <el-input
          v-model="formData.metastoreUris"
          maxlength="100"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item
        v-if="formData.dbType !== 'KAFKA'"
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
        v-if="formData.dbType === 'KAFKA'"
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
        <el-button
          type="default"
          @click="openAdvancedConfig"
          class="advanced-config-button"
        >
          高级配置
        </el-button>
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

  <!-- 高级配置弹窗 -->
  <BlockModal :model-config="advancedConfigModalConfig">
    <div class="advanced-config-modal">
      <div class="config-header">
        <span class="config-title">配置项列表</span>
        <el-button
          type="primary"
          size="small"
          @click="addConfig"
          class="add-config-btn"
        >
          + 添加配置项
        </el-button>
      </div>

      <div class="config-list">
        <div class="config-labels">
          <span class="label-key">配置项名称</span>
          <span class="label-value">配置项值</span>
          <span class="label-action">操作</span>
        </div>

        <div
          v-for="(config, index) in formData.advancedConfig"
          :key="index"
          class="config-item"
        >
          <el-input
            v-model="config.key"
            placeholder="请输入配置项名称"
            class="config-key"
            size="small"
          />
          <el-input
            v-model="config.value"
            placeholder="请输入配置项值"
            class="config-value"
            size="small"
          />
          <span
            @click="removeConfig(index)"
            class="remove-text"
          >
            删除
          </span>
        </div>

        <div v-if="formData.advancedConfig.length === 0" class="empty-state">
          <span>暂无配置项，点击上方按钮添加</span>
        </div>
      </div>
    </div>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetDefaultDriverData, GetDriverListData } from '@/services/driver-management.service'
import { TestDatasourceData } from '@/services/datasource.service'

const form = ref<FormInstance>()
const callback = ref<any>()
const driverIdList = ref([])
const testLoading = ref(false)
const testResult = ref()
const modelConfig = reactive({
  title: '添加数据源',
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

const advancedConfigModalConfig = reactive({
  title: '高级配置',
  visible: false,
  width: '600px',
  customClass: 'advanced-config-modal-wrapper',
  okConfig: {
    title: '确定',
    ok: saveAdvancedConfig,
    disabled: false,
    loading: false
  },
  cancelConfig: {
    title: '取消',
    cancel: closeAdvancedConfig,
    disabled: false
  },
  needScale: false,
  zIndex: 1200,
  closeOnClickModal: false
})
const formData = reactive({
  name: '',
  dbType: '',
  driverId: '',
  metastoreUris: '',
  jdbcUrl: '',
  kafkaConfig: {
    topic: '',
  },
  username: '',
  passwd: '',
  remark: '',
  id: '',
  advancedConfig: []
})
const typeList = reactive([
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
  {
    label: 'Kafka',
    value: 'KAFKA',
  },
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
  {
    label: 'Greenplum',
    value: 'GREENPLUM',
  },
  {
    label: 'Gbase',
    value: 'GBASE',
  },
  {
    label: 'Sybase',
    value: 'SYBASE',
  },
  {
    label: 'Db2',
    value: 'DB2',
  },
  {
    label: 'Gauss',
    value: 'GAUSS',
  },
  {
    label: 'OpenGauss',
    value: 'OPEN_GAUSS',
  }
]);
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
  driverId: [
    {
      required: true,
      message: '请选择数据源驱动',
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

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  modelConfig.visible = true

  testResult.value = {
    canConnect: null,
    connectLog: ''
  }
  if (data) {
    formData.name = data.name
    formData.dbType = data.dbType
    formData.jdbcUrl = data.jdbcUrl
    formData.username = data.username
    formData.passwd = data.passwd
    formData.remark = data.remark
    formData.driverId = data.driverId
    formData.kafkaConfig = {
      topic: data?.kafkaConfig?.topic
    }
    formData.metastoreUris = data.metastoreUris
    formData.id = data.id
    // 处理高级配置
    if (data.advancedConfig && Object.keys(data.advancedConfig).length > 0) {
      formData.advancedConfig = Object.entries(data.advancedConfig).map(([key, value]) => ({
        key,
        value: value as string
      }))
    } else {
      formData.advancedConfig = []
    }
    modelConfig.title = '编辑数据源'
  } else {
    formData.name = ''
    formData.dbType = ''
    formData.jdbcUrl = ''
    formData.username = ''
    formData.passwd = ''
    formData.remark = ''
    formData.kafkaConfig = {
      topic: ''
    }
    formData.driverId = ''
    formData.metastoreUris = ''
    formData.id = ''
    formData.advancedConfig = []
    modelConfig.title = '添加数据源'
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

      // 处理高级配置数据，转换为对象格式
      const advancedConfigObj: Record<string, string> = {}
      formData.advancedConfig.forEach(config => {
        if (config.key && config.value) {
          advancedConfigObj[config.key] = config.value
        }
      })

      callback
        .value({
          ...formData,
          advancedConfig: advancedConfigObj,
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

// 高级配置相关方法
function openAdvancedConfig() {
  advancedConfigModalConfig.visible = true
}

function saveAdvancedConfig() {
  // 验证配置项
  const validationResult = validateAdvancedConfig()
  if (!validationResult.isValid) {
    ElMessage.warning(validationResult.message)
    return
  }

  advancedConfigModalConfig.visible = false
}

function validateAdvancedConfig() {
  // 过滤掉空的配置项
  const validConfigs = formData.advancedConfig.filter(config => config.key.trim() || config.value.trim())

  // 检查是否有key或value为空的配置项
  for (let i = 0; i < validConfigs.length; i++) {
    const config = validConfigs[i]
    if (!config.key.trim()) {
      return { isValid: false, message: `第${i + 1}个配置项的名称不能为空` }
    }
    if (!config.value.trim()) {
      return { isValid: false, message: `第${i + 1}个配置项的值不能为空` }
    }
  }

  // 检查key是否重复
  const keys = validConfigs.map(config => config.key.trim())
  const uniqueKeys = [...new Set(keys)]
  if (keys.length !== uniqueKeys.length) {
    return { isValid: false, message: '配置项名称不能重复' }
  }

  // 更新formData，移除空的配置项
  formData.advancedConfig = validConfigs

  return { isValid: true, message: '' }
}

function closeAdvancedConfig() {
  advancedConfigModalConfig.visible = false
}

function addConfig() {
  formData.advancedConfig.push({ key: '', value: '' })
}

function removeConfig(index: number) {
  formData.advancedConfig.splice(index, 1)
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
    gap: 8px;

    .advanced-config-button {
      color: getCssVar('color', 'primary');
      border-color: getCssVar('color', 'primary');

      &:hover {
        background-color: getCssVar('color', 'primary');
        color: getCssVar('color', 'white');
      }
    }
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

// 高级配置弹窗样式
.advanced-config-modal-wrapper {
  .advanced-config-modal {
    padding: 20px;

    .config-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid getCssVar('border-color', 'lighter');

      .config-title {
        font-size: 14px;
        font-weight: 500;
        color: getCssVar('text-color', 'primary');
      }

      .add-config-btn {
        font-size: 12px;
        padding: 6px 12px;
      }
    }

    .config-list {
      .config-labels {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;
        padding: 0 4px;

        .label-key,
        .label-value {
          flex: 1;
          font-size: 12px;
          color: getCssVar('text-color', 'secondary');
          font-weight: 500;
        }

        .label-action {
          width: 60px;
          font-size: 12px;
          color: getCssVar('text-color', 'secondary');
          font-weight: 500;
          text-align: center;
        }
      }

      .config-item {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;

        .config-key,
        .config-value {
          flex: 1;
        }

        .remove-text {
          width: 60px;
          font-size: 12px;
          color: getCssVar('color', 'primary');
          cursor: pointer;
          text-align: center;
          user-select: none;
          display: flex;
          align-items: center;
          justify-content: center;

          &:hover {
            text-decoration: underline;
          }
        }
      }

      .empty-state {
        text-align: center;
        padding: 40px 0;
        color: getCssVar('text-color', 'placeholder');
        font-size: 14px;
      }
    }
  }
}
</style>
