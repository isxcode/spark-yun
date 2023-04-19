package com.isxcode.star.backend.module.calculate.engine.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.calculate.engine.req.CaeAddEngineReq;
import com.isxcode.star.api.pojos.calculate.engine.req.CaeQueryEngineReq;
import com.isxcode.star.api.pojos.calculate.engine.req.CaeUpdateEngineReq;
import com.isxcode.star.api.pojos.calculate.engine.res.CaeQueryEngineRes;
import com.isxcode.star.backend.module.calculate.engine.service.CalculateEngineBizService;
import com.isxcode.star.api.response.SuccessResponse;
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

@Tag(name = "计算引擎模块")
@RestController
@RequestMapping(ModulePrefix.CALCULATE_ENGINE)
@RequiredArgsConstructor
public class CalculateEngineController {

  private final CalculateEngineBizService calculateEngineBizService;

  @Operation(summary = "添加计算引擎接口")
  @PostMapping("/addEngine")
  @SuccessResponse("添加成功")
  public void addEngine(@Valid @RequestBody CaeAddEngineReq caeAddEngineReq) {

    calculateEngineBizService.addEngine(caeAddEngineReq);
  }

  @Operation(summary = "更新计算引擎接口")
  @PostMapping("/updateEngine")
  @SuccessResponse("更新成功")
  public void addEngine(@Valid @RequestBody CaeUpdateEngineReq caeUpdateEngineReq) {

    calculateEngineBizService.updateEngine(caeUpdateEngineReq);

  }

  @Operation(summary = "查询计算引擎接口")
  @PostMapping("/queryEngine")
  @SuccessResponse("查询计算引擎成功")
  public Page<CaeQueryEngineRes> queryEngine( @Valid @RequestBody CaeQueryEngineReq caeQueryEngineReq) {

    return calculateEngineBizService.queryEngines(caeQueryEngineReq);
  }

  @Operation(summary = "删除计算引擎接口")
  @GetMapping("/delEngine")
  @SuccessResponse("删除成功")
  public void delEngine(@Schema(description = "计算引擎唯一id", example = "sy_b0288cadb2ab4325ae519ff329a95cda") @RequestParam String engineId) {

    calculateEngineBizService.delEngine(engineId);
  }

  @Operation(summary = "检测计算引擎接口")
  @GetMapping("/checkEngine")
  @SuccessResponse("检测成功")
  public void checkEngine(@Schema(description = "计算引擎唯一id", example = "sy_b0288cadb2ab4325ae519ff329a95cda") @RequestParam String engineId) {

    calculateEngineBizService.checkEngine(engineId);
  }
}
