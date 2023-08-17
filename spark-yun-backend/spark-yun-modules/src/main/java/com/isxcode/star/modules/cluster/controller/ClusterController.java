package com.isxcode.star.modules.cluster.controller;

import com.isxcode.star.api.cluster.pojos.req.*;
import com.isxcode.star.api.cluster.pojos.res.PageClusterRes;
import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.backend.api.base.constants.SecurityConstants;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.cluster.service.ClusterBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "计算引擎模块")
@RestController
@RequestMapping(ModuleCode.CLUSTER)
@RequiredArgsConstructor
public class ClusterController {

  private final ClusterBizService clusterBizService;

  @Operation(summary = "添加计算集群接口")
  @PostMapping("/addCluster")
  @SuccessResponse("添加成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void addCluster(@Valid @RequestBody AddClusterReq addClusterReq) {

    clusterBizService.addCluster(addClusterReq);
  }

  @Operation(summary = "更新计算集群接口")
  @PostMapping("/updateCluster")
  @SuccessResponse("更新成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void updateCluster(@Valid @RequestBody UpdateClusterReq updateClusterReq) {

    clusterBizService.updateCluster(updateClusterReq);
  }

  @Operation(summary = "分页查询计算集群接口")
  @PostMapping("/pageCluster")
  @SuccessResponse("查询计算集群成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public Page<PageClusterRes> pageCluster(@Valid @RequestBody PageClusterReq pageClusterReq) {

    return clusterBizService.pageCluster(pageClusterReq);
  }

  @Operation(summary = "删除计算集群接口")
  @GetMapping("/deleteCluster")
  @SuccessResponse("删除成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void deleteCluster(@Valid @RequestBody DeleteClusterReq deleteClusterReq) {

    clusterBizService.deleteCluster(deleteClusterReq);
  }

  @Operation(summary = "检测计算集群接口")
  @PostMapping("/checkCluster")
  @SuccessResponse("检测成功")
  @Parameter(
      name = SecurityConstants.HEADER_TENANT_ID,
      description = "租户id",
      required = true,
      in = ParameterIn.HEADER,
      schema = @Schema(type = "string", example = "sy_tenantId"))
  public void checkCluster(@Valid @RequestBody CheckClusterReq checkClusterReq) {

    clusterBizService.checkCluster(checkClusterReq);
  }
}
