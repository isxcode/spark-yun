<template>
  <BlockModal :model-config="modelConfig">
    <el-form ref="form" class="add-computer-group acquisition-task-add" label-position="top" :model="formData"
      :rules="rules">
      <el-form-item label="采集任务名称" prop="name">
        <el-input v-model="formData.name" maxlength="200" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="数据源类型" prop="dbType">
        <el-select :disabled="!!formData.id" v-model="formData.dbType" placeholder="请选择" @change="dbTypeChangeEvent">
          <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="数据源" prop="datasourceId">
        <el-select :disabled="!!formData.id" v-model="formData.datasourceId" placeholder="请选择" @visible-change="getDataSourceList">
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
      <el-form-item label="启用调度">
        <el-switch v-model="formData.cronConfig.enable" />
      </el-form-item>
      <template v-if="formData.cronConfig.enable">
        <div class="cron-config">
          <el-form-item label="调度配置">
            <el-radio-group v-model="formData.cronConfig.setMode" size="small" @change="cronTypeChange">
              <el-radio-button label="SIMPLE">简易</el-radio-button>
              <el-radio-button label="ADVANCE">高级定义</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="生效时间" prop="cronConfig.workDate">
            <el-date-picker
              v-model="formData.cronConfig.workDate"
              type="daterange"
              range-separator="至"
              start-placeholder="开始生效日期"
              end-placeholder="结束生效日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item label="cron表达式" prop="cronConfig.cron" v-if="formData.cronConfig.setMode === 'ADVANCE'">
            <el-input
              v-model="formData.cronConfig.cron"
              placeholder="请输入"
            />
          </el-form-item>
          <template v-else>
            <el-form-item label="调度周期" prop="cronConfig.range">
              <el-select v-model="formData.cronConfig.range" placeholder="请选择" :disabled="!formData.cronConfig.enable" @change="changeScheduleRangeEvent">
                <el-option
                  v-for="item in scheduleRange"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <!-- 调度周期 -> 秒 -->
            <template v-if="formData.cronConfig.range === 'sec'">
              <el-form-item label="结束时间" prop="cronConfig.endDate">
                <el-date-picker :disabled="!formData.cronConfig.enable" v-model="formData.cronConfig.endDate" type="date" placeholder="请选择" clearable />
              </el-form-item>
            </template>
            <!-- 调度周期 -> 分钟 -->
            <template v-if="formData.cronConfig.range === 'min'">
              <el-form-item label="开始时间" prop="cronConfig.startDateMin">
                <el-time-select
                  v-model="formData.cronConfig.startDateMin"
                  :disabled="!formData.cronConfig.enable"
                  start="00:00"
                  step="01:00"
                  end="23:00"
                  placeholder="请选择">
                </el-time-select>
              </el-form-item>
              <el-form-item label="时间间隔（分钟）" prop="cronConfig.minNum">
                <el-input-number class="time-num-input" :disabled="!formData.cronConfig.enable" placeholder="请输入" v-model="formData.cronConfig.minNum" :min="0" controls-position="right" />
              </el-form-item>
              <el-form-item label="结束时间" prop="cronConfig.endDateMin">
                <el-time-select
                  v-model="formData.cronConfig.endDateMin"
                  :disabled="!formData.cronConfig.enable"
                  start="00:00"
                  step="01:00"
                  end="23:00"
                  placeholder="请选择">
                </el-time-select>
              </el-form-item>
            </template>
            <!-- 调度周期 -> 小时 -->
            <template v-if="formData.cronConfig.range === 'hour'">
              <el-form-item label="开始时间" prop="cronConfig.startDate">
                <el-time-select
                  :disabled="!formData.cronConfig.enable"
                  v-model="formData.cronConfig.startDate"
                  start="00:00"
                  step="01:00"
                  end="23:00"
                  placeholder="请选择">
                </el-time-select>
              </el-form-item>
              <el-form-item label="时间间隔（小时）" prop="cronConfig.hourNum">
                <el-input-number class="time-num-input" :disabled="!formData.cronConfig.enable" placeholder="请输入" v-model="formData.cronConfig.hourNum" :min="0" controls-position="right" />
              </el-form-item>
              <el-form-item label="结束时间" prop="cronConfig.endDate">
                <el-time-select
                  v-model="formData.cronConfig.endDate"
                  :disabled="!formData.cronConfig.enable"
                  start="00:00"
                  step="01:00"
                  end="23:00"
                  placeholder="请选择">
                </el-time-select>
              </el-form-item>
            </template>
            <!-- 调度周期 -> 日 -->
            <template v-if="formData.cronConfig.range === 'day'">
              <el-form-item label="调度时间" prop="cronConfig.scheduleDate">
                <el-time-picker
                  :disabled="!formData.cronConfig.enable"
                  v-model="formData.cronConfig.scheduleDate"
                  format="HH:mm"
                  value-format="HH:mm"
                  placeholder="请选择"
                />
              </el-form-item>
            </template>
            <!-- 调度周期 -> 月 -->
            <template v-if="formData.cronConfig.range === 'month'">
              <el-form-item label="调度时间" prop="cronConfig.scheduleDate">
                <el-time-picker
                  :disabled="!formData.cronConfig.enable"
                  v-model="formData.cronConfig.scheduleDate"
                  format="HH:mm"
                  value-format="HH:mm"
                  placeholder="请选择"
                />
              </el-form-item>
              <el-form-item label="指定时间" prop="cronConfig.monthDay">
                <el-select v-model="formData.cronConfig.monthDay" :disabled="!formData.cronConfig.enable" placeholder="请选择">
                  <el-option
                    v-for="item in dayList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </template>
            <!-- 调度周期 -> 周 -->
            <template v-if="formData.cronConfig.range === 'week'">
              <el-form-item label="调度时间" prop="cronConfig.scheduleDate">
                <el-time-picker
                  :disabled="!formData.cronConfig.enable"
                  v-model="formData.cronConfig.scheduleDate"
                  format="HH:mm"
                  value-format="HH:mm"
                  placeholder="请选择"
                />
              </el-form-item>
              <el-form-item label="指定时间" prop="cronConfig.weekDate">
                <el-select v-model="formData.cronConfig.weekDate" placeholder="请选择" :disabled="!formData.cronConfig.enable">
                  <el-option
                    v-for="item in weekDateList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </template>
          </template>
        </div>
      </template>

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
const scheduleRange = ref<Option[]>([
  {
    label: '分钟',
    value: 'min',
  },
  {
    label: '小时',
    value: 'hour',
  },
  {
    label: '日',
    value: 'day',
  },
  {
    label: '月',
    value: 'month',
  },
  {
    label: '星期',
    value: 'week',
  }
])
const weekDateList = ref<Option[]>([
  {
    label: '星期一',
    value: '1',
  },
  {
    label: '星期二',
    value: '2',
  },
  {
    label: '星期三',
    value: '3',
  },
  {
    label: '星期四',
    value: '4',
  },
  {
    label: '星期五',
    value: '5',
  },
  {
    label: '星期六',
    value: '6',
  },
  {
    label: '星期日',
    value: '7'
  }
])
const dayList = ref()

