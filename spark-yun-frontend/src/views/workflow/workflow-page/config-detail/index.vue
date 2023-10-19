<template>
  <BlockDrawer :drawer-config="drawerConfig">
    <el-scrollbar>
        <div class="work-flow-config">
          <!-- 资源配置 -->
          <div class="config-item">
            <div class="item-title">资源配置</div>
            <el-form
              ref="form"
              label-position="left"
              label-width="120px"
              :model="formData"
              :rules="rules"
            >
              <el-form-item label="模式">
                <el-radio-group v-model="formData.type" size="small">
                  <el-radio-button label="0">简易</el-radio-button>
                  <el-radio-button label="1">高级定义</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="计算集群">
                <el-select v-model="formData.sourceLevel" placeholder="请选择">
                  <el-option
                    v-for="item in sourceLevelOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="sparkConfig" v-if="formData.type === '1'">
                <el-input
                  v-model="formData.sparkConfig"
                  :rows="6"
                  type="textarea"
                  placeholder="请输入"
                />
              </el-form-item>
              <el-form-item label="资源等级" v-else>
                <el-select v-model="formData.sourceLevel" placeholder="请选择">
                  <el-option
                    v-for="item in sourceLevelOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-form>
          </div>
          <el-divider />
          <!-- 定时调度 -->
          <div class="config-item">
            <div class="item-title">调度配置</div>
            <el-form
              ref="form"
              label-position="left"
              label-width="120px"
              :model="formData"
              :rules="rules"
            >
              <el-form-item label="启用">
                <el-switch v-model="formData.cronType" />
              </el-form-item>
              <el-form-item label="模式">
                <el-radio-group v-model="formData.cronType" size="small">
                  <el-radio-button label="0">简易</el-radio-button>
                  <el-radio-button label="1">高级定义</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="cron表达式" v-if="formData.cronType === '1'">
                <el-input
                  v-model="formData.sparkConfig"
                  placeholder="请输入"
                />
              </el-form-item>
              <template v-else>
                <el-form-item label="调度周期">
                  <el-select v-model="formData.range" placeholder="请选择" :disabled="!formData.scheduleStatus" @change="changeScheduleRangeEvent">
                    <el-option
                      v-for="item in scheduleRange"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
                <!-- 调度周期 -> 秒 -->
                <template v-if="formData.range === 'sec'">
                  <el-form-item label="结束时间">
                    <el-date-picker :disabled="!formData.scheduleStatus" v-model="formData.endDate" type="date" placeholder="请选择" clearable />
                  </el-form-item>
                </template>
                <!-- 调度周期 -> 分钟 -->
                <template v-if="formData.range === 'min'">
                  <el-form-item label="开始时间">
                    <el-time-select
                      v-model="formData.startDateMin"
                      :disabled="!formData.scheduleStatus"
                      start="00:00"
                      step="01:00"
                      end="23:00"
                      placeholder="请选择">
                    </el-time-select>
                  </el-form-item>
                  <el-form-item label="时间间隔（分钟）">
                    <el-input-number :disabled="!formData.scheduleStatus" v-model="formData.minNum" :min="0" controls-position="right" />
                  </el-form-item>
                  <el-form-item label="结束时间">
                    <el-time-select
                      v-model="formData.endDateMin"
                      :disabled="!formData.scheduleStatus"
                      start="00:00"
                      step="01:00"
                      end="23:00"
                      placeholder="请选择">
                    </el-time-select>
                  </el-form-item>
                </template>
                <!-- 调度周期 -> 小时 -->
                <template v-if="formData.range === 'hour'">
                  <el-form-item label="开始时间">
                    <el-time-select
                      :disabled="!formData.scheduleStatus"
                      v-model="formData.startDate"
                      start="00:00"
                      step="01:00"
                      end="23:00"
                      placeholder="请选择">
                    </el-time-select>
                  </el-form-item>
                  <el-form-item label="时间间隔（小时）">
                    <el-input-number :disabled="!formData.scheduleStatus" v-model="formData.hourNum" :min="0" controls-position="right" />
                  </el-form-item>
                  <el-form-item label="结束时间">
                    <el-time-select
                      v-model="formData.endDate"
                      :disabled="!formData.scheduleStatus"
                      start="00:00"
                      step="01:00"
                      end="23:00"
                      placeholder="请选择">
                    </el-time-select>
                  </el-form-item>
                </template>
                <!-- 调度周期 -> 日 -->
                <template v-if="formData.range === 'day'">
                  <el-form-item label="调度时间">
                    <el-time-picker
                      :disabled="!formData.scheduleStatus"
                      v-model="formData.scheduleDate"
                      format="hh:mm"
                      value-format="hh:mm"
                      placeholder="请选择"
                    />
                  </el-form-item>
                </template>
                <!-- 调度周期 -> 月 -->
                <template v-if="formData.range === 'month'">
                  <el-form-item label="调度时间">
                    <el-time-picker
                      :disabled="!formData.scheduleStatus"
                      v-model="formData.scheduleDate"
                      format="hh:mm"
                      value-format="hh:mm"
                      placeholder="请选择"
                    />
                  </el-form-item>
                  <el-form-item label="指定时间">
                    <el-select v-model="formData.monthDay" :disabled="!formData.scheduleStatus" placeholder="请选择">
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
                <template v-if="formData.range === 'week'">
                  <el-form-item label="调度时间">
                    <el-time-picker
                      :disabled="!formData.scheduleStatus"
                      v-model="formData.scheduleDate"
                      format="hh:mm"
                      value-format="hh:mm"
                      placeholder="请选择"
                    />
                  </el-form-item>
                  <el-form-item label="指定时间">
                    <el-select v-model="formData.weekDate" placeholder="请选择" :disabled="!formData.scheduleStatus">
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
            </el-form>
          </div>
          <el-divider />
          <!-- 定时调度 -->
          <div class="config-item">
            <div class="item-title">同步规则</div>
            <el-form
              ref="form"
              label-position="left"
              label-width="120px"
              :model="formData"
              :rules="rules"
            >
              <el-form-item label="模式">
                <el-radio-group v-model="formData.cronType" size="small">
                  <el-radio-button label="0">简易</el-radio-button>
                  <el-radio-button label="1">高级定义</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="sparkConfig" v-if="formData.cronType === '1'">
                <el-input
                  v-model="formData.sparkConfig"
                  placeholder="请输入"
                />
              </el-form-item>
              <template v-else>
                <el-form-item label="分区数">
                  <el-input-number
                    v-model="num"
                    :min="0"
                    placeholder="请输入"
                    controls-position="right"
                  />
                </el-form-item>
                <el-form-item label="并发数">
                  <el-input-number
                    v-model="num"
                    :min="0"
                    placeholder="请输入"
                    controls-position="right"
                  />
                </el-form-item>
              </template>
            </el-form>
          </div>
        </div>
      </el-scrollbar>
  </BlockDrawer>
