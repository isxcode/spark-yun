<template>
  <BlockDrawer :drawer-config="drawerConfig">
    <el-scrollbar>
        <div class="work-flow-config">
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
                <el-form-item label="类型" prop="type">
                  <el-select v-model="cronConfig.type" placeholder="请选择">
                    <el-option
                      v-for="item in typeList"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
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
          <!-- 外部调用 -->
          <div class="config-item">
            <div class="item-title">外部调用</div>
            <el-form
              label-position="left"
              label-width="120px"
              :model="otherConfig"
            >
              <el-form-item label="启用">
                <el-switch v-model="otherConfig.invokeStatus" @change="getInvokeUrl" />
              </el-form-item>
              <el-form-item v-if="otherConfig.invokeUrl" label="调用链接" class="invoke-url-copy">
                <el-input v-model="otherConfig.invokeUrl" :disabled="true" />
                <span
                  class="invoke-url-copy__text"
                  @click="copyUrlEvent(otherConfig.invokeUrl)"
                >复制</span>
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
import {ScheduleRange, WeekDateList, CronConfigRules} from './config-detail'
import { GetInvokeUrl } from '@/services/workflow.service';

const scheduleRange = ref(ScheduleRange);
const weekDateList = ref(WeekDateList)
const dayList = ref()
const cronConfigForm = ref<FormInstance>()
const callback = ref<any>()
  const typeList = ref([
  {
    label: '单一调度',
    value: 'ALL'
  },
  {
    label: '离散调度',
    value: 'SINGLE'
  }
])
const workflowId = ref('')
const cronConfigRules = reactive<FormRules>(CronConfigRules)
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
})
// 定时配置
let cronConfig = reactive({
  setMode: '',       // 模式
  type: '',
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

const state = reactive({
  secondsText: '',
  minutesText: '',
  hoursText: '',
  daysText: '',
  weeksText: '',
  monthsText: '',
  yearsText: ''
})
// 外部调用
let otherConfig = reactive({
  invokeStatus: false,
  invokeUrl: ''
})

const cron = computed(() => {
  return `${state.secondsText || '*'} ${state.minutesText || '*'} ${state.hoursText || '*'} ${
    state.daysText || '*'
  } ${state.monthsText || '*'} ${state.weeksText || '?'} ${state.yearsText || '*'}`;
});

function showModal(cb: () => void, data: any) {
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
    cronConfig.setMode = 'SIMPLE'
  } else {
    workflowId.value = data.workflowId
    Object.keys(cronConfig).forEach((key: string) => {
      cronConfig[key] = data.cronConfig[key]
    })
    otherConfig.invokeStatus = data.invokeStatus === 'OFF' || !data.invokeStatus ? false : true
    otherConfig.invokeUrl = data.invokeUrl
  }

  drawerConfig.visible = true;
}

function okEvent() {
  // 获取cron表达式
  cronConfigForm.value?.validate((valid: boolean) => {
    if (valid) {
      getCron()
      const cron = `${state.secondsText || '*'} ${state.minutesText || '*'} ${state.hoursText || '*'} ${
        state.daysText || '*'
      } ${state.monthsText || '*'} ${state.weeksText || '?'} ${state.yearsText || '*'}`
      callback.value({
        cronConfig: {
          ...cronConfig,
          cron: cronConfig.setMode === 'SIMPLE' ? cron : cronConfig.cron,
        },
        ...otherConfig
      }).then((res: any) => {
        drawerConfig.okConfig.loading = false
        if (res === undefined) {
          drawerConfig.visible = false
        } else {
          drawerConfig.visible = true
        }
      }).catch((err: any) => {
        drawerConfig.okConfig.loading = false
      })
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
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

function cronTypeChange(e: string) {
  cronConfig.cron = ''
}

function getInvokeUrl(e: boolean) {
  if (e) {
    GetInvokeUrl({
      workflowId: workflowId.value
    }).then((res: any) => {
      otherConfig.invokeUrl = res.data.url
    }).catch((error: any) => {
      otherConfig.invokeUrl = ''
      console.error(error)
    })
  }
}

async function copyUrlEvent(text: string) {
  try {
    await navigator.clipboard.writeText(text);
    ElMessage({
      duration: 800,
      message: '复制成功',
      type: 'success',
    });
  } catch (err) {
    console.error("Failed to copy: ", err);
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
        .el-form-item__content {
          .el-radio-group {
            justify-content: flex-end;
          }
        }
        &.invoke-url-copy {
          .el-input {
            width: calc(100% - 36px);
          }
          .invoke-url-copy__text {
            position: absolute;
            right: 0;
            font-size: 12px;
            color: getCssVar('color', 'primary');
            cursor: pointer;
            user-select: none;
            &:hover {
              text-decoration: underline;
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