const modelConfig = reactive({
  title: '添加',
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
const state = reactive({
  secondsText: '',
  minutesText: '',
  hoursText: '',
  daysText: '',
  weeksText: '',
  monthsText: '',
  yearsText: ''
})
const formData = reactive({
  name: '',
  dbType: '',
  datasourceId: '',
  collectType: 'ALL_TABLE',
  tablePattern: '',
  cronConfig: {
    setMode: 'SIMPLE',       // 模式
    enable: false,             // 启用
    cron: '',                 // cron表达式
    workDate: [],             // 生效时间
    range: '',         // 调度周期
    startDateMin: '',  // 开始时间 - 分钟
    minNum: undefined,        // 间隔时间 - 分钟
    endDateMin: '',    // 结束时间 - 分钟
    startDate: '',     // 开始时间 - 小时
    hourNum: undefined,     // 间隔时间 - 小时
    endDate: '',       // 结束时间 - 小时
    scheduleDate: '',  // 调度时间 - 日/周
    weekDate: '',      // 指定时间 - 星期
    monthDay: '',      // 指定时间 - 月
  },
  remark: '',
  id: ''
})
const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入采集任务名称', trigger: ['blur', 'change'] }],
  dbType: [{ required: true, message: '请选择数据源类型', trigger: ['blur', 'change'] }],
  datasourceId: [{ required: true, message: '请选择数据源', trigger: ['blur', 'change'] }],
  collectType: [{ required: true, message: '请选择表', trigger: ['blur', 'change'] }],
  tablePattern: [{ required: true, message: '请输入正则表达式', trigger: ['blur', 'change'] }],

  'cronConfig.workDate': [{ required: true, message: '请选择生效时间', trigger: ['blur', 'change'] }],
  'cronConfig.cron': [{ required: true, message: '请输入cron表达式', trigger: ['blur', 'change'] }],
  'cronConfig.range': [{ required: true, message: '请选择调度周期', trigger: ['blur', 'change'] }],
  'cronConfig.startDateMin': [{ required: true, message: '请选择开始时间', trigger: ['blur', 'change'] }],
  'cronConfig.minNum': [{ required: true, message: '请输入时间间隔（分钟）', trigger: ['blur', 'change'] }],
  'cronConfig.hourNum': [{ required: true, message: '请输入时间间隔（小时）', trigger: ['blur', 'change'] }],
  'cronConfig.endDateMin': [{ required: true, message: '请选择结束时间', trigger: ['blur', 'change'] }],
  'cronConfig.startDate': [{ required: true, message: '请选择开始时间', trigger: ['blur', 'change'] }],
  'cronConfig.endDate': [{ required: true, message: '请选择结束时间', trigger: ['blur', 'change'] }],
  'cronConfig.scheduleDate': [{ required: true, message: '请选择调度时间', trigger: ['blur', 'change'] }],
  'cronConfig.monthDay': [{ required: true, message: '请选择指定时间', trigger: ['blur', 'change'] }],
  'cronConfig.weekDate': [{ required: true, message: '请选择指定时间', trigger: ['blur', 'change'] }],
})

