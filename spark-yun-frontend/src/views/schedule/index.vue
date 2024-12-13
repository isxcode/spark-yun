<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table zqy-schedule">
    <div class="zqy-table-top">
      <el-radio-group v-model="tableType" @change="changeTypeEvent">
        <el-radio-button label="workflow">作业流</el-radio-button>
        <el-radio-button label="work">作业</el-radio-button>
      </el-radio-group>
      <div class="zqy-tenant__select">
        <el-select
          v-model="executeStatus"
          clearable
          placeholder="请选择状态进行搜索"
          @change="initPageTable"
        >
          <el-option
            v-for="item in typeList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-select
          v-if="tableType === 'workflow'"
          class="workflow-search"
          v-model="workflowId"
          clearable
          placeholder="请选择工作流进行搜索"
          @change="initPageTable"
        >
          <el-option
            v-for="item in workFlowList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </div>
      <div class="zqy-seach">
        <el-input
          v-model="keyword"
          placeholder="请输入实例编码/作业 回车进行搜索"
          :maxlength="200"
          clearable
          @input="inputEvent"
          @keyup.enter="initPageTable"
        />
      </div>
    </div>
    <LoadingPage
      :visible="loading"
      :network-error="networkError"
      @loading-refresh="initPageTable"
    >
      <div class="zqy-table">
        <BlockTable
          :table-config="tableType === 'work' ? tableConfig : tableConfigWorkFlow"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        >
          <template #nameSlot="scopeSlot">
            <span
              class="name-click"
              @click="redirectWork(scopeSlot.row)"
            >{{ scopeSlot.row.workflowInstanceId }}</span>
          </template>
          <template #instanceTypeTag="scopeSlot">
            <div class="btn-group">
              <el-tag
                v-if="scopeSlot.row.instanceType === 'MANUAL' || scopeSlot.row.type === 'MANUAL'"
                class="ml-2"
                type="info"
              >
                手动执行
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.instanceType === 'AUTO' || scopeSlot.row.type === 'AUTO'"
                class="ml-2"
                type="info"
              >
                调度执行
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.instanceType === 'INVOKE' || scopeSlot.row.type === 'INVOKE'"
                class="ml-2"
                type="info"
              >
                外部调用
              </el-tag>
            </div>
          </template>
          <template #typeSlot="scopeSlot">
            {{ getTypeData(scopeSlot.row.workType) }}
          </template>
          <template #duration="scopeSlot">
            {{ scopeSlot.row.duration !== undefined && scopeSlot.row.duration !== null ? formatSeconds(scopeSlot.row.duration) : '-' }}
          </template>
          <template #statusTag="scopeSlot">
            <ZStatusTag :status="scopeSlot.row.status"></ZStatusTag>
          </template>
          <template #options="scopeSlot">
            <div class="btn-group">
              <span @click="showDetailModal(scopeSlot.row, 'log')">日志</span>
              <el-dropdown trigger="click">
                <span class="click-show-more">更多</span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-if="['SPARK_SQL', 'DATA_SYNC_JDBC', 'BASH', 'PYTHON', 'EXCEL_SYNC_JDBC', 'CURL'].includes(scopeSlot.row.workType)"
                      @click="showDetailModal(scopeSlot.row, 'yarnLog')"
                    >
                      运行日志
                    </el-dropdown-item>
                    <el-dropdown-item
                        v-if="scopeSlot.row.status === 'SUCCESS' && scopeSlot.row.workType !== 'EXE_JDBC'"
                        @click="showDetailModal(scopeSlot.row, 'result')"
                    >
                      运行结果
                    </el-dropdown-item>
                    <el-dropdown-item
                        v-if="scopeSlot.row.status === 'RUNNING'"
                        @click="stopWork(scopeSlot.row)"
                    >
                      中止
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="scopeSlot.row.status !== 'RUNNING'"
                      @click="retry(scopeSlot.row)"
                    >
                      重跑
                    </el-dropdown-item>
                    <el-dropdown-item @click="deleteSchedule(scopeSlot.row)">
                      删除
                    </el-dropdown-item>
                    <el-dropdown-item @click="backToWorkflowIns(scopeSlot.row)">
                      跳转实例
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
          <template #workFlowOptions="scopeSlot">
            <div class="btn-group">
              <span @click="showDagDetail(scopeSlot.row)">DAG</span>
              <el-dropdown trigger="click">
                <span class="click-show-more">更多</span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="reRunWorkFlowDataEvent(scopeSlot.row)">
                      重跑
                    </el-dropdown-item>
                    <el-dropdown-item v-if="!['SUCCESS','FAIL','ABORT'].includes(scopeSlot.row.status)" @click="stopWorkFlow(scopeSlot.row)">
                      中止
                    </el-dropdown-item>
                    <el-dropdown-item @click="deleteWorkflowSchedule(scopeSlot.row)">
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </BlockTable>
      </div>
    </LoadingPage>
    <DetailModal ref="detailModalRef" />
    <dag-detail ref="dagDetailRef"></dag-detail>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, onUnmounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import DetailModal from './detail-modal/index.vue'
