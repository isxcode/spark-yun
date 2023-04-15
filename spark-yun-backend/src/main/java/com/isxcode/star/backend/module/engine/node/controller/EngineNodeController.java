package com.isxcode.star.backend.module.engine.node.controller;

import com.isxcode.star.api.constants.ModulePrefix;
import com.isxcode.star.api.pojos.engine.node.req.EnoAddNodeReq;
import com.isxcode.star.api.pojos.engine.node.req.EnoQueryNodeReq;
import com.isxcode.star.api.pojos.engine.node.req.EnoUpdateNodeReq;
import com.isxcode.star.api.pojos.engine.node.res.EnoCheckAgentRes;
import com.isxcode.star.api.pojos.engine.node.res.EnoInstallAgentRes;
import com.isxcode.star.api.pojos.engine.node.res.EnoQueryNodeRes;
import com.isxcode.star.api.pojos.engine.node.res.EnoRemoveAgentRes;
import com.isxcode.star.backend.module.engine.node.service.EngineNodeBizService;
import com.isxcode.star.common.response.SuccessResponse;
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

@Tag(name = "引擎节点模块")
@RestController
@RequestMapping(ModulePrefix.ENGINE_NODE)
@RequiredArgsConstructor
public class EngineNodeController {

  private final EngineNodeBizService engineNodeBizService;

  @Operation(summary = "添加引擎节点接口")
  @PostMapping("/addNode")
  @SuccessResponse("添加成功")
  public void addNode(@Valid @RequestBody EnoAddNodeReq enoAddNodeReq) {

    engineNodeBizService.addNode(enoAddNodeReq);
  }

  @Operation(summary = "更新引擎节点接口")
  @PostMapping("/updateNode")
  @SuccessResponse("更新成功")
  public void updateNode(@Valid @RequestBody EnoUpdateNodeReq enoUpdateNodeReq) {

    engineNodeBizService.updateNode(enoUpdateNodeReq);
  }

  @Operation(summary = "查询节点列表接口")
  @PostMapping("/queryNode")
  @SuccessResponse("查询节点列表成功")
  public Page<EnoQueryNodeRes> queryNode(@Valid @RequestBody EnoQueryNodeReq enoQueryNodeReq) {

    return engineNodeBizService.queryNodes(enoQueryNodeReq);
  }

  @Operation(summary = "删除节点接口")
  @GetMapping("/delNode")
  @SuccessResponse("删除成功")
  public void delNode(@Schema(description = "引擎节点唯一id", example = "sy_de7c0478a75343ae853a637af1f4819c") @RequestParam String engineNodeId) {

    engineNodeBizService.delNode(engineNodeId);
  }

  @Operation(summary = "检查代理接口")
  @GetMapping("/checkAgent")
  @SuccessResponse("检测完成")
  public EnoCheckAgentRes checkAgent(@Schema(description = "引擎节点唯一id", example = "sy_aaa9440040aa455d84c17f96d8cd7844") @RequestParam String engineNodeId) {

    return engineNodeBizService.checkAgent(engineNodeId);
  }

  @Operation(summary = "安装代理接口")
  @GetMapping("/installAgent")
  @SuccessResponse("安装完成")
  public EnoInstallAgentRes installAgent(@Schema(description = "引擎节点唯一id", example = "sy_aaa9440040aa455d84c17f96d8cd7844") @RequestParam String engineNodeId) {

    return engineNodeBizService.installAgent(engineNodeId);
  }

  @Operation(summary = "卸载代理接口")
  @GetMapping("/removeAgent")
  @SuccessResponse("卸载完成")
  public EnoRemoveAgentRes removeAgent(@Schema(description = "引擎节点唯一id", example = "sy_aaa9440040aa455d84c17f96d8cd7844") @RequestParam String engineNodeId) {

    return engineNodeBizService.removeAgent(engineNodeId);
  }
}
