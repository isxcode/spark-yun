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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 数据源模块controller. */
@RestController
@RequestMapping(ModulePrefix.DATASOURCE)
@RequiredArgsConstructor
public class DatasourceController {

  private final DatasourceBizService datasourceBizService;

  /** 添加数据源接口. */
  @SuccessResponse("添加数据源成功")
  @PostMapping("/addDatasource")
  public void addDatasource(@Valid @RequestBody DasAddDatasourceReq dasAddDatasourceReq) {

    datasourceBizService.addDatasource(dasAddDatasourceReq);
  }

  /** 查询所有数据源. */
  @SuccessResponse("查询数据源成功")
  @GetMapping("/queryDatasource")
  public Page<DasQueryDatasourceRes> queryDatasource(
      @Valid @RequestBody DasQueryDatasourceReq dasQueryDatasourceReq) {

    return datasourceBizService.queryDatasource(dasQueryDatasourceReq);
  }

  /** 删除数据源. */
  @SuccessResponse("删除数据源成功")
  @GetMapping("/delDatasource")
  public void delDatasource(@RequestParam String datasourceId) {

    datasourceBizService.delDatasource(datasourceId);
  }

  /** 测试数据源连接. */
  @SuccessResponse("连接测试成功")
  @PostMapping("/testConnect")
  public DasTestConnectRes testConnect(@Valid @RequestBody DasTestConnectReq dasTestConnectRes) {

    return datasourceBizService.testConnect(dasTestConnectRes);
  }
}
