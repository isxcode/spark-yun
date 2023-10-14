package com.isxcode.star.modules.cluster.controller;

import com.isxcode.star.api.cluster.pojos.req.*;
import com.isxcode.star.api.cluster.pojos.res.EnoQueryNodeRes;
import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import com.isxcode.star.modules.cluster.service.biz.ClusterNodeBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "引擎节点模块")
@RestController
@RequestMapping(ModuleCode.CLUSTER_NODE)
@RequiredArgsConstructor
public class ClusterNodeController {

	private final ClusterNodeBizService clusterNodeBizService;

	@Operation(summary = "添加引擎节点接口")
	@PostMapping("/addClusterNode")
	@SuccessResponse("添加成功")
	public void addClusterNode(@Valid @RequestBody AddClusterNodeReq addClusterNodeReq) {

		clusterNodeBizService.addClusterNode(addClusterNodeReq);
	}

	@Operation(summary = "更新引擎节点接口")
	@PostMapping("/updateClusterNode")
	@SuccessResponse("更新成功")
	public void updateClusterNode(@Valid @RequestBody UpdateClusterNodeReq updateClusterNodeReq) {

		clusterNodeBizService.updateClusterNode(updateClusterNodeReq);
	}

	@Operation(summary = "查询节点列表接口")
	@PostMapping("/pageClusterNode")
	@SuccessResponse("查询节点列表成功")
	public Page<EnoQueryNodeRes> pageClusterNode(@Valid @RequestBody PageClusterNodeReq pageClusterNodeReq) {

		return clusterNodeBizService.pageClusterNode(pageClusterNodeReq);
	}

	@Operation(summary = "删除节点接口")
	@PostMapping("/deleteClusterNode")
	@SuccessResponse("删除成功")
	public void deleteClusterNode(@Valid @RequestBody DeleteClusterNodeReq deleteClusterNodeReq) {

		clusterNodeBizService.deleteClusterNode(deleteClusterNodeReq);
	}

	@Operation(summary = "检测节点接口")
	@PostMapping("/checkAgent")
	@SuccessResponse("开始检测")
	public void checkAgent(@Valid @RequestBody CheckAgentReq checkAgentReq) {

		clusterNodeBizService.checkAgent(checkAgentReq);
	}

	@Operation(summary = "安装节点接口")
	@PostMapping("/installAgent")
	@SuccessResponse("激活中")
	public void installAgent(@Valid @RequestBody InstallAgentReq installAgentReq) {

		clusterNodeBizService.installAgent(installAgentReq);
	}

	@Operation(summary = "停止节点接口")
	@PostMapping("/stopAgent")
	@SuccessResponse("停止中")
	public void stopAgent(@Valid @RequestBody StopAgentReq stopAgentReq) {

		clusterNodeBizService.stopAgent(stopAgentReq);
	}

	@Operation(summary = "激活节点接口")
	@PostMapping("/startAgent")
	@SuccessResponse("激活中")
	public void startAgent(@Valid @RequestBody StartAgentReq startAgentReq) {

		clusterNodeBizService.startAgent(startAgentReq);
	}

	@Operation(summary = "卸载代理接口")
	@PostMapping("/removeAgent")
	@SuccessResponse("卸载中")
	public void removeAgent(@Valid @RequestBody RemoveAgentReq removeAgentReq) {

		clusterNodeBizService.removeAgent(removeAgentReq);
	}

	@Operation(summary = "清理代理接口")
	@PostMapping("/cleanAgent")
	@SuccessResponse("清理成功")
	public void cleanAgent(@Valid @RequestBody CleanAgentReq cleanAgentReq) {

		clusterNodeBizService.cleanAgent(cleanAgentReq);
	}
}
