<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table zqy-schedule">
    <div class="zqy-table-top">
      <div />
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
          :table-config="tableConfig"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        >
          <template #instanceTypeTag="scopeSlot">
            <div class="btn-group">
              <el-tag
                v-if="scopeSlot.row.instanceType === 'MANUAL'"
                class="ml-2"
                type="info"
              >
                手动执行
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.instanceType === 'WORK'"
                class="ml-2"
                type="info"
              >
                调度执行
              </el-tag>
            </div>
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
                已终止
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.status === 'RUNNING'"
                class="ml-2"
              >
                运行中
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
                      v-if="scopeSlot.row.status !== 'RUNNING'"
                      @click="showDetailModal(scopeSlot.row, 'yarnLog')"
                    >
                      运行日志
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="scopeSlot.row.status !== 'RUNNING'"
                      @click="retry(scopeSlot.row)"
                    >
                      重新运行
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="scopeSlot.row.status === 'SUCCESS'"
                      @click="showDetailModal(scopeSlot.row, 'result')"
                    >
                      结果
                    </el-dropdown-item>
                    <el-dropdown-item @click="deleteSchedule(scopeSlot.row)">
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
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import DetailModal from './detail-modal/index.vue'

import { BreadCrumbList, TableConfig } from './schedule.config'
import { GetScheduleList, DeleteScheduleLog, ReStartRunning } from '@/services/schedule.service'
import { ElMessage, ElMessageBox } from 'element-plus'

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const detailModalRef = ref(null)

function initData(tableLoading?: boolean) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
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

function inputEvent(e: string) {
  if (e === '') {
    initData()
  }
}

function handleSizeChange(e: number) {
  tableConfig.pagination.pageSize = e
  initData()
}

function handleCurrentChange(e: number) {
  tableConfig.pagination.currentPage = e
  initData()
}

onMounted(() => {
  initData()
})
</script>

<style lang="scss">
.zqy-seach-table {
  .click-show-more {
    font-size: $--app-small-font-size;
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
