<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table zqy-schedule">
    <div class="zqy-table-top">
      <el-radio-group v-model="tableType" @change="changeTypeEvent">
        <el-radio-button label="workflow">作业流</el-radio-button>
        <el-radio-button label="work">作业</el-radio-button>
      </el-radio-group>
      <div class="zqy-seach">
        <el-input
          v-model="keyword"
          placeholder="请输入实例编码/作业 回车进行搜索"
          :maxlength="200"
          clearable
          @input="inputEvent"
          @keyup.enter="initData(false)"
        />
        <el-button
          type="primary"
          @click="initData(false)"
        >
          刷新
        </el-button>
      </div>
    </div>
    <LoadingPage
      :visible="loading"
      :network-error="networkError"
      @loading-refresh="initData(false)"
    >
      <div class="zqy-table">
        <BlockTable
          :table-config="tableType === 'work' ? tableConfig : tableConfigWorkFlow"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        >
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
            <div class="btn-group">
              <el-tag
                v-if="scopeSlot.row.status === 'SUCCESS'"
                class="ml-2"
                type="success"
              >
                成功
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.status === 'FAIL'"
                class="ml-2"
                type="danger"
              >
                失败
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.status === 'ABORT'"
                class="ml-2"
                type="warning"
              >
                已中止
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.status === 'ABORTING'"
                class="ml-2"
              >
                中止中
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.status === 'RUNNING'"
                class="ml-2"
              >
                运行中
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.status === 'PENDING'"
                class="ml-2"
              >
                等待中
              </el-tag>
              <el-tag
                v-if="!scopeSlot.row.status"
                class="ml-2"
                type="info"
              >
                未运行
              </el-tag>
            </div>
          </template>
          <template #options="scopeSlot">
            <div class="btn-group">
              <span @click="showDetailModal(scopeSlot.row, 'log')">日志</span>
              <el-dropdown trigger="click">
                <span class="click-show-more">更多</span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-if="['SPARK_SQL', 'DATA_SYNC_JDBC', 'BASH', 'PYTHON'].includes(scopeSlot.row.workType)"
                      @click="showDetailModal(scopeSlot.row, 'yarnLog')"
                    >
                      运行日志
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="scopeSlot.row.status !== 'RUNNING' && scopeSlot.row.instanceType === 'WORK'"
                      @click="retry(scopeSlot.row)"
                    >
                      重新运行
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="scopeSlot.row.status === 'SUCCESS' && scopeSlot.row.workType !== 'EXE_JDBC'"
                      @click="showDetailModal(scopeSlot.row, 'result')"
                    >
                      运行结果
                    </el-dropdown-item>
                    <el-dropdown-item @click="deleteSchedule(scopeSlot.row)">
                      删除
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
                    <el-dropdown-item>
                      删除
                    </el-dropdown-item>
                    <el-dropdown-item @click="reRunWorkFlowDataEvent(scopeSlot.row)">
                      重跑
                    </el-dropdown-item>
                    <el-dropdown-item @click="stopWorkFlow(scopeSlot.row)">
                      中止
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
import { GetScheduleList, DeleteScheduleLog, ReStartRunning, GetScheduleWorkFlowList } from '@/services/schedule.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ReRunWorkflow, StopWorkflowData } from '@/services/workflow.service'

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

function initData(tableLoading?: boolean) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  if (tableType.value === 'workflow') {
    GetScheduleWorkFlowList({
      page: tableConfigWorkFlow.pagination.currentPage - 1,
      pageSize: tableConfigWorkFlow.pagination.pageSize,
      searchKeyWord: keyword.value
    })
      .then((res: any) => {
        tableConfigWorkFlow.tableData = res.data.content
        tableConfigWorkFlow.pagination.total = res.data.totalElements
        loading.value = false
        tableConfigWorkFlow.loading = false
        networkError.value = false
      })
      .catch(() => {
        tableConfigWorkFlow.tableData = []
        tableConfigWorkFlow.pagination.total = 0
        loading.value = false
        tableConfigWorkFlow.loading = false
        networkError.value = true
      })
  } else {
    GetScheduleList({
      page: tableConfig.pagination.currentPage - 1,
      pageSize: tableConfig.pagination.pageSize,
      searchKeyWord: keyword.value
    })
      .then((res: any) => {
        tableConfig.tableData = res.data.content
        tableConfig.pagination.total = res.data.totalElements
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
      })
  }
}

function changeTypeEvent() {
  tableConfigWorkFlow.pagination.currentPage = 1
  tableConfigWorkFlow.pagination.pageSize = 10
  tableConfig.pagination.currentPage = 1
  tableConfig.pagination.pageSize = 10
  initData()
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

// // 删除
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

function getTypeData(e: string) {
  if (!e) {
    return
  }
  const typeList = [
    {
      label: 'Jdbc执行作业',
      value: 'EXE_JDBC'
    },
    {
      label: 'Jdbc查询作业',
      value: 'QUERY_JDBC'
    },
    {
      label: 'Prql查询作业',
      value: 'PRQL'
    },
    {
      label: 'SparkSql查询作业',
      value: 'SPARK_SQL'
    },
    {
      label: 'SparkSql容器作业',
      value: 'SPARK_CONTAINER_SQL'
    },
    {
      label: '数据同步作业',
      value: 'DATA_SYNC_JDBC'
    },
    {
      label: 'bash作业',
      value: 'BASH'
    },
    {
      label: 'python作业',
      value: 'PYTHON'
    },
    {
      label: '自定义作业',
      value: 'SPARK_JAR'
    },
    {
      label: '接口调用作业',
      value: 'API'
    }
  ]
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

onMounted(() => {
  if (!tableType.value) {
    tableType.value = 'workflow'
  }

  tableConfigWorkFlow.pagination.currentPage = 1
  tableConfigWorkFlow.pagination.pageSize = 10
  initData()
  timer.value = setInterval(() => {
    initData(true)
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
