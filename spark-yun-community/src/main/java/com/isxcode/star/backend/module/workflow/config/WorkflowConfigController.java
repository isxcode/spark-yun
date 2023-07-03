package com.isxcode.star.backend.module.workflow.config;

import com.isxcode.star.api.annotations.SuccessResponse;
import com.isxcode.star.api.constants.base.ModulePrefix;
import com.isxcode.star.api.constants.base.SecurityConstants;
import com.isxcode.star.api.pojos.work.config.req.WocConfigWorkReq;
import com.isxcode.star.api.pojos.workflow.config.req.WfcConfigWorkflowReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