import DagDetail from './dag-detail/index.vue'

import { BreadCrumbList, TableConfig, TableConfigWorkFlow } from './schedule.config'
import { GetScheduleList, DeleteScheduleLog, ReStartRunning, GetScheduleWorkFlowList, DeleteWorkFlowScheduleLog } from '@/services/schedule.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { GetWorkflowList, ReRunWorkflow, StopWorkflowData, TerWorkItemConfig } from '@/services/workflow.service'
import { TypeList } from '../workflow/workflow.config'
import { nextTick } from 'process'

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const tableConfigWorkFlow = reactive(TableConfigWorkFlow)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const detailModalRef = ref()
const dagDetailRef = ref()
const tableType = ref('')  // work or workflow
const timer = ref()
const executeStatus = ref('')
const typeList = ref([
  {
    label: '成功',
    value: 'SUCCESS',
  },
  {
    label: '失败',
    value: 'FAIL',
  },
  {
    label: '已中止',
    value: 'ABORT',
  },
  {
    label: '中止中',
    value: 'ABORTING',
  },
  {
    label: '运行中',
    value: 'RUNNING',
  },
  {
    label: '等待中',
    value: 'PENDING',
  }
])
const workFlowList = ref([])
const workflowId = ref('')

function initData(tableLoading?: boolean, type?: string) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  if (tableType.value === 'workflow') {
    GetScheduleWorkFlowList({
      page: tableConfigWorkFlow.pagination.currentPage - 1,
      pageSize: tableConfigWorkFlow.pagination.pageSize,
      searchKeyWord: keyword.value,
      workflowId: workflowId.value,
      executeStatus: executeStatus.value
    }).then((res: any) => {
      if (type) {
        res.data.content.forEach((item: any) => {
          tableConfigWorkFlow.tableData.forEach((col: any) => {
            if (item.workflowInstanceId === col.workflowInstanceId) {
              col.status = item.status
              col.planStartDateTime = item.planStartDateTime
              col.nextPlanDateTime = item.nextPlanDateTime
            }
          })
        })
      } else {
        tableConfigWorkFlow.tableData = res.data.content
        tableConfigWorkFlow.pagination.total = res.data.totalElements
      }
      loading.value = false
      tableConfigWorkFlow.loading = false
      networkError.value = false
    }).catch(() => {
      tableConfigWorkFlow.tableData = []
      tableConfigWorkFlow.pagination.total = 0
      loading.value = false
      tableConfigWorkFlow.loading = false
      networkError.value = true

      if (timer.value) {
        clearInterval(timer.value)
      }
      timer.value = null
    })
  } else {
    GetScheduleList({
      page: tableConfig.pagination.currentPage - 1,
      pageSize: tableConfig.pagination.pageSize,
      searchKeyWord: keyword.value,
      executeStatus: executeStatus.value
    })
      .then((res: any) => {
        if (type) {
          res.data.content.forEach((item: any) => {
            tableConfig.tableData.forEach((col: any) => {
              if (item.id === col.id) {
                col.status = item.status
                col.planStartDateTime = item.planStartDateTime
                col.nextPlanDateTime = item.nextPlanDateTime
              }
            })
          })
        } else {
          tableConfig.tableData = res.data.content
          tableConfig.pagination.total = res.data.totalElements
        }
        loading.value = false
        tableConfig.loading = false
        networkError.value = false
      })
      .catch(() => {
        tableConfig.tableData = []
        tableConfig.pagination.total = 0
        loading.value = false
        tableConfig.loading = false
        networkError.value = true

        if (timer.value) {
          clearInterval(timer.value)
        }
        timer.value = null
      })
  }
}

function changeTypeEvent() {
  keyword.value = ''
  workflowId.value = ''
  initPageTable()
}

function initPageTable() {
  tableConfigWorkFlow.pagination.currentPage = 1
  tableConfigWorkFlow.pagination.pageSize = 10
  tableConfig.pagination.currentPage = 1
  tableConfig.pagination.pageSize = 10
  initData()
}

function getWorkFlows() {
  GetWorkflowList({
    page: 0,
    pageSize: 100000,
    searchKeyWord: ''
  }).then((res: any) => {
    workFlowList.value = res.data.content
  }).catch(() => {
    workFlowList.value = []
  })
}

function showDetailModal(data: any, type: string) {
  detailModalRef.value.showModal(
    () => {
      console.log('关闭')
    },
    data,
    type
  )
}

