package com.isxcode.star.modules.datasource.controller;

import com.isxcode.star.api.datasource.pojos.req.*;
import com.isxcode.star.api.datasource.pojos.res.GetConnectLogRes;
import com.isxcode.star.api.datasource.pojos.res.PageDatasourceRes;
import com.isxcode.star.api.datasource.pojos.res.TestConnectRes;
import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.api.user.constants.RoleType;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.datasource.service.biz.DatasourceBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "数据源模块")
@RestController
@RequestMapping(ModuleCode.DATASOURCE)
@RequiredArgsConstructor
public class DatasourceController {

  private final DatasourceBizService datasourceBizService;

  @Secured({RoleType.TENANT_ADMIN})
  @Operation(summary = "添加数据源接口")
  @PostMapping("/addDatasource")
  @SuccessResponse("添加成功")
  public void addDatasource(@Valid @RequestBody AddDatasourceReq addDatasourceReq) {

    datasourceBizService.addDatasource(addDatasourceReq);
  }

  @Secured({RoleType.TENANT_ADMIN})
  @Operation(summary = "更新数据源接口")
  @PostMapping("/updateDatasource")
  @SuccessResponse("更新成功")
  public void updateDatasource(@Valid @RequestBody UpdateDatasourceReq updateDatasourceReq) {

    datasourceBizService.updateDatasource(updateDatasourceReq);
  }

  @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
  @Operation(summary = "查询数据源列表接口")
  @PostMapping("/pageDatasource")
  @SuccessResponse("查询数据源成功")
  public Page<PageDatasourceRes> pageDatasource(
      @Valid @RequestBody PageDatasourceReq pageDatasourceReq) {

    return datasourceBizService.pageDatasource(pageDatasourceReq);
  }

  @Secured({RoleType.TENANT_ADMIN})
  @Operation(summary = "删除数据源接口")
  @PostMapping("/deleteDatasource")
  @SuccessResponse("删除成功")
  public void deleteDatasource(@Valid @RequestBody DeleteDatasourceReq deleteDatasourceReq) {

    datasourceBizService.deleteDatasource(deleteDatasourceReq);
  }

  @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
  @Operation(summary = "测试数据源连接接口")
  @PostMapping("/testConnect")
  @SuccessResponse("检测完成")
  public TestConnectRes testConnect(@Valid @RequestBody GetConnectLogReq testConnectReq) {

    return datasourceBizService.testConnect(testConnectReq);
  }

  @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
  @Operation(summary = "查询连接日志")
  @PostMapping("/getConnectLog")
  @SuccessResponse("获取成功")
  public GetConnectLogRes getConnectLog(@Valid @RequestBody GetConnectLogReq getConnectLogReq) {

    return datasourceBizService.getConnectLog(getConnectLogReq);
  }
}
