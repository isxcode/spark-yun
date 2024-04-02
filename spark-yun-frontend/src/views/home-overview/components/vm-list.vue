<template>
  <div class="vm-list">
    <div class="vm-list__header">
      <span class="vm-list__title">实例列表</span>
      <div class="vm-list__ops">
        <el-icon class="vm-list__icon"><RefreshRight /></el-icon>
        <el-input class="vm-list__search" v-model="keyWord" placeholder="名称" />
      </div>
    </div>
    <div class="vm-list__body">
      <el-table class="vm-list__table" :data="tableData">
        <el-table-column prop="workflowName" label="名称" width="120" show-overflow-tooltip/>
        <el-table-column prop="status" label="状态" align="center">
          <template #default="{ row }">
            <vm-status :status="row.status"></vm-status>
          </template>
        </el-table-column>
        <el-table-column prop="lastModifiedBy" label="发布人">
          <template #default="{ row }">
            <person-tag :person-name="row.lastModifiedBy"></person-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDateTime" label="开始时间" show-overflow-tooltip />
        <el-table-column prop="endDateTime" label="结束时间" show-overflow-tooltip />
        <el-table-column label="操作" align="center">
          <template #default="{ row }">
            <el-icon class="vm-list__more"><MoreFilled /></el-icon>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="vm-list__pagination" small layout="prev, pager, next" :total="total" @current-change="handleCurrentChange"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import VmStatus from './vm-status.vue';
import PersonTag from './person-tag.vue';
import { ComputeInstance, queryComputeInstances } from '../services/computer-group';

const keyWord = ref('')

const tableData = ref<Array<ComputeInstance>>([])
const total = ref<number>(0)
const paginationInfo = ref<{
  page: number,
  pageSize: number
}>({
  page: 1,
  pageSize: 5
})

function queryVmlistData() {
  queryComputeInstances({
    ...paginationInfo.value,
    SearchKeyword: keyWord.value
  }).then(({ data }) => {
    tableData.value = data.content
    total.value = data.size
  })
}

function handleCurrentChange(page: number) {
  paginationInfo.value.page = page

  queryVmlistData()
}

onMounted(() => {
  queryVmlistData()
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

    .el-table__inner-wrapper::before {
      display: none;
    }

    &.el-table th.el-table__cell.is-leaf, .el-table td.el-table__cell {
      border: 0;
    }

    .el-table__row {
      height: 52px;
    }
  }

  .vm-list__more {
    transform: rotate(90deg);
    cursor: pointer;

    &:hover {
      color: getCssVar('color', 'primary');
    }
  }

  .vm-list__pagination {
    display: flex;
    justify-content: flex-end;
  }
}
</style>