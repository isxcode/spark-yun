package com.isxcode.star.agent.controller;

import com.isxcode.star.agent.service.YunAgentBizService;
import com.isxcode.star.api.agent.pojos.req.ContainerCheckReq;
import com.isxcode.star.api.agent.pojos.req.DeployContainerReq;
import com.isxcode.star.api.agent.pojos.req.ExecuteContainerSqlReq;
import com.isxcode.star.api.agent.pojos.req.YagExecuteWorkReq;
import com.isxcode.star.api.agent.pojos.res.*;
import com.isxcode.star.api.main.constants.ModuleCode;
import com.isxcode.star.common.annotations.successResponse.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "至轻云代理模块")
@RequestMapping(ModuleCode.YUN_AGENT)
@RestController
@RequiredArgsConstructor
public class YunAgentController {

    private final YunAgentBizService yunAgentBizService;

    @Operation(summary = "提交作业接口")
    @PostMapping("/executeWork")
    @SuccessResponse("提交成功")
    public ExecuteWorkRes executeWork(@Valid @RequestBody YagExecuteWorkReq yagExecuteWorkReq) {

        return yunAgentBizService.executeWork(yagExecuteWorkReq);
    }

    @Operation(summary = "获取作业运行状态接口")
    @GetMapping("/getStatus")
    @SuccessResponse("获取成功")
    public YagGetStatusRes getStatus(@RequestParam String appId, @RequestParam String agentType,
        @RequestParam String sparkHomePath) {

        return yunAgentBizService.getStatus(appId, agentType, sparkHomePath);
    }

    @Operation(summary = "获取作业运行日志接口")
    @GetMapping("/getLog")
    @SuccessResponse("获取成功")
    public YagGetLogRes getLog(@RequestParam String appId, @RequestParam String agentType,
        @RequestParam String sparkHomePath) {

        return yunAgentBizService.getLog(appId, agentType, sparkHomePath);
    }

    @Operation(summary = "获取Stdout作业运行日志接口")
    @GetMapping("/getStdoutLog")
    @SuccessResponse("获取成功")
    public YagGetStdoutLogRes getStdoutLog(@RequestParam String appId, @RequestParam String agentType,
        @RequestParam String sparkHomePath) {

        return yunAgentBizService.getStdoutLog(appId, agentType, sparkHomePath);
    }

    @Operation(summary = "获取作业运行返回数据接口")
    @GetMapping("/getData")
    @SuccessResponse("获取成功")
    public YagGetDataRes getData(@RequestParam String appId, @RequestParam String agentType,
        @RequestParam String sparkHomePath) {

        return yunAgentBizService.getData(appId, agentType, sparkHomePath);
    }

    @Operation(summary = "中止作业接口")
    @GetMapping("/stopJob")
    @SuccessResponse("中止成功")
    public void stopJob(@RequestParam String appId, @RequestParam String agentType, @RequestParam String sparkHomePath,
        @RequestParam String agentHomePath) {
        yunAgentBizService.stopJob(appId, agentType, sparkHomePath, agentHomePath);
    }

    @Operation(summary = "提交容器接口")
    @PostMapping("/deployContainer")
    @SuccessResponse("提交成功")
    public DeployContainerRes deployContainer(@Valid @RequestBody DeployContainerReq deployContainerReq) {

        return yunAgentBizService.deployContainer(deployContainerReq);
    }

    @Operation(summary = "心跳检测接口")
    @GetMapping("/heartCheck")
    @SuccessResponse("正常心跳")
    public void heartCheck() {}

    @Operation(summary = "容器心跳检测接口")
    @PostMapping("/containerCheck")
    public ContainerCheckRes containerCheck(@RequestBody ContainerCheckReq containerCheckReq) {

        return yunAgentBizService.containerCheck(containerCheckReq);
    }

    @Operation(summary = "通过sql调用容器获取数据")
    @PostMapping("/executeContainerSql")
    public ContainerGetDataRes executeContainerSql(@RequestBody ExecuteContainerSqlReq executeContainerSqlReq) {

        return yunAgentBizService.executeContainerSql(executeContainerSqlReq);
    }
}
