<template>
  <vxe-table class="block-table" :row-config="{ isHover: true }" :data="tableConfig.tableData" :loading="tableConfig.loading">
    <vxe-column :type="tableConfig.seqType" align="center" width="44" fixed="left" />
    <template v-for="(colConfig, colIndex) in tableConfig.colConfigs">
      <vxe-column v-if="colConfig.customSlot" :key="colConfig.prop" :width="colConfig.width" :field="colConfig.prop" :show-overflow="colConfig.showOverflowTooltip || false" v-bind="colConfig">
        <template #default="{ row, rowIndex, column }">
          <slot :name="colConfig.customSlot" :row="row" :index="rowIndex" :column="columnSlotAdapter(column, colConfig)" :colIndex="colIndex" v-bind="colConfig" />
        </template>
      </vxe-column>
      <vxe-column v-else :key="colConfig.prop" :show-header-overflow="colConfig.showHeaderOverflow || false" :width="colConfig.width" :field="colConfig.prop" :show-overflow="colConfig.showOverflowTooltip || false" v-bind="colConfig" />
    </template>
    <template #empty>
      <!-- 空页面 -->
      <EmptyPage />
    </template>
  </vxe-table>
  <el-pagination v-if="tableConfig.pagination" class="pagination" popper-class="pagination-popper" background layout="prev, pager, next, sizes, total, jumper" :hide-on-single-page="false" :total="tableConfig.pagination.total" :page-size="tableConfig.pagination.pageSize" :current-page="tableConfig.pagination.currentPage" :page-sizes="[10, 20]" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
</template>

<script lang="ts" setup>
import { defineProps, defineEmits } from "vue";
import EmptyPage from "@/components/empty-page/index.vue";

interface Pagination {
  currentPage: number;
  pageSize: number;
  total: number;
}

interface colConfig {
  prop: string;
  title: string;
  align?: string;
  showOverflowTooltip?: boolean;
  customSlot?: string;
  minWidth?: number;
  width?: number;
  formatter?: () => string;
}

interface TableConfig {
  tableData: Array<any>;
  colConfigs: Array<colConfig>;
  seqType: string;
  pagination?: Pagination; // 分页数据
  loading?: boolean; // 表格loading
}

defineProps<{
  tableConfig: TableConfig;
}>();

const emit = defineEmits(["size-change", "current-change"]);

const handleSizeChange = (e: number) => {
  emit("size-change", e);
};
const handleCurrentChange = (e: number) => {
  emit("current-change", e);
};

function columnSlotAdapter(column: any, colConfig: any) {
  return {
    property: column.property,
    title: colConfig.title,
    realWidth: column.renderWidth,
  };
}
</script>

<style lang="scss">
.block-table {
  .vxe-table--header tr.vxe-header--row > th {
    height: $--app-item-height;
    padding: 0;
    background-color: #fff;
  }
  .vxe-table--body tr > td.vxe-body--column {
    height: 40px;
    padding: 0;
    .vxe-cell {
      font-size: $--app-small-font-size;
    }
  }
  .vxe-table--empty-content {
    height: 132px;
  }
}
.pagination {
  display: flex;
  padding: 20px 0;
  margin-right: 0;
  justify-content: flex-end;

  &.el-pagination.is-background .btn-prev,
  &.el-pagination.is-background .btn-next,
  &.el-pagination.is-background .el-pager li {
    background: #fff;
    border: $--app-border-color solid 1px;
    &.active {
      background-color: $--app-primary-color;
    }
  }

  &.el-pagination.is-background .el-pager li.active:not(.disabled):hover {
    color: #fff !important;
  }
}
.pagination-popper.el-select-dropdown {
  z-index: 6000 !important;
  min-width: 100px !important;

  .el-select-dropdown__item {
    text-align: center;
  }
}
</style>
