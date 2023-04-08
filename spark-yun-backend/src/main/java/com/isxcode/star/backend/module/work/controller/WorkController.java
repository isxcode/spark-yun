package com.isxcode.star.backend.module.work.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.work.req.WokGetDataReq;
import com.isxcode.star.api.pojos.work.req.WokGetStatusReq;
import com.isxcode.star.api.pojos.work.req.WokGetWorkLogReq;
import com.isxcode.star.api.pojos.work.req.WokQueryWorkReq;
import com.isxcode.star.api.pojos.work.req.WokAddWorkReq;
import com.isxcode.star.api.pojos.work.req.WokStopJobReq;
import com.isxcode.star.api.pojos.work.res.WokGetDataRes;
import com.isxcode.star.api.pojos.work.res.WokGetStatusRes;
import com.isxcode.star.api.pojos.work.res.WokGetWorkLogRes;
import com.isxcode.star.api.pojos.work.res.WokGetWorkRes;
import com.isxcode.star.api.pojos.work.res.WokQueryWorkRes;
import com.isxcode.star.api.pojos.work.res.WokRunWorkRes;
import com.isxcode.star.backend.module.work.service.WorkBizService;
import com.isxcode.star.common.response.SuccessResponse;
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

import javax.validation.Valid;

@Tag(name = "作业模块")
@RestController
@RequestMapping(ModulePrefix.WORK)
@RequiredArgsConstructor
public class WorkController {

  private final WorkBizService workBizService;

  @Operation(summary = "添加作业接口")
  @PostMapping("/addWork")
  @SuccessResponse("创建成功")
  public void addWork(@Valid @RequestBody WokAddWorkReq addWorkReq) {

    workBizService.addWork(addWorkReq);
  }

  @Operation(summary = "运行作业接口")
  @GetMapping("/runWork")
  @SuccessResponse("提交成功")
  public WokRunWorkRes runWork(@Schema(description = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4") @RequestParam String workId) {

    return workBizService.runWork(workId);
  }

  @Operation(summary = "查询作业运行日志接口")
  @PostMapping("/getWorkLog")
  @SuccessResponse("查询成功")
  public WokGetWorkLogRes getWorkLog(@Valid @RequestBody WokGetWorkLogReq wokGetWorkLogReq) {

    return workBizService.getWorkLog(wokGetWorkLogReq);
  }

  @Operation(summary = "查询作业返回数据接口")
  @PostMapping("/getData")
  @SuccessResponse("查询成功")
  public WokGetDataRes getData(@Valid @RequestBody WokGetDataReq wokGetWorkRes) {

    return workBizService.getData(wokGetWorkRes);
  }

  @Operation(summary = "中止作业接口")
  @PostMapping("/stopJob")
  @SuccessResponse("中止成功")
  public void stopJob(@Valid @RequestBody WokStopJobReq wokStopJobReq) {

    workBizService.stopJob(wokStopJobReq);
  }

  @Operation(summary = "获取作业当前运行状态接口")
  @PostMapping("/getStatus")
  @SuccessResponse("获取成功")
  public WokGetStatusRes getStatus(@Valid @RequestBody WokGetStatusReq wokGetStatusReq) {

    return workBizService.getStatus(wokGetStatusReq);
  }

  @Operation(summary = "删除作业接口")
  @GetMapping("/delWork")
  @SuccessResponse("删除成功")
  public void delWork(@Schema(description = "作业唯一id", example = "sy_12baf74d710c43a78858e547bf41a586") @RequestParam String workId) {

    workBizService.delWork(workId);
  }

  @Operation(summary = "查询作业列表接口")
  @PostMapping("/queryWork")
  @SuccessResponse("查询成功")
  public Page<WokQueryWorkRes> queryWork(@Valid @RequestBody WokQueryWorkReq wocQueryWorkReq) {

    return workBizService.queryWork(wocQueryWorkReq);
  }

  @Operation(summary = "获取作业信息接口")
  @GetMapping("/getWork")
  @SuccessResponse("获取成功")
  public WokGetWorkRes getWork(@Schema(description = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4") @RequestParam String workId) {

    return workBizService.getWork(workId);
  }
}
