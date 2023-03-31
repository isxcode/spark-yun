package com.isxcode.star.agent.controller;

import com.isxcode.star.agent.service.AgentBizService;
import com.isxcode.star.api.pojos.agent.req.ExecuteReq;
import com.isxcode.star.api.pojos.agent.res.ExecuteRes;
import com.isxcode.star.api.pojos.agent.res.GetDataRes;
import com.isxcode.star.api.pojos.agent.res.GetLogRes;
import com.isxcode.star.api.pojos.agent.res.GetStatusRes;
import com.isxcode.star.api.pojos.agent.res.HeartCheckRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AgentController {

  private final AgentBizService agentBizService;

  /**
   * 提交作业.
   */
  @PostMapping("/execute")
  public ExecuteRes execute(@RequestBody ExecuteReq executeReq) {

    return agentBizService.execute(executeReq);
  }

  /**
   * 获取任务状态成功.
   */
  @GetMapping("/getStatus")
  public GetStatusRes getStatus(@RequestParam String applicationId) {

    return agentBizService.getStatus(applicationId);
  }

  /**
   * 获取作业日志成功.
   */
  @GetMapping("/getLog")
  public GetLogRes getLog(@RequestParam String applicationId) {

    return agentBizService.getLog(applicationId);
  }

  /**
   * 获取数据成功.
   */
  @PostMapping("/getData")
  public GetDataRes getData(@RequestParam String applicationId) {

    return agentBizService.getData(applicationId);
  }

  /**
   * 停止作业成功.
   */
  @PostMapping("/stopJob")
  public void stopJob(@RequestParam String applicationId) {

    agentBizService.stopJob(applicationId);
  }

  /**
   * 心跳检测.
   */
  @GetMapping("/heartCheck")
  public HeartCheckRes heartCheck() {

    return new HeartCheckRes("健康");
  }
}