function showModal(cb: () => void, data: any): void {
  dayList.value = []
  for (let i = 1; i <= 31; i++) {
    dayList.value.push({
      label: `${i}号`,
      value: `${i}`
    })
  }
  if (data) {
    Object.keys(formData).forEach((key: string) => {
      if (key == 'cronConfig' && !data[key]) {
        formData[key] = {
          setMode: 'SIMPLE',       // 模式
          enable: false,             // 启用
          cron: '',                 // cron表达式
          workDate: [],             // 生效时间
          range: '',         // 调度周期
          startDateMin: '',  // 开始时间 - 分钟
          minNum: undefined,        // 间隔时间 - 分钟
          endDateMin: '',    // 结束时间 - 分钟
          startDate: '',     // 开始时间 - 小时
          hourNum: undefined,     // 间隔时间 - 小时
          endDate: '',       // 结束时间 - 小时
          scheduleDate: '',  // 调度时间 - 日/周
          weekDate: '',      // 指定时间 - 星期
          monthDay: '',      // 指定时间 - 月
        }
      } else {
        formData[key] = data[key]
      }
    })
    formData.datasourceId && getDataSourceList(true)
    modelConfig.title = '编辑'
  } else {
    Object.keys(formData).forEach((key: string) => {
      if (key == 'cronConfig') {
        formData[key] = {
          setMode: 'SIMPLE',       // 模式
          enable: false,             // 启用
          cron: '',                 // cron表达式
          workDate: [],             // 生效时间
          range: '',         // 调度周期
          startDateMin: '',  // 开始时间 - 分钟
          minNum: undefined,        // 间隔时间 - 分钟
          endDateMin: '',    // 结束时间 - 分钟
          startDate: '',     // 开始时间 - 小时
          hourNum: undefined,     // 间隔时间 - 小时
          endDate: '',       // 结束时间 - 小时
          scheduleDate: '',  // 调度时间 - 日/周
          weekDate: '',      // 指定时间 - 星期
          monthDay: '',      // 指定时间 - 月
        }
      } else {
        formData[key] = ''
      }
      formData.collectType = 'ALL_TABLE'
    })
    modelConfig.title = '添加'
  }

  callback.value = cb
  modelConfig.visible = true
}

function getCron() {
  if (formData.cronConfig.range === 'min') {
    // 调度周期为分钟
    state.secondsText = '0'
    state.minutesText = `0/${formData.cronConfig.minNum}`
    state.hoursText = `${formData.cronConfig.startDateMin.split(':')[0]}-${formData.cronConfig.endDateMin.split(':')[0]}`
  } else if (formData.cronConfig.range === 'hour') {
    // 调度周期为小时
    state.secondsText = '0'
    state.minutesText = '0'
    state.hoursText = `${formData.cronConfig.startDate.split(':')[0]}-${formData.cronConfig.endDate.split(':')[0]}/${formData.cronConfig.hourNum}`
  } else if (formData.cronConfig.range === 'day') {
    // 调度周期为日
    state.secondsText = '0'
    state.minutesText = `${formData.cronConfig.scheduleDate.split(':')[1]}`
    state.hoursText = `${formData.cronConfig.scheduleDate.split(':')[0]}`
  } else if (formData.cronConfig.range === 'month') {
    // 调度周期为日
    state.secondsText = '0'
    state.minutesText = `${formData.cronConfig.scheduleDate.split(':')[1]}`
    state.hoursText = `${formData.cronConfig.scheduleDate.split(':')[0]}`
    state.daysText = `${formData.cronConfig.monthDay}`
  } else if (formData.cronConfig.range === 'week') {
    state.secondsText = '0'
    state.minutesText = `${formData.cronConfig.scheduleDate.split(':')[1]}`
    state.hoursText = `${formData.cronConfig.scheduleDate.split(':')[0]}`
    state.daysText = '?'
    state.weeksText = `${formData.cronConfig.weekDate}`
  }
}

function okEvent() {
  form.value?.validate((valid) => {
    if (valid) {
      modelConfig.okConfig.loading = true
      getCron()
      const cron = `${state.secondsText || '*'} ${state.minutesText || '*'} ${state.hoursText || '*'} ${
        state.daysText || '*'
      } ${state.monthsText || '*'} ${state.weeksText || '?'} ${state.yearsText || '*'}`
      formData.cronConfig.cron = formData.cronConfig.setMode === 'SIMPLE' ? cron : formData.cronConfig.cron
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

function cronTypeChange(e: string) {
  formData.cronConfig.cron = ''
}

function dbTypeChangeEvent() {
  formData.datasourceId = ''
}

function changeScheduleRangeEvent() {
  formData.cronConfig.startDateMin = ''
  formData.cronConfig.minNum = undefined
  formData.cronConfig.endDateMin = ''
  formData.cronConfig.startDate = ''
  formData.cronConfig.hourNum = undefined
  formData.cronConfig.endDate = ''
  formData.cronConfig.scheduleDate = ''
  formData.cronConfig.weekDate = ''
  formData.cronConfig.monthDay = ''
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