<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table">
    <div class="zqy-table-top">
      <el-button type="primary" @click="addData"> 添加数据源 </el-button>
      <div class="zqy-seach">
        <el-input v-model="keyword" placeholder="请输入名称/类型/连接信息/用户名/备注 回车进行搜索" :maxlength="200" clearable @input="inputEvent" @keyup.enter="initData(false)" />
      </div>
    </div>
    <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
      <div class="zqy-table">
        <BlockTable :table-config="tableConfig" @size-change="handleSizeChange" @current-change="handleCurrentChange">
          <template #statusTag="scopeSlot">
            <div class="btn-group">
              <el-tag v-if="scopeSlot.row.status === 'ACTIVE'" class="ml-2" type="success"> 可用 </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'NO_ACTIVE'" class="ml-2" type="danger"> 不可用 </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'NEW'" type="info"> 待配置 </el-tag>
              <el-tag v-if="scopeSlot.row.status === 'UN_CHECK'"> 待检测 </el-tag>
            </div>
          </template>
          <template #options="scopeSlot">
            <div class="btn-group">
              <span @click="editData(scopeSlot.row)">编辑</span>
              <span v-if="!scopeSlot.row.checkLoading" @click="checkData(scopeSlot.row)">检测</span>
              <el-icon v-else class="is-loading">
                <Loading />
              </el-icon>
              <span @click="deleteData(scopeSlot.row)">删除</span>
            </div>
          </template>
        </BlockTable>
      </div>
    </LoadingPage>
    <AddModal ref="addModalRef" />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from "vue";
import Breadcrumb from "@/layout/bread-crumb/index.vue";
import BlockTable from "@/components/block-table/index.vue";
import LoadingPage from "@/components/loading/index.vue";
import AddModal from "./add-modal/index.vue";

import { BreadCrumbList, TableConfig, FormData } from "./datasource.config";
import { GetDatasourceList, AddDatasourceData, UpdateDatasourceData, CheckDatasourceData, DeleteDatasourceData } from "@/services/datasource.service";
import { ElMessage, ElMessageBox } from "element-plus";

const keyword = ref("");
const loading = ref(false);
const networkError = ref(false);
const addModalRef = ref(null);
const breadCrumbList = reactive(BreadCrumbList);
const tableConfig: any = reactive(TableConfig);

function initData(tableLoading?: boolean) {
  loading.value = tableLoading ? false : true;
  networkError.value = networkError.value || false;
  GetDatasourceList({
    page: tableConfig.pagination.currentPage - 1,
    pageSize: tableConfig.pagination.pageSize,
    searchKeyWord: keyword.value,
  })
    .then((res: any) => {
      tableConfig.tableData = res.data.content;
      tableConfig.pagination.total = res.data.totalElements;
      loading.value = false;
      tableConfig.loading = false;
      networkError.value = false;
    })
    .catch(() => {
      tableConfig.tableData = [];
      tableConfig.pagination.total = 0;
      loading.value = false;
      tableConfig.loading = false;
      networkError.value = true;
    });
}

function addData() {
  addModalRef.value.showModal((formData: FormData) => {
    return new Promise((resolve: any, reject: any) => {
      AddDatasourceData(formData)
        .then((res: any) => {
          ElMessage.success(res.msg);
          initData();
          resolve();
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  });
}

function editData(data: any) {
  addModalRef.value.showModal((formData: FormData) => {
    return new Promise((resolve: any, reject: any) => {
      UpdateDatasourceData(formData)
        .then((res: any) => {
          ElMessage.success(res.msg);
          initData();
          resolve();
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }, data);
}

// 检测
function checkData(data: any) {
  data.checkLoading = true;
  CheckDatasourceData({
    datasourceId: data.id,
  })
    .then((res: any) => {
      data.checkLoading = false;
      ElMessage.success(res.msg);
      initData(true);
    })
    .catch(() => {
      data.checkLoading = false;
    });
}

// 删除
function deleteData(data: any) {
  ElMessageBox.confirm("确定删除该数据源吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    DeleteDatasourceData({
      datasourceId: data.id,
    })
      .then((res: any) => {
        ElMessage.success(res.msg);
        initData();
      })
      .catch(() => {});
  });
}

function inputEvent(e: string) {
  if (e === "") {
    initData();
  }
}

function handleSizeChange(e: number) {
  tableConfig.pagination.pageSize = e;
  initData(true);
}

function handleCurrentChange(e: number) {
  tableConfig.pagination.currentPage = e;
  initData(true);
}

onMounted(() => {
  initData();
});
</script>
