package com.isxcode.star.modules.workflow.controller;

import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.api.workflow.pojos.req.*;
import com.isxcode.star.api.workflow.pojos.res.GetRunWorkInstancesRes;
import com.isxcode.star.api.workflow.pojos.res.GetWorkflowRes;
import com.isxcode.star.api.workflow.pojos.res.PageWorkflowRes;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.workflow.service.WorkflowBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
  public void addWorkflow(@Valid @RequestBody AddWorkflowReq addWorkflowReq) {

    workflowBizService.addWorkflow(addWorkflowReq);
  }

  @Operation(summary = "更新作业流接口")
  @PostMapping("/updateWorkflow")
  @SuccessResponse("更新成功")
  public void updateWorkflow(@Valid @RequestBody UpdateWorkflowReq updateWorkflowReq) {

    workflowBizService.updateWorkflow(updateWorkflowReq);
  }

  @Operation(summary = "查询作业流接口")
  @PostMapping("/pageWorkflow")
  @SuccessResponse("查询成功")
  public Page<PageWorkflowRes> pageWorkflow(@Valid @RequestBody PageWorkflowReq pageWorkflowReq) {

    return workflowBizService.pageWorkflow(pageWorkflowReq);
  }

  @Operation(summary = "删除作业流接口")
  @PostMapping("/deleteWorkflow")
  @SuccessResponse("删除成功")
  public void deleteWorkflow(@Valid @RequestBody DeleteWorkflowReq deleteWorkflowReq) {

    workflowBizService.deleteWorkflow(deleteWorkflowReq);
  }

  @Operation(summary = "运行工作流接口")
  @PostMapping("/runWorkflow")
  @SuccessResponse("提交成功")
  public String runWorkflow(@Valid @RequestBody RunWorkflowReq runWorkflowReq) {

    return workflowBizService.runWorkflow(runWorkflowReq);
  }

  @Operation(summary = "获取作业流信息接口")
  @PostMapping("/getWorkflow")
  @SuccessResponse("获取成功")
  public GetWorkflowRes getWorkflow(@Valid @RequestBody GetWorkflowReq getWorkflowReq) {

    return workflowBizService.getWorkflow(getWorkflowReq);
  }

  @Operation(summary = "作业流导出接口")
  @PostMapping("/exportWorkflow")
  public void exportWorkflow(
      @Valid @RequestBody ExportWorkflowReq exportWorkflowReq, HttpServletResponse response) {

    workflowBizService.exportWorkflow(exportWorkflowReq, response);
  }

  @Operation(summary = "作业流导入接口(Swagger有Bug不能使用)")
  @PostMapping("/importWorkflow")
  @SuccessResponse("导入成功")
  public void importWorkflow(
      @RequestParam("workflowConfigFile") MultipartFile workflowConfigFile,
      @Schema(description = "作业流唯一id", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
          @RequestParam(required = false)
          String workflowId) {

    workflowBizService.importWorkflow(workflowConfigFile, workflowId);
  }

  @Operation(summary = "中止工作流接口")
  @PostMapping("/abortFlow")
  @SuccessResponse("中止成功")
  public void abortFlow(@Valid @RequestBody AbortFlowReq abortFlowReq) {
    workflowBizService.abortFlow(abortFlowReq);
  }

  @Operation(summary = "中断工作流接口")
  @PostMapping("/breakFlow")
  @SuccessResponse("中断成功")
  public void breakFlow(@Valid @RequestBody BreakFlowReq breakFlowReq) {

    workflowBizService.breakFlow(breakFlowReq);
  }

  @Operation(summary = "重跑工作流接口")
  @PostMapping("/reRunFlow")
  @SuccessResponse("重跑成功")
  public void reRunFlow(@Valid @RequestBody ReRunFlowReq reRunFlowReq) {

    workflowBizService.reRunFlow(reRunFlowReq);
  }

  @Operation(summary = "重跑当前节点接口")
  @PostMapping("/runCurrentNode")
  @SuccessResponse("重跑成功")
  public void runCurrentNode(@Valid @RequestBody RunCurrentNodeReq runCurrentNodeReq) {

    workflowBizService.runCurrentNode(runCurrentNodeReq);
  }

  @Operation(summary = "重跑下游接口")
  @PostMapping("/runAfterFlow")
  @SuccessResponse("重跑成功")
  public void runAfterFlow(@Valid @RequestBody RunAfterFlowReq runAfterFlowReq) {

    workflowBizService.runAfterFlow(runAfterFlowReq);
  }

  @Operation(summary = "查询作业流运行实例接口")
  @PostMapping("/getRunWorkInstances")
  @SuccessResponse("查询成功")
  public GetRunWorkInstancesRes getRunWorkInstances(
      @Valid @RequestBody GetRunWorkInstancesReq getRunWorkInstancesReq) {

    return workflowBizService.getRunWorkInstances(getRunWorkInstancesReq);
  }

  //  @Operation(summary = "配置作业流接口")
  //  @PostMapping("/configWorkflow")
  //  @SuccessResponse("保存成功")
  //  public void configWorkflow(@Valid @RequestBody WfcConfigWorkflowReq wfcConfigWorkflowReq) {
  //
  //    workflowConfigBizService.configWorkflow(wfcConfigWorkflowReq);
  //  }
  //
  //  @Operation(summary = "收藏工作流接口")
  //  @GetMapping("/favourWorkflow")
  //  @SuccessResponse("收藏成功")
  //  public void favourWorkflow(
  //    @Schema(description = "作业流唯一id", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
  //    @RequestParam
  //    String workflowId) {
  //
  //    workflowFavourBizService.favourWorkflow(workflowId);
  //  }
}
