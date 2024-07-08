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
        label="数据源驱动"
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
mysql: jdbc:mysql://:/
oracle: jdbc:oracle:thin:@::
sqlserver: jdbc:sqlserver://:;databaseName=
postgre: jdbc:postgresql://:/
clickhouse: jdbc:clickhouse://:/
hive: jdbc:hive2://:/
sap: jdbc:sap://:/
达梦: jdbc:dm://:/
doris: jdbc:mysql://:/
oceanbase: jdbc:oceanbase://:/
tidb: jdbc:mysql://:/
starrocks: jdbc:mysql://:/
db2: jdbc:db2://:/</pre>
            </template>
            <el-icon style="left: 50px" class="tooltip-msg"><QuestionFilled /></el-icon>
        </el-tooltip>
        <el-input
          v-model="formData.jdbcUrl"
          maxlength="100"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item v-if="formData.dbType === 'HIVE'" label="hive.metastore.uris">
        <el-tooltip content="thift://127.0.0.1:9083" placement="top">
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
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetDefaultDriverData, GetDriverListData } from '@/services/driver-management.service'

const form = ref<FormInstance>()
const callback = ref<any>()
const driverIdList = ref([])
const modelConfig = reactive({
  title: '添加数据源',
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
  driverId: '',
  metastoreUris: '',
  jdbcUrl: '',
  username: '',
  passwd: '',
  remark: '',
  id: ''
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
  if (data) {
    formData.name = data.name
    formData.dbType = data.dbType
    formData.jdbcUrl = data.jdbcUrl
    formData.username = data.username
    formData.passwd = data.passwd
    formData.remark = data.remark
    formData.driverId = data.driverId
    formData.metastoreUris = data.metastoreUris
    formData.id = data.id
    modelConfig.title = '编辑数据源'
  } else {
    formData.name = ''
    formData.dbType = ''
    formData.jdbcUrl = ''
    formData.username = ''
    formData.passwd = ''
    formData.remark = ''
    formData.driverId = ''
    formData.metastoreUris = ''
    formData.id = ''
    modelConfig.title = '添加数据源'
  }
  getDriverIdList(true)
  nextTick(() => {
    form.value?.resetFields()
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
</style>
