package com.isxcode.star.yun.agent.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.yun.agent.req.YagExecuteWorkReq;
import com.isxcode.star.api.pojos.yun.agent.res.YagExecuteWorkRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetDataRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetLogRes;
import com.isxcode.star.api.pojos.yun.agent.res.YagGetStatusRes;
import com.isxcode.star.common.response.SuccessResponse;
import com.isxcode.star.yun.agent.service.YunAgentBizService;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "代理模块")
@RequestMapping(ModulePrefix.YUN_AGENT)
@RestController
@RequiredArgsConstructor
public class YunAgentController {

  private final YunAgentBizService yunAgentBizService;

  @Operation(summary = "提交作业接口", description = "执行作业，将作业通过代理提交给yarn处理")
  @PostMapping("/executeWork")
  @SuccessResponse("提交成功")
  public YagExecuteWorkRes executeWork(@Valid @RequestBody YagExecuteWorkReq yagExecuteWorkReq) {

    return yunAgentBizService.executeWork(yagExecuteWorkReq);
  }

  @Operation(summary = "获取作业运行状态接口")
  @GetMapping("/getStatus")
  @SuccessResponse("获取成功")
  public YagGetStatusRes getStatus(@RequestParam String applicationId) {

    return yunAgentBizService.getStatus(applicationId);
  }

  @Operation(summary = "获取作业运行日志接口", description = "获取作业运行日志")
  @GetMapping("/getLog")
  @SuccessResponse("获取成功")
  public YagGetLogRes getLog(@RequestParam String applicationId) {

    return yunAgentBizService.getLog(applicationId);
  }

  @Operation(summary = "获取作业运行返回数据接口", description = "获取query数据")
  @GetMapping("/getData")
  @SuccessResponse("获取成功")
  public YagGetDataRes getData(@RequestParam String applicationId) {

    return yunAgentBizService.getData(applicationId);
  }

  @Operation(summary = "中止作业接口", description = "中止作业")
  @GetMapping("/stopJob")
  @SuccessResponse("中止成功")
  public void stopJob(@RequestParam String applicationId) {

    yunAgentBizService.stopJob(applicationId);
  }

  /**
   * 心跳检测.
   */
  @Operation(summary = "心跳检测接口", description = "心跳检测")
  @GetMapping("/heartCheck")
  @SuccessResponse("正常心跳")
  public void heartCheck() {
  }
}
