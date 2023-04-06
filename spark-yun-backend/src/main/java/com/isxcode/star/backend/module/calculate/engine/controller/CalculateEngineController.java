package com.isxcode.star.backend.module.calculate.engine.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.calculate.engine.req.CaeAddEngineReq;
import com.isxcode.star.api.pojos.calculate.engine.req.CaeQueryEngineReq;
import com.isxcode.star.api.pojos.calculate.engine.res.CaeQueryEngineRes;
import com.isxcode.star.backend.module.calculate.engine.service.CalculateEngineBizService;
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

/** 计算引擎部分. */
@RestController
@RequestMapping(ModulePrefix.CALCULATE_ENGINE)
@RequiredArgsConstructor
public class CalculateEngineController {

  private final CalculateEngineBizService calculateEngineBizService;

  /** 添加计算引擎. */
  @PostMapping("/addEngine")
  @SuccessResponse("添加计算引擎成功")
  public void addEngine(@Valid @RequestBody CaeAddEngineReq caeAddEngineReq) {

    calculateEngineBizService.addEngine(caeAddEngineReq);
  }

  /** 查询计算引擎. */
  @GetMapping("/queryEngine")
  @SuccessResponse("查询计算引擎成功")
  public Page<CaeQueryEngineRes> queryEngine(
      @Valid @RequestBody CaeQueryEngineReq caeQueryEngineReq) {

    return calculateEngineBizService.queryEngines(caeQueryEngineReq);
  }

  /** 删除计算引擎. */
  @GetMapping("/delEngine")
  @SuccessResponse("删除计算引擎成功")
  public void delEngine(@RequestParam String engineId) {

    calculateEngineBizService.delEngine(engineId);
  }
}
