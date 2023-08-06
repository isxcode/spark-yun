package com.isxcode.star.modules.datasource.controller;

import com.isxcode.star.api.datasource.pojos.req.DasAddDatasourceReq;
import com.isxcode.star.api.datasource.pojos.req.DasQueryDatasourceReq;
import com.isxcode.star.api.datasource.pojos.req.DasUpdateDatasourceReq;
import com.isxcode.star.api.datasource.pojos.res.DasGetConnectLogRes;
import com.isxcode.star.api.datasource.pojos.res.DasQueryDatasourceRes;
import com.isxcode.star.api.datasource.pojos.res.DasTestConnectRes;
import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.backend.api.base.constants.ModulePrefix;
import com.isxcode.star.backend.api.base.constants.SecurityConstants;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.datasource.service.DatasourceBizService;
import com.isxcode.star.modules.userlog.UserLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "数据源模块")
@RestController
@RequestMapping(ModulePrefix.DATASOURCE)
@RequiredArgsConstructor
public class DatasourceController {

  private final DatasourceBizService datasourceBizService;

  @UserLog
  @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
  @Operation(summary = "添加数据源接口")
  @PostMapping("/addDatasource")
  @SuccessResponse("添加成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void addDatasource(@Valid @RequestBody DasAddDatasourceReq dasAddDatasourceReq) {

    datasourceBizService.addDatasource(dasAddDatasourceReq);
  }

  @UserLog
  @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
  @Operation(summary = "更新数据源接口")
  @PostMapping("/updateDatasource")
  @SuccessResponse("更新成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void updateDatasource(@Valid @RequestBody DasUpdateDatasourceReq dasAddDatasourceReq) {

    datasourceBizService.updateDatasource(dasAddDatasourceReq);
  }

  @UserLog
  @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
  @Operation(summary = "查询数据源列表接口")
  @PostMapping("/queryDatasource")
  @SuccessResponse("查询数据源成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public Page<DasQueryDatasourceRes> queryDatasource(
      @Valid @RequestBody DasQueryDatasourceReq dasQueryDatasourceReq) {

    return datasourceBizService.queryDatasource(dasQueryDatasourceReq);
  }

  @UserLog
  @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
  @Operation(summary = "删除数据源接口")
  @GetMapping("/delDatasource")
  @SuccessResponse("删除成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public void delDatasource(
      @Schema(description = "数据源唯一id", example = "sy_344c3d583fa344f7a2403b19c5a654dc")
          @RequestParam
          String datasourceId) {

    datasourceBizService.delDatasource(datasourceId);
  }

  @UserLog
  @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
  @Operation(summary = "测试数据源连接接口")
  @GetMapping("/testConnect")
  @SuccessResponse("检测完成")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public DasTestConnectRes testConnect(
      @Schema(description = "数据源唯一id", example = "sy_344c3d583fa344f7a2403b19c5a654dc")
          @RequestParam
          String datasourceId) {

    return datasourceBizService.testConnect(datasourceId);
  }

  @UserLog
  @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
  @Operation(summary = "查询连接日志")
  @GetMapping("/getConnectLog")
  @SuccessResponse("获取成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string"))
  public DasGetConnectLogRes getConnectLog(
      @Schema(description = "数据源唯一id", example = "sy_344c3d583fa344f7a2403b19c5a654dc")
          @RequestParam
          String datasourceId) {

    return datasourceBizService.getConnectLog(datasourceId);
  }
}
