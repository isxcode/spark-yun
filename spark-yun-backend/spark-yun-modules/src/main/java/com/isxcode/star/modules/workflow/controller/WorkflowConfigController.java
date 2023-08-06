package com.isxcode.star.modules.workflow.controller;

import com.isxcode.star.api.workflow.pojos.req.WfcConfigWorkflowReq;
import com.isxcode.star.backend.api.base.constants.ModulePrefix;
import com.isxcode.star.backend.api.base.constants.SecurityConstants;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.workflow.service.WorkflowConfigBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "作业流配置模块")
@RestController
@RequestMapping(ModulePrefix.WORKFLOW_CONFIG)
@RequiredArgsConstructor
public class WorkflowConfigController {

  private final WorkflowConfigBizService workflowConfigBizService;

  @Operation(summary = "配置作业流接口")
  @PostMapping("/configWorkflow")
  @SuccessResponse("保存成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void configWorkflow(@Valid @RequestBody WfcConfigWorkflowReq wfcConfigWorkflowReq) {

    workflowConfigBizService.configWorkflow(wfcConfigWorkflowReq);
  }
}
