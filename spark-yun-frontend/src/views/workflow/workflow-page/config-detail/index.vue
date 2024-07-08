<template>
  <BlockDrawer :drawer-config="drawerConfig">
    <el-scrollbar>
        <div class="work-flow-config">
          <!-- 数据源配置 -->
          <div class="config-item" v-if="['QUERY_JDBC', 'PRQL', 'EXE_JDBC'].includes(workItemConfig.workType)">
            <div class="item-title">数据源配置</div>
            <el-form
              ref="dataSourceConfig"
              label-position="left"
              label-width="120px"
              :model="dataSourceForm"
              :rules="dataSourceRules"
            >
              <el-form-item label="数据源" prop="datasourceId">
                <el-select
                  v-model="dataSourceForm.datasourceId"
                  placeholder="请选择"
                >
                  <el-option
                    v-for="item in dataSourceList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-form>
            <el-divider />
          </div>
          <template v-else-if="!['SPARK_CONTAINER_SQL'].includes(workItemConfig.workType)">
            <!-- 资源配置 -->
            <div class="config-item" v-if="!['API'].includes(workItemConfig.workType)">
              <div class="item-title">资源配置</div>
              <el-form
                ref="clusterConfigForm"
                label-position="left"
                label-width="120px"
                :model="clusterConfig"
                :rules="clusterConfigRules"
              >
                <el-form-item label="模式" v-if="!['BASH', 'PYTHON'].includes(workItemConfig.workType)">
                  <el-radio-group v-model="clusterConfig.setMode" size="small">
                    <el-radio-button label="SIMPLE">简易</el-radio-button>
                    <el-radio-button label="ADVANCE">高级定义</el-radio-button>
                  </el-radio-group>
                </el-form-item>
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
                <el-form-item label="是否连接hive" v-if="['SPARK_SQL'].includes(workItemConfig.workType)">
                  <el-switch v-model="clusterConfig.enableHive" />
                </el-form-item>
                <el-form-item label="Hive数据源" :prop="'datasourceId'" v-if="clusterConfig.enableHive && ['SPARK_SQL'].includes(workItemConfig.workType)">
                  <el-select
                    v-model="clusterConfig.datasourceId"
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
                <el-form-item label="集群节点" prop="clusterNodeId" v-if="['BASH', 'PYTHON'].includes(workItemConfig.workType)">
                  <el-select v-model="clusterConfig.clusterNodeId" placeholder="请选择" @visible-change="getClusterNodeList">
                    <el-option
                      v-for="item in clusterNodeList"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="sparkConfig" v-if="clusterConfig.setMode === 'ADVANCE'" :class="{ 'show-screen__full': sparkJsonFullStatus }">
                  <el-icon class="modal-full-screen" @click="fullScreenEvent('sparkJsonFullStatus')"><FullScreen v-if="!sparkJsonFullStatus" /><Close v-else /></el-icon>
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
                </el-form-item>
              </el-form>
            </div>
          </template>
          <el-divider />
          <!-- 定时调度 -->
          <div class="config-item">
            <div class="item-title">调度配置</div>
            <el-form
              ref="cronConfigForm"
              label-position="left"
              label-width="120px"
              :model="cronConfig"
              :rules="cronConfigRules"
            >
              <el-form-item label="启用">
                <el-switch v-model="cronConfig.enable" />
              </el-form-item>
              <template v-if="cronConfig.enable">
                <el-form-item label="模式">
                  <el-radio-group v-model="cronConfig.setMode" size="small" @change="cronTypeChange">
                    <el-radio-button label="SIMPLE">简易</el-radio-button>
                    <el-radio-button label="ADVANCE">高级定义</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="生效时间" prop="workDate">
                  <el-date-picker
                    v-model="cronConfig.workDate"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始生效日期"
                    end-placeholder="结束生效日期"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>

                <el-form-item label="cron表达式" prop="cron" v-if="cronConfig.setMode === 'ADVANCE'">
                  <el-input
                    v-model="cronConfig.cron"
                    placeholder="请输入"
                  />
                </el-form-item>
                <template v-else>
                  <el-form-item label="调度周期" prop="range">
                    <el-select v-model="cronConfig.range" placeholder="请选择" :disabled="!cronConfig.enable" @change="changeScheduleRangeEvent">
                      <el-option
                        v-for="item in scheduleRange"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                  <!-- 调度周期 -> 秒 -->
                  <template v-if="cronConfig.range === 'sec'">
                    <el-form-item label="结束时间" prop="endDate">
                      <el-date-picker :disabled="!cronConfig.enable" v-model="cronConfig.endDate" type="date" placeholder="请选择" clearable />
                    </el-form-item>
                  </template>
                  <!-- 调度周期 -> 分钟 -->
                  <template v-if="cronConfig.range === 'min'">
                    <el-form-item label="开始时间" prop="startDateMin">
                      <el-time-select
                        v-model="cronConfig.startDateMin"
                        :disabled="!cronConfig.enable"
                        start="00:00"
                        step="01:00"
                        end="23:00"
                        placeholder="请选择">
                      </el-time-select>
                    </el-form-item>
                    <el-form-item label="时间间隔（分钟）" prop="minNum">
                      <el-input-number :disabled="!cronConfig.enable" v-model="cronConfig.minNum" :min="0" controls-position="right" />
                    </el-form-item>
                    <el-form-item label="结束时间" prop="endDateMin">
                      <el-time-select
                        v-model="cronConfig.endDateMin"
                        :disabled="!cronConfig.enable"
                        start="00:00"
                        step="01:00"
                        end="23:00"
                        placeholder="请选择">
                      </el-time-select>
                    </el-form-item>
                  </template>
                  <!-- 调度周期 -> 小时 -->
                  <template v-if="cronConfig.range === 'hour'">
                    <el-form-item label="开始时间" prop="startDate">
                      <el-time-select
                        :disabled="!cronConfig.enable"
                        v-model="cronConfig.startDate"
                        start="00:00"
                        step="01:00"
                        end="23:00"
                        placeholder="请选择">
                      </el-time-select>
                    </el-form-item>
                    <el-form-item label="时间间隔（小时）" prop="hourNum">
                      <el-input-number :disabled="!cronConfig.enable" v-model="cronConfig.hourNum" :min="0" controls-position="right" />
                    </el-form-item>
                    <el-form-item label="结束时间" prop="endDate">
                      <el-time-select
                        v-model="cronConfig.endDate"
                        :disabled="!cronConfig.enable"
                        start="00:00"
                        step="01:00"
                        end="23:00"
                        placeholder="请选择">
                      </el-time-select>
                    </el-form-item>
                  </template>
                  <!-- 调度周期 -> 日 -->
                  <template v-if="cronConfig.range === 'day'">
                    <el-form-item label="调度时间" prop="scheduleDate">
                      <el-time-picker
                        :disabled="!cronConfig.enable"
                        v-model="cronConfig.scheduleDate"
                        format="HH:mm"
                        value-format="HH:mm"
                        placeholder="请选择"
                      />
                    </el-form-item>
                  </template>
                  <!-- 调度周期 -> 月 -->
                  <template v-if="cronConfig.range === 'month'">
                    <el-form-item label="调度时间" prop="scheduleDate">
                      <el-time-picker
                        :disabled="!cronConfig.enable"
                        v-model="cronConfig.scheduleDate"
                        format="HH:mm"
                        value-format="HH:mm"
                        placeholder="请选择"
                      />
                    </el-form-item>
                    <el-form-item label="指定时间" prop="monthDay">
                      <el-select v-model="cronConfig.monthDay" :disabled="!cronConfig.enable" placeholder="请选择">
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
                  <template v-if="cronConfig.range === 'week'">
                    <el-form-item label="调度时间" prop="scheduleDate">
                      <el-time-picker
                        :disabled="!cronConfig.enable"
                        v-model="cronConfig.scheduleDate"
                        format="HH:mm"
                        value-format="HH:mm"
                        placeholder="请选择"
                      />
                    </el-form-item>
                    <el-form-item label="指定时间" prop="weekDate">
                      <el-select v-model="cronConfig.weekDate" placeholder="请选择" :disabled="!cronConfig.enable">
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
              </template>
            </el-form>
          </div>
          <el-divider />
          <!-- 同步规则 -->
          <div class="config-item" v-if="workItemConfig.workType === 'DATA_SYNC_JDBC'">
            <div class="item-title">同步规则</div>
            <el-form
              ref="syncRuleForm"
              label-position="left"
              label-width="120px"
              :model="syncRule"
              :rules="syncRuleConfigRules"
            >
              <el-form-item label="模式">
                <el-radio-group v-model="syncRule.setMode" size="small">
                  <el-radio-button label="SIMPLE">简易</el-radio-button>
                  <el-radio-button label="ADVANCE">高级定义</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="sqlConfig" v-if="syncRule.setMode === 'ADVANCE'">
                <code-mirror v-model="syncRule.sqlConfigJson" basic :lang="sqllang"/>
              </el-form-item>
              <template v-else>
                <el-form-item label="分区数">
                  <el-tooltip content="分区数推荐大于等于并发数,且成倍数关系" placement="top">
                      <el-icon style="left: -80px" class="tooltip-msg"><QuestionFilled /></el-icon>
                  </el-tooltip>
                  <el-input-number
                    v-model="syncRule.numPartitions"
                    :min="0"
                    placeholder="请输入"
                    controls-position="right"
                  />
                </el-form-item>
                <el-form-item label="并发数">
                  <el-input-number
                    v-model="syncRule.numConcurrency"
                    :min="0"
                    placeholder="请输入"
                    controls-position="right"
                  />
                </el-form-item>
              </template>
            </el-form>
          </div>
          <!-- 函数配置 -->
          <div class="config-item" v-if="['SPARK_SQL', 'DATA_SYNC_JDBC'].includes(workItemConfig.workType)">
            <div class="item-title">函数配置</div>
            <el-form
              ref="syncRuleForm"
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
          <div class="config-item" v-if="['SPARK_SQL', 'SPARK_JAR', 'DATA_SYNC_JDBC'].includes(workItemConfig.workType)">
            <div class="item-title">依赖配置</div>
            <el-form
              ref="syncRuleForm"
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
          <!-- 容器配置 -->
          <div class="config-item" v-if="['SPARK_CONTAINER_SQL'].includes(workItemConfig.workType)">
            <div class="item-title">容器配置</div>
            <el-form
              ref="syncRuleForm"
              label-position="left"
              label-width="120px"
              :model="containerConfig"
            >
              <el-form-item label="依赖">
                <el-select v-model="containerConfig.containerId" clearable
                    filterable placeholder="请选择">
                    <el-option v-for="item in containerIdList" :key="item.value" :label="item.label"
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
import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import BlockDrawer from '@/components/block-drawer/index.vue'
import {ScheduleRange, WeekDateList, ResourceLevelOptions, DataSourceRules, ClusterConfigRules, SyncRuleConfigRules, CronConfigRules} from './config-detail'
import {json} from '@codemirror/lang-json'
import {sql} from '@codemirror/lang-sql'
import CodeMirror from 'vue-codemirror6'
import { GetWorkItemConfig, SaveWorkItemConfig } from '@/services/workflow.service';
import { GetComputerGroupList, GetComputerPointData } from '@/services/computer-group.service';
import { GetDatasourceList } from '@/services/datasource.service'
import { jsonFormatter } from '@/utils/formatter'
import { GetFileCenterList } from '@/services/file-center.service'
import { GetCustomFuncList } from '@/services/custom-func.service'
import { GetSparkContainerList } from '@/services/spark-container.service'

