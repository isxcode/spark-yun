package com.isxcode.star.backend.module.workflow.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.engine.req.AddEngineReq;
import com.isxcode.star.api.pojos.engine.res.QueryEngineRes;
import com.isxcode.star.api.pojos.workflow.req.AddWorkflowReq;
import com.isxcode.star.api.pojos.workflow.res.QueryWorkflowRes;
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

  @PostMapping("/addWorkflow")
  public void addWorkflow(@Valid @RequestBody AddWorkflowReq addWorkflowReq) {

    workflowBizService.addWorkflow(addWorkflowReq);
  }

  @GetMapping("/queryWorkflow")
  public List<QueryWorkflowRes> queryWorkflow() {
    return workflowBizService.queryWorkflow();
  }

  @GetMapping("/delWorkflow")
  public void delWorkflow(@RequestParam String workflowId) {

    workflowBizService.delWorkflow(workflowId);
  }
}
