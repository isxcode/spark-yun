package com.isxcode.star.backend.module.cluster.node;

import com.isxcode.star.api.annotations.SuccessResponse;
import com.isxcode.star.api.constants.base.ModulePrefix;
import com.isxcode.star.api.pojos.cluster.node.req.EnoAddNodeReq;
import com.isxcode.star.api.pojos.cluster.node.req.EnoQueryNodeReq;
import com.isxcode.star.api.pojos.cluster.node.req.EnoUpdateNodeReq;
import com.isxcode.star.api.pojos.cluster.node.res.EnoQueryNodeRes;
import com.isxcode.star.backend.module.user.action.UserLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
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
@RequestMapping(ModulePrefix.CLUSTER_NODE)
@RequiredArgsConstructor
public class ClusterNodeController {

  private final ClusterNodeBizService engineNodeBizService;

  @UserLog
  @Operation(summary = "添加引擎节点接口")
  @PostMapping("/addNode")
  @SuccessResponse("添加成功")
  public void addNode(@Valid @RequestBody EnoAddNodeReq enoAddNodeReq) {

    engineNodeBizService.addNode(enoAddNodeReq);
  }

  @UserLog
  @Operation(summary = "更新引擎节点接口")
  @PostMapping("/updateNode")
  @SuccessResponse("更新成功")
  public void updateNode(@Valid @RequestBody EnoUpdateNodeReq enoUpdateNodeReq) {

    engineNodeBizService.updateNode(enoUpdateNodeReq);
  }

  @UserLog
  @Operation(summary = "查询节点列表接口")
  @PostMapping("/queryNode")
  @SuccessResponse("查询节点列表成功")
  public Page<EnoQueryNodeRes> queryNode(@Valid @RequestBody EnoQueryNodeReq enoQueryNodeReq) {

    return engineNodeBizService.queryNodes(enoQueryNodeReq);
  }

  @UserLog
  @Operation(summary = "删除节点接口")
  @GetMapping("/delNode")
  @SuccessResponse("删除成功")
  public void delNode(
      @Schema(description = "引擎节点唯一id", example = "sy_de7c0478a75343ae853a637af1f4819c")
          @RequestParam
          String engineNodeId) {

    engineNodeBizService.delNode(engineNodeId);
  }

  @UserLog
  @Operation(summary = "检测节点接口")
  @GetMapping("/checkAgent")
  @SuccessResponse("开始检测")
  public void checkAgent(
      @Schema(description = "引擎节点唯一id", example = "sy_aaa9440040aa455d84c17f96d8cd7844")
          @RequestParam
          String engineNodeId) {

    engineNodeBizService.checkAgent(engineNodeId);
  }

  @UserLog
  @Operation(summary = "安装节点接口")
  @GetMapping("/installAgent")
  @SuccessResponse("激活中")
  public void installAgent(
      @Schema(description = "引擎节点唯一id", example = "sy_aaa9440040aa455d84c17f96d8cd7844")
          @RequestParam
          String engineNodeId) {

    engineNodeBizService.installAgent(engineNodeId);
  }

  @UserLog
  @Operation(summary = "停止节点接口")
  @GetMapping("/stopAgent")
  @SuccessResponse("停止中")
  public void stopAgent(
      @Schema(description = "引擎节点唯一id", example = "sy_aaa9440040aa455d84c17f96d8cd7844")
          @RequestParam
          String engineNodeId) {

    engineNodeBizService.stopAgent(engineNodeId);
  }

  @UserLog
  @Operation(summary = "激活节点接口")
  @GetMapping("/startAgent")
  @SuccessResponse("激活中")
  public void startAgent(
      @Schema(description = "引擎节点唯一id", example = "sy_aaa9440040aa455d84c17f96d8cd7844")
          @RequestParam
          String engineNodeId) {

    engineNodeBizService.startAgent(engineNodeId);
  }

  @UserLog
  @Operation(summary = "卸载代理接口")
  @GetMapping("/removeAgent")
  @SuccessResponse("卸载中")
  public void removeAgent(
      @Schema(description = "引擎节点唯一id", example = "sy_aaa9440040aa455d84c17f96d8cd7844")
          @RequestParam
          String engineNodeId) {

    engineNodeBizService.removeAgent(engineNodeId);
  }
}