const scheduleRange = ref(ScheduleRange);
const weekDateList = ref(WeekDateList)
const clusterList = ref([])  // 计算集群
const clusterNodeList = ref([])  // 集群节点
const dataSourceList = ref([])
const dayList = ref()
const lang = ref<any>(json())
const sqllang = ref<any>(sql())
const workItemConfig = ref()
const fileIdList = ref([]) // 资源文件
const libIdList = ref([])  // 依赖

const resourceLevelOptions = ref(ResourceLevelOptions) // 资源等级
const dataSourceConfig = ref<FormInstance>()
const clusterConfigForm = ref<FormInstance>()
const cronConfigForm = ref<FormInstance>()
const syncRuleForm = ref<FormInstance>()
const containerIdList = ref([]) // 容器列表
const callback = ref(null)

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
let dataSourceForm = reactive({
  datasourceId: '',         // 数据源id
})
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
// 定时配置
let cronConfig = reactive({
  setMode: '',       // 模式
  enable: true,             // 启用
  cron: '',                 // cron表达式
  workDate: '',             // 生效时间
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
})
// 数据同步规则
let syncRule = reactive({
  // 数据同步
  setMode: '',       // 模式
  numPartitions: undefined,     // 分区数
  numConcurrency: undefined,    // 并发数
  // sqlConfig: '',
  sqlConfigJson: ''
})
// 容器配置
let containerConfig = reactive({
  containerId: ''
})
const fileConfig = reactive({
  funcList: [],
  libList: []
})
const dataSourceRules = reactive<FormRules>(DataSourceRules)
const clusterConfigRules = reactive<FormRules>(ClusterConfigRules)
const cronConfigRules = reactive<FormRules>(CronConfigRules)
const syncRuleConfigRules = reactive<FormRules>(SyncRuleConfigRules)
const state = reactive({
  secondsText: '',
  minutesText: '',
  hoursText: '',
  daysText: '',
  weeksText: '',
  monthsText: '',
  yearsText: ''
})