</template>

<script lang="ts" setup>
import { computed, reactive, ref } from 'vue'
import { FormInstance, FormRules } from 'element-plus'
import BlockDrawer from '@/components/block-drawer/index.vue'
import {ScheduleRange, WeekDateList} from './config-detail'

// const form = ref<FormInstance>()
const callback = ref()

const scheduleRange = ref(ScheduleRange);
const weekDateList = ref(WeekDateList)
const dayList = ref()
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
const formData = reactive({
  scheduleStatus: true, // 调度状态
  workDateRange: '', // 生效时间
  cronType: '',       // 配置类型    
  corn: '',
  range: '', // 调度周期
  startDateMin: '',  // 开始时间 - 分钟
  minNum: '',        // 间隔时间 - 分钟
  endDateMin: '',    // 结束时间 - 分钟
  startDate: '',     // 开始时间 - 小时
  hourNum: null,     // 间隔时间 - 小时
  endDate: '',       // 结束时间 - 小时
  scheduleDate: '',  // 调度时间 - 日/周
  weekDate: '',      // 指定时间 - 星期
  monthDay: '',      // 指定时间 - 月

});
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

function showModal(callback: any) {
  dayList.value = []
  for (let i = 1; i <= 31; i++) {
    dayList.value.push({
      label: `${i}号`,
      value: `${i}`
    })
  }
  callback.value = callback
  drawerConfig.visible = true;
}

function okEvent() {
  getCron()
  callback.value({
    ...formData,
    cron: cron
  })
  drawerConfig.visible = false;
}

function closeEvent() {
  drawerConfig.visible = false;
}

function changeScheduleRangeEvent() {
  formData.startDateMin = ''
  formData.minNum = ''
  formData.endDateMin = ''
  formData.startDate = ''
  formData.hourNum = null
  formData.endDate = ''
  formData.scheduleDate = ''
  formData.weekDate = ''
  formData.monthDay = ''
}

function getCron() {
  if (formData.range === 'min') {
    // 调度周期为分钟
    state.secondsText = '0'
    state.minutesText = `0/${formData.minNum}`
    state.hoursText = `${formData.startDateMin.split(':')[0]}-${formData.endDateMin.split(':')[0]}`
  } else if (formData.range === 'hour') {
    // 调度周期为小时
    state.secondsText = '0'
    state.minutesText = '0'
    state.hoursText = `${formData.startDate.split(':')[0]}-${formData.endDate.split(':')[0]}/${formData.hourNum}`
  } else if (formData.range === 'day') {
    // 调度周期为日
    state.secondsText = '0'
    state.minutesText = `${formData.scheduleDate.split(':')[1]}`
    state.hoursText = `${formData.scheduleDate.split(':')[0]}`
  } else if (formData.range === 'month') {
    // 调度周期为日
    state.secondsText = '0'
    state.minutesText = `${formData.scheduleDate.split(':')[1]}`
    state.hoursText = `${formData.scheduleDate.split(':')[0]}`
    state.daysText = `${formData.monthDay}`
  } else if (formData.range === 'week') {
    state.secondsText = '0'
    state.minutesText = `${formData.scheduleDate.split(':')[1]}`
    state.hoursText = `${formData.scheduleDate.split(':')[0]}`
    state.daysText = '?'
    state.weeksText = `${formData.weekDate}`
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