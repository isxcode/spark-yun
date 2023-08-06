package com.isxcode.star.modules.workflow.controller;

import com.isxcode.star.backend.api.base.constants.ModulePrefix;
import com.isxcode.star.backend.api.base.constants.SecurityConstants;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.workflow.service.WorkflowFavourBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "作业流收藏模块")
@RestController
@RequestMapping(ModulePrefix.WORKFLOW_FAVOUR)
@RequiredArgsConstructor
public class WorkflowFavourController {

  private final WorkflowFavourBizService workflowFavourBizService;

  @Operation(summary = "收藏工作流接口")
  @GetMapping("/favourWorkflow")
  @SuccessResponse("收藏成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void favourWorkflow(
      @Schema(description = "作业流唯一id", example = "sy_ba1f12b5c8154f999a02a5be2373a438")
          @RequestParam
          String workflowId) {

    workflowFavourBizService.favourWorkflow(workflowId);
  }
}
