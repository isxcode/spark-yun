package com.isxcode.star.backend.module.engine.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.engine.req.AddEngineReq;
import com.isxcode.star.api.pojos.engine.res.QueryEngineRes;
import com.isxcode.star.backend.module.engine.service.EngineBizService;
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
@RequestMapping(ModulePrefix.ENGINE)
@RequiredArgsConstructor
public class EngineController {

  private final EngineBizService engineBizService;

  @PostMapping("/addEngine")
  public void addEngine(@Valid @RequestBody AddEngineReq addEngineReq) {

    engineBizService.addEngine(addEngineReq);
  }

  @GetMapping("/queryEngine")
  public List<QueryEngineRes> queryEngine() {
    return engineBizService.queryEngines();
  }

  @GetMapping("/delEngine")
  public void delEngine(@RequestParam String engineId) {

    engineBizService.delEngine(engineId);
  }

  @PostMapping("/updateUser")
  public void updateUser() {}
}
