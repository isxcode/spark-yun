<!--
 * @Author: fanciNate
 * @Date: 2023-05-23 07:25:46
 * @LastEditTime: 2023-05-27 14:40:14
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/views/workflow/work-item/return-data.vue
-->
<template>
  <BlockTable class="return-data" :table-config="tableConfig" />
</template>

<script lang="ts" setup>
import { reactive } from "vue";
import BlockTable from "@/components/block-table/index.vue";
import { GetResultData } from "@/services/schedule.service";

const tableConfig = reactive({
  tableData: [],
  colConfigs: [],
  seqType: "seq",
  loading: false,
});

function initData(id: string): void {
  getResultDatalist(id);
}

// 获取结果
function getResultDatalist(id: string) {
  if (!id) {
    tableConfig.colConfigs = [];
    tableConfig.tableData = [];
    tableConfig.loading = false;
    return;
  }
  tableConfig.loading = true;
  GetResultData({
    instanceId: id,
  })
    .then((res: any) => {
      const col = res.data.data.slice(0, 1)[0];
      const tableData = res.data.data.slice(1, res.data.data.length);
      tableConfig.colConfigs = col.map((colunm: any) => {
        return {
          prop: colunm,
          title: colunm,
          minWidth: 100,
          showHeaderOverflow: true,
          showOverflowTooltip: true,
        };
      });
      tableConfig.tableData = tableData.map((columnData: any) => {
        const dataObj: any = {};
        col.forEach((c: any, index: number) => {
          dataObj[c] = columnData[index];
        });
        return dataObj;
      });
      tableConfig.loading = false;
    })
    .catch(() => {
      tableConfig.colConfigs = [];
      tableConfig.tableData = [];
      tableConfig.loading = false;
    });
}

defineExpose({
  initData,
});
</script>

<style lang="scss">
.return-data {
  .vxe-table--body-wrapper {
    max-height: calc(100vh - 468px);
    overflow: auto;
  }
}
</style>
