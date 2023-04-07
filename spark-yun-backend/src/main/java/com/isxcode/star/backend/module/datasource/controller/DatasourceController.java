package com.isxcode.star.backend.module.datasource.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.datasource.req.DasAddDatasourceReq;
import com.isxcode.star.api.pojos.datasource.req.DasQueryDatasourceReq;
import com.isxcode.star.api.pojos.datasource.req.DasTestConnectReq;
import com.isxcode.star.api.pojos.datasource.res.DasQueryDatasourceRes;
import com.isxcode.star.api.pojos.datasource.res.DasTestConnectRes;
import com.isxcode.star.backend.module.datasource.service.DatasourceBizService;
import com.isxcode.star.common.response.SuccessResponse;
import javax.validation.Valid;

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

@Tag(name = "数据源模块")
@RestController
@RequestMapping(ModulePrefix.DATASOURCE)
@RequiredArgsConstructor
public class DatasourceController {

  private final DatasourceBizService datasourceBizService;

  @Operation(summary = "添加数据源接口")
  @PostMapping("/addDatasource")
  @SuccessResponse("添加数据源成功")
  public void addDatasource(@Valid @RequestBody DasAddDatasourceReq dasAddDatasourceReq) {

    datasourceBizService.addDatasource(dasAddDatasourceReq);
  }

  @Operation(summary = "查询数据源列表接口")
  @PostMapping("/queryDatasource")
  @SuccessResponse("查询数据源成功")
  public Page<DasQueryDatasourceRes> queryDatasource(@Valid @RequestBody DasQueryDatasourceReq dasQueryDatasourceReq) {

    return datasourceBizService.queryDatasource(dasQueryDatasourceReq);
  }

  @Operation(summary = "删除数据源接口")
  @GetMapping("/delDatasource")
  @SuccessResponse("删除数据源成功")
  public void delDatasource(@Schema(description = "数据源唯一id", example = "sy_344c3d583fa344f7a2403b19c5a654dc") @RequestParam String datasourceId) {

    datasourceBizService.delDatasource(datasourceId);
  }

  @Operation(summary = "测试数据源连接接口")
  @PostMapping("/testConnect")
  @SuccessResponse("连接测试成功")
  public DasTestConnectRes testConnect(@Valid @RequestBody DasTestConnectReq dasTestConnectRes) {

    return datasourceBizService.testConnect(dasTestConnectRes);
  }
}
