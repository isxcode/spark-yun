package com.isxcode.star.backend.module.workflow.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.engine.req.AddEngineReq;
import com.isxcode.star.api.pojos.engine.res.QueryEngineRes;
import com.isxcode.star.api.pojos.work.req.AddWorkReq;
import com.isxcode.star.api.pojos.work.req.ConfigWorkReq;
import com.isxcode.star.api.pojos.work.res.GetWorkRes;
import com.isxcode.star.api.pojos.work.res.QueryWorkRes;
import com.isxcode.star.api.pojos.work.res.RunWorkRes;
import com.isxcode.star.api.pojos.workflow.req.AddWorkflowReq;
import com.isxcode.star.api.pojos.workflow.res.QueryWorkflowRes;
import com.isxcode.star.backend.module.workflow.service.WorkBizService;
import com.isxcode.star.backend.module.workflow.service.WorkflowBizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/** 只负责用户接口入口. */
@RestController
@RequestMapping(ModulePrefix.WORKFLOW)
@RequiredArgsConstructor
public class WorkflowController {

  private final WorkflowBizService workflowBizService;

  private final WorkBizService workBizService;

  /**
   * 添加作业流.
   */
  @PostMapping("/addWorkflow")
  public void addWorkflow(@Valid @RequestBody AddWorkflowReq addWorkflowReq) {

    workflowBizService.addWorkflow(addWorkflowReq);
  }

  /**
   * 查询作业流.
   */
  @GetMapping("/queryWorkflow")
  public List<QueryWorkflowRes> queryWorkflow() {
    return workflowBizService.queryWorkflow();
  }

  /**
   * 删除作业流.
   */
  @GetMapping("/delWorkflow")
  public void delWorkflow(@RequestParam String workflowId) {

    workflowBizService.delWorkflow(workflowId);
  }

  /**
   * 添加作业.
   */
  @PostMapping("/addWork")
  public void addWork(@Valid @RequestBody AddWorkReq addWorkReq) {

    workBizService.addWork(addWorkReq);
  }

  /**
   * 运行作业.
   */
  @GetMapping("/runWork")
  public RunWorkRes runWork(@RequestParam String workId) throws ClassNotFoundException {

    return workBizService.runWork(workId);
  }

  /**
   * 保存配置.
   */
  @PostMapping("/configWork")
  public void configWork(@RequestBody ConfigWorkReq configWorkReq) {

    workBizService.configWork(configWorkReq);
  }

  /**
   * 删除作业.
   */
  @GetMapping("/delWork")
  public void delWork(@RequestParam String workId) {

    workBizService.delWork(workId);
  }

  /**
   * 查询作业.
   */
  @GetMapping("/queryWork")
  public List<QueryWorkRes> queryWork(@RequestParam String workflowId) {

    return workBizService.queryWork(workflowId);
  }

  /**
   * 查询作业.
   */
  @GetMapping("/getWork")
  public GetWorkRes getWork(@RequestParam String workId) {

    return workBizService.getWork(workId);
  }

}
