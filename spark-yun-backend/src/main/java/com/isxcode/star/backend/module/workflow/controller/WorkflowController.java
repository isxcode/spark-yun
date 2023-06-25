package com.isxcode.star.backend.module.workflow.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.workflow.req.WocQueryWorkflowReq;
import com.isxcode.star.api.pojos.workflow.req.WofAddWorkflowReq;
import com.isxcode.star.api.pojos.workflow.req.WofUpdateWorkflowReq;
import com.isxcode.star.api.pojos.workflow.res.WofQueryWorkflowRes;
import com.isxcode.star.backend.module.workflow.service.WorkflowBizService;
import com.isxcode.star.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "作业流模块")
@RestController
@RequestMapping(ModulePrefix.WORKFLOW)
@RequiredArgsConstructor
public class WorkflowController {

  private final WorkflowBizService workflowBizService;

  @Operation(summary = "创建作业流接口")
  @PostMapping("/addWorkflow")
  @SuccessResponse("创建成功")
  public void addWorkflow(@Valid @RequestBody WofAddWorkflowReq wofAddWorkflowReq) {

    workflowBizService.addWorkflow(wofAddWorkflowReq);
  }

  @Operation(summary = "更新作业流接口")
  @PostMapping("/updateWorkflow")
  @SuccessResponse("更新成功")
  public void updateWorkflow(@Valid @RequestBody WofUpdateWorkflowReq wofUpdateWorkflowReq) {

    workflowBizService.updateWorkflow(wofUpdateWorkflowReq);
  }

  @Operation(summary = "查询作业流接口")
  @PostMapping("/queryWorkflow")
  @SuccessResponse("查询成功")
  public Page<WofQueryWorkflowRes> queryWorkflow(@Valid @RequestBody WocQueryWorkflowReq wocQueryWorkflowReq) {

    return workflowBizService.queryWorkflow(wocQueryWorkflowReq);
  }

  @Operation(summary = "删除作业流接口")
  @GetMapping("/delWorkflow")
  @SuccessResponse("删除成功")
  public void delWorkflow(@Schema(description = "作业流唯一id",example = "sy_ba1f12b5c8154f999a02a5be2373a438") @RequestParam String workflowId) {

    workflowBizService.delWorkflow(workflowId);
  }
}
