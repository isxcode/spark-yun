package com.isxcode.star.backend.module.engine.node.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.engine.node.req.EnoAddNodeReq;
import com.isxcode.star.api.pojos.engine.node.req.EnoQueryNodeReq;
import com.isxcode.star.api.pojos.engine.node.res.EnoQueryNodeRes;
import com.isxcode.star.backend.module.engine.node.service.EngineNodeBizService;
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

/** 引擎节点. */
@RestController
@RequestMapping(ModulePrefix.ENGINE_NODE)
@RequiredArgsConstructor
public class EngineNodeController {

  private final EngineNodeBizService engineNodeBizService;

  /** 添加引擎节点. */
  @PostMapping("/addNode")
  @SuccessResponse("添加集群节点成功")
  public void addNode(@Valid @RequestBody EnoAddNodeReq enoAddNodeReq) {

    engineNodeBizService.addNode(enoAddNodeReq);
  }

  /** 查询节点列表. */
  @PostMapping("/queryNode")
  @SuccessResponse("查询节点列表成功")
  public Page<EnoQueryNodeRes> queryNode(@Valid @RequestBody EnoQueryNodeReq enoQueryNodeReq) {

    return engineNodeBizService.queryNodes(enoQueryNodeReq);
  }

  /** 删除节点. */
  @GetMapping("/delNode")
  @SuccessResponse("删除节点成功")
  public void delNode(@RequestParam String engineNodeId) {

    engineNodeBizService.delNode(engineNodeId);
  }

  /** 安装代理. */
  @GetMapping("/installAgent")
  @SuccessResponse("安装成功")
  public void installAgent(@RequestParam String engineNodeId) {

    engineNodeBizService.installAgent(engineNodeId);
  }

  /** 检查代理是否可以安装. */
  @GetMapping("/checkAgent")
  @SuccessResponse("检测完成")
  public void checkAgent(@RequestParam String engineNodeId) {

    engineNodeBizService.checkAgent(engineNodeId);
  }

  /** 卸载代理. */
  @GetMapping("/removeAgent")
  @SuccessResponse("卸载完成")
  public void removeAgent(@RequestParam String engineNodeId) {

    engineNodeBizService.removeAgent(engineNodeId);
  }
}