function retry(data: any) {
  ReStartRunning({
    instanceId: data.id
  })
    .then((res: any) => {
      ElMessage.success(res.msg)
      initData()
    })
    .catch((error: any) => {
      console.error(error)
    })
}

function stopWork(data: any) {
  TerWorkItemConfig({instanceId: data.id})
    .then((res: any) => {
      ElMessage.success(res.msg)
      initData()
    })
    .catch((error: any) => {
      console.error(error)
    })
}

// 删除
function deleteSchedule(data: any) {
  ElMessageBox.confirm('确定删除该调度历史吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    DeleteScheduleLog({
      instanceId: data.id
    })
      .then((res: any) => {
        ElMessage.success(res.msg)
        initData()
      })
      .catch(() => {})
  })
}

// 删除作业流调度
function deleteWorkflowSchedule(data: any) {
  ElMessageBox.confirm('确定删除该调度历史吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    DeleteWorkFlowScheduleLog({
      workflowInstanceId: data.workflowInstanceId
    }).then((res: any) => {
      ElMessage.success(res.msg)
      initData()
    }).catch(() => {})
  })
}

function getTypeData(e: string) {
  if (!e) {
    return
  }
  const typeList = [...TypeList]
  return typeList.find(itme => itme.value === e)?.label
}

function inputEvent(e: string) {
  if (e === '') {
    initData()
  }
}

function handleSizeChange(e: number) {
  tableConfig.pagination.pageSize = e
  tableConfigWorkFlow.pagination.pageSize = e
  initData()
}

function handleCurrentChange(e: number) {
  tableConfig.pagination.currentPage = e
  tableConfigWorkFlow.pagination.currentPage = e
  initData()
}

// 重跑工作流
function reRunWorkFlowDataEvent(data: any) {
    ReRunWorkflow({
        workflowInstanceId: data.workflowInstanceId
    }).then((res: any) => {
        ElMessage.success(res.msg)
        initData()
        if (!timer.value) {
            timer.value = setInterval(() => {
              initData(true)
            }, 1000)
        }
    }).catch(() => {
    })
}
// 中止工作流
function stopWorkFlow(data: any) {
    StopWorkflowData({
        workflowInstanceId: data.workflowInstanceId
    }).then((res: any) => {
        ElMessage.success(res.msg)
        initData()
        if (!timer.value) {
            timer.value = setInterval(() => {
              initData(true)
            }, 1000)
        }
    }).catch(() => {
    })
}
// 展示工作流对应流程图
function showDagDetail(data: any) {
  dagDetailRef.value.showModal(data)
}
function formatSeconds(value: number) {
  let time = value
  if (time >= 60 && time <= 3600) {
    time = parseInt(time / 60) + '分' + time % 60 + '秒';
  } else {
    if (time > 3600) {
      time = parseInt(time / 3600) + '小时' + parseInt(((time % 3600) / 60)) + '分' + time % 60 + '秒';
    }
    else {
      time = time + '秒';
    }
  }
  return time;
}

// 跳转到作业页面
function redirectWork(e: any) {
  tableType.value = 'work'
  keyword.value = e.workflowInstanceId
  workflowId.value = ''

  tableConfigWorkFlow.pagination.currentPage = 1
  tableConfigWorkFlow.pagination.pageSize = 10
  tableConfig.pagination.currentPage = 1
  tableConfig.pagination.pageSize = 10
  initData()
}

// 跳转回作业流实例
function backToWorkflowIns(e: any) {
  tableType.value = 'workflow'
  keyword.value = e.workflowInstanceId
  workflowId.value = ''

  tableConfigWorkFlow.pagination.currentPage = 1
  tableConfigWorkFlow.pagination.pageSize = 10
  tableConfig.pagination.currentPage = 1
  tableConfig.pagination.pageSize = 10
  initData()
}

onMounted(() => {
  if (!tableType.value) {
    tableType.value = 'workflow'
  }

  getWorkFlows()

  tableConfigWorkFlow.pagination.currentPage = 1
  tableConfigWorkFlow.pagination.pageSize = 10
  initData()
  timer.value = setInterval(() => {
    initData(true, 'interval')
  }, 3000)
})
onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
  timer.value = null
})
</script>

<style lang="scss">
.zqy-seach-table {
  .click-show-more {
    font-size: getCssVar('font-size', 'extra-small');
  }

  &.zqy-schedule {
    .zqy-table-top {
      .el-radio-group {
        .el-radio-button__inner {
          font-size: getCssVar('font-size', 'extra-small');
        }
      }

      .workflow-search {
        margin-left: 12px;
      }
    }
    .zqy-seach {
      display: flex;
      align-items: center;
      .el-button {
        margin-left: 12px;
      }
    }
  }
}
</style>
