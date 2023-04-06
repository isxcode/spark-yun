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
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 代理模块. */
@RestController
@RequestMapping(ModulePrefix.YUN_AGENT)
@RequiredArgsConstructor
public class YunAgentController {

  private final YunAgentBizService yunAgentBizService;

  /** 执行作业，将作业通过代理提交给yarn处理. */
  @SuccessResponse("提交成功")
  @PostMapping("/executeWork")
  public YagExecuteWorkRes executeWork(@Valid @RequestBody YagExecuteWorkReq yagExecuteWorkReq) {

    return yunAgentBizService.executeWork(yagExecuteWorkReq);
  }

  /** 获取任务状态. */
  @SuccessResponse("获取成功")
  @GetMapping("/getStatus")
  public YagGetStatusRes getStatus(@RequestParam String applicationId) {

    return yunAgentBizService.getStatus(applicationId);
  }

  /** 获取作业运行日志. */
  @SuccessResponse("获取成功")
  @GetMapping("/getLog")
  public YagGetLogRes getLog(@RequestParam String applicationId) {

    return yunAgentBizService.getLog(applicationId);
  }

  /** 获取query数据. */
  @SuccessResponse("获取成功")
  @GetMapping("/getData")
  public YagGetDataRes getData(@RequestParam String applicationId) {

    return yunAgentBizService.getData(applicationId);
  }

  /** 中止作业. */
  @SuccessResponse("中止成功")
  @GetMapping("/stopJob")
  public void stopJob(@RequestParam String applicationId) {

    yunAgentBizService.stopJob(applicationId);
  }

  /** 心跳检测. */
  @SuccessResponse("正常心跳")
  @GetMapping("/heartCheck")
  public void heartCheck() {}
}
