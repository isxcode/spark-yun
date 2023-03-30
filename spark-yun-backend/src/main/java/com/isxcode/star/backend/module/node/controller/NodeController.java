package com.isxcode.star.backend.module.node.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.node.req.AddNodeReq;
import com.isxcode.star.api.pojos.node.res.QueryNodeRes;
import com.isxcode.star.backend.module.node.service.NodeBizService;
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
@RequestMapping(ModulePrefix.NODE)
@RequiredArgsConstructor
public class NodeController {

  private final NodeBizService nodeBizService;

  @PostMapping("/addNode")
  public void addNode(@Valid @RequestBody AddNodeReq addNodeReq) {

    nodeBizService.addNode(addNodeReq);
  }

  @GetMapping("/queryNode")
  public List<QueryNodeRes> queryNode(@RequestParam String engineId) {
    return nodeBizService.queryNodes(engineId);
  }

  @GetMapping("/delNode")
  public void delNode(@RequestParam String nodeId) {

    nodeBizService.delNode(nodeId);
  }

  @PostMapping("/updateUser")
  public void updateUser() {}
}
