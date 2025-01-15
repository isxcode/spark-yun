package com.isxcode.star.modules.work.controller;

import com.isxcode.star.api.instance.req.*;
import com.isxcode.star.api.instance.res.GetWorkInstanceValuePathRes;
import com.isxcode.star.api.instance.res.GetWorkflowInstanceRes;
import com.isxcode.star.api.instance.res.QueryInstanceRes;
import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.api.work.req.*;
import com.isxcode.star.api.work.res.*;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.work.service.ExcelSyncService;
import com.isxcode.star.modules.work.service.biz.SyncWorkBizService;
import com.isxcode.star.modules.work.service.biz.WorkBizService;
import com.isxcode.star.modules.work.service.biz.WorkConfigBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "作业模块")
@RequestMapping(ModuleCode.WORK)
@RestController
@RequiredArgsConstructor
public class WorkController {

    private final WorkBizService workBizService;

    private final WorkConfigBizService workConfigBizService;

    private final SyncWorkBizService syncWorkBizService;

    private final ExcelSyncService excelSyncService;

    @Operation(summary = "添加作业接口")
    @PostMapping("/addWork")
    @SuccessResponse("创建成功")
    public GetWorkRes addWork(@Valid @RequestBody AddWorkReq addWorkReq) {

        return workBizService.addWork(addWorkReq);
    }

    @Operation(summary = "更新作业接口")
    @PostMapping("/updateWork")
    @SuccessResponse("更新成功")
    public void updateWork(@Valid @RequestBody UpdateWorkReq updateWorkReq) {

        workBizService.updateWork(updateWorkReq);
    }

    @Operation(summary = "运行作业接口")
    @PostMapping("/runWork")
    @SuccessResponse("提交成功")
    public RunWorkRes runWork(@Valid @RequestBody RunWorkReq runWorkReq) {

        return workBizService.runWork(runWorkReq);
    }

    @Operation(summary = "查询作业运行日志接口")
    @PostMapping("/getYarnLog")
    @SuccessResponse("查询成功")
    public GetWorkLogRes getYarnLog(@Valid @RequestBody GetYarnLogReq getYarnLogReq) {

        return workBizService.getWorkLog(getYarnLogReq);
    }

    @Operation(summary = "查询作业返回数据接口")
    @PostMapping("/getData")
    @SuccessResponse("查询成功")
    public GetDataRes getData(@Valid @RequestBody GetDataReq getDataReq) {

        return workBizService.getData(getDataReq);
    }

    @Operation(summary = "中止作业接口")
    @PostMapping("/stopJob")
    @SuccessResponse("中止成功")
    public void stopJob(@Valid @RequestBody StopJobReq stopJobReq) {

        workBizService.stopJob(stopJobReq);
    }

    @Operation(summary = "获取作业当前运行状态接口")
    @PostMapping("/getStatus")
    @SuccessResponse("获取成功")
    public GetStatusRes getStatus(@Valid @RequestBody GetStatusReq getStatusReq) {

        return workBizService.getStatus(getStatusReq);
    }

    @Secured({RoleType.TENANT_ADMIN})
    @Operation(summary = "删除作业接口")
    @PostMapping("/deleteWork")
    @SuccessResponse("删除成功")
    public void deleteWork(@Valid @RequestBody DeleteWorkReq deleteWorkReq) {

        workBizService.deleteWork(deleteWorkReq);
    }

    @Operation(summary = "查询作业列表接口")
    @PostMapping("/pageWork")
    @SuccessResponse("查询成功")
    public Page<PageWorkRes> pageWork(@Valid @RequestBody PageWorkReq pageWorkReq) {

        return workBizService.pageWork(pageWorkReq);
    }

    @Operation(summary = "获取作业信息接口")
    @PostMapping("/getWork")
    @SuccessResponse("获取成功")
    public GetWorkRes getWork(@Valid @RequestBody GetWorkReq getWorkReq) {

        return workBizService.getWork(getWorkReq);
    }

    @Operation(summary = "配置作业接口")
    @PostMapping("/configWork")
    @SuccessResponse("保存成功")
    public void configWork(@Valid @RequestBody ConfigWorkReq configWorkReq) {

        workConfigBizService.configWork(configWorkReq);
    }

    @Operation(summary = "查询作业提交日志接口")
    @PostMapping("/getSubmitLog")
    @SuccessResponse("查询成功")
    public GetSubmitLogRes getSubmitLog(@Valid @RequestBody GetSubmitLogReq getSubmitLogReq) {

        return workBizService.getSubmitLog(getSubmitLogReq);
    }

    @Operation(summary = "作业重命名接口")
    @PostMapping("/renameWork")
    @SuccessResponse("修改成功")
    public void renameWork(@Valid @RequestBody RenameWorkReq renameWorkReq) {

        workBizService.renameWork(renameWorkReq);
    }

    @Operation(summary = "作业复制接口")
    @PostMapping("/copyWork")
    @SuccessResponse("复制成功")
    public void copyWork(@Valid @RequestBody CopyWorkReq copyWorkReq) {

        workBizService.copyWork(copyWorkReq);
    }

