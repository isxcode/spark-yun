<template>
  <div class="vm-list">
      <div class="vm-list__header">
          <span class="vm-list__title">实例列表</span>
          <div class="vm-list__ops">
              <el-icon class="vm-list__icon" @click="queryVmlistData">
                  <RefreshRight />
              </el-icon>
              <el-input
                  class="vm-list__search"
                  v-model="keyWord"
                  placeholder="作业流"
                  @keydown.enter="queryVmlistData"
              />
          </div>
      </div>
      <div class="vm-list__body">
          <el-table
              class="vm-list__table"
              :class="{ 'vm-list__table-empty': !tableData.length }"
              :data="tableData"
              v-loading="tableLoading"
          >
              <el-table-column prop="workflowName" label="作业流" width="180" show-overflow-tooltip />
              <el-table-column prop="status" label="状态">
                  <template #default="{ row }">
                      <ZStatusTag :status="row.status"></ZStatusTag>
                  </template>
              </el-table-column>
              <el-table-column prop="lastModifiedBy" label="发布人">
                  <template #default="{ row }">
                      <person-tag :person-name="row.lastModifiedBy"></person-tag>
                  </template>
              </el-table-column>
              <el-table-column prop="startDateTime" label="开始时间" width="170" show-overflow-tooltip />
              <el-table-column prop="endDateTime" label="结束时间" width="170" show-overflow-tooltip />
              <el-table-column label="操作" align="center">
                  <template #default="{ row }">
                      <el-dropdown trigger="click">
                          <el-icon class="vm-list__more">
                              <MoreFilled />
                          </el-icon>
                          <template #dropdown>
                              <el-dropdown-menu>
                                  <el-dropdown-item @click="showDagDetail(row)">DAG</el-dropdown-item>
                                  <el-dropdown-item @click="reRunWorkFlowDataEvent(row)">重跑</el-dropdown-item>
                                  <el-dropdown-item @click="deleteWorkflowSchedule(row)">删除</el-dropdown-item>
                              </el-dropdown-menu>
                          </template>
                      </el-dropdown>
                  </template>
              </el-table-column>
              <template #empty>
                  <empty-page></empty-page>
              </template>
          </el-table>
          <el-pagination
              class="vm-list__pagination"
              small
              layout="prev, pager, next"
              :total="total"
              @current-change="handleCurrentChange"
          />
      </div>
      <dag-detail ref="dagDetailRef"></dag-detail>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import VmStatus from './vm-status.vue'
import PersonTag from './person-tag.vue'
import { ComputeInstance, queryComputeInstances } from '../services/computer-group'
import DagDetail from '@/views/schedule/dag-detail/index.vue'
import { ReRunWorkflow } from '@/services/workflow.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DeleteWorkFlowScheduleLog } from '@/services/schedule.service'

const keyWord = ref('')

const tableData = ref<Array<ComputeInstance>>([])
const total = ref<number>(0)
const paginationInfo = ref<{
  page: number
  pageSize: number
}>({
  page: 0,
  pageSize: 5
})
const dagDetailRef = ref()
const timer = ref()
const tableLoading = ref<boolean>(false)

function queryVmlistData(loading?: boolean) {
  tableLoading.value = loading === true ? false : true
  queryComputeInstances({
      ...paginationInfo.value,
      searchKeyWord: keyWord.value || null
  }).then(({ data }) => {
      tableData.value = data.content
      total.value = data.totalElements
      tableLoading.value = false
  }).catch(() => {
      tableLoading.value = false
  })
}

function handleCurrentChange(page: number) {
  paginationInfo.value.page = page - 1

  queryVmlistData()
}

// 展示工作流对应流程图
function showDagDetail(data: any) {
  dagDetailRef.value.showModal(data)
}

// 重跑工作流
function reRunWorkFlowDataEvent(data: any) {
  ReRunWorkflow({
      workflowInstanceId: data.workflowInstanceId
  }).then((res: any) => {
      ElMessage.success(res.msg)
      queryVmlistData()
  }).catch(() => {})
}

// 删除作业流调度
function deleteWorkflowSchedule(data: any) {
  ElMessageBox.confirm('确定删除该实例吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
  }).then(() => {
      DeleteWorkFlowScheduleLog({
          workflowInstanceId: data.workflowInstanceId
      }).then((res: any) => {
          ElMessage.success(res.msg)
          queryVmlistData()
      }).catch(() => {})
  })
}

onMounted(() => {
  queryVmlistData()
  timer.value = setInterval(() => {
      queryVmlistData(true)
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
.vm-list {
  margin-bottom: 24px;

  .vm-list__header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 40px;
  }

  .vm-list__title {
      font-size: getCssVar('font-size', 'medium');
      font-weight: bold;
  }

  .vm-list__body {
      margin-top: 24px;
      border-radius: 8px;
      padding: 12px 36px;
      background-color: getCssVar('color', 'white');
      box-shadow: getCssVar('box-shadow', 'lighter');
  }

  .vm-list__ops {
      display: flex;
      align-items: center;
  }

  .vm-list__icon {
      margin-right: 12px;
      cursor: pointer;

      &:hover {
          color: getCssVar('color', 'primary');
      }

      &:last-child {
          margin-right: 0;
      }
  }

  .vm-list__search.el-input .el-input__wrapper {
      border-radius: 16px;
  }

  .vm-list__table {
      --el-table-header-text-color: #000;
      --el-table-text-color: #000;

      &.vm-list__table-empty {
          .el-table__body-wrapper {
              min-height: 132px;
              .el-scrollbar__wrap {
                  min-height: 132px;
              }
          }
      }

      .el-table__inner-wrapper::before {
          display: none;
      }

      &.el-table th.el-table__cell.is-leaf,
      .el-table td.el-table__cell {
          border: 0;
      }

      .el-table__row {
          height: 52px;
      }
  }

  .vm-list__more {
      transform: rotate(90deg);
      cursor: pointer;
      margin-top: 4px;

      &:hover {
          color: getCssVar('color', 'primary');
      }
  }

  .vm-list__empty {
      padding: 20px 0 0;
      --el-empty-image-width: 60px;

      .el-empty__description {
          margin-top: 0;
      }
  }

  .vm-list__pagination {
      display: flex;
      height: 52px;
      align-items: center;
      justify-content: flex-end;
  }
}
</style>