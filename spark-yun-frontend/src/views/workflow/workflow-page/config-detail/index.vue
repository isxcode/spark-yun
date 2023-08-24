<template>
  <BlockDrawer :drawer-config="drawerConfig">
    <!-- <span style="font-size: 12px">表达式：{{ cron }}</span> -->
    <el-form
      ref="form"
      class="config-detail-form"
      label-position="left"
      :model="formData"
      label-width="140px"
    >
      <el-form-item label="调度状态">
        <el-switch :disabled="false" v-model="formData.scheduleStatus" />
      </el-form-item>
      <el-form-item label="生效时间">
        <el-date-picker
          v-model="formData.workDateRange"
          :disabled="!formData.scheduleStatus"
          type="daterange"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
        />
      </el-form-item>

      <el-form-item label="配置方式">
        <el-radio-group v-model="formData.cronType" :disabled="!formData.scheduleStatus">
          <el-radio :label="'0'">手动配置</el-radio>
          <el-radio :label="'1'">自定义配置</el-radio>
        </el-radio-group>
      </el-form-item>

      <template v-if="formData.cronType === '1'">
        <el-form-item label="Corn表达式">
          <el-input :disabled="!formData.scheduleStatus" v-model="formData.corn" placeholder="请输入" show-word-limit />
        </el-form-item>
      </template>
      <template v-if="formData.cronType === '0'">
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
  </BlockDrawer>
</template>

<script lang="ts" setup>
import { computed, reactive, ref } from 'vue'
import { FormInstance, FormRules } from 'element-plus'
import BlockDrawer from '@/components/block-drawer/index.vue'

// const form = ref<FormInstance>()
const callback = ref()

const scheduleRange = ref([
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
]);
const weekDateList = ref([
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
    value: '7',
  }
])
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

  // state.secondsText = ''
  // state.minutesText = '*'
  // state.hoursText = '*'
  // state.daysText = '*'
  // state.weeksText = '?'
  // state.monthsText = '*'
  // state.yearsText = '*'
}

defineExpose({
  showModal,
});
</script>

<style lang="scss">
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