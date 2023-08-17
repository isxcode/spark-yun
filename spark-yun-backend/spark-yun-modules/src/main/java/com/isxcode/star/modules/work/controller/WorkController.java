package com.isxcode.star.modules.work.controller;

import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.api.work.pojos.req.*;
import com.isxcode.star.api.work.pojos.res.*;
import com.isxcode.star.backend.api.base.constants.SecurityConstants;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.work.service.WorkBizService;
import com.isxcode.star.modules.work.service.WorkConfigBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "作业模块")
@RestController
@RequestMapping(ModuleCode.WORK)
@RequiredArgsConstructor
public class WorkController {

  private final WorkBizService workBizService;

  private final WorkConfigBizService workConfigBizService;

  @Operation(summary = "添加作业接口")
  @PostMapping("/addWork")
  @SuccessResponse("创建成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void addWork(@Valid @RequestBody AddWorkReq addWorkReq) {

    workBizService.addWork(addWorkReq);
  }

  @Operation(summary = "更新作业接口")
  @PostMapping("/updateWork")
  @SuccessResponse("更新成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void updateWork(@Valid @RequestBody UpdateWorkReq updateWorkReq) {

    workBizService.updateWork(updateWorkReq);
  }

  @Operation(summary = "运行作业接口")
  @PostMapping("/runWork")
  @SuccessResponse("提交成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public RunWorkRes runWork(@Valid @RequestBody RunWorkReq runWorkReq) {

    return workBizService.runWork(runWorkReq);
  }

  @Operation(summary = "查询作业运行日志接口")
  @PostMapping("/getYarnLog")
  @SuccessResponse("查询成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public GetWorkLogRes getYarnLog(@Valid @RequestBody GetYarnLogReq getYarnLogReq) {

    return workBizService.getWorkLog(getYarnLogReq);
  }

  @Operation(summary = "查询作业返回数据接口")
  @PostMapping("/getData")
  @SuccessResponse("查询成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public GetDataRes getData(@Valid @RequestBody GetDataReq getDataReq) {

    return workBizService.getData(getDataReq);
  }

  @Operation(summary = "中止作业接口")
  @PostMapping("/stopJob")
  @SuccessResponse("中止成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void stopJob(@Valid @RequestBody StopJobReq stopJobReq) {

    workBizService.stopJob(stopJobReq);
  }

  @Operation(summary = "获取作业当前运行状态接口")
  @PostMapping("/getStatus")
  @SuccessResponse("获取成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public GetStatusRes getStatus(@Valid @RequestBody GetStatusReq getStatusReq) {

    return workBizService.getStatus(getStatusReq);
  }

  @Operation(summary = "删除作业接口")
  @PostMapping("/deleteWork")
  @SuccessResponse("删除成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void deleteWork(@Valid @RequestBody DeleteWorkReq deleteWorkReq) {

    workBizService.deleteWork(deleteWorkReq);
  }

  @Operation(summary = "查询作业列表接口")
  @PostMapping("/pageWork")
  @SuccessResponse("查询成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public Page<PageWorkRes> pageWork(@Valid @RequestBody PageWorkReq pageWorkReq) {

    return workBizService.pageWork(pageWorkReq);
  }

  @Operation(summary = "获取作业信息接口")
  @PostMapping("/getWork")
  @SuccessResponse("获取成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public GetWorkRes getWork(@Valid @RequestBody GetWorkReq getWorkReq) {

    return workBizService.getWork(getWorkReq);
  }

  @Operation(summary = "查询作业提交日志接口")
  @PostMapping("/getSubmitLog")
  @SuccessResponse("查询成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public GetSubmitLogRes getSubmitLog(@Valid @RequestBody GetSubmitLogReq getSubmitLogReq) {

    return workBizService.getSubmitLog(getSubmitLogReq);
  }

  @Operation(summary = "作业重命名接口")
  @PostMapping("/renameWork")
  @SuccessResponse("修改成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void renameWork(@Valid @RequestBody RenameWorkReq renameWorkReq) {

    workBizService.renameWork(renameWorkReq);
  }

  @Operation(summary = "作业复制接口")
  @PostMapping("/copyWork")
  @SuccessResponse("复制成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void copyWork(@Valid @RequestBody CopyWorkReq copyWorkReq) {

    workBizService.copyWork(copyWorkReq);
  }

  @Operation(summary = "作业置顶接口")
  @PostMapping("/topWork")
  @SuccessResponse("置顶成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void topWork(@Valid @RequestBody TopWorkReq topWorkReq) {

    workBizService.topWork(topWorkReq);
  }

  @Operation(summary = "配置作业接口")
  @PostMapping("/configWork")
  @SuccessResponse("保存成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void configWork(@Valid @RequestBody ConfigWorkReq configWorkReq) {

    workConfigBizService.configWork(configWorkReq);
  }
}