const cron = computed(() => {
  return `${state.secondsText || '*'} ${state.minutesText || '*'} ${state.hoursText || '*'} ${
    state.daysText || '*'
  } ${state.monthsText || '*'} ${state.weeksText || '?'} ${state.yearsText || '*'}`;
});

function showModal(data?: any, cb?: any) {
  callback.value = cb
  // 调度日期初始化
  dayList.value = []
  for (let i = 1; i <= 31; i++) {
    dayList.value.push({
      label: `${i}号`,
      value: `${i}`
    })
  }
  if (!data) {
    clusterConfig.setMode = 'SIMPLE'
    cronConfig.setMode = 'SIMPLE'
    syncRule.setMode = 'SIMPLE'
  } else {
    if (!['QUERY_JDBC', 'PRQL', 'EXE_JDBC'].includes(data.workType)) {
      // 获取集群参数
      getClusterList()
    }
    if (['SPARK_CONTAINER_SQL'].includes(data.workType)) {
      getSparkContainerList(true)
    }
    // 获取函数配置和依赖配置
    getFuncList()
    getFileCenterList()

    getDataSourceList(true)
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

function getConfigDetailData() {
  GetWorkItemConfig({
      workId: workItemConfig.value.id
  }).then((res: any) => {
    if (['QUERY_JDBC', 'PRQL', 'EXE_JDBC'].includes(workItemConfig.value.workType)) {
      dataSourceForm.datasourceId = res.data.datasourceId
    }
    if (res.data.clusterConfig) {
      Object.keys(clusterConfig).forEach((key: string) => {
        if (key !== 'datasourceId') {
          clusterConfig[key] = res.data.clusterConfig[key]
        }
      })
      clusterConfig.sparkConfigJson = jsonFormatter(clusterConfig.sparkConfigJson)
    }
    if (['SPARK_SQL'].includes(workItemConfig.value.workType)) {
      clusterConfig.datasourceId = res.data.datasourceId
    }
    if (res.data.cronConfig) {
      Object.keys(cronConfig).forEach((key: string) => {
        cronConfig[key] = res.data.cronConfig[key]
      })
    }
    if (res.data.syncRule) {
      Object.keys(syncRule).forEach((key: string) => {
        syncRule[key] = res.data.syncRule[key]
      })
      syncRule.sqlConfigJson = jsonFormatter(syncRule.sqlConfigJson)
    }
    fileConfig.funcList = res.data.funcList || []
    fileConfig.libList = res.data.libList || []
    clusterConfig.setMode = clusterConfig.setMode || 'SIMPLE'
    cronConfig.setMode = cronConfig.setMode || 'SIMPLE'
    syncRule.setMode = syncRule.setMode || 'SIMPLE'
    containerConfig.containerId = res.data.containerId

    getClusterNodeList(true)
  }).catch((err: any) => {
    console.error(err)
  })
}

function okEvent() {
  // 获取cron表达式
  let status = true
  const formArr = [dataSourceConfig, clusterConfigForm, cronConfigForm, syncRuleForm]
  formArr.forEach(f => {
    f.value?.validate((valid: boolean) => {
      if (!valid) {
        status = false
      }
    })
  })
  setTimeout(() => {
    if (status) {
      getCron()
      const clusObj = clusterConfig
      if (['SPARK_SQL'].includes(workItemConfig.value.workType)) {
        dataSourceForm.datasourceId = clusObj.datasourceId
        delete clusObj.datasourceId
      }
      const cron = `${state.secondsText || '*'} ${state.minutesText || '*'} ${state.hoursText || '*'} ${
        state.daysText || '*'
      } ${state.monthsText || '*'} ${state.weeksText || '?'} ${state.yearsText || '*'}`
      SaveWorkItemConfig({
        workId: workItemConfig.value.id,
        datasourceId: dataSourceForm.datasourceId || undefined,
        clusterConfig: clusObj,
        cronConfig: {
          ...cronConfig,
          cron: cronConfig.setMode === 'SIMPLE' ? cron : cronConfig.cron
        },
        syncRule: syncRule,
        ...fileConfig,
        ...containerConfig
      }).then((res: any) => {
        if (callback.value && callback.value instanceof Function) {
          callback.value()
        }
        ElMessage.success('保存成功')
        drawerConfig.visible = false;
      }).catch(err => {
        clusterConfig.datasourceId = dataSourceForm.datasourceId
        console.error(err)
      })
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  });
}

function closeEvent() {
  drawerConfig.visible = false;
}

function changeScheduleRangeEvent() {
  cronConfig.startDateMin = ''
  cronConfig.minNum = undefined
  cronConfig.endDateMin = ''
  cronConfig.startDate = ''
  cronConfig.hourNum = undefined
  cronConfig.endDate = ''
  cronConfig.scheduleDate = ''
  cronConfig.weekDate = ''
  cronConfig.monthDay = ''
}

function cronTypeChange(e: string) {
  cronConfig.cron = ''
}
function getCron() {
  if (cronConfig.range === 'min') {
    // 调度周期为分钟
    state.secondsText = '0'
    state.minutesText = `0/${cronConfig.minNum}`
    state.hoursText = `${cronConfig.startDateMin.split(':')[0]}-${cronConfig.endDateMin.split(':')[0]}`
  } else if (cronConfig.range === 'hour') {
    // 调度周期为小时
    state.secondsText = '0'
    state.minutesText = '0'
    state.hoursText = `${cronConfig.startDate.split(':')[0]}-${cronConfig.endDate.split(':')[0]}/${cronConfig.hourNum}`
  } else if (cronConfig.range === 'day') {
    // 调度周期为日
    state.secondsText = '0'
    state.minutesText = `${cronConfig.scheduleDate.split(':')[1]}`
    state.hoursText = `${cronConfig.scheduleDate.split(':')[0]}`
  } else if (cronConfig.range === 'month') {
    // 调度周期为日
    state.secondsText = '0'
    state.minutesText = `${cronConfig.scheduleDate.split(':')[1]}`
    state.hoursText = `${cronConfig.scheduleDate.split(':')[0]}`
    state.daysText = `${cronConfig.monthDay}`
  } else if (cronConfig.range === 'week') {
    state.secondsText = '0'
    state.minutesText = `${cronConfig.scheduleDate.split(':')[1]}`
    state.hoursText = `${cronConfig.scheduleDate.split(':')[0]}`
    state.daysText = '?'
    state.weeksText = `${cronConfig.weekDate}`
  }
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
function getClusterNodeList(e: boolean) {
  if (e && clusterConfig.clusterId) {
    GetComputerPointData({
      page: 0,
      pageSize: 10000,
      searchKeyWord: '',
      clusterId: clusterConfig.clusterId
    }).then((res: any) => {
      clusterNodeList.value = res.data.content.map((item: any) => {
        return {
          label: item.name,
          value: item.id
        }
      })
    }).catch(() => {
      clusterNodeList.value = []
    })
  }
}
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

function getSparkContainerList(e: boolean, searchType?: string) {
  if (e) {
    GetSparkContainerList({
      page: 0,
      pageSize: 10000,
      searchKeyWord: ''
    }).then((res: any) => {
      containerIdList.value = res.data.content.map((item: any) => {
        return {
          label: item.name,
          value: item.id
        }
      })
    })
    .catch(() => {
      containerIdList.value = []
    })
  }
}

function clusterIdChangeEvent() {
  clusterConfig.clusterNodeId = ''
}

// 全屏
function fullScreenEvent(type: string) {
  if (type === 'sparkJsonFullStatus') {
    sparkJsonFullStatus.value = !sparkJsonFullStatus.value
  }
}

defineExpose({
  showModal,
})
</script>

<style lang="scss">
.work-flow-config {
  padding: 12px;
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
        position: relative;
        .tooltip-msg {
            position: absolute;
            top: 7px;
            color: getCssVar('color', 'info');
            font-size: 16px;
        }
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

          // 全屏样式
          .modal-full-screen {
            position: absolute;
            top: 8px;
            left: -22px;
            cursor: pointer;
            &:hover {
              color: getCssVar('color', 'primary');;
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