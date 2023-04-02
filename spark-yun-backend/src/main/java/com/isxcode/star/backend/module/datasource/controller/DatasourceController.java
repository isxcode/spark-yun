package com.isxcode.star.backend.module.datasource.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.datasource.req.AddDatasourceReq;
import com.isxcode.star.api.pojos.datasource.req.TestConnectReq;
import com.isxcode.star.api.pojos.datasource.res.QueryDatasourceRes;
import com.isxcode.star.api.pojos.datasource.res.TestConnectRes;
import com.isxcode.star.backend.module.datasource.service.DatasourceBizService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 只负责用户接口入口. */
@RestController
@RequestMapping(ModulePrefix.DATASOURCE)
@RequiredArgsConstructor
public class DatasourceController {

  private final DatasourceBizService datasourceBizService;

  @PostMapping("/addDatasource")
  public void addDatasource(@Valid @RequestBody AddDatasourceReq addDatasourceReq) {

    datasourceBizService.addDatasource(addDatasourceReq);
  }

  @GetMapping("/queryDatasource")
  public List<QueryDatasourceRes> queryDatasource() {

    return datasourceBizService.queryDatasource();
  }

  @GetMapping("/delDatasource")
  public void delDatasource(@RequestParam String datasourceId) {

    datasourceBizService.delDatasource(datasourceId);
  }

  @PostMapping("/testConnect")
  public TestConnectRes testConnect(@RequestBody TestConnectReq testConnectReq)
      throws ClassNotFoundException {

    return datasourceBizService.testConnect(testConnectReq);
  }

  @PostMapping("/updateUser")
  public void updateUser() {}
}
