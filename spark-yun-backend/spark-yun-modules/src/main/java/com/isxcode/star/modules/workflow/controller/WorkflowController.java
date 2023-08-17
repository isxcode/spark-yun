package com.isxcode.star.modules.workflow.controller;

import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.api.workflow.pojos.req.*;
import com.isxcode.star.api.workflow.pojos.res.GetWorkflowRes;
import com.isxcode.star.api.workflow.pojos.res.WofQueryRunWorkInstancesRes;
import com.isxcode.star.api.workflow.pojos.res.PageWorkflowRes;
import com.isxcode.star.backend.api.base.constants.SecurityConstants;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.common.userlog.UserLog;
import com.isxcode.star.modules.workflow.service.WorkflowBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "作业流模块")
@RestController
@RequestMapping(ModuleCode.WORKFLOW)
@RequiredArgsConstructor
public class WorkflowController {

  private final WorkflowBizService workflowBizService;


  @Operation(summary = "创建作业流接口")
  @PostMapping("/addWorkflow")
  @SuccessResponse("创建成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void addWorkflow(@Valid @RequestBody AddWorkflowReq addWorkflowReq) {

    workflowBizService.addWorkflow(addWorkflowReq);
  }

  @Operation(summary = "更新作业流接口")
  @PostMapping("/updateWorkflow")
  @SuccessResponse("更新成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void updateWorkflow(@Valid @RequestBody UpdateWorkflowReq updateWorkflowReq) {

    workflowBizService.updateWorkflow(updateWorkflowReq);
  }

  @Operation(summary = "查询作业流接口")
  @PostMapping("/pageWorkflow")
  @SuccessResponse("查询成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public Page<PageWorkflowRes> pageWorkflow(@Valid @RequestBody PageWorkflowReq pageWorkflowReq) {

    return workflowBizService.pageWorkflow(pageWorkflowReq);
  }

  @Operation(summary = "删除作业流接口")
  @PostMapping("/deleteWorkflow")
  @SuccessResponse("删除成功")
 @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void deleteWorkflow(@Valid @RequestBody DeleteWorkflowReq deleteWorkflowReq) {

    workflowBizService.deleteWorkflow(deleteWorkflowReq);
  }

  @Operation(summary = "运行工作流接口")
  @GetMapping("/runWorkflow")
  @SuccessResponse("提交成功")
   @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public String runWorkflow(@Valid @RequestBody RunWorkflowReq runWorkflowReq) {

    return workflowBizService.runWorkflow(runWorkflowReq);
  }


  @Operation(summary = "查询作业流运行实例接口")
  @GetMapping("/queryRunWorkInstances")
  @SuccessResponse("查询成功")
 @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public WofQueryRunWorkInstancesRes queryRunWorkInstances(
      @Schema(description = "作业流实例唯一id", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
          @RequestParam
          String workflowInstanceId) {

    return workflowBizService.queryRunWorkInstances(workflowInstanceId);
  }

  @UserLog
  @Operation(summary = "获取作业流信息接口")
  @GetMapping("/getWorkflow")
  @SuccessResponse("获取成功")
   @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public GetWorkflowRes getWorkflow(
      @Schema(description = "作业流唯一id", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
          @RequestParam
          String workflowId) {

    return workflowBizService.getWorkflow(workflowId);
  }

  @Operation(summary = "作业流导出接口")
  @PostMapping("/exportWorks")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void exportWorks(
          @RequestBody ExportWorkflowReq wofExportWorkflowReq, HttpServletResponse response) {

    workflowBizService.exportWorks(wofExportWorkflowReq, response);
  }

  @Operation(summary = "作业流导入接口(Swagger有Bug不能使用)")
  @PostMapping("/importWorks")
  @SuccessResponse("导入成功")
   @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void importWorks(
      @RequestParam("workFile") MultipartFile workFile,
      @Schema(description = "作业流唯一id", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
          @RequestParam(required = false)
          String workflowId) {

    workflowBizService.importWorks(workFile, workflowId);
  }

  @Operation(summary = "中止工作流接口")
  @GetMapping("/abortFlow")
  @SuccessResponse("中止成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void abortFlow(
      @Schema(description = "作业流实例唯一id", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
          @RequestParam
          String workflowInstanceId) {

    workflowBizService.abortFlow(workflowInstanceId);
  }

  @Operation(summary = "中断工作流接口")
  @GetMapping("/breakFlow")
  @SuccessResponse("中断成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void breakFlow(
      @Schema(description = "作业实例唯一id", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
          @RequestParam
          String workInstanceId) {

    workflowBizService.breakFlow(workInstanceId);
  }

  @Operation(summary = "重跑工作流接口")
  @GetMapping("/reRunFlow")
  @SuccessResponse("重跑成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void reRunFlow(
      @Schema(description = "作业流实例唯一id", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
          @RequestParam
          String workflowInstanceId) {

    workflowBizService.reRunFlow(workflowInstanceId);
  }

  @Operation(summary = "重跑当前节点接口")
  @GetMapping("/runCurrentNode")
  @SuccessResponse("重跑成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void runCurrentNode(
      @Schema(description = "作业实例唯一id", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
          @RequestParam
          String workInstanceId) {

    workflowBizService.runCurrentNode(workInstanceId);
  }

  @Operation(summary = "重跑下游接口")
  @GetMapping("/runAfterFlow")
  @SuccessResponse("重跑成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void runAfterFlow(
      @Schema(description = "作业实例唯一id", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
          @RequestParam
          String workInstanceId) {

    workflowBizService.runAfterFlow(workInstanceId);
  }

  //  @Operation(summary = "配置作业流接口")
  //  @PostMapping("/configWorkflow")
  //  @SuccessResponse("保存成功")
  //  @Parameter(
  //      name = SecurityConstants.HEADER_TENANT_ID,
  //      description = "租户id",
  //      required = true,
  //      in = ParameterIn.HEADER,
  //      schema = @Schema(type = "string"))
  //  public void configWorkflow(@Valid @RequestBody WfcConfigWorkflowReq wfcConfigWorkflowReq) {
  //
  //    workflowConfigBizService.configWorkflow(wfcConfigWorkflowReq);
  //  }
  //
  //  @Operation(summary = "收藏工作流接口")
  //  @GetMapping("/favourWorkflow")
  //  @SuccessResponse("收藏成功")
  //  @Parameter(
  //      name = SecurityConstants.HEADER_TENANT_ID,
  //      description = "租户id",
  //      required = true,
  //      in = ParameterIn.HEADER,
  //      schema = @Schema(type = "string"))
  //  public void favourWorkflow(
  //      @Schema(description = "作业流唯一id", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
  //          @RequestParam
  //          String workflowId) {
  //
  //    workflowFavourBizService.favourWorkflow(workflowId);
  //  }
}