    @Operation(summary = "作业置顶接口")
    @PostMapping("/topWork")
    @SuccessResponse("置顶成功")
    public void topWork(@Valid @RequestBody TopWorkReq topWorkReq) {

        workBizService.topWork(topWorkReq);
    }

    @Operation(summary = "获取数据源表信息接口")
    @PostMapping("/getDataSourceTables")
    @SuccessResponse("查询成功")
    public GetDataSourceTablesRes getDataSourceTables(@Valid @RequestBody GetDataSourceTablesReq getDataSourceTablesReq)
        throws Exception {

        return syncWorkBizService.getDataSourceTables(getDataSourceTablesReq);
    }

    @Operation(summary = "获取数据表字段信息接口")
    @PostMapping("/getDataSourceColumns")
    @SuccessResponse("查询成功")
    public GetDataSourceColumnsRes getDataSourceColumns(
        @Valid @RequestBody GetDataSourceColumnsReq getDataSourceColumnsReq) throws Exception {

        return syncWorkBizService.getDataSourceColumns(getDataSourceColumnsReq);
    }

    @Operation(summary = "数据预览接口")
    @PostMapping("/getDataSourceData")
    @SuccessResponse("查询成功")
    public GetDataSourceDataRes getDataSourceData(@Valid @RequestBody GetDataSourceDataReq getDataSourceDataReq)
        throws Exception {

        return syncWorkBizService.getDataSourceData(getDataSourceDataReq);
    }

    @Operation(summary = "DDL预生成接口")
    @PostMapping("/getCreateTableSql")
    @SuccessResponse("查询成功")
    public GetCreateTableSqlRes getCreateTableSql(@Valid @RequestBody GetCreateTableSqlReq getCreateTableSqlReq)
        throws Exception {

        return syncWorkBizService.getCreateTableSql(getCreateTableSqlReq);
    }

    @Operation(summary = "获取Excel文件的字段信息接口")
    @PostMapping("/getExcelColumns")
    @SuccessResponse("查询成功")
    public GetExcelColumnsRes getExcelColumns(@Valid @RequestBody GetExcelColumnsReq getExcelColumnsReq) {

        return excelSyncService.getExcelColumns(getExcelColumnsReq);
    }

    @Operation(summary = "excel数据预览接口")
    @PostMapping("/getExcelData")
    @SuccessResponse("查询成功")
    public GetExcelDataRes getExcelData(@Valid @RequestBody GetExcelDataReq getExcelDataReq) {

        return excelSyncService.getExcelData(getExcelDataReq);
    }

    @Operation(summary = "预览excel文件名接口")
    @PostMapping("/parseExcelName")
    @SuccessResponse("查询成功")
    public ParseExcelNameRes parseExcelName(@Valid @RequestBody ParseExcelNameReq parseExcelNameReq) {

        return excelSyncService.parseExcelName(parseExcelNameReq);
    }

    @Operation(summary = "查看作业实例接口")
    @PostMapping("/queryInstance")
    @SuccessResponse("查询成功")
    public Page<QueryInstanceRes> queryInstance(@Valid @RequestBody QueryInstanceReq woiQueryInstanceReq) {

        return workBizService.queryInstance(woiQueryInstanceReq);
    }

    @Operation(summary = "查询单个作业流实例信息接口")
    @PostMapping("/getWorkflowInstance")
    @SuccessResponse("查询成功")
    public GetWorkflowInstanceRes getWorkflowInstance(
        @Valid @RequestBody GetWorkflowInstanceReq wfiGetWorkflowInstanceReq) {

        return workBizService.getWorkflowInstance(wfiGetWorkflowInstanceReq);
    }

    @Operation(summary = "获取作业返回的jsonPath接口")
    @PostMapping("/getWorkInstanceJsonPath")
    @SuccessResponse("查询成功")
    public List<GetWorkInstanceValuePathRes> getWorkInstanceJsonPath(
        @Valid @RequestBody GetWorkInstanceJsonPathReq getWorkInstanceJsonPathReq) {

        return workBizService.getWorkInstanceJsonPath(getWorkInstanceJsonPathReq);
    }

    @Operation(summary = "获取作业返回的正则解析结果接口")
    @PostMapping("/getWorkInstanceRegexPath")
    @SuccessResponse("查询成功")
    public GetWorkInstanceValuePathRes getWorkInstanceRegexPath(
        @Valid @RequestBody GetWorkInstanceRegexPathReq getWorkInstanceJsonPathReq) {

        return workBizService.getWorkInstanceRegexPath(getWorkInstanceJsonPathReq);
    }

    @Operation(summary = "获取作业返回的几行几列结果接口")
    @PostMapping("/getWorkInstanceTablePath")
    @SuccessResponse("查询成功")
    public GetWorkInstanceValuePathRes getWorkInstanceTablePath(
        @Valid @RequestBody GetWorkInstanceTablePathReq getWorkInstanceTablePathReq) {

        return workBizService.getWorkInstanceTablePath(getWorkInstanceTablePathReq);
    }

}
