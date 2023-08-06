package com.isxcode.star.modules.work.controller;

import com.isxcode.star.api.work.pojos.req.WocConfigWorkReq;
import com.isxcode.star.backend.api.base.constants.ModulePrefix;
import com.isxcode.star.backend.api.base.constants.SecurityConstants;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.work.service.WorkConfigBizService;
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

@Tag(name = "作业配置模块")
@RestController
@RequestMapping(ModulePrefix.WORK_CONFIG)
@RequiredArgsConstructor
public class WorkConfigController {

  private final WorkConfigBizService workConfigBizService;

  @Operation(summary = "配置作业接口")
  @PostMapping("/configWork")
  @SuccessResponse("保存成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void configWork(@RequestBody WocConfigWorkReq configWorkReq) {

    workConfigBizService.configWork(configWorkReq);
  }
}
